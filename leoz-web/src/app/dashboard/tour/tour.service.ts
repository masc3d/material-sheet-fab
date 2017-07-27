import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Http, RequestOptions, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Position } from './position.model';
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';
import { MsgService } from '../../shared/msg/msg.service';
import { Driver } from './driver.model';

@Injectable()
export class TourService {

  private displayMarkerSubject = new BehaviorSubject<boolean>( false );
  public displayMarker = this.displayMarkerSubject.asObservable().distinctUntilChanged();

  private activeMarkerSubject = new BehaviorSubject<Position>( <Position> { latitude: 50.8645, longitude: 9.6917 } );
  public activeMarker = this.activeMarkerSubject.asObservable().distinctUntilChanged();

  private displayRouteSubject = new BehaviorSubject<boolean>( false );
  public displayRoute = this.displayRouteSubject.asObservable().distinctUntilChanged();

  private activeRouteSubject = new BehaviorSubject<Position[]>( <Position[]> [] );
  public activeRoute = this.activeRouteSubject.asObservable().distinctUntilChanged();

  private locationUrl = `${environment.apiUrl}/internal/v2/location/recent`;
  private routeUrl = `${environment.apiUrl}/internal/v2/location`;

  constructor( private http: Http, private msgService: MsgService ) {
  }

  private getLocation( userId: number ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const queryParameters = new URLSearchParams();
    queryParameters.set( 'user-id', String(userId) );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.locationUrl, options );
  }

  private getRoute( userId: number ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const queryParameters = new URLSearchParams();
    queryParameters.set( 'user-id', String(userId) );
    queryParameters.set( 'from', '05/31/2017' );  // hardcoded for developement

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.routeUrl, options );
  }

  changeActiveMarker( selectedDriver: Driver ) {
    this.getLocation( selectedDriver.id )
      .subscribe( ( response: Response ) => {
          const driverLocations = response.json();
          if (driverLocations && driverLocations.length > 0) {
            const positions = <Position[]> driverLocations[ 0 ][ 'gpsDataPoints' ];
            if (positions && positions.length > 0) {
              this.displayRouteSubject.next( false );
              this.displayMarkerSubject.next( true );
              this.activeMarkerSubject.next( positions[ 0 ] );
              this.msgService.clear();
            } else {
              this.locationError();
            }
          } else {
            this.locationError();
          }
        },
        ( error: Response ) => this.msgService.handleResponse( error ) );
  }

  changeActiveRoute( selectedDriver ) {
    this.getRoute( selectedDriver.id )
      .subscribe( ( response: Response ) => {
          const driverLocations = response.json();
          if (driverLocations && driverLocations.length > 0) {
            const positions = <Position[]> driverLocations[ 0 ][ 'gpsDataPoints' ];
            if (positions && positions.length > 0) {
              this.displayMarkerSubject.next( false );
              this.displayRouteSubject.next( true );
              this.activeRouteSubject.next( positions );
              this.msgService.clear();
            } else {
              this.routeError();
            }
          } else {
            this.routeError();
          }
        },
        ( error: Response ) => this.msgService.handleResponse( error ) );
  }

  locationError(): void {
    this.displayRouteSubject.next( false );
    // hide actual marker
    this.displayMarkerSubject.next( false );
    // display error msg: could not get geolocation points
    this.msgService.error( 'could not get geolocation points' );
  }

  routeError(): void {
    this.displayMarkerSubject.next( false );
    // hide actual route
    this.displayRouteSubject.next( false );
    // display error msg: could not get route
    this.msgService.error( 'could not get route' );
  }
}
