import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BaseRequestOptions, HttpModule } from '@angular/http';

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
import { TranslatePipe } from './translate/translate.pipe';
import { TRANSLATION_PROVIDERS } from './translate/translation';
import { TranslateService } from './translate/translate.service';

@NgModule({
  declarations: [
    AppComponent,
    TopMenuComponent,
    LoginComponent,
    TranslatePipe
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpModule,
    HomeModule,
    TourModule,
    UserModule,
    DashboardModule,
    AppRoutingModule
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    BaseRequestOptions,
    TRANSLATION_PROVIDERS,
    TranslateService
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
