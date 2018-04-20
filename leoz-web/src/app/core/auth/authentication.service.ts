import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { distinctUntilChanged, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { RoleGuard } from './role.guard';
import { MsgService } from '../../shared/msg/msg.service';
import { User } from '../models/user.model';
import { Station } from './station.model';

@Injectable()
export class AuthenticationService {

  private authUrl = `${environment.apiUrl}/internal/v1/authorize/web`;
  private debitorUrl = `${environment.apiUrl}/internal/v1/station/debitor/`;


  allowedStations: number[];

  private debitorStationsSubject = new BehaviorSubject<Station[]>( null );
  public debitorStations$ = this.debitorStationsSubject.asObservable().pipe( distinctUntilChanged() );

  private activeStationSubject = new BehaviorSubject<Station>( null );
  public activeStation$ = this.activeStationSubject.asObservable().pipe( distinctUntilChanged() );

  constructor( private router: Router,
               private http: HttpClient,
               private roleGuard: RoleGuard,
               private msgService: MsgService ) {
    this.debitorStationsSubject.next( JSON.parse( localStorage.getItem( 'debitorStations' ) ) );
    this.activeStationSubject.next( JSON.parse( localStorage.getItem( 'activeStation' ) ) );
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
    } ).pipe(
      map( ( response: HttpResponse<any> ) => {
        if (response.status === 200) {
          const userJson = response.body;
          const user: User = userJson[ 'user' ];
          this.roleGuard.userRole = user.role;
          if (!user.active) {
            this.msgService.error( 'user account deactivated' );
            return response;
          }
          if (!user.allowedStations || user.allowedStations.length === 0) {
            this.msgService.error( 'user has none allowed station' );
            return response;
          }
          localStorage.setItem( 'currentUser', JSON.stringify( userJson ) );
          localStorage.setItem( 'version', environment.version );
          this.fetchStations( user.debitorId );
        }
        return response;
      } ) );
  }

  public fetchStations( debitorId: number ) {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    this.allowedStations = currUser.user.allowedStations;
    this.http.get<Station[]>( this.debitorUrl + debitorId.toString() )
      .subscribe( ( stations ) => {
          const debitorStations = stations
            .filter(( station: Station ) => this.allowedStations.indexOf(station.stationNo) >= 0)
            .map( ( station: Station ) => <Station> {
            stationNo: station.stationNo,
            address: station.address,
            exportValuablesAllowed: station.exportValuablesAllowed,
            exportValuablesWithoutBagAllowed: station.exportValuablesWithoutBagAllowed
          } );
          this.debitorStationsSubject.next( debitorStations );
          localStorage.setItem( 'debitorStations', JSON.stringify( debitorStations ) );
          if(debitorStations.length > 0) {
            const firstStation = debitorStations[ 0 ];
            this.changeActiveStation( <Station>{
              stationNo: firstStation.stationNo,
              address: firstStation.address,
              exportValuablesAllowed: firstStation.exportValuablesAllowed,
              exportValuablesWithoutBagAllowed: firstStation.exportValuablesWithoutBagAllowed
            } );
          }
        },
        ( error: Response ) => {
          console.log( error );
          const defaultStation = <Station> {
            stationNo: -1,
            exportValuablesAllowed: false,
            exportValuablesWithoutBagAllowed: false
          };
          localStorage.setItem( 'debitorStations', JSON.stringify( [ -1 ] ) );
          this.changeActiveStation( defaultStation );
        } );
  }

  public changeActiveStation( station: Station ) {
    this.activeStationSubject.next( station );
    localStorage.setItem( 'activeStation', JSON.stringify( station ) );
  }
}
