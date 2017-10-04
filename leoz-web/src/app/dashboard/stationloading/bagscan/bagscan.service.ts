import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { LoadinglistService } from '../loadinglistscan/loadinglist.service';
import { Response } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { BagData } from './bagdata.model';
import { Observable } from 'rxjs/Observable';
import { HttpResponse } from '@angular/common/http';

@Injectable()
export class BagscanService extends LoadinglistService {

  public activeBagDataSubject = new BehaviorSubject<BagData>( <BagData> {} );
  public activeBagData = this.activeBagDataSubject.asObservable().distinctUntilChanged();

  protected bagDataUrl = `${environment.apiUrl}/internal/v1/bagscan/bagdata`;
  protected validateBagIdUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/bagid`;
  protected validateBackLabelUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/backlabel`;
  protected validateBackSealUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/backseal`;
  protected packageUrl = `${environment.apiUrl}/internal/v1/bagscan/packages`;
  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/bagscan/new`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/bagscan/report/header`;

  protected loadinglistNoPredicate: Function = ( loadinglistNo ) => loadinglistNo < 100000;

  setActiveLoadinglist( selected: number ) {
    // REST fetch corresponding bagId, sealNo, backlabelNo
    this.activeBagDataSubject.next( <BagData> {} );
    this.getBagData( selected );
    super.setActiveLoadinglist( selected );
  }

  /**
   * Headset of the Bag
   * @param {number} baglistNo
   */
  getBagData( baglistNo: number ): void {
    this.http.post<BagData>( this.bagDataUrl,
      { 'baglistNo': baglistNo } )
      .subscribe( ( bagData ) => this.activeBagDataSubject.next( bagData ),
        ( error: Response ) => {
          console.log( error );
          this.activeBagDataSubject.next( <BagData> {} );
        } );
  }

  validateBagId( bagId: string ): Observable<HttpResponse<any>> {
    return this.http.post( this.validateBagIdUrl, { 'bagId': bagId },
      { observe: 'response' } )
  }

  validateBackLabel( bagId: string, backLabel: string ): Observable<HttpResponse<any>> {
    return this.http.post( this.validateBackLabelUrl,
      { 'bagId': bagId, 'backLabel': backLabel },
      {
        observe: 'response'
      } );
  }

  validateBackSeal( bagId: string, backLabel: string, backSeal: string ): Observable<HttpResponse<any>> {
    return this.http.post( this.validateBackSealUrl,
      { 'bagId': bagId, 'backLabel': backLabel, 'backSeal': backSeal },
      {
        observe: 'response'
      } )
  }
}
