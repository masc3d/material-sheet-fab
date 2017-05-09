import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BaseRequestOptions, HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { YagaModule } from '@yaga/leaflet-ng2';

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
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { LoginComponent } from './login/login.component';
import { AuthenticationService } from './auth/authentication.service';
import { AuthenticationGuard } from './auth/authentication.guard';

@NgModule({
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
    LeftMenuComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    RouterModule.forRoot(routes, {useHash: true}),
    YagaModule,
    AccordionModule.forRoot()
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    ErrormsgService,
    DriverService,
    TourService,
    BaseRequestOptions
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
