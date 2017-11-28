import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { environment } from '../../../../environments/environment';
import { AbstractExportService } from '../abstract-export.service';
import { BagData } from './bagdata.model';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';

@Injectable()
export class BagscanService extends AbstractExportService {

  public activeBagDataSubject = new BehaviorSubject<BagData>( <BagData> {} );
  public activeBagData$ = this.activeBagDataSubject.asObservable().distinctUntilChanged();

  protected bagDataUrl = `${environment.apiUrl}/internal/v1/bagscan/bagdata`;
  protected validateBagIdUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/bagid`;
  protected validateBackLabelUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/backlabel`;
  protected validateBackSealUrl = `${environment.apiUrl}/internal/v1/bagscan/validate/backseal`;

  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/bagscan/new`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/bagscan/report/header`;

  protected subscribeActiveStation( auth: AuthenticationService ) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => {
      this.activeStation = activeStation;
      this.packageUrl = `${environment.apiUrl}/internal/v1/export/station/${this.activeStation.stationNo.toString()}/bag`;
    } );
  }

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
        ( error ) => {
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
