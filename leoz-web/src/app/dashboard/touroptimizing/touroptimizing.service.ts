import { Injectable } from '@angular/core';

import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { InetConnectionService } from '../../core/inet-connection.service';
import { WorkingdateService } from '../../core/workingdate.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Tour } from '../../core/models/tour.model';

@Injectable()
export class TouroptimizingService {

  protected allDeliverylistsUrl = `${environment.apiUrl}/internal/v1/deliverylist/info`; // ?=2018-01-12
  protected allToursUrl = `${environment.apiUrl}/internal/v1/tour`; // ?debitor-id=2052&station-no=100
  protected generateToursUrl = `${environment.apiUrl}/internal/v1/tour/deliverylist`; // POST body: [...deliverylistIds]

  private toursSubject = new BehaviorSubject<Tour[]>( [] );
  public tours$ = this.toursSubject.asObservable().distinctUntilChanged();

  // private stationNo: string;
  // private allDeliverylistsUrl: string;

  constructor( protected http: HttpClient,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService ) {
  }

  getTours( debitorId: number, stationNo: number ): void {
    this.toursSubject.next( [] );
    // this.allDeliverylistsUrl = `${environment.apiUrl}/internal/v1/deliverylist/station/${this.stationNo}`; // ?=100
    /**
     * URL defekt liefert nie ein Ergebnis: internal/v1/tour; // ?debitor-id=2052&station-no=100
     * => kann dann auch weggeworfen werden :)
     */
    this.http.get<Tour[]>( this.allToursUrl, {
      params: new HttpParams()
        .set( 'debitor-id', debitorId.toString() )
        .set( 'station-no', stationNo.toString() )
    } )
      .subscribe( ( tours ) => {
          console.table( tours );
          if (tours.length === 0) {
            // scheinbar keine Touren vorhanden => aus Deliverylisten Touren generieren
            this.getDeliverylists( this.generateTours );
          }
          // tours.forEach( ( tour: TourListItem ) => {
          //   this.getDeliverylistById( tour.id )
          //     .subscribe( ( deliverylistById ) => {
          //         deliverylistById.totalShipments = deliverylistById.orders.length;
          //         deliverylistById.totalPackages = deliverylistById.orders
          //           .map( o => o.parcels.length )
          //           .reduce( ( a, b ) => a + b );
          //         const parcels = deliverylistById.orders.map( o => o.parcels );
          //         deliverylistById.totalWeight = [].concat(...parcels)
          //           .map( p => p.dimension.weight)
          //           .reduce( (a, b) => a + b);
          //         deliverylistById.selected = false;
          //         const tmpArr = [ ...this.toursSubject.getValue(), deliverylistById ];
          //         this.toursSubject.next( tmpArr );
          //       },
          //       ( _ ) => {
          //         this.ics.isOffline();
          //         this.toursSubject.next( [] );
          //       } );
          // } );
        },
        ( error: HttpErrorResponse ) => {
          if (error.status === 404) {
            // scheinbar keine Touren vorhanden => aus Deliverylisten Touren generieren
            this.getDeliverylists( this.generateTours );
          } else {
            this.ics.isOffline();
            this.toursSubject.next( [] );
          }
        } );
  }

  switchSelectionAllTours( allSelected: boolean ) {
    const tmpArr = [ ...this.toursSubject.getValue() ];
    tmpArr.forEach( ( tour: Tour ) => tour.selected = allSelected );
    this.toursSubject.next( tmpArr );
  }

  private getDeliverylists( successCallback: Function ) {
    /**
     * alle aktuellen delivarylists holen
     * URL internal/v1/deliverylist/info`; // ?=2018-01-12
     * liefert auch leere Rollkarten d.h. Deliverylist.orders.stops.tasks.removed = true
     * und kann nicht auf Stationsebene gefiltert werden
     */
    this.http.get<Deliverylist[]>( this.allDeliverylistsUrl, {
      params: new HttpParams()
        .set( 'delivery-date', this.wds.deliveryDateForWS() )
    } ).subscribe( ( deliverylists ) => {
        // result => Touren generieren => this.toursSubject.next( result );
        successCallback( this, deliverylists.map( dl => dl.id ) );
      },
      ( _ ) => {
        this.ics.isOffline();
        this.toursSubject.next( [] );
      } );
  }

  private generateTours( $this: TouroptimizingService, deliverylistIds: number[] ) {
    $this.http.post<Tour[]>( $this.generateToursUrl, deliverylistIds ).subscribe( ( tours ) => {
        $this.toursSubject.next(
          tours.map( tour => {
            tour.totalShipments = tour.orders.length;
            tour.totalPackages = tour.orders
              .map( o => o.parcels.length )
              .reduce( ( a, b ) => a + b );
            const parcels = tour.orders.map( o => o.parcels );
            tour.totalWeight = [].concat( ...parcels )
              .map( p => p.dimension.weight )
              .reduce( ( a, b ) => a + b );
            tour.selected = false;
            return tour;
          } ) );
      },
      ( _ ) => {
        $this.ics.isOffline();
        $this.toursSubject.next( [] );
      } );
  }
}
