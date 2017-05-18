import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourMapComponent } from './tour-map.component';
import { HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourService } from '../tour.service';

describe('TourMapComponent', () => {
  let component: TourMapComponent;
  let fixture: ComponentFixture<TourMapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TourMapComponent ],
      imports: [
        HttpModule,
        YagaModule
      ],
      providers: [
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
