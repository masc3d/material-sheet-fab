import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { TopMenuComponent } from './menu/top-menu/top-menu.component';
import { LoginComponent } from './login/login.component';
import { AuthenticationService } from './auth/authentication.service';
import { AuthenticationGuard } from './auth/authentication.guard';
import { TourModule} from './tour/tour.module';
import { UserModule} from './user/user.module';
import { AppRoutingModule } from './app-routing.module';
import { DashboardModule } from './dashboard/dashboard.module';
import { HomeModule } from './home/home.module';
import { TranslateModule } from './translate/translate.module';

@NgModule({
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpModule,
    HomeModule,
    TourModule,
    UserModule,
    DashboardModule,
    TranslateModule,
    AppRoutingModule,
  ],
  declarations: [
    AppComponent,
    TopMenuComponent,
    LoginComponent
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
