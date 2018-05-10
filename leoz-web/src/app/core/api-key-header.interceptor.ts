import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export class ApiKeyHeaderInterceptor implements HttpInterceptor {

  intercept( req: HttpRequest<any>, next: HttpHandler ): Observable<HttpEvent<any>> {
    if (req.url === environment.pingURL) {
      return next.handle( req );
    }
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    let apiKeyHeader = req.headers.append( 'Content-Type', 'application/json' );
    if (currUser && currUser.key) {
      apiKeyHeader = apiKeyHeader.append( 'x-api-key', currUser.key );
    }
    return next.handle( req.clone( { headers: apiKeyHeader } ) );
  }
}
