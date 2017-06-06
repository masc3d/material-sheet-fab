import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MsgBoxComponent } from './msg-box.component';
import { SharedModule } from '../shared.module';
import { TranslateModule } from '../../core/translate/translate.module';
import { MsgService } from './msg.service';
import { Msg } from './msg.model';

describe('MsgBoxComponent', () => {
  let component: MsgBoxComponent;
  let fixture: ComponentFixture<MsgBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsgBoxComponent ],
      imports: [
        TranslateModule
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsgBoxComponent);
    component = fixture.componentInstance;
    component.msg = <Msg> {text: '', alertStyle: ''};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
