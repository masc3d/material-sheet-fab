import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PanelMenuModule } from 'primeng/primeng';

import { LeftMenuComponent } from './left-menu.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedModule } from '../../shared/shared.module';
import { CoreModule } from '../../core/core.module';

describe( 'LeftMenuComponent', () => {
  let component: LeftMenuComponent;
  let fixture: ComponentFixture<LeftMenuComponent>;

  beforeEach( async( () => {
    TestBed.configureTestingModule( {
      imports: [
        RouterTestingModule,
        SharedModule,
        PanelMenuModule,
        CoreModule.forRoot() ],
      declarations: [ LeftMenuComponent ]
    } )
      .compileComponents();
  } ) );

  beforeEach( () => {
    fixture = TestBed.createComponent( LeftMenuComponent );
    component = fixture.componentInstance;
    fixture.detectChanges();
  } );

  it( 'should create', () => {
    expect( component ).toBeTruthy();
  } );
} );
