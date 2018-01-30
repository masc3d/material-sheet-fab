import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { TourListItem } from '../../core/models/tour-list-item.model';
import * as moment from 'moment';

@Component( {
  selector: 'app-tourlistitem',
  template: `
    <div class="ui-g row-tourlistitem">
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas {{faIcon}}"></i></div>
      <div class="ui-g-12 ui-lg-3" style="font-weight: bold">{{listItem.id}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fa-sum"></i>&nbsp;S</div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalShipments}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fa-sum"></i>&nbsp;P</div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalPackages}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas fa-balance-scale"></i></div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalWeight}}</div>
      <div class="ui-g-12 ui-lg-2">&nbsp;</div>
      <div class="ui-g-12 ui-lg-1">&nbsp;</div>
      <div class="ui-g-12 ui-lg-5">{{formattedCreatetime}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="far fa-clock"></i></div>
      <div class="ui-g-12 ui-lg-1">{{listItem.time}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas fa-road"></i></div>
      <div class="ui-g-12 ui-lg-1">{{listItem.distance}}</div>
      <div class="ui-g-12 ui-lg-1">
        <span *ngIf="listItem.optimized; else notOptimized">
          <i class="fas fa-sync" style="color: #00a200;"></i>
        </span>
        <ng-template #notOptimized>
          <i class="fas fa-sync" style="color: #ff0000;"></i>
        </ng-template>

      </div>
      <div class="ui-g-12 ui-lg-1">
        <p-checkbox name="optimize" [(ngModel)]="listItem.selected" binary="true"></p-checkbox>
      </div>
    </div>
  `,
  styles: [ `
    .row-tourlistitem {
      font-size: 14px;
      padding: 5px;
      border-bottom: 2px solid #D5D5D5;
      background: linear-gradient(to bottom, white, #f1f1f1);
      border-radius: 10px;
    }

    .row-tourlistitem > div {
      padding: 0;
      height: 19px;
      background: transparent;
    }
  ` ]
} )
export class TourlistitemComponent implements OnInit{
  @Input() listItem: TourListItem;

  @Input() faIcon: string;
  formattedCreatetime: string;

  ngOnInit() {
    this.formattedCreatetime = moment(this.listItem.created).format( 'YYYY-MM-DD HH:mm:ss' );
  }

}
