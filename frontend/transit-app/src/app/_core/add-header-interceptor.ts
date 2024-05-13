import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';

export class AddHeaderInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let clonedRequest: any;
    ////console.log(req.url);
    //api URL check if its api.transit-project.de
    clonedRequest = req.clone();

    //replacing %26 with &
    if (req.method == 'GET' && (req.url.startsWith(environment.backend) || req.url.startsWith(environment.altBackend))) {
      clonedRequest.urlWithParams = clonedRequest.urlWithParams.replace('%26', '&');
    }

    if (req.method == 'PATCH' && (req.url.startsWith(environment.backend) || req.url.startsWith(environment.altBackend))) {
      // Clone the request to add the new header
      clonedRequest = req.clone({headers: req.headers.append('Content-Type', 'application/merge-patch+json')});
    }

    // Pass the cloned request instead of the original request to the next handle
    return next.handle(clonedRequest);
  }
}
