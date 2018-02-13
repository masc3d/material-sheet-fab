import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/filter';

import { LazyLoadEvent, SelectItem } from 'primeng/api';
import { ElectronService } from '../../../core/electron/electron.service';

import { Exportlist } from '../exportlist.model';
import { Package } from '../../../core/models/package.model';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { BagscanService } from './bagscan.service';
import { BagIdChangeResponse } from './bag-id-change-response';
import { KeyUpEventService } from '../../../core/key-up-event.service';
import { SoundService } from '../../../core/sound.service';
import { BagData } from './bagdata.model';
import { PrintingService } from '../../../core/printing/printing.service';
import { BagscanReportingService } from '../../../core/reporting/bagscan-reporting.service';
import { TYPE_VALUABLE } from '../../../core/constants';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-bagscan',
  templateUrl: './bagscan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [ `
    .chargeLvlGreen {
      color: white;
      background-color: green;
    }

    .chargeLvlRed {
      color: white;
      background-color: red;
    }

    .chargeLvlYellow {
      color: black;
      background-color: yellow;
    }
  ` ]
  // styles: [ '.ui-g-12 { border: 1px solid green; }' ]
} )
export class BagscanComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  @ViewChild( 'bagId' ) bagIdField: ElementRef;
  @ViewChild( 'backLabel' ) backLabelField: ElementRef;
  @ViewChild( 'backSeal' ) backSealField: ElementRef;
  @ViewChild( 'packageNo' ) packageNoField: ElementRef;
  @ViewChild( 'blueSealNo' ) blueSealNoField: ElementRef;
  @ViewChild( 'reasonDetails' ) reasonDetailsField: ElementRef;

  public openPackages$: Observable<Package[]>;
  lazyOpenPackages: Package[];
  openPackagesArr: Package[];
  activeBaglist: Exportlist;
  activeBagData: BagData;

  openPackcount: number;
  loadedDiamondcount: number;
  loadedPackcount: number;
  bagWeight: number;
  chargingLevel: number;
  allPackagesCount: number;

  chargingLevelStyle: string;
  displayEmergencySealBlock: boolean;

  public shortScanMsg = '';
  styleShortScanMsg = { 'background-color': '#ffffff', 'color': '#000000' };

  baglistItems: SelectItem[];
  baglists: SelectItem[];
  bagscanForm: FormGroup;
  loading: boolean;
  private bagIdChangedResp: BagIdChangeResponse;

  constructor( private fb: FormBuilder,
               private bagscanService: BagscanService,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private keyUpService: KeyUpEventService,
               private soundService: SoundService,
               private reportingService: BagscanReportingService,
               private printingService: PrintingService,
               private electronService: ElectronService ) {
    super( translate, cd, msgService, () => {
      this.baglists = this.createBaglistItems( this.baglistItems );
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.loading = false;
    // this.loading = true;

    this.displayEmergencySealBlock = false;

    this.openPackages$ = this.bagscanService.openPackages$;
    this.openPackcount = 0;
    this.loadedDiamondcount = 0;
    this.loadedPackcount = 0;
    this.bagWeight = 0;
    this.chargingLevel = 0;
    this.allPackagesCount = 0;
    this.chargingLevelStyle = 'chargeLvlRed';
    this.setEmptyBagIdChangedResp();

    this.openPackages$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( packages: Package[] ) => {
        this.openPackagesArr = packages;
        this.openPackcount = this.openPackagesArr.length;
      } );

    this.bagscanService.allPackages$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( packages: Package[] ) => {
        const packagesLoaded = packages
          .filter( ( p: Package ) => p.loadinglistNo > 0 )
          .length;
        this.chargingLevel = packages.length > 0
          ? Math.round( (packagesLoaded / packages.length) * 100 )
          : 0;
        if (this.chargingLevel >= 0 && this.chargingLevel < 50) {
          this.chargingLevelStyle = 'chargeLvlRed';
        } else if (this.chargingLevel >= 50 && this.chargingLevel < 75) {
          this.chargingLevelStyle = 'chargeLvlYellow';
        } else {
          this.chargingLevelStyle = 'chargeLvlGreen';
        }
      } );

    this.bagscanService.loadlists$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( selectItems: SelectItem[] ) => {
        this.baglistItems = selectItems;
        this.baglists = this.createBaglistItems( this.baglistItems );
      } );

    this.bagscanForm = this.fb.group( {
      bagId: [ null ],
      backLabel: [ { value: '', disabled: true } ],
      backSeal: [ { value: '', disabled: true } ],
      packageNo: [ { value: '', disabled: true } ],
      blueSealNo: [ { value: '', disabled: false } ],
      reasonDetails: [ { value: '', disabled: false } ],
      printlabel: [ null ],
      selectbaglist: [ null ],
    } );

    this.bagscanService.activeLoadinglist$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeLoadinglist: Exportlist ) => {
        // console.log('this.activeBaglist changed...', activeLoadinglist);
        this.activeBaglist = activeLoadinglist;
        this.calcStats();
      } );

    this.bagscanService.activeBagData$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeBagData: BagData ) => {
        this.activeBagData = activeBagData;
        // fill and activate or deactivate Fields depending on BagData
        if (this.activeBagData.bagId && this.activeBagData.backLabel && this.activeBagData.backSeal) {
          this.bagscanForm.get( 'bagId' ).patchValue( this.activeBagData.bagId );
          this.bagscanForm.get( 'backLabel' ).patchValue( this.activeBagData.backLabel );
          this.bagscanForm.get( 'backSeal' ).patchValue( this.activeBagData.backSeal );
          this.bagscanForm.get( 'bagId' ).disable();
          this.bagscanForm.get( 'backLabel' ).disable();
          this.bagscanForm.get( 'backSeal' ).disable();
          this.bagscanForm.get( 'packageNo' ).enable();
        } else {
          this.bagscanForm.get( 'bagId' ).patchValue( '' );
          this.bagscanForm.get( 'backLabel' ).patchValue( '' );
          this.bagscanForm.get( 'backSeal' ).patchValue( '' );
          this.bagscanForm.get( 'bagId' ).enable();
          this.bagscanForm.get( 'backLabel' ).disable();
          this.bagscanForm.get( 'backSeal' ).disable();
          this.bagscanForm.get( 'packageNo' ).disable();
        }
      } );

    this.bagscanService.getAllPackages();
    this.bagIdField.nativeElement.focus();

    this.registerKeyboardEvents();
  }

  private setEmptyBagIdChangedResp() {
    this.bagIdChangedResp = {
      status: '',
      unitBackLabel: '',
      sealYellowLabel: ''
    };
  }

  isValuable( pack: Package ) {
    return pack.typeOfPackaging === TYPE_VALUABLE;
  }

  loadOpenPackagesLazy( event: LazyLoadEvent ) {
    console.log( 'pre loadOpenPackagesLazy LazyLoadEvent: ', event );
    console.log( 'this.openPackagesArr: ', this.openPackagesArr );
    this.loading = true;
    if (this.openPackagesArr.length > 0) {
      const startIndex = event ? event.first : 0;
      const rows = event ? event.rows : 20;
      this.lazyOpenPackages = this.openPackagesArr.slice( startIndex, startIndex + rows );
      console.log( 'this.lazyOpenPackages:', this.lazyOpenPackages );
    }
    this.loading = false;
    this.cd.markForCheck();
    console.log( 'post loadOpenPackagesLazy LazyLoadEvent: ', event );
    // this.cd.detectChanges();
  }

  private registerKeyboardEvents() {
    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F2' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.startPacking() );
    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F3' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.clearFields() );
    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F5' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.finishBag() );
    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F7' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.saveEmergencySeal() );
    this.keyUpService.keyUpEvents$
      .filter( ( ev: KeyboardEvent ) => ev.key === 'F10' )
      .takeUntil( this.ngUnsubscribe )
      .subscribe( () => this.switchSeal() );
  }

  private createBaglistItems( selectItems: SelectItem[] ) {
    return [
      { label: this.translate.instant( 'baglist' ), value: null },
      ...selectItems
    ];
  }

  private calcStats() {
    // rot 0-50 gelb 51-75 grün 76-100
    this.openPackcount = this.openPackagesArr.length;
    this.loadedDiamondcount = this.activeBaglist.packages
      .filter( ( p: Package ) => p.typeOfPackaging === TYPE_VALUABLE ).length;
    this.loadedPackcount = this.activeBaglist.packages.length;
    this.bagWeight = this.bagscanService.sumWeights( this.activeBaglist.packages );
  }

  public selectBaglist( selected: number ) {
    if (selected) {
      this.bagscanService.setActiveLoadinglist( selected );
      // this.resetPayloadField();
      // this.actionMsgListSubject.next( [ 'actionChangeLoadlist' ] );
    }
  }

  startPacking(): void {
    if (this.bagscanForm.get( 'bagId' ).value.length > 0
      && this.bagscanForm.get( 'backLabel' ).value.length > 0
      && this.bagscanForm.get( 'backSeal' ).value.length > 0) {
      this.bagscanService.newLoadlist();
      this.bagscanForm.get( 'packageNo' ).enable();
      this.packageNoField.nativeElement.focus();
    }
  }

  bagIdChanged(): void {
    this.bagscanService.validateBagId( this.bagscanForm.get( 'bagId' ).value )
      .subscribe( ( response: BagIdChangeResponse ) => {
          console.log( 'hier....', response );
          this.bagIdChangedResp = response;
          switch (response.status) {
            case 'OPENED':
              this.handleSuccess( 'valid bagId',
                () => {
                  this.bagscanForm.get( 'bagId' ).disable();
                  this.bagscanForm.get( 'backLabel' ).enable();
                  this.backLabelField.nativeElement.focus();
                } );
              break;
            default:
              this.setEmptyBagIdChangedResp();
              // unknown reponse status from REST
              break;
          }
        },
        ( error: HttpErrorResponse ) => {
          console.log( 'error', error );
          this.setEmptyBagIdChangedResp();
          switch (error.status) {
            case 200:
              break;
            case 409:
              console.log( '409' );
              this.handleError( error.error.title );
              break;
            default:
              break;
          }
        } );
  }

  backLabelChanged(): void {
    // validate backLabel
    if (this.bagscanForm.get( 'backLabel' ).value === this.bagIdChangedResp.unitBackLabel) {
      console.log( 'pre this.handleSuccess......' );
      this.handleSuccess( 'valid backLabel',
        () => {
          this.bagscanForm.get( 'backLabel' ).disable();
          this.bagscanForm.get( 'backSeal' ).enable();
          this.backSealField.nativeElement.focus();
          this.cd.markForCheck();
        } );
    } else {
      this.handleError( 'invalid label' );
    }
  }

  backSealChanged(): void {
    // validate backSeal
    if (this.bagscanForm.get( 'backSeal' ).value === this.bagIdChangedResp.sealYellowLabel) {
      this.handleSuccess( 'valid backSeal',
        () => {
          this.bagscanForm.get( 'backSeal' ).disable();
          this.cd.markForCheck();
        } );
    } else {
      this.handleError( 'invalid backseal' );
    }
  }

  scanPackToBag() {
    // Paketnummer scannen und mit allen Parametern an den Webservice übergeben
    this.bagscanService.scanPackToBag( this.bagscanForm.get( 'bagId' ).value,
      this.bagscanForm.get( 'backLabel' ).value,
      this.bagscanForm.get( 'packageNo' ).value,
      this.activeBaglist.label,
      this.bagscanForm.get( 'backSeal' ).value )
      .subscribe( ( response: HttpResponse<any> ) => {
          console.log( 'hier....', response );
          // bei Erfolg: Meldung leeren, grün, nur dieses Feld leeren, Packstückliste aktualisieren
        },
        ( error: HttpErrorResponse ) => {
          console.log( 'error', error );
          // bei Fehler: Fehlermeldung anzeigen, rot, nur dieses Feld leeren
        } );
  }

  toggleVisibilitySeal() {
    if (!this.bagscanForm.get( 'packageNo' ).disabled) {
      this.handleError( 'noSealActivation' );
      this.soundService.play( 'critical' );
      this.displayEmergencySealBlock = false;
    } else if (!this.bagscanForm.get( 'bagId' ).value) {
      this.handleError( 'noBagIDSet' );
      this.soundService.play( 'critical' );
      this.bagIdField.nativeElement.focus();
      this.displayEmergencySealBlock = false;
    } else if (!this.bagscanForm.get( 'backLabel' ).value) {
      this.handleError( 'noBackLabelSet' );
      this.soundService.play( 'critical' );
      this.backLabelField.nativeElement.focus();
      this.displayEmergencySealBlock = false;
    } else if (this.bagscanForm.get( 'backSeal' ).value) {
      this.handleError( 'noBackSealSet' );
      this.soundService.play( 'critical' );
      this.backSealField.nativeElement.focus();
      this.displayEmergencySealBlock = false;
    } else {
      this.bagscanForm.get( 'backSeal' ).disable();
      this.displayEmergencySealBlock = true;
      this.clearEmergencySealFields();
      setTimeout( () => this.blueSealNoField.nativeElement.focus(), 0 );
    }
  }

  private handleSuccess( sucessType: string, callback ?: Function ) {
    switch (sucessType) {
      case 'valid bagId':
        this.setSuccessStyle();
        break;
      case 'valid backLabel':
        this.setSuccessStyle();
        break;
      case 'valid backSeal':
        this.setSuccessStyle();
        break;
      default:
        this.setSuccessStyle();
        break;
    }
    if (callback) {
      callback();
    }
  }

  private setSuccessStyle() {
    this.soundService.play( 'ding' );
    this.shortScanMsg = '';
    this.styleShortScanMsg = { 'background-color': 'green', 'color': 'white' };
    this.cd.markForCheck();
  }

  private handleError( errorType: string ) {
    // switch (errorType) {
    //   case 'BagId wrong check digit':
    //     this.shortScanMsg = 'BagId wrong check digit'; // 'noLlNoSet';
    //     break;
    //   case 'noBagIDSet':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noBagIDSet';
    //     break;
    //   case 'noBackLabelSet':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noBackLabelSet';
    //     break;
    //   case 'noBackSealSet':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noBackSealSet';
    //     break;
    //   case 'noSealActivation':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noSealActivation';
    //     break;
    //   case 'noBlueSealNoSet':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noBlueSealNoSet';
    //     break;
    //   case 'noReasonSet':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noReasonSet';
    //     break;
    //   case 'not found':
    //     // Beschreibung....
    //     this.shortScanMsg = 'noDataInDatabase';
    //     break;
    //   default:
    //     console.log( 'unhandled errorType' );
    //     break;
    // }
    this.soundService.play( 'critical' );
    this.shortScanMsg = errorType;
    this.styleShortScanMsg = { 'background-color': 'red', 'color': 'white' };
    this.cd.markForCheck();
    console.log( errorType );
  }

  clearFields() {
    this.bagscanForm.get( 'bagId' ).patchValue( '' );
    this.bagscanForm.get( 'backLabel' ).patchValue( '' );
    this.bagscanForm.get( 'backSeal' ).patchValue( '' );
    this.bagscanForm.get( 'packageNo' ).patchValue( '' );
    this.bagscanForm.get( 'bagId' ).enable();
    this.bagscanForm.get( 'backLabel' ).disable();
    this.bagscanForm.get( 'backSeal' ).disable();
    this.bagscanForm.get( 'packageNo' ).disable();
    this.clearEmergencySealFields();
    this.displayEmergencySealBlock = false;
    this.bagIdField.nativeElement.focus();
  }

  private clearEmergencySealFields() {
    this.bagscanForm.get( 'blueSealNo' ).patchValue( '' );
    this.bagscanForm.get( 'reasonDetails' ).patchValue( '' );
    this.shortScanMsg = '';
    this.styleShortScanMsg = { 'background-color': 'white', 'color': 'white' };
  }

  refreshData() {
    this.bagscanService.getAllPackages();
  }

  private finishBag() {
    // if successful => this.clearFields()
    console.log( 'finishBag' );
  }

  private switchSeal() {
    console.log( 'switchSeal' );
  }

  saveEmergencySeal() {
    if (!this.bagscanForm.get( 'blueSealNo' ).value) {
      this.handleError( 'noBlueSealNoSet' );
      this.soundService.play( 'critical' );
      this.blueSealNoField.nativeElement.focus();
    } else if (!this.bagscanForm.get( 'reasonDetails' ).value) {
      this.handleError( 'noReasonSet' );
      this.soundService.play( 'critical' );
      this.reasonDetailsField.nativeElement.focus();
    } else {
      // more error msg may be possible
      this.setSuccessStyle();
      this.bagscanForm.get( 'backSeal' ).patchValue( this.bagscanForm.get( 'blueSealNo' ).value );
    }
  }

  generateLabel() {
    if (this.electronService.isElectron()) {
      const doc = this.reportingService.generateReports();
      this.electronService.previewPDF( doc.output( 'datauristring' ) )
    } else {
      this.printingService.printReports(
        this.reportingService.generateReports(),
        'lbl.pdf', false );
    }
  }

}

