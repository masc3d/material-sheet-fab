import { TestBed, async } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { AppFooterComponent } from './app-footer/app-footer.component';
import { TopMenuComponent } from './menu/top-menu/top-menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BaseRequestOptions, HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourService } from './tour/tour.service';
import { HomeComponent } from './home/home.component';
import { TourComponent } from './tour/tour.component';
import { TourMapComponent } from './tour/tour-map/tour-map.component';
import { TourDriverListComponent } from './tour/tour-driver-list/tour-driver-list.component';
import { APP_BASE_HREF } from '@angular/common';
import { routes } from './app-routing.module';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        AppFooterComponent,
        HomeComponent,
        TopMenuComponent,
        TourComponent,
        TourMapComponent,
        TourDriverListComponent
      ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        RouterModule.forRoot(routes, {useHash: false}),
        YagaModule
      ],
      providers: [
        TourService,
        BaseRequestOptions,
        {provide: APP_BASE_HREF, useValue : '/' }
      ]
    }).compileComponents();
  }));

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

});
