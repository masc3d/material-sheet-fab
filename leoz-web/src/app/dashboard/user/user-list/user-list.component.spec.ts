import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserListComponent } from './user-list.component';
import { HttpModule } from '@angular/http';
import { UserService } from '../user.service';
import { SharedModule } from '../../../shared/shared.module';
import { CoreModule } from '../../../core/core.module';
import { UserComponent } from '../user.component';
import { UserFormComponent } from '../user-form/user-form.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ButtonModule, DataTableModule, DropdownModule } from 'primeng/primeng';

describe('UserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;
  let userService: UserService;
  let spy: jasmine.Spy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserComponent,
        UserFormComponent,
        UserListComponent ],
      imports: [ RouterTestingModule,
        HttpModule,
        SharedModule,
        DataTableModule,
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
    fixture = TestBed.createComponent(UserListComponent);
    component = fixture.componentInstance;
    userService = fixture.debugElement.injector.get(UserService);
  });

  it('should create', () => {
    spy = spyOn(userService, 'getUsers');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
