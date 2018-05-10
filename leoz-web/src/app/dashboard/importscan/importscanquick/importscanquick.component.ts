import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { Message } from 'primeng/components/common/api';
import { ImportscanquickService } from './importscanquick.service';

@Component( {
  selector: 'app-importscanquick',
  templateUrl: './importscanquick.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class ImportscanquickComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  emptyOrPInt: RegExp = /^[0-9]*$/;

  packs: any[];
  packsLoading$: Observable<boolean>;
  displayWeightCorrections = false;

  public msgs$: Observable<Message[]>;
  public sticky$: Observable<boolean>;

  tourfilterBaseData = true;
  tourfilterDispo = false;
  tourfilterOptimization = false;

  constructor( public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               protected importscanquickService: ImportscanquickService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.packsLoading$ = this.importscanquickService.packsLoading$;

    this.packs = [
      { status: 'ok', packNo: '123456789012', tour: '98765', zip: '12307', weight: '1', dimensions: '1x2x3' },
      { status: 'nok', packNo: '123456789013', tour: '', zip: '95444', weight: '1', dimensions: '1x2x3' }
    ];
  }

  handleChange( evt ) {
    console.log( 'handleChange($event)', evt );
    switch (evt.index) {
      case 1:
        // fetch already scanned at this station
        this.packs = [
          { status: 'ok', packNo: '123456789012', tour: '98765', zip: '12307', weight: '1', dimensions: '1x2x3' },
          { status: 'nok', packNo: '123456789013', tour: '', zip: '95444', weight: '1', dimensions: '1x2x3' }
        ];
        break;
      case 2:
        // fetch inbound for this station
        this.packs = [
          { status: '', packNo: '987654321012', tour: '', zip: '12307', weight: '1', dimensions: '1x2x3' },
          { status: '', packNo: '987654321013', tour: '', zip: '12307', weight: '1', dimensions: '1x2x3' },
          { status: '', packNo: '987654321014', tour: '', zip: '12307', weight: '1', dimensions: '1x2x3' }
        ];
        break;
      default:
        // dummy only for screenies
        this.packs = [
          { status: 'ok', packNo: '123456789012', tour: '98765', zip: '12307', weight: '1', dimensions: '1x2x3' },
          { status: 'nok', packNo: '123456789013', tour: '', zip: '95444', weight: '1', dimensions: '1x2x3' }
        ];
        break;
    }
    this.cd.detectChanges();
  }

  changeTourFilter( checked, type: string ) {
    console.log( 'changeTourFilter', checked, type );
  }

  showWeightCorrections() {
    this.displayWeightCorrections = true;
  }
}
