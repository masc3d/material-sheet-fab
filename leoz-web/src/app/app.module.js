var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BaseRequestOptions, HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { YagaModule } from '@yaga/leaflet-ng2/lib/yaga.module';
import { AppComponent } from './app.component';
import { AppFooterComponent } from './app-footer/app-footer.component';
import { HomeComponent } from './home/home.component';
import { TopMenuComponent } from './menu/top-menu/top-menu.component';
import { routes } from './app.routing';
import { DriverComponent } from './driver/driver.component';
import { TourComponent } from './tour/tour.component';
import { DriverService } from './driver/driver.service';
import { DriverFormComponent } from './driver/driver-form/driver-form.component';
import { DriverListComponent } from './driver/driver-list/driver-list.component';
import { TourMapComponent } from './tour/tour-map/tour-map.component';
import { TourDriverListComponent } from './tour/tour-driver-list/tour-driver-list.component';
import { TourService } from './tour/tour.service';
import { ErrormsgService } from './error/errormsg.service';
import { LeftMenuComponent } from './menu/left-menu/left-menu.component';
import { AccordionModule } from 'ngx-bootstrap';
var AppModule = (function () {
    function AppModule() {
    }
    return AppModule;
}());
AppModule = __decorate([
    NgModule({
        declarations: [
            AppComponent,
            AppFooterComponent,
            HomeComponent,
            TopMenuComponent,
            DriverComponent,
            TourComponent,
            DriverFormComponent,
            DriverListComponent,
            TourMapComponent,
            TourDriverListComponent,
            LeftMenuComponent
        ],
        imports: [
            BrowserModule,
            FormsModule,
            ReactiveFormsModule,
            HttpModule,
            RouterModule.forRoot(routes, { useHash: false }),
            YagaModule,
            AccordionModule.forRoot()
        ],
        providers: [
            ErrormsgService,
            DriverService,
            TourService,
            BaseRequestOptions
        ],
        bootstrap: [AppComponent]
    })
], AppModule);
export { AppModule };
//# sourceMappingURL=app.module.js.map