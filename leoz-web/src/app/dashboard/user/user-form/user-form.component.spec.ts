import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  InputTextModule,
  DropdownModule,
  ButtonModule
} from 'primeng/primeng';

import { UserFormComponent } from './user-form.component';
import { UserService } from '../user.service';
import { HttpModule } from '@angular/http';
import { SharedModule } from '../../../shared/shared.module';
import { CoreModule } from '../../../core/core.module';
import { RouterTestingModule } from '@angular/router/testing';

describe( 'UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;

  beforeEach( async( () => {
    TestBed.configureTestingModule( {
      declarations: [ UserFormComponent ],
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        HttpModule,
        SharedModule,
        InputTextModule,
        DropdownModule,
        ButtonModule,
        CoreModule.forRoot(),
      ],
      providers: [
        UserService
      ]
    } )
      .compileComponents();
  } ) );

  beforeEach( () => {
    fixture = TestBed.createComponent( UserFormComponent );
    component = fixture.componentInstance;
    fixture.detectChanges();
  } );

  it( 'should create', () => {
    expect( component ).toBeTruthy();
  } );
} );
