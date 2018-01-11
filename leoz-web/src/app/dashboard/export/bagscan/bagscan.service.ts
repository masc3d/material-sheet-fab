import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { environment } from '../../../../environments/environment';
import { AbstractExportService } from '../abstract-export.service';
import { BagData } from './bagdata.model';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';
import * as moment from 'moment';

@Injectable()
export class BagscanService extends AbstractExportService {

  public activeBagDataSubject = new BehaviorSubject<BagData>( <BagData> {} );
  public activeBagData$ = this.activeBagDataSubject.asObservable().distinctUntilChanged();

  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/export/bag/loadinglist`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/bagscan/report/header`;

  protected subscribeActiveStation( auth: AuthenticationService ) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => {
      this.activeStation = activeStation;
      const stationNo = this.activeStation.stationNo;
      this.packageUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/bag/order?send-date=${this.wds.workingDate()}`;
    } );
  }

  validateBagId( bagId: number ): Observable<Object> {
    const stationNo = this.activeStation.stationNo;
    const validateBagIdUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}/bag/${bagId}`;
    return this.http.get( validateBagIdUrl);
  }

  scanPackToBag( bagId: number, bagbackNo: number, packageId: number, loadlistNo: number, yellowseal: number ): Observable<Object> {
    const scanPackToBagUrl = `${environment.apiUrl}/internal/v1/export/bag/${bagId}/fill`;
    const params = new HttpParams()
      .set('bagback-no', bagbackNo.toString())
      .set('parcel-no-or-reference', packageId.toString())
      .set('yellowseal', yellowseal.toString())
      .set('loadinglist-no', loadlistNo.toString())
      .set('station-no', this.activeStation.stationNo.toString());

    return this.http.patch( scanPackToBagUrl, null, {
      observe: 'response',
      params: params
    } ).map( ( response: HttpResponse<any> ) => {
      return response;
    } );
  }

}
