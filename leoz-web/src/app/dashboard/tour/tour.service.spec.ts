import { TestBed, inject } from '@angular/core/testing';

import { TourService } from './tour.service';
import { Http, HttpModule } from '@angular/http';

describe('TourService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpModule ],
      providers: [TourService]
    });
  });

  it('should ...', inject([TourService], (service: TourService) => {
    expect(service).toBeTruthy();
  }));
});
