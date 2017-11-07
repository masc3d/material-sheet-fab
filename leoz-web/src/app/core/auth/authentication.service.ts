import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { environment } from '../../../environments/environment';
import { RoleGuard } from './role.guard';
import { MsgService } from '../../shared/msg/msg.service';
import { User } from '../../dashboard/user/user.model';
import { Station } from './station.model';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class AuthenticationService {

  private authUrl = `${environment.apiUrl}/internal/v1/authorize/web`;
  private debitorUrl = `${environment.apiUrl}/internal/v1/station/debitor/`;

  private debitorStationsSubject = new BehaviorSubject<Station[]>( null );
  public debitorStations$ = this.debitorStationsSubject.asObservable().distinctUntilChanged();

  private activeStationSubject = new BehaviorSubject<Station>( null );
  public activeStation$ = this.activeStationSubject.asObservable().distinctUntilChanged();

  constructor( private router: Router,
               private http: HttpClient,
               private roleGuard: RoleGuard,
               private msgService: MsgService ) {
    this.debitorStationsSubject.next(JSON.parse( localStorage.getItem( 'debitorStations' ) ) );
    this.activeStationSubject.next(JSON.parse( localStorage.getItem( 'activeStation' ) ) );
  }

  logout() {
    this.roleGuard.userRole = null;
    localStorage.removeItem( 'currentUser' );
    localStorage.removeItem( 'debitorStations' );
    localStorage.removeItem( 'activeStation' );
    localStorage.removeItem( 'version' );
    this.router.navigate( [ 'login' ] );
  }

  login( username: string, password: string ): Observable<HttpResponse<any>> {

    const body = {
      email: `${username}`,
      password: `${password}`
    };

    return this.http.patch( this.authUrl, body, {
      observe: 'response'
    } ).map( ( response: HttpResponse<any> ) => {
      if (response.status === 200) {
        const userJson = response.body;
        const user: User = userJson[ 'user' ];
        this.roleGuard.userRole = user.role;
        if (user.active) {
          localStorage.setItem( 'currentUser', JSON.stringify( userJson ) );
          localStorage.setItem('version', environment.version );
          this.fetchStations( user.debitorId );
        } else {
          this.msgService.error( 'user account deactivated' );
        }
      }
      return response;
    } );
  }

  public fetchStations( debitorId: number ) {
    this.http.get<Station[]>( this.debitorUrl + debitorId.toString() )
      .subscribe( ( stations ) => {
          const debitorStations = stations.map( ( station: Station ) => <Station>{
            stationNo: station.stationNo,
            exportValuablesAllowed: station.exportValuablesAllowed,
            exportValuablesWithoutBagAllowed: station.exportValuablesWithoutBagAllowed
          } );
          this.debitorStationsSubject.next( debitorStations );
          localStorage.setItem( 'debitorStations', JSON.stringify( debitorStations ) );
          const firstStation = stations[ 0 ];
          this.changeActiveStation( <Station>{
            stationNo: firstStation.stationNo,
            exportValuablesAllowed: firstStation.exportValuablesAllowed,
            exportValuablesWithoutBagAllowed: firstStation.exportValuablesWithoutBagAllowed
          } );
        },
        ( error: Response ) => {
          console.log( error );
          const defaultStation = <Station> {
            stationNo: -1,
            exportValuablesAllowed: false,
            exportValuablesWithoutBagAllowed: false
          };
          localStorage.setItem( 'debitorStations', JSON.stringify( [-1] ) );
          this.changeActiveStation( defaultStation );
        } );
  }

  public changeActiveStation( station: Station ) {
    this.activeStationSubject.next( station );
    localStorage.setItem( 'activeStation', JSON.stringify( station ) );
  }
}
