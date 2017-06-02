import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Http, RequestOptions, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Position } from './position.model';
import { Subscription } from 'rxjs/Subscription';
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';

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

    const queryParameters = new URLSearchParams();
    queryParameters.set('email', email);

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.locationUrl, options );
  }

  changeActiveMarker( selectedDriver ) {
    this.subscription = this.getLocation(selectedDriver.email)
      .subscribe( ( response: Response ) =>  {
          const positions = <Position[]> response.json()[0]['gpsDataPoints'];
          this.activeMarkerSubject.next( positions[0] );
      },
      ( error: Response ) => this.errorHandler( error ) );
  }

  errorHandler( error: Response ) {
    console.log( error );
    return Observable.of( [] );
  }
}
