import { Injectable } from '@angular/core';

import * as moment from 'moment';

import { environment } from '../../environments/environment';

@Injectable()
export class WorkingdateService {

  public workingDate() {
    const nowMinusXHours = moment().subtract( environment.nowMinusXHours, 'hours' );
    return nowMinusXHours.format( 'YYYY/MM/DD' );
  }
}
