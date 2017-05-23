import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourComponent } from './tour.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourService } from './tour.service';
import { TourRoutingModule } from './tour-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { CoreModule } from '../../core/core.module';
import { DriverService } from './driver.service';

describe('TourComponent', () => {
  let component: TourComponent;
  let fixture: ComponentFixture<TourComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TourComponent,
        TourDriverListComponent,
        TourMapComponent
      ],
      imports: [
        HttpModule,
        YagaModule,
        SharedModule,
        CoreModule.forRoot(),
        TourRoutingModule
      ],
      providers: [
        DriverService,
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
