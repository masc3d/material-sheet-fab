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
import { FormBuilder, Validators } from '@angular/forms';
import { DriverService } from '../driver.service';
var DriverFormComponent = (function () {
    function DriverFormComponent(fb, driverService) {
        this.fb = fb;
        this.driverService = driverService;
    }
    DriverFormComponent.prototype.ngOnInit = function () {
        var _this = this;
        var positionFormGroup = this.fb.group({
            lat: [null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/)],
            lng: [null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/)],
        });
        this.driverForm = this.fb.group({
            firstname: [null, [Validators.minLength(3), Validators.maxLength(10)]],
            surname: [null, [Validators.minLength(3), Validators.maxLength(10)]],
            drivernumber: [null, [Validators.required, Validators.pattern('^[0-9]{4}$')]],
            tournumber: [null, [Validators.pattern('^[0-9]{4}$')]],
            position: positionFormGroup
        });
        this.driverService.activeDriver.subscribe(function (activeDriver) {
            _this.activeDriver = activeDriver;
            _this.driverForm.patchValue({
                firstname: activeDriver.firstname,
                surname: activeDriver.surname,
                drivernumber: activeDriver.drivernumber,
                tournumber: activeDriver.tournumber,
                position: {
                    lat: activeDriver.position ? activeDriver.position.lat : '',
                    lng: activeDriver.position ? activeDriver.position.lng : ''
                }
            });
        });
    };
    DriverFormComponent.prototype.onSubmit = function () {
        console.log(this.driverForm.value);
    };
    return DriverFormComponent;
}());
DriverFormComponent = __decorate([
    Component({
        selector: 'app-driver-form',
        templateUrl: './driver-form.component.html',
        styles: ["\n    input.ng-invalid {\n      border-left: 5px solid red;\n    }\n\n    input.ng-valid {\n      border-left: 5px solid green;\n    }\n  "]
    }),
    __metadata("design:paramtypes", [FormBuilder,
        DriverService])
], DriverFormComponent);
export { DriverFormComponent };
//# sourceMappingURL=driver-form.component.js.map