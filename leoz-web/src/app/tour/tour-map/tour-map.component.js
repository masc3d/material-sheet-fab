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
import { TourService } from '../tour.service';
import { Position } from '../../driver/driver.model';
var TourMapComponent = (function () {
    function TourMapComponent(tourService) {
        this.tourService = tourService;
    }
    TourMapComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.lat = 48.57;
        this.lng = 13.26;
        this.displayMarker = false;
        this.name = '';
        this.tourService.activeDriverMarker.subscribe(function (activeDriverMarker) {
            _this.displayMarker = activeDriverMarker.position instanceof Position;
            if (_this.displayMarker) {
                _this.name = activeDriverMarker.surname;
                _this.lat = activeDriverMarker.position.lat;
                _this.lng = activeDriverMarker.position.lng;
            }
        });
    };
    return TourMapComponent;
}());
TourMapComponent = __decorate([
    Component({
        selector: 'app-tour-map',
        templateUrl: './tour-map.component.html'
    }),
    __metadata("design:paramtypes", [TourService])
], TourMapComponent);
export { TourMapComponent };
//# sourceMappingURL=tour-map.component.js.map