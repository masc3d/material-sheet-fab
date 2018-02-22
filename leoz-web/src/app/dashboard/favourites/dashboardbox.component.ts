import { Component, Input } from '@angular/core';

@Component( {
  selector: 'app-dashboardbox',
  styles: [`
        .dashboardBox {
            border: 1px solid #ffffff;
            background-color: #efefef8c;
            width: 210px;
            height: 95px;
            text-align: center;
            padding-top: 20px;
            cursor: pointer;
        }
        .dashboardBox:hover {
            box-shadow: 1px 1px 5px #999;
            transition: all 200ms ease;
            transform: scale(1.03);
        }
        .dashboardBox i {
            display: block;
            font-size: 36px;
            color: #194b7d;
            cursor: pointer;
        }
        .dashboardBox label {
            display: block;
            padding-top: 16px;
            font-size: 18px;
            font-weight: bold;
            color: #194b7d;
            cursor: pointer;
        }
    `],
  template: `
    <div class="dashboardBox">
        <i [ngClass]="faClass"></i>
        <label>{{label}}</label>
    </div>
    `
} )
export class DashboardboxComponent {
  @Input() faClass: string;
  @Input() label: string;
}
