import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

export class MockHttpInterceptor implements HttpInterceptor {

  intercept( req: HttpRequest<any>, next: HttpHandler ): Observable<HttpEvent<any>> {
    const url: string = req.url;

    if (url.endsWith( '/internal/v1/loadinglist/report/header' ) || url.endsWith( '/internal/v1/bagscan/report/header' )) {
      return new Observable( resp => {
        resp.next( new HttpResponse( {
          status: 200,
          body: loadlistHeaderData
        } ) );
        resp.complete();
      } );
    }
    return next.handle( req );
  }
}

const loadlistHeaderData = {
    'loadlistNo': 123456,
    'dateFrom': '2018-09-15',
    'dateTo': '2018-09-16',
    'loadingAddress': 'hardcoded from mock-interceptor, von-Hünenfeld-Str. 2, DE-50820 Köln',
    'hubAddress': 'DER KURIER GmbH & co. KG, Dörrwiese 2, DE-36285 Neuenstein',
    'shipmentCount': 1234,
    'packageCount': 4567,
    'totalWeight': 1278.91
  };
