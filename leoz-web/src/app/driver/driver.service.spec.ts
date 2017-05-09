import { TestBed, inject } from '@angular/core/testing';

import { DriverService } from './driver.service';
import { ErrormsgService } from '../error/errormsg.service';
import { HttpModule } from '@angular/http';

describe('DriverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpModule ],
      providers: [
        DriverService,
        ErrormsgService
      ]
    });
  });

  it('should ...', inject([DriverService], (service: DriverService) => {
    expect(service).toBeTruthy();
  }));
});
