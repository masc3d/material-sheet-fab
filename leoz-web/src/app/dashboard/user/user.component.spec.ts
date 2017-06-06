import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpModule } from '@angular/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
DataTableModule,
InputTextModule,
DropdownModule,
ButtonModule } from 'primeng/primeng';

import { UserComponent } from './user.component';
import { UserService } from './user.service';
import { UserFormComponent } from './user-form/user-form.component';
import { UserListComponent } from './user-list/user-list.component';
import { SharedModule } from '../../shared/shared.module';
import { CoreModule } from '../../core/core.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;
  let userService: UserService;
  let spy: jasmine.Spy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        UserComponent,
        UserFormComponent,
        UserListComponent
      ],
      imports: [
        RouterTestingModule,
        HttpModule,
        BrowserAnimationsModule,
        SharedModule,
        DataTableModule,
        InputTextModule,
        DropdownModule,
        ButtonModule,
        CoreModule.forRoot()
      ],
      providers: [
        UserService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    userService = fixture.debugElement.injector.get(UserService);
  });

  it('should create', () => {
    spy = spyOn(userService, 'getUsers');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
