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
import { DriverService } from '../../driver/driver.service';
import { TourService } from '../tour.service';
var TourDriverListComponent = (function () {
    // errorMsg: string;
    function TourDriverListComponent(driverService, tourService) {
        this.driverService = driverService;
        this.tourService = tourService;
    }
    TourDriverListComponent.prototype.ngOnInit = function () {
        this.drivers = this.driverService.getDrivers();
        // this.driverService.getDrivers().subscribe((resDriverList) => {
        //     this.drivers = resDriverList;
        //   },
        //   resError => this.errorMsg = resError);
    };
    TourDriverListComponent.prototype.showPosition = function (driver) {
        this.tourService.changeActiveDriverMarker(driver);
    };
    return TourDriverListComponent;
}());
TourDriverListComponent = __decorate([
    Component({
        selector: 'app-tour-driver-list',
        templateUrl: './tour-driver-list.component.html'
    }),
    __metadata("design:paramtypes", [DriverService,
        TourService])
], TourDriverListComponent);
export { TourDriverListComponent };
//# sourceMappingURL=tour-driver-list.component.js.map