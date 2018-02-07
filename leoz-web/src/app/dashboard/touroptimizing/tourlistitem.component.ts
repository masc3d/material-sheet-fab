import { Component, Input, OnInit } from '@angular/core';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';

import * as moment from 'moment';

import { TourListItem } from '../../core/models/tour-list-item.model';

@Component( {
  selector: 'app-tourlistitem',
  template: `
    <div class="ui-g row-tourlistitem" [@deletedNewChanged]="listItem.state"
         style="display: block; opacity: 0;">
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas {{faIcon}}"></i></div>
      <div class="ui-g-12 ui-lg-2" style="font-weight: bold">{{listItem.id}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fa-sum"></i>&nbsp;S</div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalShipments}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fa-sum"></i>&nbsp;P</div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalPackages}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas fa-balance-scale"></i></div>
      <div class="ui-g-12 ui-lg-1">{{listItem.totalWeight}}</div>
      <div class="ui-g-12 ui-lg-3">{{formattedCreatetime}}</div>
      <div class="ui-g-12 ui-lg-1">&nbsp;</div>
      <div class="ui-g-12 ui-lg-2" style="font-weight: bold">{{listItem.deliverylistId}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="far fa-clock"></i></div>
      <div class="ui-g-12 ui-lg-1">{{listItem.time}}</div>
      <div class="ui-g-12 ui-lg-1 iconBlue"><i class="fas fa-road"></i></div>
      <div class="ui-g-12 ui-lg-3">{{listItem.distance}}</div>
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
      border-radius: 10px;
    }

    .row-tourlistitem > div {
      padding: 0;
      height: 19px;
    }
  ` ],
  animations: [
    trigger( 'deletedNewChanged', [
      state( 'new', style( {
        height: '*',
        opacity: 1,
        display: 'block',
        backgroundColor: '#f1f1f1'
      } ) ),
      state( 'deleted', style( {
        height: 0,
        opacity: 0,
        display: 'none',
        backgroundColor: '#f1f1f1'
      } ) ),
      state( 'changed', style( {
        height: '*',
        opacity: 1,
        display: 'block',
        backgroundColor: '#f1d6dc'
      } ) ),
      transition( 'void => deleted', animate( '500ms ease-out' ) ),
      transition( 'new => deleted', animate( '500ms ease-out' ) ),
      transition( 'void => new', animate( '500ms ease-in' ) ),
      transition( 'deleted => new', animate( '500ms ease-in' ) ),
      transition( 'void => changed', animate( '500ms ease-in' ) ),
      transition( 'changed <=> new', animate( '500ms ease' ) )
    ] )
  ]
} )
export class TourlistitemComponent implements OnInit {

  @Input() listItem: TourListItem;
  @Input() faIcon: string;
  formattedCreatetime: string;

  ngOnInit() {
    this.formattedCreatetime = moment( this.listItem.created ).format( 'DD.MM.YY HH:mm' );
  }

}
