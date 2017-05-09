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
import { ErrormsgService } from '../error/errormsg.service';
var AppFooterComponent = (function () {
    function AppFooterComponent(errormsgService) {
        this.errormsgService = errormsgService;
    }
    AppFooterComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.errormsgService.latestError.subscribe(function (latestErrorResponse) { return _this.latestError = latestErrorResponse; });
        console.log(this.latestError);
    };
    return AppFooterComponent;
}());
AppFooterComponent = __decorate([
    Component({
        selector: 'app-footer',
        templateUrl: './app-footer.component.html',
        providers: [ErrormsgService]
    }),
    __metadata("design:paramtypes", [ErrormsgService])
], AppFooterComponent);
export { AppFooterComponent };
//# sourceMappingURL=app-footer.component.js.map