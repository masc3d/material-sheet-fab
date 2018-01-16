import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

@Component( {
  selector: 'app-packages-row',
  template: `
    <div class="ui-g-12 ui-g-nopad" [class]="oddBgColor">
      <div class="ui-g-12 ui-lg-3 ui-g-nopad">{{pack.packageNo}}</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">{{pack.weight}}</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">{{pack.length}}</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">{{pack.witdh}}</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">{{pack.height}}</div>
      <div class="ui-g-12 ui-lg-3 ui-g-nopad">{{pack.type}}</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">&nbsp;</div>
      <div class="ui-g-12 ui-lg-1 ui-g-nopad">{{pack.orderPos}}</div>
    </div>
  `,
  styles: [ `
    .oddBgColor > div {
      background-color: #adadaf;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class PackagesRowComponent implements OnInit {
  @Input() pack: { packageNo: string, weight: string, length: string, witdh: string, height: string, type: string, orderPos: string };

  @Input() index: number;

  oddBgColor: string;

  ngOnInit() {
    this.oddBgColor = this.index % 2 === 1 ? 'oddBgColor' : '';
  }

}
