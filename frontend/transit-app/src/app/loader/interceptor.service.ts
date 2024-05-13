import {HttpHandler, HttpInterceptor, HttpRequest, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {finalize, Observable} from 'rxjs';
import { LoaderService } from './loader.service';

@Injectable({
  providedIn: 'root'
})
export class InterceptorService implements HttpInterceptor{

  constructor(public loaderSerivce:LoaderService) {}
    intercept(req:HttpRequest<any>, next:HttpHandler):Observable<HttpEvent<any>>{


      this.loaderSerivce.isLoading.next(true);

      return next.handle(req).pipe(
        finalize(
          () => {
            this.loaderSerivce.isLoading.next(false);
          }
        )

      )
  }
}
