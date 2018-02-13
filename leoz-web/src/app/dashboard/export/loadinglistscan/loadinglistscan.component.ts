import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/filter';

import { SelectItem } from 'primeng/api';

import { Exportlist } from '../exportlist.model';
import { Package } from '../../../core/models/package.model';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { KeyUpEventService } from '../../../core/key-up-event.service';
import { SoundService } from '../../../core/sound.service';
import { PrintingService } from '../../../core/printing/printing.service';
import { LoadinglistReportingService } from '../../../core/reporting/loadinglist-reporting.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { LoadinglistReportHeader } from './loadinglist-report-header.model';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { LoadinglistscanService } from './loadinglistscan.service';
import { TYPE_VALUABLE } from '../../../core/constants';
import { checkdigitInt25 } from '../../../core/math/checkdigitInt25';
import { MsgService } from '../../../shared/msg/msg.service';
import { roundDecimals } from '../../../core/math/roundDecimals';

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
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class LoadinglistscanComponent extends AbstractTranslateComponent implements OnInit {

  @ViewChild( 'scanfield' ) scanfield: ElementRef;

  styleWeightExceeded = {};
  styleScanMsgListBG = { 'background-color': '#ffffff', 'color': '#000000' };
  styleScanMsgfieldBG = { 'background-color': '#ffffff', 'color': '#000000' };

  dateFormat: string;
  loadlistItems: SelectItem[];

  loadlists: SelectItem[];
  private actionMsgListSubject = new BehaviorSubject<string[]>( [] );
  public actionMsgList$ = this.actionMsgListSubject.asObservable().distinctUntilChanged();
  private actionMsgListTmp: string[];

  public scanMsgsSubject = new BehaviorSubject<ScanMsg[]>( [] );
  public scanMsgs$ = this.scanMsgsSubject.asObservable().distinctUntilChanged();
  public shortScanMsg = '';

  activeLoadinglist: Exportlist;
  allLoadlists: Exportlist[];
  totalWeight: number;
  freeWeight: number;
  openPackcount: number;
  loadedPackcount: number;
  openWeight: number;
  selectedPackages: Package[];

  loadinglistscanForm: FormGroup;
  public openPackages$: Observable<Package[]>;
  public loadedPackages$: Observable<Package[]>;
  private openPackagesArr: Package[];
  exportdate: any;
  latestMarkedIndex: number;
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
      this.loadlists = this.createLoadinglistItems( this.loadlistItems );
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

    this.actionMsgListSubject.next( [ 'noAction' ] );

    this.exportdate = this.initExportdate();

    this.openPackages$ = this.loadinglistService.openPackages$;
    this.loadedPackages$ = this.loadinglistService.loadedPackages$;
    this.selectedPackages = [];
    this.openPackagesArr = [];

    this.latestMarkedIndex = -1;
    this.latestDirection = 'INIT';

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F12' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.markSinglePackage( 0 ) );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === '+' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.scanPackNos() );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowDown' && !ev.shiftKey )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.markSinglePackage( this.latestMarkedIndex + 1 ) );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowUp' && !ev.shiftKey )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.markSinglePackage( this.latestMarkedIndex - 1 ) );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowDown' && ev.shiftKey )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.markPackage( 'DOWN' ) );

    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'ArrowUp' && ev.shiftKey )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.markPackage( 'UP' ) );


    this.loadinglistService.openPackages$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( packages: Package[] ) => {
        this.openPackagesArr = packages;
        this.openStats( packages );
      } );

    this.loadinglistService.loadlists$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( selectItems: SelectItem[] ) => {
        this.loadlistItems = selectItems;
        this.loadlists = this.createLoadinglistItems( this.loadlistItems );
      } );


    this.loadinglistscanForm = this.fb.group( {
      payload: [ null ],
      selectloadlist: [ null ],
      scanfield: [ null ],
      loadlistnumber: [ { value: '', disabled: true } ],
      printlabel: [ null ],
      basedon: [ null ]
    } );

    this.loadinglistService.activeLoadinglist$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeLoadinglist: Exportlist ) => {
        this.activeLoadinglist = activeLoadinglist;

        this.calculateWeights();
        this.resetScanMsgs();
      } );

    this.loadinglistService.allLoadlists$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( allLoadlists: Exportlist[] ) => {
        this.allLoadlists = allLoadlists;
      } );

    this.actionMsgList$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( actionMsgList: string[] ) => {
        this.actionMsgListTmp = actionMsgList;
      } );

    this.totalWeight = null;
    this.freeWeight = null;
    this.openPackcount = null;
    this.loadedPackcount = null;
    this.openWeight = null;

    this.loadinglistService.getAllPackages();
  }

  isValuable( pack: Package ) {
    return pack.typeOfPackaging === TYPE_VALUABLE;
  }

  private resetScanMsgs() {
    // bei Auswahl neue Ladeliste oder Selektion einer bestehenden Ladeliste:
    //    Feld Scan Nachrichten = leer und Grundfarbe
    //    Feld unterhalb der Aktionen = leer und Grundfarbe
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

  private markSinglePackage( index: number ) {
    this.selectedPackages = [];
    if (this.openPackagesArr[ index ]) {
      this.latestMarkedIndex = index;
      this.selectedPackages.push( this.openPackagesArr[ index ] );
    }
  }

  private markPackage( direction: string ) {
    let index = this.latestMarkedIndex;
    if (this.latestDirection !== 'INIT' && this.latestDirection !== direction) {
      // direcction changed
    } else if (direction === 'UP') {
      index -= 1;
    } else {
      index += 1;
    }
    if (this.openPackagesArr[ index ]) {
      const actualPackage = this.openPackagesArr[ index ];
      if (this.selectedPackages.includes( actualPackage )) {
        const idx = this.selectedPackages.indexOf( actualPackage );
        this.selectedPackages.splice( idx, 1 );
      } else {
        this.selectedPackages.push( this.openPackagesArr[ index ] );
      }
    }
    this.latestMarkedIndex = index;
    this.latestDirection = direction;
  }

  public onRowSelect( event ) {
    this.latestMarkedIndex = this.openPackagesArr.indexOf( event.data );
  }

  public newLoadlist() {
    this.loadinglistService.newLoadlist();
    this.resetPayloadField();
    this.loadinglistscanForm.get( 'selectloadlist' ).patchValue( null );
    this.actionMsgListSubject.next( [ 'actionNewLoadlist' ] );
  }

  public changePayload() {
    this.actionMsgListSubject.next( [ ...this.actionMsgListTmp, 'actionChangePayload' ] );
    this.calculateWeights();
  }

  public scan( waitingForResults: number, scanFunction: Function ) {
    // check if loadlist is set
    if (this.activeLoadinglist.loadlistNo) {
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

  public scanPackSingle() {
    this.scan( 1,
      () => this.scanPackNo( this.loadinglistscanForm.get( 'scanfield' ).value )
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
    return this.padStart(`${someNo}${checkdigit}`, 12, '0');
  }

  public scanPackNos() {
    this.scan( this.selectedPackages.length,
      () => this.selectedPackages.forEach(
        ( pack: Package ) => this.scanPackNo( this.addCheckdigit( pack.parcelNo ) )
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

  private handleError( packageId: string, errorType: string ) {
    switch (errorType) {
      case 'invalid senddate':
        // Umbuchung funktioniert, zu testen: wenn ausgeliefert, .....
        this.addScanMsg( packageId, 'error', 'red', '#ffffff', 'noScan',
          'invalidSenddate',
          'critical' );
        break;
      case 'valore':
        // muss noch geprüft werden, wenn Webservice korrigiert ist
        this.addScanMsg( packageId, 'error', 'red', '#ffffff', 'noScan',
          'noValoreScan', 'critical' );
        break;
      default:
        this.addScanMsg( packageId, 'error', 'red', '#ffffff', 'noScan',
          errorType, 'critical' );
        break;
    }
  }

  public scanPackNo( packageId: string ) {
    this.loadinglistService.scanPack( packageId, this.activeLoadinglist.label )
      .subscribe( ( response: HttpResponse<any> ) => {
          console.log( 'response', response );
          switch (response.status) {
            case 200:
              this.handleSuccess( response.body.title );
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
              this.handleError( packageId, error.error[ 'title' ] );
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
    this.loadedPackcount = this.activeLoadinglist.packages.length;
    this.totalWeight = this.loadinglistService.sumWeights( this.activeLoadinglist.packages );
    this.freeWeight = this.loadinglistscanForm.get( 'payload' ).value
      ? roundDecimals(this.loadinglistscanForm.get( 'payload' ).value - this.totalWeight)
      : null;
    this.styleWeightExceeded = (this.freeWeight && this.freeWeight < 0) ? { 'color': 'red' } : {};
  }

  private openStats( packages: Package[] ) {
    this.openPackcount = packages.length;
    this.openWeight = this.loadinglistService.sumWeights( packages );
  }

  private initExportdate() {
    const d = new Date();
    d.setHours( d.getHours() - 5 );
    return this.datePipe.transform( d, this.dateFormat );
  }

  private createLoadinglistItems( selectItems: SelectItem[] ) {
    return [
      { label: this.translate.instant( 'loadlist' ), value: null },
      ...selectItems
    ];
  }

  public selectLoadlist( selected: number ) {
    if (selected) {
      this.loadinglistService.setActiveLoadinglist( selected, this.addCheckdigit( selected ) );
      this.resetPayloadField();
      this.actionMsgListSubject.next( [ 'actionChangeLoadlist' ] );
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
    const listsToPrint = this.loadinglistscanForm.get( 'basedon' ).value === 'alllists'
      ? this.allLoadlists : [ this.activeLoadinglist ];
    this.loadinglistService.reportHeaderData( String( this.activeLoadinglist.loadlistNo ) )
      .subscribe( ( response: HttpResponse<any> ) => {
          switch (response.status) {
            case 200:
              const filename = 'll_' + listsToPrint.map( ( loadlist: Exportlist ) => loadlist.loadlistNo ).join( '_' );
              this.printingService.printReports( this.reportingService
                  .generateReports( listsToPrint, <LoadinglistReportHeader> response.body ),
                filename, saving );
              break;
            default:
              // unknown reponse status from REST
              console.log( response );
              break;
          }
          this.receivedResponse();
        },
        ( error: HttpErrorResponse ) => {
          console.log( error );
          this.receivedResponse();
        } );
  }

}
