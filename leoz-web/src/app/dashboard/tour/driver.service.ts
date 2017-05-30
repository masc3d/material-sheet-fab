import { Injectable } from '@angular/core';
import { Headers, Http, RequestMethod, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';
import { environment } from '../../../environments/environment';
import { Driver } from './driver.model';

@Injectable()
export class DriverService {

  private driverListUrl = `${environment.apiUrl}/internal/v1/user`;

  // private usersSubject = new BehaviorSubject<User[]>( [] );
  // public users = this.usersSubject.asObservable().distinctUntilChanged();
  private activeDriverSubject = new BehaviorSubject<Driver>( <Driver> {} );
  public activeDriver = this.activeDriverSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http ) {
  }

  getDrivers() {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const headers = new Headers();
    headers.append( 'Content-Type', 'application/json' );
    headers.append( 'x-api-key', currUser.key );

    const queryParameters = new URLSearchParams();
    // queryParameters.set('email', currUser.email);
    // queryParameters.set('debitor-id', currUser.debitorNo);

    const options = new RequestOptions( {
      method: RequestMethod.Get,
      headers: headers,
      search: queryParameters
    } );

    return this.http.request( this.driverListUrl, options )
      .map( ( response: Response ) => <Driver[]> response.json() )
      .catch( ( error: Response ) => this.errorHandler( error ) );
  }

  errorHandler( error: Response ) {
    console.log( error );
    return Observable.of( [] );
  }

}
