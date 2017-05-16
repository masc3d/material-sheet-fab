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
import { TourComponent } from './tour/tour.component';
import { DriverService } from './tour/driver.service';
import { TourMapComponent } from './tour/tour-map/tour-map.component';
import { TourDriverListComponent } from './tour/tour-driver-list/tour-driver-list.component';
import { TourService } from './tour/tour.service';
import { LeftMenuComponent } from './menu/left-menu/left-menu.component';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { LoginComponent } from './login/login.component';
import { AuthenticationService } from './auth/authentication.service';
import { AuthenticationGuard } from './auth/authentication.guard';
import { UserComponent} from './user/user.component';
import { UserFormComponent} from './user/user-form/user-form.component';
import { UserListComponent} from './user/user-list/user-list.component';
import { UserService} from './user/user.service';

@NgModule({
  declarations: [
    AppComponent,
    AppFooterComponent,
    HomeComponent,
    TopMenuComponent,
    TourComponent,
    UserComponent,
    UserFormComponent,
    UserListComponent,
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
    DriverService,
    UserService,
    TourService,
    BaseRequestOptions
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
