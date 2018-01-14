import { Injectable } from '@angular/core';

import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { Observable } from 'rxjs/Observable';
import { InetConnectionService } from '../../core/inet-connection.service';
import { WorkingdateService } from '../../core/workingdate.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class TouroptimizingService {

  protected allDeliverylistsUrl = `${environment.apiUrl}/internal/v1/deliverylist/info`; // ?=2018-01-12

  private deliverylistsSubject = new BehaviorSubject<Deliverylist[]>( [] );
  public deliverylists$ = this.deliverylistsSubject.asObservable().distinctUntilChanged();

  constructor( protected http: HttpClient,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService ) {
  }

  getDeliverylists(): void {
    this.deliverylistsSubject.next( [] );
    this.http.get<Deliverylist[]>( this.allDeliverylistsUrl, {
      params: new HttpParams()
        .set( 'delivery-date', this.wds.deliveryDateForWS() )
    } )
      .subscribe( ( deliverylists ) => {
          deliverylists.forEach( ( deliverylist: Deliverylist ) => {
            this.getDeliverylistById( deliverylist.id )
              .subscribe( ( deliverylistById ) => {
                  deliverylistById.totalShipments = deliverylistById.orders.length;
                  deliverylistById.totalPackages = deliverylistById.orders
                    .map( o => o.parcels.length )
                    .reduce( ( a, b ) => a + b );
                  const parcels = deliverylistById.orders.map( o => o.parcels );
                  deliverylistById.totalWeight = [].concat(...parcels)
                    .map( p => p.dimension.weight)
                    .reduce( (a, b) => a + b);
                  const tmpArr = [ ...this.deliverylistsSubject.getValue(), deliverylistById ];
                  this.deliverylistsSubject.next( tmpArr );
                },
                ( _ ) => {
                  this.ics.isOffline();
                  this.deliverylistsSubject.next( [] );
                } );
          } );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.deliverylistsSubject.next( [] );
        } );
  }

  getDeliverylistById( id: number ): Observable<Deliverylist> {
    const deliverylistByIdUrl = `${environment.apiUrl}/internal/v1/deliverylist/${id}`;
    return this.http.get( deliverylistByIdUrl );
  }
}
