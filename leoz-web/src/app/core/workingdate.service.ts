import { Injectable } from '@angular/core';

import * as moment from 'moment';

import { NOW_MINUS_X_HOURS } from './constants';

@Injectable({
  providedIn: 'root',
})
export class WorkingdateService {

  private nowMinusXHours: moment.Moment;

  private determineWorkingDate() {
    this.nowMinusXHours = moment().subtract( NOW_MINUS_X_HOURS, 'hours' );
  }

  public workingDate(): moment.Moment {
    this.determineWorkingDate();
    return this.nowMinusXHours;
  }

  public hubDeliveryDate(): moment.Moment {
    this.determineWorkingDate();
    return moment( this.nowMinusXHours ).add( 1, 'days' );
  }
}
