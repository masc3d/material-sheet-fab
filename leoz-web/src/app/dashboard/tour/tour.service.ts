import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Headers, Http, RequestMethod, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Position } from './position.model';
import { Subscription } from 'rxjs/Subscription';

@Injectable()
export class TourService {

  private activeMarkerSubject = new BehaviorSubject<Position>( <Position> {} );
  public activeMarker = this.activeMarkerSubject.asObservable().distinctUntilChanged();
  private locationUrl = `${environment.apiUrl}/internal/v1/location`;
  private subscription: Subscription;

  constructor( private http: Http ) {
  }

  getLocation(email: string): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const headers = new Headers();
    headers.append( 'Content-Type', 'application/json' );
    headers.append( 'x-api-key', currUser.key );

    // const queryParameters = new URLSearchParams();
    // queryParameters.set('email', email);
    // queryParameters.set('debitor-id', currUser.debitorNo);

    const options = new RequestOptions( {
      method: RequestMethod.Get,
      headers: headers,
      // search: queryParameters
    } );

    return this.http.request( `${this.locationUrl}?email=${email}`, options );
  }

  changeActiveMarker( selectedDriver ) {
    this.subscription = this.getLocation(selectedDriver.email)
      .subscribe( ( response: Response ) =>  {
          const positions = <Position[]> response.json();
          this.activeMarkerSubject.next( positions[0] );
      },
      ( error: Response ) => this.errorHandler( error ) );
  }

  errorHandler( error: Response ) {
    console.log( error );
    return Observable.of( [] );
  }
}
