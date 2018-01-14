import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component( {
  selector: 'app-tourlistitem',
  template: `
    <div class="ui-g row-tourlistitem">
      <div class="ui-g-12 ui-lg-1">&nbsp;</div>
      <div class="ui-g-12 ui-lg-3">&nbsp;</div>
      <div class="ui-g-12 ui-lg-1"><i class="fa-sum" style="color: #184b7d;"></i>&nbsp;S</div>
      <div class="ui-g-12 ui-lg-1">{{totalShipments}}</div>
      <div class="ui-g-12 ui-lg-1"><i class="fa-sum" style="color: #184b7d;"></i>&nbsp;P</div>
      <div class="ui-g-12 ui-lg-1">{{totalPackages}}</div>
      <div class="ui-g-12 ui-lg-1"><i class="fas fa-balance-scale" style="color: #184b7d;"></i></div>
      <div class="ui-g-12 ui-lg-3">{{totalWeight}}</div>
      <div class="ui-g-12 ui-lg-1"><i class="fas {{faIcon}}" style="color: #184b7d;"></i></div>
      <div class="ui-g-12 ui-lg-3" style="font-weight: bold">{{id}}</div>
      <div class="ui-g-12 ui-lg-6">&nbsp;</div>
      <div class="ui-g-12 ui-lg-1">
                    <span *ngIf="optimized; else notOptimized">
                      <i class="fas fa-sync" style="color: #00a200;"></i>
                    </span>
        <ng-template #notOptimized>
          <i class="fas fa-sync" style="color: #ff0000;"></i>
        </ng-template>
      </div>
      <div class="ui-g-12 ui-lg-1">
        <p-checkbox name="optimize" value="optimize"></p-checkbox>
      </div>
      <div class="ui-g-12 ui-lg-4">&nbsp;</div>
      <div class="ui-g-12 ui-lg-1"><i class="far fa-clock" style="color: #184b7d;"></i></div>
      <div class="ui-g-12 ui-lg-1">{{time}}</div>
      <div class="ui-g-12 ui-lg-1"><i class="fas fa-road" style="color: #184b7d;"></i></div>
      <div class="ui-g-12 ui-lg-5">{{distance}}</div>
    </div>
  `,
  styles: [`
    .row-tourlistitem {
      font-size: 14px;
      padding: 5px;
      border-bottom: 2px solid #D5D5D5;
      background: linear-gradient(to bottom, white, #f1f1f1);
      border-radius: 10px;
    }

    .row-tourlistitem > div {
      padding: 0;
      height: 15px;
      background: transparent;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TourlistitemComponent {

  @Input() id: number;
  @Input() faIcon: string;
  @Input() totalShipments: number;
  @Input() totalPackages: number;
  @Input() totalWeight: number;
  @Input() optimized: boolean;
  @Input() time: string;
  @Input() distance: number;
}
