import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import { distinctUntilChanged, takeUntil } from 'rxjs/operators';

import * as moment from 'moment';

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
import { TranslateService } from '../../core/translate/translate.service';
import { SseTouroptimizationMsgModel } from '../../core/models/sse-touroptimization-msg.model';
import { SseTourchangesMsgModel } from '../../core/models/sse-tourchanges-msg.model';


@Injectable()
export class TouroptimizingService {

  protected allToursUrl = `${environment.apiUrl}/internal/v1/tour`; // ?debitor-id=2052&station-no=100
  protected deleteToursUrl = `${environment.apiUrl}/internal/v1/tour`; // DELETE ?id=1&id=2
  protected optimizeToursUrl = `${environment.apiUrl}/internal/v1/tour/optimize`; // PATCH ?id=1&id=2
  protected optimizeToursSSEUrl = `${environment.apiUrl}/internal/v1/tour/optimize/status/sse`; // EventSource ?station-no=100
  protected sseTourchangesUrl = `${environment.apiUrl}/internal/v1/tour/subscribe/sse`; // EventSource ?station-no=100

  private toursLoadingSubject = new BehaviorSubject<boolean>( true );
  public toursLoading$ = this.toursLoadingSubject.asObservable().pipe( distinctUntilChanged() );

  private toursSubject = new BehaviorSubject<Tour[]>( [] );
  public tours$ = this.toursSubject.asObservable().pipe( distinctUntilChanged() );

  private latestDeliverylists: Deliverylist[];

  private optimizationInProgress: number[] = [];

  private optimizationFailed = false;
  private todayMinus4Days: string;

  constructor( protected http: HttpClient,
               protected translate: TranslateService,
               protected msgService: MsgService,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService,
               private sse: SseService ) {
    this.latestDeliverylists = [];
    this.todayMinus4Days = moment()
      .subtract(4, 'days')
      .format( 'YYYY-MM-DD' );
  }

  initSSEtouroptimization( ngUnsubscribe: Subject<void> ) {
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const sseUrl = `${this.optimizeToursSSEUrl}?station-no=${activeStation.stationNo.toString()}`;
    this.sse.observeMessages<SseTouroptimizationMsgModel>( sseUrl )
      .pipe(
        takeUntil( ngUnsubscribe )
      )
      .subscribe( ( data ) => {
        console.log( data );
        const id = data.id;
        this.optimizing( id, data.inProgress );
        if (!data.inProgress) {
          if (data.success) {
            console.log('tour.optimizationFailed', this.optimizationFailed);
            this.msgService.clear();
            this.getTours();
          } else {
            // mark tour with id as 'optimization failed'
            const tmpTours = this.toursSubject.getValue()
              .map( tour => {
                if (tour.id === id) {
                  tour.optimizationFailed = true;
                  this.optimizationFailed = true;
                }
                return tour;
              } );
            this.toursSubject.next( tmpTours );
            const text = `${this.translate.instant( 'Tour' )}: ${id} ${this.translate.instant( 'could not be optimized' )}`;
            this.msgService.error( text, true );
          }
        }
      } );
  }

  private optimizing( id: any, inProgress: boolean ) {
    if (inProgress) {
      this.optimizationInProgress.push( id );
    } else {
      this.optimizationInProgress = this.optimizationInProgress.filter( el => el !== id );
    }
    const tmpTours = this.toursSubject
      .getValue()
      .map( tour => {
        if (tour.id === id) {
          tour.isOptimizing = inProgress;
        }
        return tour;
      } );
    this.toursSubject.next( tmpTours );
  }

  initSSEtourChanges( ngUnsubscribe: Subject<void> ) {
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const sseUrl = `${this.sseTourchangesUrl}?station-no=${activeStation.stationNo.toString()}`;
    this.sse.observeMessages<SseTourchangesMsgModel>( sseUrl )
      .pipe(
        takeUntil( ngUnsubscribe )
      )
      .subscribe( ( data ) => {
        console.log( data );
        if (data && data.deleted) {
          this.msgService.clear();
          this.getTours();
        }
      } );
  }

  getTours(): void {
    const activeStation = JSON.parse( localStorage.getItem( 'activeStation' ) );
    this.http.get<Tour[]>( this.allToursUrl, {
      params: new HttpParams()
        .set( 'station-no', activeStation.stationNo.toString() )
        .set( 'from', this.todayMinus4Days )
    } )
      .subscribe( ( tours ) => {
          this.toursSubject.next( this.processTourData( tours ) );
          this.toursLoadingSubject.next( false );
        },
        ( error: HttpErrorResponse ) => {
          if (error.status === 404) {
            this.toursSubject.next( [] );
            this.toursLoadingSubject.next( false );
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

    const tmpTours = this.toursSubject.getValue()
      .filter( tour => tourIds.indexOf( tour.id ) === -1 );
    this.toursSubject.next( tmpTours );

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
    httpParams = httpParams.append( 'wait-for-completion', 'false' );
    const defaultBody = {
      'appointments': {
        'omit': false
      },
      'traffic': optimizeTraffic
    };

    if (!dontShiftOneDayFromNow) {
      defaultBody.appointments[ 'shiftDaysFromNow' ] = 1;
    }
    if (!optimizeExistingtours && vehicles.length > 0) {
      defaultBody[ 'vehicles' ] = vehicles;
    }
    this.http.patch( this.optimizeToursUrl, defaultBody, {
      params: httpParams
    } )
      .subscribe( _ => {
          this.msgService.info( 'optimization_progress', true, true );
        },
        error => {
          this.msgService.error( error.error.detail, true );
        } );
  }

  switchSelectionAllTours( allSelected: boolean ) {
    const tmpArr = [ ...this.toursSubject.getValue() ];
    tmpArr.forEach( ( tour: Tour ) => tour.selected = allSelected );
    this.toursSubject.next( tmpArr );
  }

  private processTourData( tours: Tour[] ): Tour[] {
    return tours.map( tour => {
      tour.totalShipments = tour.orders ? tour.orders.length : 0;

      tour.totalPackages = tour.orders
        ? tour.orders
          .map( o => o.parcels.length )
          .reduce( ( a, b ) => a + b )
        : 0;
      const parcels = tour.orders ? tour.orders.map( o => o.parcels ) : [];
      const mappedParcels = [].concat( ...parcels )
        .map( p => p.dimension.weight );
      tour.totalWeight = 0;
      if (mappedParcels.length > 0) {
        tour.totalWeight = roundDecimals( mappedParcels
          .reduce( ( a, b ) => a + b ), 100 );
      }

      tour.distance = (tour.route && tour.route.distance > 0)
        ? tour.distance = tour.route.distance
        : 0;

      tour.drivingTime = (tour.route && tour.route.drivingTime > 0)
        ? tour.drivingTime = tour.route.drivingTime
        : 0;

      tour.isOptimizing = this.optimizationInProgress.indexOf( tour.id ) >= 0;
      tour.optimizationFailed = false; // if REST delivers this status use deliverd values
      tour.selected = false;
      const dl = this.latestDeliverylists.filter( deliverylist => deliverylist.id === tour.customId );
      tour.state = dl.length > 0 && dl[ 0 ].modified > tour.created ? 'changed' : 'new';
      return tour;
    } );
  }

  public showSpinner() {
    this.toursLoadingSubject.next( true );
  }
}
