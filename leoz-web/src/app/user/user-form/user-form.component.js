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
import { UserService } from '../user.service';
var UserFormComponent = (function () {
    function UserFormComponent(fb, userService) {
        this.fb = fb;
        this.userService = userService;
    }
    UserFormComponent.prototype.ngOnInit = function () {
        var _this = this;
        var positionFormGroup = this.fb.group({
            lat: [null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/)],
            lng: [null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/)],
        });
        this.userForm = this.fb.group({
            firstname: [null, [Validators.minLength(3), Validators.maxLength(10)]],
            surname: [null, [Validators.minLength(3), Validators.maxLength(10)]],
            usernumber: [null, [Validators.required, Validators.pattern('^[0-9]{4}$')]],
            tournumber: [null, [Validators.pattern('^[0-9]{4}$')]],
            position: positionFormGroup
        });
        this.userService.activeUser.subscribe(function (activeUser) {
            _this.activeUser = activeUser;
            _this.userForm.patchValue({
                firstname: activeUser.firstname,
                surname: activeUser.surname,
                usernumber: activeUser.usernumber,
                tournumber: activeUser.tournumber,
                position: {
                    lat: activeUser.position ? activeUser.position.lat : '',
                    lng: activeUser.position ? activeUser.position.lng : ''
                }
            });
        });
    };
    UserFormComponent.prototype.onSubmit = function () {
        console.log(this.userForm.value);
    };
    return UserFormComponent;
}());
UserFormComponent = __decorate([
    Component({
        selector: 'app-user-form',
        templateUrl: './user-form.component.html',
        styles: ["\n    input.ng-invalid {\n      border-left: 5px solid red;\n    }\n\n    input.ng-valid {\n      border-left: 5px solid green;\n    }\n  "]
    }),
    __metadata("design:paramtypes", [FormBuilder,
        UserService])
], UserFormComponent);
export { UserFormComponent };
//# sourceMappingURL=user-form.component.js.map
