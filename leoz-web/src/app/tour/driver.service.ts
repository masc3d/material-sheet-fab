import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';

import { Driver, Position } from './driver.model';
import { environment } from '../../environments/environment';

@Injectable()
export class DriverService {

  // private driverListUrl = `${environment.apiUrl}/drivers`;
  private driverListUrl = `${environment.apiUrl}/driverlist.json`;

  private activeDriverSubject = new BehaviorSubject<Driver>(new Driver());
  public activeDriver = this.activeDriverSubject.asObservable().distinctUntilChanged();

  constructor(private http: Http) {
  }

  getDrivers() {
    return this.http.get(this.driverListUrl)
      .map((response: Response) => {
        const driverArr: Driver[] = [];
        response.json().forEach(function (json) {
          const driver = Object.assign(new Driver(), json);
          driver.position = Object.assign(new Position(), driver.position);
          driverArr.push(driver);
        });
        return driverArr;
      })
      .catch((error: Response) => this.errorHandler(error));
  }

  errorHandler(error: Response) {
    console.log(error);
    return Observable.of([]);
  }

  changeActiveDriver(selectedDriver) {
    this.activeDriverSubject.next(selectedDriver);
  }
}
