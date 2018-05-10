import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Observable ,  BehaviorSubject } from 'rxjs';
import { distinctUntilChanged, filter, takeUntil } from 'rxjs/operators';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { KeyUpEventService } from '../../../core/key-up-event.service';
import { SoundService } from '../../../core/sound.service';
import { PrintingService } from '../../../core/printing/printing.service';
import { LoadinglistReportingService } from '../../../core/reporting/loadinglist-reporting.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { LoadinglistscanService } from './loadinglistscan.service';
import { TYPE_VALUABLE } from '../../../core/constants';
import { checkdigitInt25 } from '../../../core/math/checkdigitInt25';
import { MsgService } from '../../../shared/msg/msg.service';
import { roundDecimals } from '../../../core/math/roundDecimals';
import { Loadinglist } from '../../../core/models/loadinglist.model';
import { Exportparcel } from '../../../core/models/exportparcel.model';

interface ScanMsg {
  packageId: string;
  type: string;
  bgColor: string;
  txtColor: string;
  shortMsgText: string;
  msgText: string;
  sound: string;
}

@Component( {
  selector: 'app-loadinglistscan',
  templateUrl: './loadinglistscan.component.html',
  providers: [ DatePipe ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class LoadinglistscanComponent extends AbstractTranslateComponent implements OnInit {

  @ViewChild( 'scanfield' ) scanfield: ElementRef;

  styleWeightExceeded = {};
  styleScanMsgListBG = { 'background-color': '#ffffff', 'color': '#000000' };
  styleScanMsgfieldBG = { 'background-color': '#ffffff', 'color': '#000000' };

  dateFormat: string;

  private actionMsgListSubject = new BehaviorSubject<string[]>( [ 'noAction' ] );
  public actionMsgList$ = this.actionMsgListSubject.asObservable().pipe( distinctUntilChanged() );
  private actionMsgListTmp: string[];

  public scanMsgsSubject = new BehaviorSubject<ScanMsg[]>( [] );
  public scanMsgs$ = this.scanMsgsSubject.asObservable().pipe( distinctUntilChanged() );
  public shortScanMsg = '';

  activeLoadinglist: Loadinglist;
  allLoadlists: Loadinglist[];
  allLoadlistsWithEmptyFirstEntry: Loadinglist[];
  totalWeight: number;
  freeWeight: number;
  openPackcount: number;
  loadedPackcount: number;
  openWeight: number;
  selectedParcels: Exportparcel[];

  loadinglistscanForm: FormGroup;
  public openParcels$: Observable<Exportparcel[]>;
  public loadedParcels$: Observable<Exportparcel[]>;
  private loadedParcels: Exportparcel[];
  exportdate: any;

  latestDirection: string;
  public scanProgress: number;
  public scanInProgress: boolean;
  private waitingForResults: number;
  private receivedResponses: number;

  private actualScanMsgs: ScanMsg[];
  notMicrodoof: boolean;

  constructor( private fb: FormBuilder,
               private loadinglistService: LoadinglistscanService,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private datePipe: DatePipe,
               private keyUpService: KeyUpEventService,
               private soundService: SoundService,
               private reportingService: LoadinglistReportingService,
               private printingService: PrintingService,
               private browserCheck: BrowserCheck ) {
    super( translate, cd, msgService, () => {
      this.exportdate = this.initExportdate();
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.scanProgress = 0;
    this.scanInProgress = false;
    this.waitingForResults = 0;
    this.receivedResponses = 0;
    this.actualScanMsgs = [];

    this.openParcels$ = this.loadinglistService.openParcels$;
    this.loadedParcels$ = this.loadinglistService.loadedParcels$;
    this.selectedParcels = [];

    this.latestDirection = 'INIT';
    this.totalWeight = null;
    this.freeWeight = null;
    this.openPackcount = null;
    this.loadedPackcount = null;
    this.openWeight = null;

    this.exportdate = this.initExportdate();

    this.loadinglistscanForm = this.fb.group( {
      payload: [ null ],
      selectloadlist: [ null ],
      scanfield: [ null ],
      loadlistnumber: [ { value: '', disabled: true } ],
      printlabel: [ null ],
      basedon: [ 'actuallist' ]
    } );

    this.keyUpService.onKeyUp( '+', this.ngUnsubscribe, this.scanPackNos, this );

    this.loadinglistService.openParcels$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( parcels: Exportparcel[] ) => {
        this.openStats( parcels );
      } );

    this.loadinglistService.activeLoadinglist$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( activeLoadinglist: Loadinglist ) => {
        this.activeLoadinglist = activeLoadinglist;
        this.cd.detectChanges();
      } );

    this.loadinglistService.loadedParcels$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( loadedParcels: Exportparcel[] ) => {
        this.loadedParcels = loadedParcels;
        this.calculateWeights();
        this.cd.detectChanges();
      } );

    this.loadinglistService.allLoadlists$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( allLoadlists: Loadinglist[] ) => {
        this.allLoadlists = allLoadlists;
        this.allLoadlistsWithEmptyFirstEntry = [ <Loadinglist> {
          label: null,
          loadlistNo: null,
          orders: []
        }, ...allLoadlists ];
      } );

    this.actionMsgList$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( actionMsgList: string[] ) => {
        this.actionMsgListTmp = actionMsgList;
      } );

    this.loadinglistService.getAllLoadinglists();
    this.loadinglistService.getOpenParcels();
    this.resetScanMsgs();
  }

  isValuable( parcel: Exportparcel ) {
    return parcel.typeOfPackaging === TYPE_VALUABLE;
  }

  private resetScanMsgs() {
    this.actualScanMsgs = [ {
      packageId: '',
      type: 'neutral',
      bgColor: '#ffffff',
      txtColor: '#000000',
      shortMsgText: '',
      msgText: '',
      sound: ''
    } ];
    this.displayScanMsgs();
  }

  private displayScanMsgs() {
    if (this.actualScanMsgs.length === 1) {
      const scanMsg = this.actualScanMsgs[ 0 ];
      this.displayScanMsg( scanMsg.bgColor, scanMsg.txtColor, scanMsg.shortMsgText, [ scanMsg ] );
      this.soundService.play( scanMsg.sound )
    } else if (this.actualScanMsgs.length > 1) {
      const errorMsgs = this.actualScanMsgs.filter( ( scanMsg: ScanMsg ) => scanMsg.type === 'error' );
      if (errorMsgs.length > 0) {
        this.displayScanMsg( 'red', '#ffffff', 'noScan', errorMsgs );
        this.soundService.play( 'critical' );
      } else {
        this.displayScanMsg( 'green', '#ffffff', '', [] );
        this.soundService.play( 'ding' );
      }
    }
  }

  private displayScanMsg( bgColor: string, txtColor: string, shortMsgText: string, msgTexts: ScanMsg[] ) {
    this.styleScanMsgfieldBG = { 'background-color': bgColor, 'color': txtColor };
    this.styleScanMsgListBG = { 'background-color': bgColor, 'color': txtColor };
    this.scanMsgsSubject.next( msgTexts );
    this.shortScanMsg = shortMsgText;
  }

  private addScanMsg( packageId: string, type: string, bgColor: string, txtColor: string,
                      shortMsgText: string, msgText: string, sound: string ) {
    this.actualScanMsgs.push( {
      packageId: packageId,
      type: type,
      bgColor: bgColor,
      txtColor: txtColor,
      shortMsgText: shortMsgText,
      msgText: msgText,
      sound: sound
    } );
  }

  public newLoadlist() {
    this.loadinglistService.newLoadlist();
    this.resetPayloadField();
    this.loadinglistscanForm.get( 'selectloadlist' ).patchValue( null );
    this.actionMsgListSubject.next( [ 'actionNewLoadlist' ] );
    this.resetScanMsgs();
    this.loadinglistService.getLoadedParcels( null );
  }

  public changePayload() {
    this.actionMsgListSubject.next( [ ...this.actionMsgListTmp, 'actionChangePayload' ] );
    this.calculateWeights();
  }

  public scan( waitingForResults: number, scanFunction: Function ) {
    // check if loadlist is set
    if (this.activeLoadinglist.loadinglistNo) {
      if (!this.scanInProgress) {
        // flag scan in progress with count of selected packages to wait for results
        this.scanProgress = 0;
        this.scanInProgress = true;
        this.receivedResponses = 0;
        this.waitingForResults = waitingForResults;
        this.resetScanMsgs();
        this.actualScanMsgs = [];
        scanFunction();
        this.loadinglistscanForm.get( 'scanfield' ).patchValue( '' );
        this.loadinglistscanForm.get( 'scanfield' ).disable();
      }
    } else {
      this.resetScanMsgs();
      this.actualScanMsgs = [];
      this.handleError( '', 'no activeLoadinglist' );
      this.displayScanMsgs();
      this.loadinglistscanForm.get( 'scanfield' ).patchValue( '' );
    }
  }

  public scanSingleParcel() {
    this.scan( 1,
      () => this.scanParcelNo( this.loadinglistscanForm.get( 'scanfield' ).value )
    );
  }


  private padStart( inputString: string, targetLength: number, padString: string ): string {
    while (inputString.length < targetLength) {
      inputString = padString + inputString;
    }
    return inputString;
  }

  private addCheckdigit( someNo: number ): string {
    const checkdigit = checkdigitInt25( `${someNo}` );
    return this.padStart( `${someNo}${checkdigit}`, 12, '0' );
  }

  public scanPackNos() {
    this.scan( this.selectedParcels.length,
      () => this.selectedParcels.forEach(
        ( parcel: Exportparcel ) => this.scanParcelNo( this.addCheckdigit( parcel.parcelNo ) )
      )
    );
  }

  private handleSuccess( sucessType: string ) {
    switch (sucessType) {
      case 'Parcel already scanned':
        this.addScanMsg( '', 'success', 'green', '#ffffff', 'alreadyScanned',
          'noAlreadyScanned', 'chord' );
        break;
      case 'Loadinglist changed':
        this.addScanMsg( '', 'success', 'green', '#ffffff', 'alreadyScanned',
          'transferedToOtherLoadlist', 'ding' );
        break;
      default:
        this.addScanMsg( '', 'success', 'green', '#ffffff', '', sucessType, 'ding' );
        break;
    }
  }

  private handleError( parcelNo: string, errorType: string ) {
    switch (errorType) {
      case 'invalid senddate':
        // Umbuchung funktioniert, zu testen: wenn ausgeliefert, .....
        this.addScanMsg( parcelNo, 'error', 'red', '#ffffff', 'noScan',
          'invalidSenddate',
          'critical' );
        break;
      case 'valore':
        // muss noch geprüft werden, wenn Webservice korrigiert ist
        this.addScanMsg( parcelNo, 'error', 'red', '#ffffff', 'noScan',
          'noValoreScan', 'critical' );
        break;
      default:
        this.addScanMsg( parcelNo, 'error', 'red', '#ffffff', 'noScan',
          errorType, 'critical' );
        break;
    }
  }

  public scanParcelNo( parcelNo: string ) {
    this.loadinglistService.scanPack( parcelNo, this.activeLoadinglist.label )
      .subscribe( ( response: HttpResponse<any> ) => {
          console.log( 'response', response );
          switch (response.status) {
            case 200:
              this.handleSuccess( response.body.title );
              this.loadinglistService.getOpenParcels();
              this.loadinglistService.getLoadedParcels( this.activeLoadinglist.label );
              this.loadinglistService.getAllLoadinglists();
              this.calculateWeights();
              break;
            default:
              console.log( response );
              break;
          }
          this.receivedResponse();
        },
        ( error: HttpErrorResponse ) => {
          switch (error.status) {
            case 400:
            case 404:
              this.handleError( parcelNo, error.error[ 'title' ] );
              break;
            default:
              console.log( error );
              break;
          }
          this.receivedResponse();
        } );
  }

  private receivedResponse() {
    this.receivedResponses += 1;
    this.scanProgress = Math.round( (this.receivedResponses / this.waitingForResults) * 100 );
    if (this.waitingForResults === this.receivedResponses) {
      // all responses received
      this.displayScanMsgs();
      this.scanInProgress = false;
      this.loadinglistscanForm.get( 'scanfield' ).enable();
      this.scanfield.nativeElement.focus();
    }
  }

  private calculateWeights() {
    this.loadedPackcount = this.loadedParcels.length;
    this.totalWeight = this.loadinglistService.sumWeights( this.loadedParcels );
    this.freeWeight = this.loadinglistscanForm.get( 'payload' ).value
      ? roundDecimals( this.loadinglistscanForm.get( 'payload' ).value - this.totalWeight )
      : null;
    this.styleWeightExceeded = (this.freeWeight && this.freeWeight < 0) ? { 'color': 'red' } : {};
  }

  private openStats( parcels: Exportparcel[] ) {
    this.openPackcount = parcels.length;
    this.openWeight = this.loadinglistService.sumWeights( parcels );
  }

  private initExportdate() {
    const d = new Date();
    d.setHours( d.getHours() - 5 );
    return this.datePipe.transform( d, this.dateFormat );
  }

  public selectLoadlist( loadinglist: Loadinglist ) {
    if (loadinglist) {
      this.loadinglistService.getLoadedParcels( loadinglist.label );
      this.loadinglistService.setActiveLoadinglist( loadinglist );
      this.resetPayloadField();
      this.actionMsgListSubject.next( [ 'actionChangeLoadlist' ] );
      this.resetScanMsgs();
      this.calculateWeights();
    }
  }

  private resetPayloadField() {
    this.loadinglistscanForm.get( 'payload' ).patchValue( null );
  }

  preview() {
    this.reporting( false );
  }

  saving() {
    this.reporting( true );
  }

  printing() {
    this.reporting( true );
  }

  reporting( saving: boolean ) {
    if (this.loadinglistscanForm.get( 'basedon' ).value === 'alllists') {
      this.loadinglistService.getAllLoadinglistsWithParcels()
        .subscribe( ( loadinglists: Loadinglist[] ) => {
          this.createReport(
            loadinglists.filter( ( loadinglist: Loadinglist ) => loadinglist.loadinglistType === 'NORMAL' ),
            saving );
        } );
    } else if (this.activeLoadinglist && this.activeLoadinglist.label.length > 0) {
      this.loadinglistService.getLoadinglist( this.activeLoadinglist.label )
        .subscribe( ( loadinglist: Loadinglist ) => {
          this.createReport( [ loadinglist ], saving );
        } );
    }
  }

  private createReport( loadinglists: Loadinglist[], saving: boolean ) {
    const filename = 'll_' + loadinglists.map( ( loadlist: Loadinglist ) => loadlist.loadinglistNo ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( this.loadinglistService, loadinglists ),
      filename, saving );
  }
}
