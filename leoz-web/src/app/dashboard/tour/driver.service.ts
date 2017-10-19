import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';
import { environment } from '../../../environments/environment';
import { Driver } from './driver.model';
import { MsgService } from '../../shared/msg/msg.service';

@Injectable()
export class DriverService {

  private driverListUrl = `${environment.apiUrl}/internal/v1/user`;

  private driversSubject = new BehaviorSubject<Driver[]>( [] );
  public drivers$ = this.driversSubject.asObservable().distinctUntilChanged();

  private currentDriversSubject = new BehaviorSubject<Driver>( <Driver> {} );
  public currentDriver$ = this.currentDriversSubject.asObservable().distinctUntilChanged();

  constructor( private http: HttpClient,
               private msgService: MsgService ) {
  }

  getDrivers(): void {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    this.http.get<Driver[]>( this.driverListUrl )
      .subscribe( ( drivers ) => {
          const onlyCurrDriver = drivers.filter( ( driver: Driver ) => driver.email === currUser.user.email );
          const currDriver = onlyCurrDriver.length > 0 ? onlyCurrDriver[0] : <Driver> {};
          this.driversSubject.next( drivers );
          this.currentDriversSubject.next( currDriver );
        },
        ( error: HttpErrorResponse ) => {
          this.driversSubject.next( <Driver[]> [] );
          this.currentDriversSubject.next( <Driver> {} );
          this.msgService.handleResponse( error );
        } );
  }

}
