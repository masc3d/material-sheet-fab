var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { Injectable } from '@angular/core';
import { Response, ResponseOptions } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
var ErrormsgService = (function () {
    function ErrormsgService() {
        this.latestErrorSubject = new BehaviorSubject(new Response(new ResponseOptions({ status: 200 })));
        this.latestError = this.latestErrorSubject.asObservable().distinctUntilChanged();
    }
    ErrormsgService.prototype.changeError = function (errorResponse) {
        this.latestErrorSubject.next(errorResponse);
    };
    return ErrormsgService;
}());
ErrormsgService = __decorate([
    Injectable()
], ErrormsgService);
export { ErrormsgService };
//# sourceMappingURL=errormsg.service.js.map