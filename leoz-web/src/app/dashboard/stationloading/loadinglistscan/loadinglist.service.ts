import { Injectable } from '@angular/core';
import { Response } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { SelectItem } from 'primeng/primeng';

import { Package } from '../../../core/models/package.model';
import { Loadinglist } from 'app/dashboard/stationloading/loadinglistscan/loadinglist.model';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';
import { Shipment } from '../../../core/models/shipment.model';
import { sumAndRound } from '../../../core/math/sumAndRound';

@Injectable()
export class LoadinglistService {

  protected packageUrl = `${environment.apiUrl}/internal/v1/parcel/export/station/`;
  protected scanUrl = `${environment.apiUrl}/internal/v1/parcel/export`;
  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/parcel/loadinglist/new`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/loadinglist/report/header`;

  private allPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public allPackages$ = this.allPackagesSubject.asObservable();

  private openPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public openPackages$ = this.openPackagesSubject.asObservable();

  private loadedPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public loadedPackages$ = this.loadedPackagesSubject.asObservable();

  private loadlistsSubject = new BehaviorSubject<SelectItem[]>( [] );
  public loadlists$ = this.loadlistsSubject.asObservable();

  public activeLoadinglistSubject = new BehaviorSubject<Loadinglist>( <Loadinglist> {
    loadlistNo: null,
    packages: []
  } );
  public activeLoadinglist$ = this.activeLoadinglistSubject.asObservable().distinctUntilChanged();

  public allLoadlistsSubject = new BehaviorSubject<Loadinglist[]>( [] );
  public allLoadlists$ = this.allLoadlistsSubject.asObservable().distinctUntilChanged();

  public activeLoadinglistTmp: Loadinglist;

  private activeStation: Station;

  private unique: Function = ( arrArg ) => {
    return arrArg.filter( ( elem, pos, arr ) => {
      return arr.indexOf( elem ) === pos;
    } );
  };
  protected loadinglistNoPredicate: Function = ( loadinglistNo ) => loadinglistNo > 99999;

  constructor( protected http: HttpClient,
               private auth: AuthenticationService ) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => this.activeStation = activeStation );
    this.activeLoadinglist$.subscribe( ( activeLl: Loadinglist ) => this.activeLoadinglistTmp = activeLl );

    this.allPackages$.subscribe( ( packages: Package[] ) => {

      this.openPackagesSubject.next( packages.filter( ( pack: Package ) => !pack.loadinglistNo || pack.loadinglistNo === null ) );

      this.loadedPackagesSubject.next( packages.filter( ( pack: Package ) =>
        this.activeLoadinglistTmp.loadlistNo !== null && pack.loadinglistNo === this.activeLoadinglistTmp.loadlistNo
      ) );

      const loadinglistNosSorted = packages.filter( ( pack: Package ) => pack.loadinglistNo !== null )
        .map( ( pack: Package ) => pack.loadinglistNo )
        .sort();
      const loadinglistNosUnique = this.unique( loadinglistNosSorted );

      const selectItemsArray = loadinglistNosUnique
        .filter( ( loadinglistNo: number ) => this.loadinglistNoPredicate( loadinglistNo ) )
        .map( ( loadinglistNo: number ) => <SelectItem> {
          label: String( loadinglistNo ),
          value: loadinglistNo
        } );
      this.loadlistsSubject.next( selectItemsArray );

      const allLoadlistArray = loadinglistNosUnique
        .filter( ( loadinglistNo: number ) => this.loadinglistNoPredicate( loadinglistNo ) )
        .map( ( loadinglistNo: number ) => this.createLoadinglist( loadinglistNo, packages ) );
      this.allLoadlistsSubject.next( allLoadlistArray );

      this.activeLoadinglistSubject.next( this.createLoadinglist( this.activeLoadinglistTmp.loadlistNo, packages ) );
    } );
  }

  getAllPackages(): void {
    this.http.get<Shipment[]>( this.packageUrl + this.activeStation.stationNo.toString() )
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
        ( error: Response ) => {
          console.log( error );
          this.allPackagesSubject.next( [] );
        } );
  }

  /**
   * fetch new loadlistNo via REST
   * and make this the new activeLoadlist
   * and actualize dataset
   */
  newLoadlist(): void {
    this.http.get<number>( this.newLoadlistNoUrl )
      .subscribe( ( data ) => this.setActiveLoadinglist( data ),
        ( error: Response ) => {
          console.log( error );
        } );
  }

  reportHeaderData( loadlistNo: string ): Observable<HttpResponse<any>> {
    return this.http.get( this.reportHeaderUrl, {
      params: new HttpParams().set( 'loadlistNo', loadlistNo ),
      observe: 'response'
    } );
  }

  scanPack( packageId: string, loadlistNo: number ): Observable<HttpResponse<any>> {
    return this.http.put( this.scanUrl,
      { 'parcel-no': packageId, 'loadinglist-no': loadlistNo, 'station-no': this.activeStation.stationNo } );
  }

  setActiveLoadinglist( selected: number ) {
    this.activeLoadinglistSubject.next( this.createLoadinglist( selected, [] ) );
    this.getAllPackages();
  }

  sumWeights( packages: Package[] ) {
    return sumAndRound( packages
      .map( ( parcel: Package ) => parcel.realWeight ));
  }

  private createLoadinglist( loadlistNo: number, allPackages: Package[] ) {
    const currentPackages = allPackages.filter( ( pack: Package ) => loadlistNo !== null && pack.loadinglistNo === loadlistNo );
    return <Loadinglist> { loadlistNo: loadlistNo, packages: currentPackages };
  }
}
