import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { InetConnectionService } from '../../core/inet-connection.service';
import { Subject } from 'rxjs/Subject';

@Component( {
  selector: 'app-statusbar',
  template: `
    <div id="statusbar">
      <span *ngIf="isOnline">
        <i class="fa fa-wifi" style="color: #00a200;"></i> online
      </span>
      <span *ngIf="!isOnline">
        <i class="fa fa-ban" style="color: red;"></i> offline
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
      /*background-color: #e0e0e0;*/
      background-color: transparent;
      color: #555555;
      z-index: 1001;
      border: 0;
      /*border-top: 1px solid #9a9797;*/
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class StatusbarComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<void> = new Subject<void>();

  isOnline = true;

  constructor( private cd: ChangeDetectorRef,
               private ics: InetConnectionService ) {
  }

  ngOnInit() {
    this.ics.isOnline$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( (isOnline: boolean) => {
        this.isOnline = isOnline;
        this.cd.markForCheck();
      } );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
