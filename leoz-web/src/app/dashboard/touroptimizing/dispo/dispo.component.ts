import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { takeUntil } from 'rxjs/operators';

import { Message } from 'primeng/components/common/api';
import { SortEvent } from 'primeng/api';
import * as moment from 'moment';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { TouroptimizingService } from '../touroptimizing.service';
import { Tour } from '../../../core/models/tour.model';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { PrintingService } from '../../../core/printing/printing.service';
import { StoplistReportingService } from '../../../core/reporting/stoplist-reporting.service';
import { Vehicle } from '../../../core/models/vehicle.model';
import { compareCustom } from '../../../core/compare-fn/custom-compare';
import { roundDecimalsAsString } from '../../../core/math/roundDecimals';


@Component( {
  selector: 'app-dispo',
  templateUrl: './dispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DispoComponent extends AbstractTranslateComponent implements OnInit {

  checkAll: boolean;
  tours: Tour[];
  filteredTours: Tour[];
  toursLoading$: Observable<boolean>;

  public msgs$: Observable<Message[]>;
  public sticky$: Observable<boolean>;

  selectedTours: Tour[] = [];
  selectedOptimizableTours: Tour[] = []; // tours with more than one shipment

  notMicrodoof: boolean;

  displayOptimizationOptions = false;
  dontShiftOneDayFromNow = true;
  optimizeTraffic = true;
  optimizeExistingtours = true;
  optimizeSplitTours = false;
  sprinterMaxKg: number;
  caddyMaxKg: number;
  kombiMaxKg: number;
  bikeMaxKg: number;

  latestSortField: string = null;
  latestSortOrder = 0;

  dateFormatMedium: string;

  dateFormat: string;
  tourDateFilter: Date;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected msgService: MsgService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService,
               protected browserCheck: BrowserCheck ) {
    super( translate, cd, msgService );
  }


  ngOnInit() {
    super.ngOnInit();

    this.tourDateFilter = new Date();

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.toursLoading$ = this.touroptimizingService.toursLoading$;

    this.tours = [];
    this.filteredTours = [];
    this.touroptimizingService.tours$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( tours: Tour[] ) => {
        this.tours.length = 0;
        if (this.latestSortField !== null) {
          this.tours.push( ...tours.sort( ( data1, data2 ) => {
            return compareCustom( this.latestSortOrder, data1[ this.latestSortField ], data2[ this.latestSortField ] );
          } ) );
        } else {
          const sortedAndGrouped = this.invertTreeAndFlatten( this.convert2Tree( tours ) );
          this.tours.push( ...sortedAndGrouped );
        }
        this.filterToursByDeliveryDate();
        this.cd.markForCheck();
      } );
    this.touroptimizingService.getTours();

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe );
    this.touroptimizingService.initSSEtourChanges( this.ngUnsubscribe );
  }

  private filterToursByDeliveryDate() {
    this.filteredTours.length = 0;
    this.filteredTours.push( ...this.tours.filter( tour => moment( tour.date ).isSame( this.tourDateFilter, 'day' ) ) );
  }

  customSort( event: SortEvent ) {
    if (event.field) {
      this.latestSortField = event.field;
      this.latestSortOrder = event.order;
      event.data.sort( ( data1, data2 ) => {
        return compareCustom( event.order, data1[ event.field ], data2[ event.field ] );
      } );
    }
  }

  getTours() {
    this.touroptimizingService.showSpinner();
    this.touroptimizingService.getTours();
  }

  changeCheckAllTours( evt: { checked: boolean } ) {
    this.touroptimizingService.switchSelectionAllTours( evt.checked );
  }

  protected optimizeTours() {
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    this.selectedOptimizableTours = this.selectedTours
      .filter( tour => tour.orders.length > 1 );
    if (this.selectedOptimizableTours.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      const selectedTourIds = this.selectedOptimizableTours
        .map( tour => tour.id );
      let vehicles = [];
      if (this.optimizeSplitTours) {
        vehicles = this.addVehicles( this.sprinterMaxKg, Vehicle.SPRINTER, vehicles );
        vehicles = this.addVehicles( this.caddyMaxKg, Vehicle.CADDY, vehicles );
        vehicles = this.addVehicles( this.kombiMaxKg, Vehicle.STATION_WAGON, vehicles );
        vehicles = this.addVehicles( this.bikeMaxKg, Vehicle.BIKE, vehicles );
      }
      this.touroptimizingService.optimizeAndReinitTours( selectedTourIds,
        vehicles.length > 0 ? vehicles : [ Vehicle.SPRINTER ],
        this.optimizeTraffic, this.optimizeExistingtours, this.dontShiftOneDayFromNow );
    }
    this.checkAll = false;
  }

  private addVehicles( amount: number, type: Vehicle, vehicles: Vehicle[] ): Vehicle[] {
    if (amount > 0) {
      for (let i = 0; i < amount; i += 1) {
        vehicles.push( type );
      }
    }
    return vehicles;
  }

  deleteTour( tourId ) {
    this.touroptimizingService.deleteTours( [ tourId ] );
  }

  preview() {
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    if (this.selectedTours && this.selectedTours.length === 0) {
      this.msgService.info( 'no_tours_selected', false, false );
    } else {
      this.reporting( false );
    }
  }

  saving() {
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    if (this.selectedTours && this.selectedTours.length === 0) {
      this.msgService.info( 'no_tours_selected', false, false );
    } else {
      this.reporting( true );
    }
  }

  protected reporting( saving: boolean ) {
    const listsToPrint = this.filteredTours.filter( tour => tour.selected );

    const filename = 'sl_' + listsToPrint.map( tour => tour.id ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( listsToPrint ),
      filename, saving );
  }

  optimizeDialog() {
    this.displayOptimizationOptions = false;
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    this.selectedOptimizableTours = this.selectedTours
      .filter( tour => tour.orders && tour.orders.length > 1 );
    if (this.selectedOptimizableTours.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      this.displayOptimizationOptions = true;
    }
    this.cd.markForCheck();
  }

  acceptOptimizationOptions() {
    this.optimizeTours();
    this.displayOptimizationOptions = false;
    this.cd.markForCheck();
  }

  rejectOptimizationOptions() {
    this.displayOptimizationOptions = false;
    this.cd.markForCheck();
  }

  formatTourDurationTime( duration: number ): string {
    if (duration > 0) {
      return moment.utc( duration * 1000 ).format( 'HH:mm:ss' )
    }
    return '';
  }

  roundDecimalsAsStringWrapper( input: number ): string {
    return roundDecimalsAsString( input, 10, true );
  }

  tourDateFilterPrevDay() {
    this.tourDateFilter = moment( this.tourDateFilter )
      .subtract( 1, 'days' )
      .toDate();
    this.filterToursByDeliveryDate();
  }

  tourDateFilterNextDay() {
    this.tourDateFilter = moment( this.tourDateFilter )
      .add( 1, 'days' )
      .toDate();
    this.filterToursByDeliveryDate();
  }

  private invertTree( toursTree: Map<number, Tour> ): Tour[] {
    return Array.from( toursTree ).reverse().map( numberTourPair => numberTourPair[ 1 ] );
  }

  private flatten( tours: Tour[], flattenedTours: Tour[] ): Tour[] {
    tours.forEach( tour => {
      flattenedTours.push( tour );
      if (tour.children) {
        this.flatten( tour.children, flattenedTours );
      }
    } );
    return flattenedTours;
  }

  private invertTreeAndFlatten( toursTree: Map<number, Tour> ): Tour[] {
    const inverted = this.invertTree( toursTree );
    return this.flatten( inverted, [] );
  }

  private convert2Tree( tours: Tour[] ): Map<number, Tour> {
    const toursTree = new Map<number, Tour>();
    /**
     * Initial sort is necessary for the conversion and the conversion only works,
     * if parent tourIds are smaller numbers than the childIds.
     */
    const sortedTours = tours.sort( ( t1: Tour, t2: Tour ) => t1.id - t2.id );
    const tourIdLookupMap = new Map<number, Tour>();
    const tourToParentMapping = new Map<number, number>();
    const tourToChildrenMapping = new Map<number, number[]>();

    // filling the maps for later iteration and lookup
    sortedTours.forEach( tour => {
      const tourId = tour.id;
      const parentId = tour.parentId;
      tourIdLookupMap.set( tourId, tour );
      tourToParentMapping.set( tourId, parentId );
      if (parentId) {
        const childIds = tourToChildrenMapping.has( parentId )
          ? tourToChildrenMapping.get( parentId )
          : [];
        childIds.push( tourId );
        tourToChildrenMapping.set( parentId, childIds )
      }
    } );

    tourToParentMapping.forEach( ( parentId: number, tourId: number ) => {
      if (!parentId) {
        this.rootTourTreeEntry( toursTree, tourId, tourIdLookupMap, tourToChildrenMapping );
      }
    } );

    return toursTree;
  }

  private rootTourTreeEntry( toursTree: Map<number, Tour>, tourId: number,
                             tourIdLookupMap: Map<number, Tour>, tourToChildrenMapping: Map<number, number[]> ) {
    // insert entry in toursTree as root
    const rootTour = tourIdLookupMap.get( tourId );
    toursTree.set( tourId, rootTour );
    // check if tour has children
    if (tourToChildrenMapping.has( tourId )) {
      // iterate over childs and create entries recursively
      tourToChildrenMapping.get( tourId )
        .forEach( childId => {
          const child = tourIdLookupMap.get( childId );
          this.appendChild( toursTree, rootTour, child, tourIdLookupMap, tourToChildrenMapping );
        } );
    }
  }

  private appendChild( toursTreeResult: Map<number, Tour>, parent: Tour, tour: Tour,
                       tourIdLookupMap: Map<number, Tour>, tourToChildrenMapping: Map<number, number[]> ) {
    if (parent.children) {
      parent.children.push( tour );
    } else {
      parent.children = [ tour ];
    }
    // check if tour has children
    if (tourToChildrenMapping.has( tour.id )) {
      // iterate over childs and create entries recursively
      tourToChildrenMapping.get( tour.id )
        .forEach( childId => {
          const child = tourIdLookupMap.get( childId );
          this.appendChild( toursTreeResult, tour, child, tourIdLookupMap, tourToChildrenMapping );
        } );
    }
  }
}



