var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Component } from '@angular/core';
import { DriverService } from '../driver.service';
import { ErrormsgService } from '../../error/errormsg.service';
var DriverListComponent = (function () {
    // drivers: Driver[];
    // errorMsg: string;
    function DriverListComponent(driverService) {
        this.driverService = driverService;
    }
    DriverListComponent.prototype.ngOnInit = function () {
        this.drivers = this.driverService.getDrivers();
        // this.driverService.getDrivers().subscribe((resDriverList) => {
        //     this.drivers = resDriverList;
        //   },
        //   resError => this.errorMsg = resError);
    };
    DriverListComponent.prototype.selected = function (selectedDriver) {
        this.driverService.changeActiveDriver(selectedDriver);
    };
    return DriverListComponent;
}());
DriverListComponent = __decorate([
    Component({
        selector: 'app-driver-list',
        templateUrl: './driver-list.component.html',
        providers: [ErrormsgService]
    }),
    __metadata("design:paramtypes", [DriverService])
], DriverListComponent);
export { DriverListComponent };
//# sourceMappingURL=driver-list.component.js.map