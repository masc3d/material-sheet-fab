import { ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from './auth/authentication.service';
import { AuthenticationGuard } from './auth/authentication.guard';
import { Translation } from './translate/translation';
import { TranslateService } from './translate/translate.service';
import { RoleGuard } from './auth/role.guard';

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
        AuthenticationService,
        AuthenticationGuard,
        // AdminRoleGuard,
        RoleGuard,
        // UserRoleGuard,
        // DriverRoleGuard,
        Translation,
        TranslateService ]
    };
  }

  constructor( @Optional() @SkipSelf() parentModule: CoreModule ) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only' );
    }
  }
}
