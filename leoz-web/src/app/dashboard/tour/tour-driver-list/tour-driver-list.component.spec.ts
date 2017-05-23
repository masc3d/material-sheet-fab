import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDriverListComponent } from './tour-driver-list.component';
import { HttpModule } from '@angular/http';
import { TourService } from '../tour.service';
import { SharedModule } from '../../../shared/shared.module';
import { DriverService } from '../driver.service';
import { CoreModule } from '../../../core/core.module';

describe('TourDriverListComponent', () => {
  let component: TourDriverListComponent;
  let fixture: ComponentFixture<TourDriverListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TourDriverListComponent ],
      imports: [
        HttpModule,
        SharedModule,
        CoreModule.forRoot(),
      ],
      providers: [
        DriverService,
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourDriverListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
