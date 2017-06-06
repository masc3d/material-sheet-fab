import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PanelMenuModule } from 'primeng/primeng';

import { DashboardComponent } from './dashboard.component';
import { CoreModule } from '../core/core.module';
import { SharedModule } from '../shared/shared.module';
import { LeftMenuComponent } from './left-menu/left-menu.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DashboardComponent, LeftMenuComponent ],
      imports: [
        RouterTestingModule,
        SharedModule,
        PanelMenuModule,
        CoreModule.forRoot(),
        DashboardRoutingModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
