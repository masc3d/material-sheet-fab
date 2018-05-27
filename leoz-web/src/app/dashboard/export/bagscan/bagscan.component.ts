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
import { Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { BagscanService } from './bagscan.service';
import { KeyUpEventService } from '../../../core/key-up-event.service';
import { SoundService } from '../../../core/sound.service';
import { TYPE_VALUABLE } from '../../../core/constants';
import { MsgService } from '../../../shared/msg/msg.service';
import { Loadinglist } from '../../../core/models/loadinglist.model';
import { Exportparcel } from '../../../core/models/exportparcel.model';
import { Exportorder } from '../../../core/models/exportorder.model';
import { Bag } from '../../../core/models/bag.model';
import { checkdigitInt25 } from '../../../core/math/checkdigitInt25';

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
} )
export class BagscanComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  @ViewChild( 'bagId' ) bagIdField: ElementRef;
  @ViewChild( 'backLabel' ) backLabelField: ElementRef;
  @ViewChild( 'backSeal' ) backSealField: ElementRef;
  @ViewChild( 'packageNo' ) packageNoField: ElementRef;
  @ViewChild( 'blueSealNo' ) blueSealNoField: ElementRef;
  @ViewChild( 'reasonDetails' ) reasonDetailsField: ElementRef;

  public openParcels$: Observable<Exportparcel[]>;
  openParcels: Exportparcel[];

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

  bagscanForm: FormGroup;
  loading: boolean;
  countBagsToSendBackMsg = '';

  bag: Bag;

  constructor( private fb: FormBuilder,
               private bagscanService: BagscanService,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private keyUpService: KeyUpEventService,
               private soundService: SoundService
               // private labelReportingService: LabelReportingService,
               // private printingService: PrintingService,
               // private electronService: ElectronService
  ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();
    this.loading = false;

    this.displayEmergencySealBlock = false;

    this.openParcels$ = this.bagscanService.openParcels$;
    this.openPackcount = 0;
    this.loadedDiamondcount = 0;
    this.loadedPackcount = 0;
    this.bagWeight = 0;
    this.chargingLevel = 0;
    this.allPackagesCount = 0;
    this.chargingLevelStyle = 'chargeLvlRed';
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

    this.keyUpService.onKeyUp( 'F2', this.ngUnsubscribe, this.startPacking, this );
    this.keyUpService.onKeyUp( 'F3', this.ngUnsubscribe, this.clearFields, this );
    // this.keyUpService.onKeyUp( 'F7', this.ngUnsubscribe, this.saveEmergencySeal, this );

    this.setEmptyBag();

    this.bagscanService.openParcels$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( parcels: Exportparcel[] ) => {
        this.openParcels = parcels;
        this.openPackcount = this.openParcels.length;
        this.calcStats();
        this.cd.detectChanges();
      } );

    this.bagscanService.countBagsToSendBack$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( countBagsToSendBack: number ) => {
        switch (countBagsToSendBack) {
          case 0:
            // do not display message at all
            this.countBagsToSendBackMsg = '';
            break;
          case 1:
            this.countBagsToSendBackMsg = this.translate.instant( 'bagCountSingleMsg' );
            break;
          default:
            this.countBagsToSendBackMsg = this.translate.instant( 'bagCountMsgfield' )
              .replace( /#BAGCOUNT#/, countBagsToSendBack.toString( 10 ) );
            break;
        }
        this.cd.detectChanges();
      } );

    this.bagscanService.getOpenParcels();
    this.bagscanService.countBagsToSendBack();
    this.bagIdField.nativeElement.focus();
  }

  private setEmptyBag() {
    this.bag = <Bag>{
      status: null,
      unitBackLabel: '',
      sealYellowLabel: ''
    };
  }

  isValuable( parcel: Exportparcel ) {
    return parcel.typeOfPackaging === TYPE_VALUABLE;
  }

  private calcStats() {
    // rot 0-50 gelb 51-75 grün 76-100
    this.openPackcount = this.openParcels.length;
    const loadedParcels = this.bag.ordersToexport
      ? this.bag.ordersToexport.map( ( order: Exportorder ) => order.parcels )
        .reduce( ( a: Exportparcel[], b: Exportparcel[] ) => a.concat( b ), [] )
      : <Exportparcel[]>[];
    this.loadedDiamondcount = loadedParcels
      .filter( ( p: Exportparcel ) => p.typeOfPackaging === TYPE_VALUABLE ).length;
    this.loadedPackcount = loadedParcels.length;
    this.bagWeight = this.bagscanService.sumWeights( loadedParcels );
  }

  startPacking(): void {
    if (this.bagscanForm.get( 'bagId' ).value.length > 0
      && this.bagscanForm.get( 'backLabel' ).value.length > 0
      && this.bagscanForm.get( 'backSeal' ).value.length > 0) {
      if (!this.bag.loadinglistNo) {
        this.bagscanService.newLoadlist()
          .subscribe( ( loadinglist: Loadinglist ) => {
            this.bag.loadinglistNo = loadinglist.loadinglistNo;
            this.openScanField();
          }, ( error ) => console.log( error ) );
      } else {
        this.openScanField();
      }
    }
  }

  private openScanField() {
    this.bagscanForm.get( 'packageNo' ).enable();
    this.packageNoField.nativeElement.focus();
    this.calcStats();
    this.cd.detectChanges();
  }

  bagIdChanged(): void {
    this.bagscanService.validateBagId( this.bagscanForm.get( 'bagId' ).value )
      .subscribe( ( bag: Bag ) => {
          this.bag = bag;
          switch (bag.status) {
            case Bag.Status.OPENED:
              this.handleSuccess( 'default',
                () => {
                  this.bagscanForm.get( 'bagId' ).disable();
                  this.bagscanForm.get( 'backLabel' ).enable();
                  this.backLabelField.nativeElement.focus();
                  this.calcStats();
                  this.cd.detectChanges();
                } );
              break;
            case Bag.Status.CLOSED_FROM_HUB:
              this.handleError( 'bagClosedFromHub' );
              this.setEmptyBag();
              break;
            case Bag.Status.CLOSED_FROM_STATION:
            default:
              this.handleError( 'bagClosedFromStation' );
              this.setEmptyBag();
              break;
          }
        },
        ( error: HttpErrorResponse ) => {
          this.setEmptyBag();
          this.handleError( error.error.title );
        } );
  }

  backLabelChanged(): void {
    // validate backLabel
    if (this.bagscanForm.get( 'backLabel' ).value === this.bag.unitBackLabel) {
      console.log( 'pre this.handleSuccess......' );
      this.handleSuccess( 'default',
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
    if (this.bagscanForm.get( 'backSeal' ).value === this.bag.sealYellowLabel) {
      this.handleSuccess( 'default',
        () => {
          this.bagscanForm.get( 'backSeal' ).disable();
          this.cd.markForCheck();
        } );
    } else {
      this.handleError( 'invalid backseal' );
    }
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

  scanPackToBag() {
    // Paketnummer scannen und mit allen Parametern an den Webservice übergeben
    this.bagscanService.scanPackToBag( this.bagscanForm.get( 'bagId' ).value,
      this.bagscanForm.get( 'backLabel' ).value,
      this.bagscanForm.get( 'packageNo' ).value,
      this.addCheckdigit( this.bag.loadinglistNo ),
      this.bagscanForm.get( 'backSeal' ).value )
      .subscribe( ( response: HttpResponse<any> ) => {
          console.log( '........', response );
          switch (response.status) {
            case 200:
              this.handleSuccess( response.body.title );
              this.bagscanService.getOpenParcels();
              this.packageNoField.nativeElement.focus();
              this.bagscanForm.get( 'packageNo' ).patchValue( '' );
              this.calcStats();
              this.cd.detectChanges();
              break;
            default:
              break;
          }
          // bei Erfolg: Meldung leeren, grün, nur dieses Feld leeren, Packstückliste aktualisieren
        },
        ( error: HttpErrorResponse ) => {
          this.handleError( error.error.title );
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

  private handleSuccess( sucessType: string, callback?: Function ) {
    if (sucessType !== 'default') {
      this.shortScanMsg = sucessType;
    }
    this.setSuccessStyle();
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
    this.soundService.play( 'critical' );
    this.shortScanMsg = errorType;
    this.styleShortScanMsg = { 'background-color': 'red', 'color': 'white' };
    this.cd.markForCheck();
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
    this.cd.detectChanges();
  }

  private clearEmergencySealFields() {
    this.bagscanForm.get( 'blueSealNo' ).patchValue( '' );
    this.bagscanForm.get( 'reasonDetails' ).patchValue( '' );
    this.shortScanMsg = '';
    this.styleShortScanMsg = { 'background-color': 'white', 'color': 'white' };
    this.cd.detectChanges();
  }

  refreshData() {
    this.bagscanService.getOpenParcels();
  }

  finishBag() {
    if (this.bag.loadinglistNo) {
      this.bagscanService.finishBag( this.bagscanForm.get( 'bagId' ).value,
        this.bagscanForm.get( 'backLabel' ).value,
        this.bagscanForm.get( 'packageNo' ).value,
        this.addCheckdigit( this.bag.loadinglistNo ) )
        .subscribe( ( response: HttpResponse<any> ) => {
            this.handleSuccess( response.body && response.body.title
              ? response.body.title
              : 'bag closed' );
            this.bagscanService.getOpenParcels();
            this.packageNoField.nativeElement.focus();
            this.bagscanForm.get( 'packageNo' ).patchValue( '' );
            this.cd.detectChanges();
            // bei Erfolg: Meldung leeren, grün, nur dieses Feld leeren, Packstückliste aktualisieren
          },
          ( error: HttpErrorResponse ) => {
            this.handleError( error.error[ 'title' ] );
            // bei Fehler: Fehlermeldung anzeigen, rot, nur dieses Feld leeren
          } );
      // if successful => this.clearFields()
      this.clearFields()
      console.log( 'finishBag' );
    } else {
      console.log( 'finishBag....no loadinglistNo' );
    }
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

  // should be triggered if label checked and successfully scanned packageNo
  // generateLabel() {
  //   if (this.electronService.isElectron()) {
  //     const doc = this.labelReportingService.generateReports();
  //     this.electronService.previewPDF( doc.output( 'datauristring' ) )
  //   } else {
  //     this.printingService.printReports(
  //       this.labelReportingService.generateReports(),
  //       'lbl.pdf', false );
  //   }
  // }

}

