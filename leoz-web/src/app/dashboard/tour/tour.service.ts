import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Position } from './position.model';
import { MsgService } from '../../shared/msg/msg.service';
import { Driver } from './driver.model';
import { MarkerModel } from './tour-map/marker.model';
import { TranslateService } from '../../core/translate/translate.service';
import { DriverService } from './driver.service';
import * as moment from 'moment';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import RoleEnum = Driver.RoleEnum;

@Injectable()
export class TourService {

  public homebase = <MarkerModel> {
    position: {
      latitude: 50.8645,
      longitude: 9.6917
    }, driver: {}
  };

  private displayMarkerSubject = new BehaviorSubject<boolean>( false );
  public displayMarker$ = this.displayMarkerSubject.asObservable().distinctUntilChanged();

  private activeMarkerSubject = new BehaviorSubject<MarkerModel>( this.homebase );
  public activeMarker$ = this.activeMarkerSubject.asObservable().distinctUntilChanged();

  private allMarkersSubject = new BehaviorSubject<MarkerModel[]>( [] );
  public allMarkers$ = this.allMarkersSubject.asObservable().distinctUntilChanged();

  private displayRouteSubject = new BehaviorSubject<boolean>( false );
  public displayRoute$ = this.displayRouteSubject.asObservable().distinctUntilChanged();

  private activeRouteSubject = new BehaviorSubject<Position[]>( <Position[]> [] );
  public activeRoute$ = this.activeRouteSubject.asObservable().distinctUntilChanged();

  private locationUrl = `${environment.apiUrl}/internal/v2/location/recent`;
  private locationFromToUrl = `${environment.apiUrl}/internal/v2/location`;

  private drivers: Driver[];
  private duration: number;
  private selectedDate: Date;

  constructor( private http: HttpClient,
               private msgService: MsgService,
               private translate: TranslateService,
               private driverService: DriverService ) {
    driverService.drivers$.subscribe( ( drivers: Driver[] ) => this.drivers = drivers );
    this.duration = 0;
    this.selectedDate = null;
  }

  private getLocation( userId: number ): Observable<Position[][]> {
    let usedUrl = this.locationUrl;

    let queryParameters = new HttpParams().set( 'user-id', String( userId ) );
    if (this.duration > 0) {
      queryParameters = queryParameters.append( 'duration', String( this.duration ) );
    } else {
      if (this.selectedDate) {
        // Eingrezung wenn datumsauswahl im Kalender
        const from = moment( this.selectedDate ).hours( 0 ).minutes( 0 ).seconds( 0 );
        const to = moment( this.selectedDate ).hours( 23 ).minutes( 59 ).seconds( 59 );
        queryParameters = queryParameters.append( 'from', from.format( 'MM/DD/YYYY HH:mm:ss' ) );
        queryParameters = queryParameters.append( 'to', to.format( 'MM/DD/YYYY HH:mm:ss' ) );
        usedUrl = this.locationFromToUrl;
      }
      // soll nix passieren
      // oder bei keiner Datumsauswahl => bereits vorher abprüfen
    }

    return this.http.get<any[]>( usedUrl, {
      params: queryParameters
    } );
  }

  private getAllLocations(): Observable<any[]> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    let usedUrl = this.locationUrl;
    let queryParameters = new HttpParams().set( 'debitor-id', String( currUser.user.debitorId ) );
    if (this.duration > 0) {
      queryParameters = queryParameters.append( 'duration', String( this.duration ) );
    } else {
      // Eingrezung wenn datumsauswahl im Kalender
      const from = moment( this.selectedDate ).hours( 0 ).minutes( 0 ).seconds( 0 );
      const to = moment( this.selectedDate ).hours( 23 ).minutes( 59 ).seconds( 59 );
      queryParameters = queryParameters.append( 'from', from.format( 'MM/DD/YYYY HH:mm:ss' ) );
      queryParameters = queryParameters.append( 'to', to.format( 'MM/DD/YYYY HH:mm:ss' ) );
      usedUrl = this.locationFromToUrl;

      // oder bei keiner Datumsauswahl => bereits vorher abprüfen
      // soll nix passieren
    }

    return this.http.get( usedUrl, {
      params: queryParameters
    } );
  }

  private getRoute( userId: number ): Observable<Position[][]> {
    let usedUrl = this.locationUrl;

    let queryParameters = new HttpParams().set( 'user-id', String( userId ) );
    if (this.duration > 0) {
      queryParameters = queryParameters.append( 'duration', String( this.duration ) );
    } else {
      // Eingrezung wenn datumsauswahl im Kalender
      const from = moment( this.selectedDate ).hours( 0 ).minutes( 0 ).seconds( 0 );
      const to = moment( this.selectedDate ).hours( 23 ).minutes( 59 ).seconds( 59 );
      queryParameters = queryParameters.append( 'from', from.format( 'MM/DD/YYYY HH:mm:ss' ) );
      queryParameters = queryParameters.append( 'to', to.format( 'MM/DD/YYYY HH:mm:ss' ) );
      usedUrl = this.locationFromToUrl;

      // oder bei keiner Datumsauswahl => bereits vorher abprüfen
      // soll nix passieren
    }

    return this.http.get( usedUrl, {
      params: queryParameters
    } );
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

  changeActiveMarker( selectedDriver: Driver, duration: number, selectedDate: Date ) {
    this.duration = duration;
    this.selectedDate = selectedDate;
    this.resetDisplay();
    this.getLocation( selectedDriver.id )
      .subscribe( ( driverLocations ) => {
          if (driverLocations && driverLocations.length > 0) {
            const positions = <Position[]> driverLocations[ 0 ][ 'gpsDataPoints' ];
            if (positions && positions.length > 0) {
              this.displayMarkerSubject.next( true );
              this.activeMarkerSubject.next( <MarkerModel> {
                position: positions[ positions.length - 1 ],
                driver: selectedDriver
              } );
              this.msgService.clear();
            } else {
              this.locationError();
            }
          } else {
            this.locationError();
          }
        },
        ( error: HttpErrorResponse ) => this.msgService.handleResponse( error ) );
  }

  changeActiveRoute( selectedDriver: Driver, duration: number, selectedDate: Date ) {
    this.duration = duration;
    this.selectedDate = selectedDate;
    this.resetDisplay();
    this.getRoute( selectedDriver.id )
      .subscribe( ( driverLocations ) => {
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
        ( error: HttpErrorResponse ) => this.msgService.handleResponse( error ) );
  }

  locationError(): void {
    this.resetDisplay();
    this.msgService.error( this.translate.instant( 'could not get geolocation points' ) );
  }

  routeError(): void {
    this.resetDisplay();
    this.msgService.error( this.translate.instant( 'could not get route' ) );
  }

  fetchAllPositions( filter: string, duration: number, selectedDate: Date ) {
    this.duration = duration;
    this.selectedDate = selectedDate;
    this.resetDisplay();
    this.driverService.getDrivers();
    this.getAllLocations()
      .subscribe( ( allLocations ) => {
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
        ( error: HttpErrorResponse ) => this.msgService.handleResponse( error ) );
  }
}
