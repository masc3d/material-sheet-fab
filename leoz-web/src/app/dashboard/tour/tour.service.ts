import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Http, RequestOptions, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Position } from './position.model';
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';
import { MsgService } from '../../shared/msg/msg.service';
import { Driver } from './driver.model';
import { MarkerModel } from './tour-map/marker.model';
import { TranslateService } from '../../core/translate/translate.service';
import { DriverService } from './driver.service';
import RoleEnum = Driver.RoleEnum;
import * as moment from 'moment';

@Injectable()
export class TourService {

  public homebase = <MarkerModel> {
    position: {
      latitude: 50.8645,
      longitude: 9.6917
    }, driver: {}
  };

  private displayMarkerSubject = new BehaviorSubject<boolean>( false );
  public displayMarker = this.displayMarkerSubject.asObservable().distinctUntilChanged();

  private activeMarkerSubject = new BehaviorSubject<MarkerModel>( this.homebase );
  public activeMarker = this.activeMarkerSubject.asObservable().distinctUntilChanged();

  private allMarkersSubject = new BehaviorSubject<MarkerModel[]>( [] );
  public allMarkers = this.allMarkersSubject.asObservable().distinctUntilChanged();

  private displayRouteSubject = new BehaviorSubject<boolean>( false );
  public displayRoute = this.displayRouteSubject.asObservable().distinctUntilChanged();

  private activeRouteSubject = new BehaviorSubject<Position[]>( <Position[]> [] );
  public activeRoute = this.activeRouteSubject.asObservable().distinctUntilChanged();

  private locationUrl = `${environment.apiUrl}/internal/v2/location/recent`;
  private locationFromToUrl = `${environment.apiUrl}/internal/v2/location`;

  private drivers: Driver[];
  private duration: number;

  constructor( private http: Http,
               private msgService: MsgService,
               private translate: TranslateService,
               private driverService: DriverService ) {
    driverService.drivers.subscribe( ( drivers: Driver[] ) => this.drivers = drivers );
    this.duration = 0;
  }

  private getLocation( userId: number ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const queryParameters = new URLSearchParams();
    queryParameters.set( 'user-id', String( userId ) );
    if (this.duration > 0) {
      // Eingrezung für datumsauswahl siehe Kalenderfeature
      // const from = moment().subtract(this.duration, 'minutes');
      // queryParameters.set( 'from', from.format('MM/DD/YYYY HH:mm:ss') );
      // queryParameters.set( 'to', from.format('MM/DD/YYYY HH:mm:ss') );
      // return this.http.get( this.locationFromToUrl, options );
      queryParameters.set( 'duration', String( this.duration ) );
    }

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.locationUrl, options );
  }

  private getAllLocations(): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const queryParameters = new URLSearchParams();
    queryParameters.set( 'debitor-id', String( currUser.user.debitorId ) );
    if (this.duration > 0) {
      // Eingrezung für datumsauswahl siehe Kalenderfeature
      // const from = moment().subtract(this.duration, 'minutes');
      // queryParameters.set( 'from', from.format('MM/DD/YYYY HH:mm:ss') );
      // queryParameters.set( 'to', from.format('MM/DD/YYYY HH:mm:ss') );
      // return this.http.get( this.locationFromToUrl, options );
      queryParameters.set( 'duration', String( this.duration ) );
    }

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.locationUrl, options );
  }

  private getRoute( userId: number ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const queryParameters = new URLSearchParams();
    queryParameters.set( 'user-id', String( userId ) );
    if (this.duration > 0) {
      // Eingrezung für datumsauswahl siehe Kalenderfeature
      // const from = moment().subtract(this.duration, 'minutes');
      // queryParameters.set( 'from', from.format('MM/DD/YYYY HH:mm:ss') );
      // queryParameters.set( 'to', from.format('MM/DD/YYYY HH:mm:ss') );
      // return this.http.get( this.locationFromToUrl, options );
      queryParameters.set( 'duration', String( this.duration ) );
    }

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );

    return this.http.get( this.locationUrl, options );
  }

  resetMsgs() {
    this.msgService.clear();
  }

  resetDisplay() {
    this.allMarkersSubject.next( [] );
    this.displayRouteSubject.next( false );
    this.displayMarkerSubject.next( false );
  }

  resetMarkerAndRoute() {
    this.resetDisplay();
    this.activeMarkerSubject.next( this.homebase );
    this.activeRouteSubject.next( <Position[]> [] );
  }

  changeActiveMarker( selectedDriver: Driver, duration: number ) {
    this.duration = duration;
    this.resetDisplay();
    this.getLocation( selectedDriver.id )
      .subscribe( ( response: Response ) => {
          const driverLocations = response.json();
          if (driverLocations && driverLocations.length > 0) {
            const positions = <Position[]> driverLocations[ 0 ][ 'gpsDataPoints' ];
            if (positions && positions.length > 0) {
              this.displayMarkerSubject.next( true );
              this.activeMarkerSubject.next( <MarkerModel> { position: positions[ positions.length - 1 ], driver: selectedDriver } );
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

  changeActiveRoute( selectedDriver: Driver, duration: number ) {
    this.duration = duration;
    this.resetDisplay();
    this.getRoute( selectedDriver.id )
      .subscribe( ( response: Response ) => {
          const driverLocations = response.json();
          if (driverLocations && driverLocations.length > 0) {
            const positions = <Position[]> driverLocations[ 0 ][ 'gpsDataPoints' ];
            if (positions && positions.length > 0) {
              this.displayMarkerSubject.next( true );
              this.activeMarkerSubject.next(
                <MarkerModel> { position: positions[ positions.length - 1 ], driver: selectedDriver }
              );
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
    this.resetDisplay();
    this.msgService.error( this.translate.instant( 'could not get geolocation points' ) );
  }

  routeError(): void {
    this.resetDisplay();
    this.msgService.error( this.translate.instant( 'could not get route' ) );
  }

  fetchAllPositions( filter: string, duration: number ) {
    this.duration = duration;
    this.resetDisplay();
    this.driverService.getDrivers();
    this.getAllLocations()
      .subscribe( ( response: Response ) => {
          const allLocations = response.json();
          const allMarkers = [];
          if (allLocations && allLocations.length > 0) {
            allLocations.forEach( ( userLocation: any ) => {
              if (userLocation.gpsDataPoints && userLocation.gpsDataPoints.length > 0) {
                const filtered = this.drivers.filter( ( driver: Driver ) => driver.id === userLocation.userId );
                if (filtered.length > 0) {
                  const driver = filtered[ 0 ];
                  if (driver.active) {
                    const position = <Position> userLocation.gpsDataPoints[ userLocation.gpsDataPoints.length - 1 ];
                    if (filter === 'allusers'
                      || (filter === 'alldrivers' && driver.role === RoleEnum.DRIVER)) {
                      allMarkers.push( <MarkerModel> { position: position, driver: driver } )
                    }
                  }
                }
              }
            } );
            this.msgService.clear();
          } else {
            this.locationError();
          }
          this.allMarkersSubject.next( allMarkers );
        },
        ( error: Response ) => this.msgService.handleResponse( error ) );
  }
}
