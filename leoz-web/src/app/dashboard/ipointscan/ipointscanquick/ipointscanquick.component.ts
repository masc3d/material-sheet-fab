import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-ipointscanquick',
  templateUrl: './ipointscanquick.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [ `
    .chargeLvlGreen {
      color: white;
      background-color: green;
    }

    .chargeLvlRed {
      color: white;
      background-color: red;
    }

    .chargeLvlYellow {
      color: black;
      background-color: yellow;
    }
  ` ]
  // styles: [ '.ui-g-12 { border: 1px solid green; }' ]
} )
export class IpointscanquickComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  ipointscanquickForm: FormGroup;

  constructor( private fb: FormBuilder,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               public router: Router ) {
    super( translate, cd, msgService);
  }

  ngOnInit() {
    super.ngOnInit();

    this.ipointscanquickForm = this.fb.group( {
       scanfield: [ null ],
       msgfield: [ null ],
       printlabel: [ null ]
    } );
  }
}
