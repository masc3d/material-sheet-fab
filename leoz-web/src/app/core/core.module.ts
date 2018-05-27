import { ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApiKeyHeaderInterceptor } from './api-key-header.interceptor';
import { MockHttpInterceptor } from './mock-http.interceptor';

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
        { provide: HTTP_INTERCEPTORS, useClass: MockHttpInterceptor, multi: true }
        ]
        // AuthenticationService,
        // AuthenticationGuard,
        // BagscanGuard,
        // LabelReportingService,
        // BrowserCheck,
        // ElectronService,
        // InetConnectionService,
        // KeyUpEventService,
        // LoadinglistReportingService,
        // PreloadSelectedModules,
        // PrintingService,
        // RoleGuard,
        // SseService,
        // SoundService,
        // StoplistReportingService,
        // TranslateService,
        // Translation,
        // WorkingdateService]
    };
  }

  constructor( @Optional() @SkipSelf() parentModule: CoreModule ) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only' );
    }
  }
}
