import { Injectable } from '@angular/core';

import { environment } from '../../../../environments/environment';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';
import { InetConnectionService } from '../../../core/inet-connection.service';
import { BehaviorSubject ,  Observable } from 'rxjs';
import { WorkingdateService } from '../../../core/workingdate.service';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { Exportparcel } from '../../../core/models/exportparcel.model';
import { Loadinglist } from '../../../core/models/loadinglist.model';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { LoadinglistReportHeader } from './loadinglist-report-header.model';
import { Exportorder } from '../../../core/models/exportorder.model';
import { HUB_ADDRESS } from '../../../core/constants';
import { sumAndRound } from '../../../core/math/sumAndRound';

@Injectable({
  providedIn: 'root',
})
export class LoadinglistscanService {

  protected allLoadinglistsUrl: string;
  protected openExportordersUrl: string;
  protected loadedParcelsUrl: string;
  protected scanUrl = `${environment.apiUrl}/internal/v1/export`;
  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/export/loadinglist`;

  private openParcelsSubject = new BehaviorSubject<Exportparcel[]>( [] );
  public openParcels$ = this.openParcelsSubject.asObservable();

  private loadedParcelsSubject = new BehaviorSubject<Exportparcel[]>( [] );
  public loadedParcels$ = this.loadedParcelsSubject.asObservable().pipe( distinctUntilChanged() );

  public activeLoadinglistSubject = new BehaviorSubject<Loadinglist>( <Loadinglist> {
    label: null,
    loadlistNo: null,
    orders: []
  } );
  public activeLoadinglist$ = this.activeLoadinglistSubject.asObservable().pipe( distinctUntilChanged() );

  public allLoadlistsSubject = new BehaviorSubject<Loadinglist[]>( [] );
  public allLoadlists$ = this.allLoadlistsSubject.asObservable().pipe( distinctUntilChanged() );

  protected activeStation: Station;

  protected loadinglistType = 'NORMAL';
  private unique: Function = ( arrArg ) => {
    return arrArg.filter( ( elem, pos, arr ) => {
      return arr.indexOf( elem ) === pos;
    } );
  };

  constructor( protected http: HttpClient,
               private auth: AuthenticationService,
               protected wds: WorkingdateService,
               protected ics: InetConnectionService ) {
    this.subscribeActiveStation( auth );
  }

  protected subscribeActiveStation( auth: AuthenticationService ) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => {
      this.activeStation = activeStation;
      const stationNo = this.activeStation.stationNo.toString();
      this.openExportordersUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/order?send-date=${this.wds.workingDate()
        .format( 'MM/DD/YYYY' )}`;
      this.loadedParcelsUrl = `${environment.apiUrl}/internal/v1/export/loadinglist/`;
      this.allLoadinglistsUrl = `${environment.apiUrl}/internal/v1/export/loadinglist?station-no=${stationNo}&send-date=${this.wds.workingDate()
        .format( 'MM/DD/YYYY' )}`;
    } );
  }

  getAllLoadinglistsWithParcels(): Observable<Loadinglist[]> {
    return this.http.get<Loadinglist[]>( this.allLoadinglistsUrl + '&includeParcels=true' );
  }

  getAllLoadinglists(): void {
    this.http.get<Loadinglist[]>( this.allLoadinglistsUrl + '&includeParcels=false' )
      .subscribe( ( loadinglists ) => {
          this.allLoadlistsSubject.next(
            loadinglists.filter( ( loadinglist: Loadinglist ) => loadinglist.loadinglistType === this.loadinglistType )
          );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.allLoadlistsSubject.next( [] );
        } );
  }

  getOpenParcels(): void {
    this.http.get<Exportorder[]>( this.openExportordersUrl )
      .subscribe( ( exportorders ) => {
          let parcels = [];
          exportorders.forEach( ( exportorder: Exportorder ) => {
            const address = exportorder.deliveryAddress;
            const parcelsWithAddress = exportorder.parcels.map( ( parcel: Exportparcel ) => <Exportparcel> {
              zip: address.zipCode,
              city: address.city,
              devliveryStation: exportorder.deliveryStation,
              ...parcel
            } );
            parcels = parcels.concat( parcelsWithAddress );
          } );
          this.openParcelsSubject.next( parcels
            .filter( ( parcel: Exportparcel ) => !parcel.loadinglistNo || parcel.loadinglistNo === null )
          );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.openParcelsSubject.next( [] );
        } );
  }

  getLoadinglist( loadinglistLabel: string ): Observable<Loadinglist> {
    return this.http.get<Loadinglist>( `${this.loadedParcelsUrl}${loadinglistLabel}` );
  }

  getLoadedParcels( loadinglistLabel: string ): void {
    this.http.get<Loadinglist>( `${this.loadedParcelsUrl}${loadinglistLabel}` )
      .subscribe( ( loadinglist ) => {
          const parcels = loadinglist.orders.map( ( exportorder: Exportorder ) => {
            return exportorder.parcels.map( ( parcel: Exportparcel ) => <Exportparcel> {
              zip: exportorder.deliveryAddress.zipCode,
              city: exportorder.deliveryAddress.city,
              devliveryStation: exportorder.deliveryStation,
              ...parcel
            } );
          } )
            .reduce( ( a: Exportparcel[], b: Exportparcel[] ) => a.concat( b ), [] );
          this.loadedParcelsSubject.next( parcels );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.loadedParcelsSubject.next( [] );
        } );
  }

  /**
   * fetch new loadlistNo via REST
   * and make this the new activeLoadlist
   * and actualize dataset
   */
  newLoadlist(): void {
    this.http.post<Loadinglist>( this.newLoadlistNoUrl, null )
      .subscribe( ( data ) => {
          this.setActiveLoadinglist( data );
        },
        ( _ ) => {
          this.ics.isOffline();
        }
      );
  }

  reportHeaderData( loadingList: Loadinglist ): LoadinglistReportHeader {
    const dateFrom = this.wds.workingDate(),
      dateTo = this.wds.hubDeliveryDate(),
      loadingAddress = this.activeStation.address
        ? `${this.activeStation.address.line1}, ${this.activeStation.address.street} ${this.activeStation.address.streetNo}, `
        + `${this.activeStation.address.countryCode}-${this.activeStation.address.zipCode} ${this.activeStation.address.city}`
        : '';
    const allParcels = <Exportparcel[]>[];
    if (loadingList.orders) {
      loadingList.orders
        .map( ( order: Exportorder ) => order.parcels )
        .forEach( ( parcels: Exportparcel[] ) => allParcels.push( ...parcels ) );
    }
    return <LoadinglistReportHeader> {
      loadlistNo: loadingList.loadinglistNo,
      dateFrom: dateFrom,
      dateTo: dateTo,
      loadingAddress: loadingAddress,
      hubAddress: HUB_ADDRESS,
      shipmentCount: this.shipmentCount( allParcels ),
      packageCount: allParcels.length,
      totalWeight: this.sumWeights( allParcels )
    };
  }

  scanPack( parcelNo: string, loadlistLabel: string ): Observable<HttpResponse<any>> {

    const params = new HttpParams()
      .set( 'scancode', parcelNo )
      .set( 'loadinglist-no', loadlistLabel )
      .set( 'station-no', this.activeStation.stationNo.toString() );

    return this.http.patch( this.scanUrl, null, {
      observe: 'response',
      params: params
    } ).pipe(
      map( ( response: HttpResponse<any> ) => {
        return response;
      } )
    );
  }

  setActiveLoadinglist( loadinglist: Loadinglist = null ) {
    this.activeLoadinglistSubject.next( loadinglist );
  }

  sumWeights( parcels: Exportparcel[] ) {
    return sumAndRound( parcels
      .map( ( parcel: Exportparcel ) => parcel.realWeight ) );
  }

  shipmentCount( parcels: Exportparcel[] ) {
    const uniqueOrderIds = this.unique( parcels.map( pack => pack.orderId ) );
    return uniqueOrderIds.length;
  }

}
