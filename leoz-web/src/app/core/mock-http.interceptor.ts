import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpParams, HttpRequest,
  HttpResponse
} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

export class MockHttpInterceptor implements HttpInterceptor {

  intercept( req: HttpRequest<any>, next: HttpHandler ): Observable<HttpEvent<any>> {
    const url: string = req.url;
    const method: string = req.method;
    const params: HttpParams = req.params;
    const body = req.body;

    // if (url.endsWith( '/internal/v1/loadinglist/packages' )) {
    //   switch (method) {
    //     case 'POST':
    //       // get parameters from post request
    //       switch (body.packageId) {
    //         case '1060532266':
    //           return Observable.of( new HttpResponse<any>( {
    //             body: { status: 201, packageId: '1060532266', msgText: 'success' }
    //           } ) );
    //         case '79650002359':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 201,
    //                 packageId: '79650002359', msgText: 'already scanned'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '79650002360':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 201,
    //                 packageId: '79650002360', msgText: 'transfered to other laodlist'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '7280330924':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: '7280330924', msgText: 'depot mismatch'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '7280330925':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: '7280330925', msgText: 'invalid senddate'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '7280330926':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: '7280330926', msgText: 'invalid product'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '7280330927':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: '7280330927', msgText: 'valore'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         case '7280330928':
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: '7280330928', msgText: 'no activeLoadinglist'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //         default:
    //           return new Observable( resp => {
    //             resp.next( new HttpResponse( {
    //               body: {
    //                 status: 301,
    //                 packageId: body.packageId, msgText: 'not found'
    //               }
    //             } ) );
    //             resp.complete();
    //           } );
    //       }
    //     default:
    //       return new Observable( resp => {
    //         // resp.next( new HttpErrorResponse( {
    //         resp.next( new HttpResponse( {
    //           status: 201
    //           // error: new Error( 'Unknown RequestMethod' )
    //         } ) );
    //         resp.complete();
    //       } );
    //   }
    // } else
    if (url.endsWith( '/internal/v1/loadinglist/report/header' )) {
      return new Observable( resp => {
        resp.next( new HttpResponse( {
          status: 200,
          body: loadlistHeaderData
        } ) );
        resp.complete();
      } );
    }
    // } else if (url.endsWith( '/internal/v1/bagscan/packages' )) {
    //   return new Observable( resp => {
    //     resp.next( new HttpResponse( {
    //       status: 200,
    //       body: allPackages
    //     } ) );
    //     resp.complete();
    //   } );
    // }
    return next.handle( req );
  }
}

const loadlistHeaderData = {
    'loadlistNo': 123456,
    'dateFrom': '2018-09-15',
    'dateTo': '2018-09-16',
    'loadingAddress': 'DOM Kurier- und Botendienst, von-Hünenfeld-Str. 2, DE-50820 Köln',
    'hubAddress': 'DER KURIER GmbH & co. KG, Dörrwiese 2, DE-36285 Neuenstein',
    'shipmentCount': 1234,
    'packageCount': 4567,
    'totalWeight': 1278.91
  };
const
  allPackages = [
    {
      'zip': '17419',
      'city': 'Ahlbeck',
      'devliveryStation': 21,
      'loadlistNo': null,
      'packageId': 1060532266,
      'weight': 8,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 19,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 191,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 192,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 193,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 194,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 195,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 196,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 197,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 198,
      'loadlistNo': 103247,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '83395',
      'city': 'Freilassing',
      'devliveryStation': 21,
      'loadlistNo': 13246,
      'packageId': 3350033505,
      'weight': 1,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '50667',
      'city': 'Köln',
      'devliveryStation': 198,
      'loadlistNo': 13246,
      'packageId': 3350033506,
      'weight': 1,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '54296',
      'city': 'Trier',
      'devliveryStation': 21,
      'loadlistNo': 13246,
      'packageId': 23350007761,
      'weight': 37.5,
      'wrapperType': 5,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 95,
      'loadlistNo': 13246,
      'packageId': 7150111903,
      'weight': 1.9,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 95,
      'loadlistNo': 13246,
      'packageId': 7150111999,
      'weight': 1.9,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 17150111999,
      'weight': 1.9,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': null,
      'packageId': 79650002359,
      'weight': 1,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': null,
      'packageId': 79650002360,
      'weight': 1,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '65500',
      'city': 'Kelsterbach',
      'devliveryStation': 21,
      'loadlistNo': 103247,
      'packageId': 3350033509,
      'weight': 16,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '22880',
      'city': 'Wedel',
      'devliveryStation': 95,
      'loadlistNo': 100914,
      'packageId': 45550009804,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '82105',
      'city': 'Bratislava 2',
      'devliveryStation': 21,
      'loadlistNo': 100914,
      'packageId': 6450074123,
      'weight': 5.8,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '10405',
      'city': 'Berlin',
      'devliveryStation': 21,
      'loadlistNo': null,
      'packageId': 48850016377,
      'weight': 1.5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94327',
      'city': 'Bogen',
      'devliveryStation': 71,
      'loadlistNo': null,
      'packageId': 7280330923,
      'weight': 2.3,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94113',
      'city': 'Tiefenbach',
      'devliveryStation': 71,
      'loadlistNo': null,
      'packageId': 7280330924,
      'weight': 9.7,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94113',
      'city': 'Tiefenbach',
      'devliveryStation': 36,
      'loadlistNo': null,
      'packageId': 7280330925,
      'weight': 9.7,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '6188',
      'city': 'Sietzsch',
      'devliveryStation': 44,
      'loadlistNo': null,
      'packageId': 7280330926,
      'weight': 10.3,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '44894',
      'city': 'Bochum',
      'devliveryStation': 36,
      'loadlistNo': null,
      'packageId': 7280330927,
      'weight': 2.6,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '44894',
      'city': 'Bochum',
      'devliveryStation': 36,
      'loadlistNo': null,
      'packageId': 7280330928,
      'weight': 2.6,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 555.1,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 66,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 77.2,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 800,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 801,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 801,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 801,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 802,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 802,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 803,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 803,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 804,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 805,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 806,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 807,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 808,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 809,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 810,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 811,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 812,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 813,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 814,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 815,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 816,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 817,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 818,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 819,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 820,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 821,
      'loadlistNo': 103247,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    }
  ];

const
  allBagPackages = [
    {
      'zip': '17419',
      'city': 'Ahlbeck',
      'devliveryStation': 21,
      'loadlistNo': null,
      'packageId': 1060532266,
      'weight': 8,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '21502',
      'city': 'Geesthacht',
      'devliveryStation': 19,
      'loadlistNo': 12121,
      'packageId': 2350095755,
      'weight': 0.2,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '83395',
      'city': 'Freilassing',
      'devliveryStation': 21,
      'loadlistNo': 12121,
      'packageId': 3350033505,
      'weight': 1,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '50667',
      'city': 'Köln',
      'devliveryStation': 198,
      'loadlistNo': null,
      'packageId': 3350033506,
      'weight': 1,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '54296',
      'city': 'Trier',
      'devliveryStation': 21,
      'loadlistNo': null,
      'packageId': 23350007761,
      'weight': 2,
      'wrapperType': 5,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 95,
      'loadlistNo': null,
      'packageId': 7150111903,
      'weight': 1.9,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 95,
      'loadlistNo': null,
      'packageId': 7150111999,
      'weight': 1.9,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': 12121,
      'packageId': 17150111999,
      'weight': 1.9,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': null,
      'packageId': 79650002359,
      'weight': 1,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '20538',
      'city': 'Hamburg',
      'devliveryStation': 80,
      'loadlistNo': null,
      'packageId': 79650002360,
      'weight': 1,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '65500',
      'city': 'Kelsterbach',
      'devliveryStation': 21,
      'loadlistNo': 12312,
      'packageId': 3350033509,
      'weight': 6,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '22880',
      'city': 'Wedel',
      'devliveryStation': 95,
      'loadlistNo': 12312,
      'packageId': 45550009804,
      'weight': 1.6,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '82105',
      'city': 'Bratislava 2',
      'devliveryStation': 21,
      'loadlistNo': 12312,
      'packageId': 6450074123,
      'weight': 5.8,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '10405',
      'city': 'Berlin',
      'devliveryStation': 21,
      'loadlistNo': 12312,
      'packageId': 48850016377,
      'weight': 1.5,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '48432',
      'city': 'Rheine',
      'devliveryStation': 80,
      'loadlistNo': null,
      'packageId': 33950017833,
      'weight': 5,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94327',
      'city': 'Bogen',
      'devliveryStation': 71,
      'loadlistNo': null,
      'packageId': 7280330923,
      'weight': 1.3,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94113',
      'city': 'Tiefenbach',
      'devliveryStation': 71,
      'loadlistNo': null,
      'packageId': 7280330924,
      'weight': 1.7,
      'wrapperType': 0,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '94113',
      'city': 'Tiefenbach',
      'devliveryStation': 36,
      'loadlistNo': null,
      'packageId': 7280330925,
      'weight': 9.7,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '6188',
      'city': 'Sietzsch',
      'devliveryStation': 44,
      'loadlistNo': null,
      'packageId': 7280330926,
      'weight': 1.3,
      'wrapperType': 4,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '44894',
      'city': 'Bochum',
      'devliveryStation': 36,
      'loadlistNo': null,
      'packageId': 7280330927,
      'weight': 2.6,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    },
    {
      'zip': '44894',
      'city': 'Bochum',
      'devliveryStation': 36,
      'loadlistNo': 12312,
      'packageId': 7280330928,
      'weight': 2.6,
      'wrapperType': 91,
      'loadingDate': '04.07.17'
    }
  ];
