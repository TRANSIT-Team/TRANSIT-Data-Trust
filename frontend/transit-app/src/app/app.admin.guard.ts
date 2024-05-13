import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import {KeycloakService} from "keycloak-angular";


@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {

    if (this.keycloakService.isUserInRole('adminGlobal')) {
      return true; // User has admin role, allow access
    } else {
      this.router.navigate(['/']); // Redirect to a different route if user doesn't have admin role
      return false;
    }
  }
}
