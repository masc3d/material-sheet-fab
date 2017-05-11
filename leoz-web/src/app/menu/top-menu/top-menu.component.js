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
import { DOCUMENT } from '@angular/platform-browser';
var TopMenuComponent = (function () {
    // public isShown = false;
    function TopMenuComponent(renderer, document) {
        this.renderer = renderer;
        this.document = document;
    }
    TopMenuComponent.prototype.toggle = function () {
        // this.isShown = typeof isShown === 'undefined' ? !this.isShown : isShown;
        if (this.document && this.document.body) {
            if (this.document.body.classList.contains('isOpenMenu')) {
                this.renderer.removeClass(this.document.body, 'isOpenMenu');
                this.renderer.setProperty(this.document.body, 'scrollTop', 0);
            }
            else {
                this.renderer.addClass(this.document.body, 'isOpenMenu');
            }
        }
    };
    return TopMenuComponent;
}());
TopMenuComponent = __decorate([
    Component({
        selector: 'app-top-menu',
        templateUrl: './top-menu.component.html'
    }),
    __param(1, Inject(DOCUMENT)),
    __metadata("design:paramtypes", [Renderer2, Object])
], TopMenuComponent);
export { TopMenuComponent };
//# sourceMappingURL=top-menu.component.js.map