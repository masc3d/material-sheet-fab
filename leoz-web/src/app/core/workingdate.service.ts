import { Injectable } from '@angular/core';

import * as moment from 'moment';

import { NOW_MINUS_X_HOURS } from './constants';

@Injectable()
export class WorkingdateService {

  public workingDate() {
    const nowMinusXHours = moment().subtract( NOW_MINUS_X_HOURS, 'hours' );
    return nowMinusXHours.format( 'YYYY/MM/DD' );
  }
}
