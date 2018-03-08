import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import { distinctUntilChanged, takeUntil } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { InetConnectionService } from '../../core/inet-connection.service';
import { WorkingdateService } from '../../core/workingdate.service';
import { Tour } from '../../core/models/tour.model';
import { MsgService } from '../../shared/msg/msg.service';
import { roundDecimals } from '../../core/math/roundDecimals';
import { Station } from '../../core/auth/station.model';
import { SseService } from '../../core/sse.service';
import { Vehicle } from '../../core/models/vehicle.model';


@Injectable()
export class TouroptimizingService {

  protected allDeliverylistsUrl = `${environment.apiUrl}/internal/v1/deliverylist/info`; // ?=2018-01-12
  protected allToursUrl = `${environment.apiUrl}/internal/v1/tour`; // ?debitor-id=2052&station-no=100
  protected generateToursUrl = `${environment.apiUrl}/internal/v1/tour/deliverylist`; // POST body: [...deliverylistIds]
  protected deleteToursUrl = `${environment.apiUrl}/internal/v1/tour`; // DELETE ?id=1&id=2
  protected optimizeToursUrl = `${environment.apiUrl}/internal/v1/tour/optimize`; // PATCH ?id=1&id=2
  protected optimizeToursSSEUrl = `${environment.apiUrl}/internal/v1/tour/optimize/status/sse`; // EventSource ?station-no=100
  protected sseWEUrl = `${environment.apiUrl}/internal/v1/tour/subscribe/sse`; // EventSource ?station-no=100

  private toursLoadingSubject = new BehaviorSubject<boolean>( true );
  public toursLoading$ = this.toursLoadingSubject.asObservable().pipe(distinctUntilChanged());

  private toursSubject = new BehaviorSubject<Tour[]>( [] );
  public tours$ = this.toursSubject.asObservable().pipe(distinctUntilChanged());

  private latestDeliverylistModificationSubject = new BehaviorSubject<number>( 0 );
  public latestDeliverylistModification$ = this.latestDeliverylistModificationSubject.asObservable().pipe(distinctUntilChanged());

  private latestDeliverylists: Deliverylist[];

  constructor( protected http: HttpClient,
               protected msgService: MsgService,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService,
               private sse: SseService ) {
    this.latestDeliverylists = [];
  }

  initSSEtouroptimization( ngUnsubscribe: Subject<void>, withInitialGeneration: boolean ) {
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const sseUrl = `${this.optimizeToursSSEUrl}?station-no=${activeStation.stationNo.toString()}`;
    this.sse.observeMessages<{ id?: number, inProgress?: boolean }>( sseUrl )
      .pipe(
        takeUntil( ngUnsubscribe )
      )
      .subscribe( ( data ) => {
        console.log( data );
        if (data && !data.inProgress) {
          this.msgService.clear();
          this.getTours( withInitialGeneration );
        }
      } );
  }

  initSSEtourWhatever( ngUnsubscribe: Subject<void>, withInitialGeneration: boolean ) {
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const sseUrl = `${this.sseWEUrl}?station-no=${activeStation.stationNo.toString()}`;
    this.sse.observeMessages<{ stationNo?: number, items?: Tour[], deleted?: number[] }>( sseUrl )
      .pipe(
        takeUntil( ngUnsubscribe )
      )
      .subscribe( ( data ) => {
        console.log( data );
        if (data && data.deleted) {
          this.msgService.clear();
          this.getTours( withInitialGeneration );
        }
      } );
  }

  getTours( withInitialGeneration: boolean = true ): void {
    const activeStation = JSON.parse( localStorage.getItem( 'activeStation' ) );
    /**
     * ALEX: vorerst nur station-no übergeben, bis Service angepasst ist
     */
    this.http.get<Tour[]>( this.allToursUrl, {
      params: new HttpParams()
        .set( 'station-no', activeStation.stationNo.toString() )
    } )
      .subscribe( ( tours ) => {
          if (tours.length === 0 && withInitialGeneration) {
            // scheinbar keine Touren vorhanden => aus Deliverylisten Touren generieren
            this.getDeliverylists( [ this.generateTours, this.latestModDate ] );
          } else {
            this.getDeliverylists( [ this.latestModDate, ( _ ) => this.toursSubject.next( this.processTourData( tours ) ) ] );
          }
        },
        ( error: HttpErrorResponse ) => {
          if (error.status === 404) {
            // scheinbar keine Touren vorhanden => aus Deliverylisten Touren generieren
            this.getDeliverylists( [ this.generateTours, this.latestModDate ] );
          } else {
            this.ics.isOffline();
            this.toursSubject.next( [] );
          }
        } );
  }

  deleteTours( tourIds: number[] ) {
    let httpParams = new HttpParams();
    tourIds.forEach( id => {
      httpParams = httpParams.append( 'id', id.toString() );
    } );

    this.http.delete( this.deleteToursUrl, {
      params: httpParams
    } )
      .subscribe( _ => {
        },
        error => {
          console.log( error );
        } );

  }

  optimizeAndReinitTours( tourIds: number[], vehicles: Vehicle[] = [], optimizeTraffic = true,
                          optimizeExistingtours = true, dontShiftOneDayFromNow = true ) {
    let httpParams = new HttpParams();
    tourIds.forEach( id => {
      httpParams = httpParams.append( 'id', id.toString() );
    } );
    // httpParams = httpParams.append( 'wait-for-completion', 'true' );
    httpParams = httpParams.append( 'wait-for-completion', 'false' );
    /**
     * ALEX: mal 'omit': true und mal 'omit': false / produktiv auf false stellen
     */
    const defaultBody = {
      'appointments': {
        'omit': false
      },
      'traffic': optimizeTraffic
    };

    if(!dontShiftOneDayFromNow) {
      defaultBody.appointments['shiftDaysFromNow'] = 1;
    }
    if (!optimizeExistingtours && vehicles.length > 0) {
      defaultBody[ 'vehicles' ] = vehicles;
    }
    this.http.patch( this.optimizeToursUrl, defaultBody, {
      params: httpParams
    } )
      .subscribe( _ => {
          // this.getTours(); // this.deleteAndReinitTours( tourIds )
          // this.msgService.clear();
          this.msgService.info( 'optimization_progress', true );
        },
        error => {
          this.msgService.error( error.error.detail );
        } );
  }

  switchSelectionAllTours( allSelected: boolean ) {
    const tmpArr = [ ...this.toursSubject.getValue() ];
    tmpArr.forEach( ( tour: Tour ) => tour.selected = allSelected );
    this.toursSubject.next( tmpArr );
  }

  public getDeliverylists( successCallbacks: Function[] ) {
    /**
     * ALEX: alle aktuellen delivarylists holen
     * URL internal/v1/deliverylist/info`; // ?=2018-01-12
     * liefert auch leere Rollkarten d.h. Deliverylist.orders.stops.tasks.removed = true
     * und kann nicht auf Stationsebene gefiltert werden
     */
    if (this.toursSubject.getValue().length === 0) {
      this.toursLoadingSubject.next( true );
    }
    this.http.get<Deliverylist[]>( this.allDeliverylistsUrl, {
      params: new HttpParams()
        .set( 'delivery-date', this.wds.deliveryDateForWS() )
    } ).subscribe( ( deliverylists ) => {
        this.latestDeliverylists = deliverylists;
        // result => Touren generieren => this.toursSubject.next( result );
        successCallbacks.forEach( successCallback => successCallback( deliverylists ) );
        this.toursLoadingSubject.next( false );
      },
      ( _ ) => {
        this.ics.isOffline();
        this.toursSubject.next( [] );
      } );
  }

  public latestModDate = ( deliverylists: Deliverylist[] ) => {
    let latestModTimestamp = 0;
    if (deliverylists.length > 0) {
      latestModTimestamp = Math.max( ...deliverylists.map( dl => new Date( dl.modified ).getTime() ) );
    }
    this.latestDeliverylistModificationSubject.next( latestModTimestamp );
  };

  private generateTours = ( deliverylists: Deliverylist[] ) => {
    // ALEX nur ausführen wenn deliverylists.length > 0
    this.http.post<Tour[]>( this.generateToursUrl, deliverylists.map( dl => dl.id ) )
      .subscribe( ( tours ) => {
          this.toursSubject.next( this.processTourData( tours ) );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.toursSubject.next( [] );
        } );
  };

  private processTourData( tours: Tour[] ): Tour[] {
    return tours.map( tour => {
      tour.totalShipments = tour.orders.length;

      tour.totalPackages = tour.orders
        .map( o => o.parcels.length )
        .reduce( ( a, b ) => a + b );
      const parcels = tour.orders.map( o => o.parcels );
      const mappedParcels = [].concat( ...parcels )
        .map( p => p.dimension.weight );
      tour.totalWeight = 0;
      if (mappedParcels.length > 0) {
        tour.totalWeight = roundDecimals( mappedParcels
          .reduce( ( a, b ) => a + b ), 100 );
      }
      tour.selected = false;
      const dl = this.latestDeliverylists.filter( deliverylist => deliverylist.id === tour.deliverylistId );
      tour.state = dl.length > 0 && dl[ 0 ].modified > tour.created ? 'changed' : 'new';
      return tour;
    } );
  }

}
