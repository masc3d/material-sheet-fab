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
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';

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

    const options = new RequestOptions( {
      headers:  ApiKeyHeaderFactory.headers(currUser.key),
      search: new URLSearchParams()
    } );

    return this.http.get( this.driverListUrl, options )
      .map( ( response: Response ) => <Driver[]> response.json() )
      .catch( ( error: Response ) => this.errorHandler( error ) );
  }

  errorHandler( error: Response ) {
    console.log( error );
    return Observable.of( [] );
  }

}
