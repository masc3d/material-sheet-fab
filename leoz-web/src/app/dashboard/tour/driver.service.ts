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

@Injectable()
export class DriverService {

  private driverListUrl = `${environment.apiUrl}/internal/v1/user`;

  private driversSubject = new BehaviorSubject<Driver[]>( [] );
  public drivers = this.driversSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http ) {
  }

  getDrivers(): void {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      search: new URLSearchParams()
    } );

    this.http.get( this.driverListUrl, options )
      .subscribe( ( response: Response ) => this.driversSubject.next( <Driver[]> response.json() ),
        ( error: Response ) => {
          console.log( error );
          this.driversSubject.next( <Driver[]> {} );
        } );
  }

}
