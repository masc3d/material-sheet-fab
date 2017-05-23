import { TestBed, inject } from '@angular/core/testing';

import { DriverService } from './driver.service';
import { HttpModule } from '@angular/http';

describe('DriverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpModule ],
      providers: [
        DriverService
      ]
    });
  });

  it('should ...', inject([DriverService], (service: DriverService) => {
    expect(service).toBeTruthy();
  }));
});
