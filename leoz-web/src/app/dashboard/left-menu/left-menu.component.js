var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
import { Component, Inject, Renderer2 } from '@angular/core';
import { Router } from '@angular/router';
import { DOCUMENT } from '@angular/platform-browser';
var LeftMenuComponent = (function () {
    function LeftMenuComponent(renderer, document, router) {
        this.renderer = renderer;
        this.document = document;
        this.router = router;
    }
    LeftMenuComponent.prototype.ngOnInit = function () {
    };
    LeftMenuComponent.prototype.navigate = function (path) {
        this.router.navigate([path]);
        this.closeMenu();
    };
    LeftMenuComponent.prototype.closeMenu = function () {
        if (this.document && this.document.body) {
            this.renderer.removeClass(this.document.body, 'isOpenMenu');
            this.renderer.setProperty(this.document.body, 'scrollTop', 0);
        }
    };
    return LeftMenuComponent;
}());
LeftMenuComponent = __decorate([
    Component({
        selector: 'app-left-menu',
        templateUrl: './left-menu.component.html'
    }),
    __param(1, Inject(DOCUMENT)),
    __metadata("design:paramtypes", [Renderer2, Object, Router])
], LeftMenuComponent);
export { LeftMenuComponent };
//# sourceMappingURL=left-menu.component.js.map