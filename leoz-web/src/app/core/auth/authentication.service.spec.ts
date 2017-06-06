import { TestBed, inject } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { AuthenticationService } from './authentication.service';
import { HttpModule } from '@angular/http';
import { RoleGuard } from './role.guard';

describe('AuthenticationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpModule
      ],
      providers: [
        AuthenticationService,
        RoleGuard]
    });
  });

  it('should ...', inject([AuthenticationService], (service: AuthenticationService) => {
    expect(service).toBeTruthy();
  }));
});
