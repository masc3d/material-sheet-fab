import { Headers } from '@angular/http';
export class ApiKeyHeaderFactory {

  public static headers( apiKey?: string ): Headers {
    const headers = new Headers();
    headers.append( 'Content-Type', 'application/json' );
    if (apiKey) {
      headers.append( 'x-api-key', apiKey );
    }
    return headers;
  }
}
