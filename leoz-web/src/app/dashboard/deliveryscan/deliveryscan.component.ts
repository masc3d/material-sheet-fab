import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import 'rxjs/add/operator/filter';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { KeyUpEventService } from '../../core/key-up-event.service';
import { BrowserCheck } from '../../core/auth/browser-check';
import { Package } from '../../core/models/package.model';
import { Shipment } from '../../core/models/shipment.model';


@Component( {
  selector: 'app-deliveryscan',
  templateUrl: './deliveryscan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class DeliveryscanComponent extends AbstractTranslateComponent implements OnInit {

  @ViewChild( 'scanfield' ) scanfield: ElementRef;

  deliverylistOptions: SelectItem[];
  deliverydateOptions: SelectItem[];
  tourOptions: SelectItem[];

  totalWeight: number;
  freeWeight: number;
  openPackcount: number;
  loadedPackcount: number;
  openWeight: number;
  selectedPackages: Package[];

  deliveryscanForm: FormGroup;

  shipments: Shipment[];

  exportdate: any;
  latestMarkedIndex: number;
  latestDirection: string;

  public scanProgress: number;
  public scanInProgress: boolean;
  private waitingForResults: number;
  private receivedResponses: number;

  public shortScanMsg = '';
  styleShortScanMsg = { 'background-color': '#ffffff', 'color': '#000000' };

  notMicrodoof: boolean;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               private keyUpService: KeyUpEventService,
               private browserCheck: BrowserCheck ) {
    super( translate, cd, () => {
      this.deliverylistOptions = this.createDeliverylistOptions();
      this.deliverydateOptions = this.createDeliverydateOptions();
      this.tourOptions = this.createTourOptions();
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.deliverylistOptions = this.createDeliverylistOptions();
    this.deliverydateOptions = this.createDeliverydateOptions();
    this.tourOptions = this.createTourOptions();

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.waitingForResults = 0;
    this.receivedResponses = 0;

    this.latestMarkedIndex = -1;
    this.latestDirection = 'INIT';

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowDown' && !ev.shiftKey )
      .takeUntil( this.ngUnsubscribe );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowUp' && !ev.shiftKey )
      .takeUntil( this.ngUnsubscribe );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowDown' && ev.shiftKey )
      .takeUntil( this.ngUnsubscribe );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowUp' && ev.shiftKey )
      .takeUntil( this.ngUnsubscribe );

    this.deliveryscanForm = this.fb.group( {
      payload: [ null ],
      selectloadlist: [ null ],
      scanfield: [ null ],
      loadlistnumber: [ { value: '', disabled: true } ],
      printlabel: [ null ],
      basedon: [ 'standard' ],
      basedonscan: [ '' ]
    } );
    this.shipments = [
      {
        deliveryAddress: {
          line1: 'alte Freiheit Werbung',
          street: 'Amsterdamer Str',
          zipCode: '50825',
          city: 'Köln'
        },
        orderId: 84259511468,
        deliveryPos: 1,
        deliveryDate: '24.10.2017',
        deliveryTime: '12:00',
        deliveryStatus: 1,
        deliveryCode: 2,
        parcels: [ {
          parcelNo: 84259511468
        } ]
      },
      {
        deliveryAddress: {
          line1: 'alte Freiheit Werbung',
          street: 'Amsterdamer Str',
          zipCode: '50825',
          city: 'Köln'
        },
        orderId: 84259511468,
        deliveryPos: 1,
        deliveryDate: '24.10.2017',
        deliveryTime: '12:00',
        deliveryStatus: 1,
        deliveryCode: 2,
        parcels: [ {
          parcelNo: 84259511468
        } ]
      }
    ];
  }

  private createDeliverylistOptions(): SelectItem[] {
    const listOptions = [];
    listOptions.push( { label: this.translate.instant( '12345678' ), value: 1 } );
    listOptions.push( { label: this.translate.instant( '12398765' ), value: 0 } );
    return listOptions;
  }

  private createTourOptions(): SelectItem[] {
    const tourOptions = [];
    tourOptions.push( { label: this.translate.instant( '5' ), value: 1 } );
    tourOptions.push( { label: this.translate.instant( '7' ), value: 0 } );
    return tourOptions;
  }

  private createDeliverydateOptions(): SelectItem[] {
    const dateOptions = [];
    dateOptions.push( { label: this.translate.instant( '20.10.2107' ), value: 1 } );
    dateOptions.push( { label: this.translate.instant( '21.10.2017' ), value: 0 } );
    return dateOptions;
  }
}
