import { Injectable } from '@angular/core';

import { environment } from '../../../../environments/environment';
import { AbstractExportService } from '../abstract-export.service';
import { AuthenticationService } from '../../../core/auth/authentication.service';
import { Station } from '../../../core/auth/station.model';

@Injectable()
export class LoadinglistscanService extends AbstractExportService {

  protected newLoadlistNoUrl = `${environment.apiUrl}/internal/v1/export/loadinglist`;
  protected reportHeaderUrl = `${environment.apiUrl}/internal/v1/loadinglist/report/header`;

  protected subscribeActiveStation( auth: AuthenticationService ) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => {
      this.activeStation = activeStation;
      const stationNo = this.activeStation.stationNo.toString();
      this.packageUrl = `${environment.apiUrl}/internal/v1/export/station/${stationNo}?send-date=${this.wds.workingDate()}`;
    } );
  }
}
