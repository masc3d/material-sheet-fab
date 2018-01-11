import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { InetConnectionService } from '../../core/inet-connection.service';
import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';

@Component( {
  selector: 'app-statusbar',
  template: `
    <div id="statusbar">
      <span *ngIf="isOnline">
        <i class="fas fa-wifi" style="color: #00a200;"></i> {{'connected_to_leoz' | translate }}
      </span>
      <span *ngIf="!isOnline">
        <i class="fas fa-ban" style="color: red;"></i> {{'not_connected_to_leoz' | translate }}
      </span>
    </div>
  `,
  styles: [ `
    #statusbar {
      position: fixed;
      left: 0;
      bottom: 0;
      height: 18px;
      padding: 4px;
      width: 100%;
      background-color: #e0e0e0;
      color: #555555;
      z-index: 1001;
      border: 0;
      border-top: 1px solid #9a9797;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class StatusbarComponent extends AbstractTranslateComponent implements OnInit {

  isOnline = true;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               private ics: InetConnectionService ) {
    super( translate, cd );
  }

  ngOnInit() {
    super.ngOnInit();
    this.ics.isOnline$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( (isOnline: boolean) => {
        this.isOnline = isOnline;
        this.cd.markForCheck();
      } );
  }

}
