import { TestBed, inject } from '@angular/core/testing';

import { ErrormsgService } from './errormsg.service';

describe('ErrormsgService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ErrormsgService]
    });
  });

  it('should ...', inject([ErrormsgService], (service: ErrormsgService) => {
    expect(service).toBeTruthy();
  }));
});
