import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable ,  BehaviorSubject } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';

import { environment } from '../../../../environments/environment';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';
import { Loadinglist } from '../../../core/models/loadinglist.model';
import { Bag } from '../../../core/models/bag.model';
import { InetConnectionService } from '../../../core/inet-connection.service';
import { WorkingdateService } from '../../../core/workingdate.service';
import { Exportorder } from '../../../core/models/exportorder.model';
import { Exportparcel } from '../../../core/models/exportparcel.model';
import { sumAndRound } from '../../../core/math/sumAndRound';

@Injectable({
  providedIn: 'root',
})
export class BagscanService {

  protected allLoadinglistsUrl: string;
  protected openExportordersUrl: string;
  protected loadedExportordersUrl: string;

  // public activeBagDataSubject = new BehaviorSubject<BagData>( <BagData> {} );
  // public activeBagData$ = this.activeBagDataSubject.asObservable().pipe( distinctUntilChanged() );

  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/export/bag/loadinglist`;
  protected countBagsToSendBackUrl: string;

  private countBagsToSendBackSubject = new BehaviorSubject<number>( 0 );
  public countBagsToSendBack$ = this.countBagsToSendBackSubject.asObservable().pipe( distinctUntilChanged() );

  private openParcelsSubject = new BehaviorSubject<Exportparcel[]>( [] );
  public openParcels$ = this.openParcelsSubject.asObservable();

  // public activeLoadinglistSubject = new BehaviorSubject<Loadinglist>( <Loadinglist> {
  //   label: null,
  //   loadlistNo: null,
  //   orders: []
  // } );
  // public activeLoadinglist$ = this.activeLoadinglistSubject.asObservable().pipe( distinctUntilChanged() );

  // public allLoadlistsSubject = new BehaviorSubject<Loadinglist[]>( [] );
  // public allLoadlists$ = this.allLoadlistsSubject.asObservable().pipe( distinctUntilChanged() );

  protected activeStation: Station;

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
      const stationNo = this.activeStation.stationNo;
      // this.packageUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/bag/order?send-date=${this.wds.workingDate()}`;
      this.openExportordersUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/bag/order?send-date=${this.wds.workingDate()
        .format( 'MM/DD/YYYY' )}`;
      this.loadedExportordersUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/loaded/bag/order?send-date=${this.wds.workingDate()
        .format( 'MM/DD/YYYY' )}`;
      this.allLoadinglistsUrl = `${environment.apiUrl}/internal/v1/export/loadinglist?station-no=${stationNo}&send-date=${this.wds.workingDate()
        .format( 'MM/DD/YYYY' )}`;
      this.countBagsToSendBackUrl = `${environment.apiUrl}/internal/v1/bag/station/${stationNo}/send-back`;
    } );
  }

  countBagsToSendBack() {
    this.http.get<number>( this.countBagsToSendBackUrl )
      .subscribe( ( countBagsToSendBack ) => this.countBagsToSendBackSubject.next( countBagsToSendBack ),
        ( error ) => console.log( error ) );
  }

  validateBagId( bagId: number ): Observable<Bag> {
    const stationNo = this.activeStation.stationNo;
    const validateBagIdUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/bag/${bagId}`;
    return this.http.get<Bag>( validateBagIdUrl );
  }

  scanPackToBag( bagId: number, bagbackNo: number, packageId: number, loadlistNoLabel: string, yellowseal: number ): Observable<Object> {

    console.log( 'scanPackToBag( bagId: number, bagbackNo: number, packageId: number, loadlistNo: number, yellowseal: number )',
      bagId, bagbackNo, packageId, loadlistNoLabel, yellowseal );

    const scanPackToBagUrl = `${environment.apiUrl}/internal/v1/export/bag/${bagId}/fill`;
    const params = new HttpParams()
      .set( 'bagback-no', bagbackNo.toString() )
      .set( 'scancode', packageId.toString() )
      .set( 'yellowseal', yellowseal.toString() )
      .set( 'loadinglist-no', loadlistNoLabel )
      .set( 'station-no', this.activeStation.stationNo.toString() );

    return this.http.patch( scanPackToBagUrl, null, {
      observe: 'response',
      params: params
    } ).pipe(
      map( ( response: HttpResponse<any> ) => {
        return response;
      } )
    );
  }

  finishBag( bagId: number, bagbackNo: number, packageId: number, loadlistNoLabel: string ): Observable<Object> {
    const closeBagUrl = `${environment.apiUrl}/internal/v1/export/bag/${bagId}/close`;
    const params = new HttpParams()
      .set( 'bagback-no', bagbackNo.toString() )
      .set( 'loadinglist-no', loadlistNoLabel )
      .set( 'station-no', this.activeStation.stationNo.toString() );

    return this.http.patch( closeBagUrl, null, {
      observe: 'response',
      params: params
    } ).pipe(
      map( ( response: HttpResponse<any> ) => {
        return response;
      } )
    );
  }

  // getAllLoadinglists(): void {
  //   this.http.get<Loadinglist[]>( this.allLoadinglistsUrl + '&includeParcels=false' )
  //     .subscribe( ( loadinglists ) => {
  //         this.allLoadlistsSubject.next(
  //           loadinglists.filter( ( loadinglist: Loadinglist ) => loadinglist.loadinglistType === this.loadinglistType )
  //         );
  //       },
  //       ( _ ) => {
  //         this.ics.isOffline();
  //         this.allLoadlistsSubject.next( [] );
  //       } );
  // }

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
          // this.openParcelsSubject.next( parcels );
          this.openParcelsSubject.next( parcels
            .filter( ( parcel: Exportparcel ) => !parcel.loadinglistNo || parcel.loadinglistNo === null )
          );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.openParcelsSubject.next( [] );
        } );
  }

  /**
   * fetch new loadlistNo via REST
   * and make this the new activeLoadlist
   * and actualize dataset
   */
  newLoadlist(): Observable<Loadinglist> {
    return this.http.post<Loadinglist>( this.newLoadlistNoUrl, null );
  }

  // setActiveLoadinglist( loadinglist: Loadinglist = null ) {
  //   this.activeLoadinglistSubject.next( loadinglist );
  // }

  sumWeights( parcels: Exportparcel[] ) {
    return sumAndRound( parcels
      .map( ( parcel: Exportparcel ) => parcel.realWeight ) );
  }

  shipmentCount( parcels: Exportparcel[] ) {
    const uniqueOrderIds = this.unique( parcels.map( pack => pack.orderId ) );
    return uniqueOrderIds.length;
  }

}
