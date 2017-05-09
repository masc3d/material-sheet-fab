var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';
import { Driver, Position } from './driver.model';
import { environment } from '../../environments/environment';
import { ErrormsgService } from '../error/errormsg.service';
var DriverService = (function () {
    function DriverService(http, errormsgService) {
        this.http = http;
        this.errormsgService = errormsgService;
        // private driverListUrl = `${environment.apiUrl}/drivers`;
        this.driverListUrl = environment.apiUrl + "/driverlist.json";
        this.activeDriverSubject = new BehaviorSubject(new Driver());
        this.activeDriver = this.activeDriverSubject.asObservable().distinctUntilChanged();
    }
    DriverService.prototype.getDrivers = function () {
        var _this = this;
        return this.http.get(this.driverListUrl)
            .map(function (response) {
            var driverArr = [];
            response.json().forEach(function (json) {
                var driver = Object.assign(new Driver(), json);
                driver.position = Object.assign(new Position(), driver.position);
                driverArr.push(driver);
            });
            return driverArr;
        })
            .catch(function (error) { return _this.errorHandler(error); });
    };
    DriverService.prototype.errorHandler = function (error) {
        console.log(error);
        this.errormsgService.changeError(error);
        return Observable.of([]);
    };
    DriverService.prototype.changeActiveDriver = function (selectedDriver) {
        this.activeDriverSubject.next(selectedDriver);
    };
    return DriverService;
}());
DriverService = __decorate([
    Injectable(),
    __metadata("design:paramtypes", [Http,
        ErrormsgService])
], DriverService);
export { DriverService };
//# sourceMappingURL=driver.service.js.map