import { Injectable } from '@angular/core';

import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { InetConnectionService } from '../../core/inet-connection.service';
import { WorkingdateService } from '../../core/workingdate.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Tour } from '../../core/models/tour.model';
import { MsgService } from '../../shared/msg/msg.service';
import { roundDecimals } from '../../core/math/roundDecimals';

@Injectable()
export class TouroptimizingService {

  protected allDeliverylistsUrl = `${environment.apiUrl}/internal/v1/deliverylist/info`; // ?=2018-01-12
  protected allToursUrl = `${environment.apiUrl}/internal/v1/tour`; // ?debitor-id=2052&station-no=100
  protected generateToursUrl = `${environment.apiUrl}/internal/v1/tour/deliverylist`; // POST body: [...deliverylistIds]
  protected deleteToursUrl = `${environment.apiUrl}/internal/v1/tour`; // DELETE ?id=1&id=2
  protected optimizeToursUrl = `${environment.apiUrl}/internal/v1/tour/optimize`; // PATCH ?id=1&id=2

  private toursSubject = new BehaviorSubject<Tour[]>( [] );
  public tours$ = this.toursSubject.asObservable().distinctUntilChanged();

  private latestDeliverylistModificationSubject = new BehaviorSubject<number>( 0 );
  public latestDeliverylistModification$ = this.latestDeliverylistModificationSubject.asObservable().distinctUntilChanged();

  private latestDeliverylists: Deliverylist[];

  // private stationNo: string;
  // private allDeliverylistsUrl: string;

  constructor( protected http: HttpClient,
               protected msgService: MsgService,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService ) {
    this.latestDeliverylists = [];
  }

  getTours(): void {
    const activeStation = JSON.parse( localStorage.getItem( 'activeStation' ) );
    this.toursSubject.next( [] );
    /**
     * ALEX: vorerst nur station-no übergeben, bis Service angepasst ist
     */
    this.http.get<Tour[]>( this.allToursUrl, {
      params: new HttpParams()
        .set( 'station-no', activeStation.stationNo.toString() )
    } )
      .subscribe( ( tours ) => {
          if (tours.length === 0) {
            // scheinbar keine Touren vorhanden => aus Deliverylisten Touren generieren
            this.getDeliverylists( [ this.generateTours, this.latestModDate ] );
          } else {
            this.getDeliverylists( [ this.latestModDate, (_) => this.toursSubject.next( this.processTourData( tours ) ) ] );
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

  deleteAndReinitTours( tourIds: number[] ) {
    let httpParams = new HttpParams();
    tourIds.forEach( id => {
      httpParams = httpParams.append( 'id', id.toString() );
    } );

    this.http.delete( this.deleteToursUrl, {
      params: httpParams
    } )
      .subscribe( _ => this.getTours(),
        error => console.log( error ) );

  }

  optimizeAndReinitTours( tourIds: number[] ) {
    let httpParams = new HttpParams();
    tourIds.forEach( id => {
      httpParams = httpParams.append( 'id', id.toString() );
    } );
    httpParams = httpParams.append( 'wait-for-completion', 'true' );

    const defaultBody = {
      'start': {},
      'appointments': {
        'omit': false
      },
      'vehicles': [ {} ]
    };

    this.http.patch( this.optimizeToursUrl, defaultBody, {
      params: httpParams
    } )
      .subscribe( _ => this.deleteAndReinitTours( tourIds ),
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
    this.http.get<Deliverylist[]>( this.allDeliverylistsUrl, {
      params: new HttpParams()
        .set( 'delivery-date', this.wds.deliveryDateForWS() )
    } ).subscribe( ( deliverylists ) => {
        this.latestDeliverylists = deliverylists;
        // result => Touren generieren => this.toursSubject.next( result );
        successCallbacks.forEach( successCallback => successCallback( deliverylists ) );
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
