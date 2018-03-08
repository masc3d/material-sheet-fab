import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { takeUntil } from 'rxjs/operators';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { ElectronService } from '../../../core/electron/electron.service';
import PrinterInfo = Electron.PrinterInfo;
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-printer',
  template: `
    <h2>{{'printer-selection' | translate}}</h2>
    {{printerList}}
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class PrinterComponent extends AbstractTranslateComponent implements OnInit {

  printerList: string;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private electronService: ElectronService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();
    this.printerList = '';
    console.log( 'PrinterComponent.constructor()' );
    if (this.electronService.isElectron()) {
      console.log( 'isElectron()' );
      this.electronService.printers$
        .pipe(
          takeUntil( this.ngUnsubscribe )
        )
        .subscribe( ( printers: PrinterInfo[] ) => {
          this.printerList = printers.map( printer => printer.name ).join( ' --- ' );
          console.log( 'this.printerList', this.printerList );
          this.cd.detectChanges();
        } );

      this.electronService.getPrinters();
    }
  }
}
