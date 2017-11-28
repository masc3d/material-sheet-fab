import { ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AuthenticationService } from './auth/authentication.service';
import { AuthenticationGuard } from './auth/authentication.guard';
import { Translation } from './translate/translation';
import { TranslateService } from './translate/translate.service';
import { RoleGuard } from './auth/role.guard';
import { BrowserCheck } from './auth/browser-check';
import { KeyUpEventService } from './key-up-event.service';
import { SoundService } from './sound.service';
import { PrintingService } from './printing/printing.service';
import { LoadinglistReportingService } from './reporting/loadinglist-reporting.service';
import { BagscanReportingService } from './reporting/bagscan-reporting.service';
import { ApiKeyHeaderInterceptor } from './api-key-header.interceptor';
import { MockHttpInterceptor } from './mock-http.interceptor';
import { BagscanGuard } from './auth/bagscan.guard';
import { InetConnectionService } from './inet-connection.service';
import { PreloadSelectedModules } from './PreloadSelectedModules';

@NgModule( {
  imports: [
    CommonModule
  ],
  declarations: []
} )
export class CoreModule {

  static forRoot(): ModuleWithProviders {
    return {
      ngModule: CoreModule,
      providers: [
        { provide: HTTP_INTERCEPTORS, useClass: ApiKeyHeaderInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: MockHttpInterceptor, multi: true },
        AuthenticationService,
        AuthenticationGuard,
        PreloadSelectedModules,
        KeyUpEventService,
        PrintingService,
        LoadinglistReportingService,
        BagscanReportingService,
        SoundService,
        RoleGuard,
        BagscanGuard,
        Translation,
        TranslateService,
        InetConnectionService,
        BrowserCheck]
    };
  }

  constructor( @Optional() @SkipSelf() parentModule: CoreModule ) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only' );
    }
  }
}
