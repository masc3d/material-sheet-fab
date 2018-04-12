import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { distinctUntilChanged } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { Driver } from './driver.model';
import { MsgService } from '../../shared/msg/msg.service';
import { InetConnectionService } from '../../core/inet-connection.service';
import { Station } from '../../core/auth/station.model';

@Injectable()
export class DriverService {

  private driverListUrl = `${environment.apiUrl}/internal/v1/user`;

  private driversSubject = new BehaviorSubject<Driver[]>( [] );
  public drivers$ = this.driversSubject.asObservable().pipe( distinctUntilChanged() );

  private currentDriversSubject = new BehaviorSubject<Driver>( <Driver> {} );
  public currentDriver$ = this.currentDriversSubject.asObservable().pipe( distinctUntilChanged() );

  constructor( private http: HttpClient,
               private msgService: MsgService,
               private ics: InetConnectionService ) {
  }

  getDrivers( activeStation: Station ): void {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    this.http.get<Driver[]>( this.driverListUrl )
      .subscribe( ( drivers ) => {
          const driverAllowed4Station = drivers
            .filter( ( driver: Driver ) => driver.allowedStations.indexOf( activeStation.stationNo ) >= 0 );
          const onlyCurrDriver = driverAllowed4Station.filter( ( driver: Driver ) => driver.email === currUser.user.email );
          const currDriver = onlyCurrDriver.length > 0 ? onlyCurrDriver[ 0 ] : <Driver> {};
          this.driversSubject.next( driverAllowed4Station );
          this.currentDriversSubject.next( currDriver );
        },
        ( error: HttpErrorResponse ) => {
          this.ics.isOffline();
          this.driversSubject.next( <Driver[]> [] );
          this.currentDriversSubject.next( <Driver> {} );
          this.msgService.handleResponse( error );
        } );
  }

}
