import { Injectable } from '@angular/core';
import { Http, RequestOptions, Response, URLSearchParams } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';
import { environment } from '../../../environments/environment';
import { Driver } from './driver.model';
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';
import { MsgService } from '../../shared/msg/msg.service';

@Injectable()
export class DriverService {

  private driverListUrl = `${environment.apiUrl}/internal/v1/user`;

  private driversSubject = new BehaviorSubject<Driver[]>( [] );
  public drivers = this.driversSubject.asObservable().distinctUntilChanged();

  private currentDriversSubject = new BehaviorSubject<Driver>( <Driver> {} );
  public currentDriver = this.currentDriversSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http,
               private msgService: MsgService ) {
  }

  getDrivers(): void {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      search: new URLSearchParams()
    } );

    this.http.get( this.driverListUrl, options )
      .subscribe( ( response: Response ) => {
          const drivers = <Driver[]> response.json();
          const onlyCurrDriver = drivers.filter( ( driver: Driver ) => driver.email === currUser.user.email );
          const currDriver = onlyCurrDriver.length > 0 ? onlyCurrDriver[0] : <Driver> {};
          this.driversSubject.next( drivers );
          this.currentDriversSubject.next( currDriver );
        },
        ( error: Response ) => {
          this.driversSubject.next( <Driver[]> [] );
          this.currentDriversSubject.next( <Driver> {} );
          this.msgService.handleResponse( error );
        } );
  }

}
