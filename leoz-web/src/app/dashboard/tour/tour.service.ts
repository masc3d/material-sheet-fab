import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Driver } from './driver.model';

@Injectable()
export class TourService {

  private activeDriverMarkerSubject = new BehaviorSubject<Driver>(new Driver());
  public activeDriverMarker = this.activeDriverMarkerSubject.asObservable().distinctUntilChanged();

  changeActiveDriverMarker(selectedDriver) {
    this.activeDriverMarkerSubject.next(selectedDriver);
  }
}
