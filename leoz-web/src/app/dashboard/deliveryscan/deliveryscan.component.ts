import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import 'rxjs/add/operator/filter';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { KeyUpEventService } from '../../core/key-up-event.service';
import { BrowserCheck } from '../../core/auth/browser-check';
import { Package } from '../../core/models/package.model';
import { Deliveryscan } from '../../core/models/deliveryscan.model';


@Component( {
  selector: 'app-deliveryscan',
  templateUrl: './deliveryscan.component.html'
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

  shipments: Deliveryscan[];

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
               private keyUpService: KeyUpEventService,
               private browserCheck: BrowserCheck ) {
    super( translate, () => {
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
    this.shipments = [ {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
    },
      {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
      },
      {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
      },
      {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
      },
      {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
      },
      {
        deliverypos: 1,
        deliveryline1: 'alte Freiheit Werbung',
        deliverystreet: 'Amsterdamer Str',
        deliveryzip: '50825',
        deliverycity: 'Köln',
        shipmentno: '84259511468',
        parcelno: '84259511468',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        deliverystatus: 1,
        deliverycode: 2
      }];
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
