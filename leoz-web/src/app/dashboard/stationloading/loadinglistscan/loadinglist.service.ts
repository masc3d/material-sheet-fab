import { Injectable } from '@angular/core';
import { Response } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { SelectItem } from 'primeng/primeng';

import { Package } from './package.model';
import { Loadinglist } from 'app/dashboard/stationloading/loadinglistscan/loadinglist.model';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

@Injectable()
export class LoadinglistService {

  protected packageUrl = `${environment.apiUrl}/internal/v1/loadinglist/packages`;
  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/loadinglist/new`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/loadinglist/report/header`;

  private allPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public allPackages = this.allPackagesSubject.asObservable();

  private openPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public openPackages = this.openPackagesSubject.asObservable();

  private loadedPackagesSubject = new BehaviorSubject<Package[]>( [] );
  public loadedPackages = this.loadedPackagesSubject.asObservable();

  private loadlistsSubject = new BehaviorSubject<SelectItem[]>( [] );
  public loadlists = this.loadlistsSubject.asObservable();

  public activeLoadinglistSubject = new BehaviorSubject<Loadinglist>( <Loadinglist> {
    loadlistNo: null,
    packages: []
  } );
  public activeLoadinglist = this.activeLoadinglistSubject.asObservable().distinctUntilChanged();

  public allLoadlistsSubject = new BehaviorSubject<Loadinglist[]>( [] );
  public allLoadlists = this.allLoadlistsSubject.asObservable().distinctUntilChanged();

  public activeLoadinglistTmp: Loadinglist;

  private unique: Function = ( arrArg ) => {
    return arrArg.filter( ( elem, pos, arr ) => {
      return arr.indexOf( elem ) === pos;
    } );
  };

  protected loadinglistNoPredicate: Function = ( loadinglistNo ) => loadinglistNo > 99999;

  constructor( protected http: HttpClient ) {
    this.activeLoadinglist.subscribe( ( activeLl: Loadinglist ) => this.activeLoadinglistTmp = activeLl );

    this.allPackages.subscribe( ( packages: Package[] ) => {

      this.openPackagesSubject.next( packages.filter( ( pack: Package ) => pack.loadlistNo === null ) );

      this.loadedPackagesSubject.next( packages.filter( ( pack: Package ) =>
        this.activeLoadinglistTmp.loadlistNo !== null && pack.loadlistNo === this.activeLoadinglistTmp.loadlistNo
      ) );

      const loadinglistNosSorted = packages.filter( ( pack: Package ) => pack.loadlistNo !== null )
        .map( ( pack: Package ) => pack.loadlistNo )
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
    this.http.get<Package[]>( this.packageUrl )
      .subscribe( ( packages ) => this.allPackagesSubject.next( packages ),
        ( error: Response ) => {
          console.log( error );
          this.allPackagesSubject.next( <Package[]> {} );
        } );
  }

  /**
   * fetch new loadlistNo via REST
   * and make this the new activeLoadlist
   * and actualize dataset
   */
  newLoadlist(): void {
    this.http.get( this.newLoadlistNoUrl )
      .subscribe( ( data ) => this.setActiveLoadinglist( data[ 'loadlistNo' ] ),
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

  scanPack( packageId: string, loadlistNo: string ): Observable<HttpResponse<any>> {

    return this.http.post( this.packageUrl,
      { 'packageId': packageId, 'loadlistNo': loadlistNo } );
  }

  setActiveLoadinglist( selected: number ) {
    this.activeLoadinglistSubject.next( this.createLoadinglist( selected, [] ) );
    this.getAllPackages();
  }

  sumWeights( packages: Package[] ) {
    return Math.round( packages
      .map( ( p ) => p.weight )
      .reduce( ( a, b ) => a + b, 0 ) * 10 ) / 10;
  }

  private createLoadinglist( loadlistNo: number, allPackages: Package[] ) {
    const currentPackages = allPackages.filter( ( pack: Package ) => loadlistNo !== null && pack.loadlistNo === loadlistNo );
    return <Loadinglist> { loadlistNo: loadlistNo, packages: currentPackages };
  }
}
