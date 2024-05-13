import {Injectable} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';
import {BehaviorSubject, from, Observable, of} from 'rxjs';
import { AuthService } from 'src/app/_core/auth.service';


@Injectable({
  providedIn: 'root'
})
export class MainNavService {

  visible: boolean;

  constructor(private readonly keycloakService: KeycloakService) {
    this.visible = false;

  }

  hide() {
    this.visible = false;
  }

  show() {
    this.visible = true;
  }

  //initialservice app.initializer
  getLoggedInStatus(): Observable<boolean> {
    const observableKc = from(this.keycloakService.isLoggedIn());
    return observableKc;
  }

  login(isLoggedIn$:any) {
    this.getLoggedInStatus().subscribe({
      next: data => {
        isLoggedIn$ = of(data);
      }
    });
  }


  toggle() {
    this.visible = !this.visible;
  }

  validate() {
    console.log("validate");
    this.keycloakService.isLoggedIn()
      .then(isLoggedIn => {
        this.visible = (isLoggedIn);
      })

  }


}

