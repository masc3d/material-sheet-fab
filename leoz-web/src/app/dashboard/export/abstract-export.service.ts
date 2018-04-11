import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { distinctUntilChanged, map } from 'rxjs/operators';

import { SelectItem } from 'primeng/api';

import { Package } from '../../core/models/package.model';
import { Exportlist } from './exportlist.model';
import { NewLoadinglistNoResponse } from './loadinglistscan/new-loadinglist-no-response.model';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../core/auth/authentication.service';
import { Station } from '../../core/auth/station.model';
import { Shipment } from '../../core/models/shipment.model';
import { sumAndRound } from '../../core/math/sumAndRound';
import { WorkingdateService } from '../../core/workingdate.service';
import { InetConnectionService } from '../../core/inet-connection.service';
import { LoadinglistReportHeader } from './loadinglistscan/loadinglist-report-header.model';
import { HUB_ADDRESS } from '../../core/constants';

@Injectable()
export abstract class AbstractExportService {

  protected packageUrl: string;
  protected scanUrl = `${environment.apiUrl}/internal/v1/export`;

  protected abstract newLoadlistNoUrl: string;
  protected abstract reportHeaderUrl: string;

  private allPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public allPackages$ = this.allPackagesSubject.asObservable();

  private openPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public openPackages$ = this.openPackagesSubject.asObservable();

  private loadedPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public loadedPackages$ = this.loadedPackagesSubject.asObservable();

  private loadlistsSubject = new BehaviorSubject<SelectItem[]>( [] );
  public loadlists$ = this.loadlistsSubject.asObservable();

  public activeLoadinglistSubject = new BehaviorSubject<Exportlist>( <Exportlist> {
    label: null,
    loadlistNo: null,
    packages: []
  } );
  public activeLoadinglist$ = this.activeLoadinglistSubject.asObservable().pipe( distinctUntilChanged() );

  public allLoadlistsSubject = new BehaviorSubject<Exportlist[]>( [] );
  public allLoadlists$ = this.allLoadlistsSubject.asObservable().pipe( distinctUntilChanged() );

  public activeLoadinglistTmp: Exportlist;

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
    this.activeLoadinglist$.subscribe( ( activeLl: Exportlist ) => this.activeLoadinglistTmp = activeLl );

    this.allPackages$.subscribe( ( packages: Package[] ) => {

      this.openPackagesSubject.next( packages.filter( ( pack: Package ) => !pack.loadinglistNo || pack.loadinglistNo === null ) );

      this.loadedPackagesSubject.next( packages.filter( ( pack: Package ) =>
        this.activeLoadinglistTmp.loadlistNo !== null && pack.loadinglistNo === this.activeLoadinglistTmp.loadlistNo
      ) );

      const loadinglistNosSorted = packages.filter( ( pack: Package ) => pack.loadinglistNo )
        .map( ( pack: Package ) => pack.loadinglistNo )
        .sort();

      const loadinglistNosUnique = this.unique( loadinglistNosSorted );
      const selectItemsArray = loadinglistNosUnique
        .map( ( loadinglistNo: number ) => <SelectItem> {
          label: String( loadinglistNo ),
          value: loadinglistNo
        } );
      this.loadlistsSubject.next( selectItemsArray );

      const allLoadlistArray = loadinglistNosUnique
        .map( ( loadinglistNo: number ) => this.createLoadinglist( loadinglistNo, null, packages ) );
      this.allLoadlistsSubject.next( allLoadlistArray );

      this.activeLoadinglistSubject.next(
        this.createLoadinglist( this.activeLoadinglistTmp.loadlistNo, this.activeLoadinglistTmp.label, packages ) );
    } );
  }

  protected abstract subscribeActiveStation( auth: AuthenticationService );

  getAllPackages(): void {
    this.http.get<Shipment[]>( this.packageUrl )
      .subscribe( ( shipments ) => {
          let packages = [];
          shipments.forEach( ( shipment: Shipment ) => {
            const address = shipment.deliveryAddress;
            const parcelsWithAddress = shipment.parcels.map( ( parcel: Package ) => <Package> {
              zip: address.zipCode,
              city: address.city,
              devliveryStation: shipment.deliveryStation,
              ...parcel
            } );
            packages = packages.concat( parcelsWithAddress );
          } );
          this.allPackagesSubject.next( packages );
        },
        ( _ ) => {
          this.ics.isOffline();
          this.allPackagesSubject.next( [] );
        } );
  }

  /**
   * fetch new loadlistNo via REST
   * and make this the new activeLoadlist
   * and actualize dataset
   */
  newLoadlist(): void {
    this.http.post<NewLoadinglistNoResponse>( this.newLoadlistNoUrl, null )
      .subscribe( ( data ) => {
          this.setActiveLoadinglist( data.loadinglistNo, data.label );
          console.log( 'newLoadlist()...', data.loadinglistNo, data.label );
        },
        ( _ ) => {
          this.ics.isOffline();
        }
      );
  }

  reportHeaderData( loadingList: Exportlist ): LoadinglistReportHeader {
    const dateFrom = this.wds.workingDate(),
      dateTo = this.wds.hubDeliveryDate(),
      loadingAddress = this.activeStation.address
        ? `${this.activeStation.address.line1}, ${this.activeStation.address.street} ${this.activeStation.address.streetNo}, `
        + `${this.activeStation.address.countryCode}-${this.activeStation.address.zipCode} ${this.activeStation.address.city}`
        : '',
      shipmentCount = loadingList.packages && loadingList.packages.length > 0 ? this.shipmentCount( loadingList.packages ) : 0,
      packageCount = loadingList.packages ? loadingList.packages.length : 0,
      totalWeight = loadingList.packages && loadingList.packages.length > 0 ? this.sumWeights( loadingList.packages ) : 0;
    return <LoadinglistReportHeader> {
      loadlistNo: loadingList.loadlistNo,
      dateFrom: dateFrom,
      dateTo: dateTo,
      loadingAddress: loadingAddress,
      hubAddress: HUB_ADDRESS,
      shipmentCount: shipmentCount,
      packageCount: packageCount,
      totalWeight: totalWeight
    };
  }

  scanPack( packageId: string, loadlistLabel: string ): Observable<HttpResponse<any>> {

    const params = new HttpParams()
      .set( 'parcel-no-or-reference', packageId )
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

  setActiveLoadinglist( selected: number, label: string = null ) {
    this.activeLoadinglistSubject.next( this.createLoadinglist( selected, label, [] ) );
    this.getAllPackages();
  }

  sumWeights( packages: Package[] ) {
    return sumAndRound( packages
      .map( ( parcel: Package ) => parcel.realWeight ) );
  }

  shipmentCount( packages: Package[] ) {
    const uniqueOrderIds = this.unique( packages.map( pack => pack.orderId ) );
    return uniqueOrderIds.length;
  }

  private createLoadinglist( loadlistNo: number, label: string, allPackages: Package[] ) {
    const currentPackages = allPackages.filter( ( pack: Package ) => loadlistNo !== null && pack.loadinglistNo === loadlistNo );
    return <Exportlist> { loadlistNo: loadlistNo, label: label, packages: currentPackages };
  }
}
