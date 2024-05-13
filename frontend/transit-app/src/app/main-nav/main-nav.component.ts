import {ChangeDetectorRef, Component, ElementRef, Inject, Injectable, OnInit, ViewChild} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {BehaviorSubject, mergeMap, Observable, of, Subscription, timer} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {KeycloakService} from 'keycloak-angular';
import {KeycloakProfile} from 'keycloak-js';
import {MainNavService} from './shared/main-nav.service';


import {DOCUMENT, ViewportScroller} from '@angular/common';
import {AuthService} from '../_core/auth.service';
import {ActivatedRoute, Router, TitleStrategy} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {MatListItem} from "@angular/material/list";
import {Globals} from "../_core/globals";
import {Company} from "../company/shared/company";
import {GlobalService} from "../_core/shared/global.service";
import {LoaderService} from "../loader/loader.service";
import {ChatEntry} from "../chat/shared/chat";
import {ChatService} from "../chat/shared/chat.service";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  styleUrls: ['./main-nav.component.css']
})
export class MainNavComponent implements OnInit {
  @ViewChild('matListItemAdminSub') matListItemAdmin: ElementRef;
  @ViewChild('matListItemUserSub') matListItemUser: ElementRef;
  @ViewChild('matListItemDemoSub') matListItemDemo: ElementRef;


  //public userProfile: KeycloakProfile | null = null;

  //observable
  //isLoggedIn:Observable<boolean>=of(false);
  isAdmin = false;
  pageTitle: any = "";
  isExpanded = true;
  isExpandedUser = true;
  isExpandedDemo = true;
  isExpandedAdmin = true;
  isExpandedSecurity = true;
  showSubmenu: boolean = false;
  isShowing = false;
  isShowingUser = false;
  isShowingDemo = false;
  isShowingAdmin = false;
  isShowingSecurity = false;
  showSubSubMenu: boolean = false;
  showSubSubDemo: boolean = false;
  showSubSecurity: boolean = false;
  showSubmenuUser: boolean = false;
  showSubmenuDemo: boolean = false;
  showSubmenuAdmin: boolean = false;
  showSubmenuSecurity: boolean = false;
  hasRoles: any = false;
  userName: string = '';
  isLoggedIn$: any = new BehaviorSubject(false);
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );
  miniVariant: boolean = true;
  windowScrolled = false;
  basicKeycloakRoles: string[] = ['manage-account', 'manage-account-links', 'view-profile', 'offline_access', 'uma_authorization', 'default-roles-transit-project-local', 'default-roles-transit-project'];
  globalPageTitel: string;
  private subscription: Subscription;
  loginSubscripton: Subscription;
  newMessages: number = 0;
  newMessagesHidden: any = "hidden";
  mSubscripton: Subscription;

  constructor(
    private readonly keycloakService: KeycloakService,
    private breakpointObserver: BreakpointObserver,
    public nav: MainNavService,
    public authService: AuthService,
    private router: Router,
    @Inject(DOCUMENT) private document: Document,
    public title: Title,
    private route: ActivatedRoute,
    private viewportScroller: ViewportScroller,
    public globals: Globals, private globalService: GlobalService,
    private cdr: ChangeDetectorRef,
    public loaderService: LoaderService,
    private chatService: ChatService,
    private snackbarService: SnackbarService
  ) {


  }


  ngOnInit() {


    window.addEventListener('scroll', () => {
      this.windowScrolled = window.pageYOffset !== 0;
    });

    this.authService.getLoggedInStatus().subscribe((data) => {
      this.setLoggedIn(data);
      if (data) {
        this.checkLoginStatus();
      }
    });

    this.checkChats();
  }

  checkChats() {
    this.mSubscripton = timer(0, 15000).pipe(
      mergeMap(() => this.getChatOverview()),
    ).subscribe();
  }

  getChatOverview(): Observable<any> {
    return this.chatService.getChatOverview().pipe(
      map((coll: ChatEntry[]) => {
        this.newMessages = 0;
        this.newMessagesHidden = "hidden";

        let chatSummaries: ChatEntry[] = [];
        chatSummaries = coll;
        let countM: number = chatSummaries.filter((c: ChatEntry) => c.readStatus == false && c.person == "YOU").length;

        if (countM > 0) {
          this.newMessages = countM;
          this.newMessagesHidden = undefined;
        } else {
          this.newMessagesHidden = "hidden";
        }

      })
    );
  }

  checkLoginStatus() {
    this.loginSubscripton = timer(0, 10000).pipe(
      mergeMap(() => this.authService.getLoggedInStatus()),
      map((data: any) => {
        if (data == false) {
          // this.router.navigate([this.document.location.origin], {});
          // window.location.href=this.document.location.origin;
          this.login();
          window.location.href = this.document.location.origin;
        }
      })
    ).subscribe();
  }

  ngAfterViewInit() {
    this.miniVariant = true;
  }


  ngAfterContentChecked() {
    this.globalPageTitel = this.title.getTitle().replace("TRANSIT | ", "");
    if (this.globalPageTitel == "Auftrag bearbeiten #") {
      this.globalPageTitel = this.title.getTitle().replace("TRANSIT | Auftrag bearbeiten #", "Auftrag #" + this.router.url.replace('/orders/order/', ''));
    }
  }

  setLoggedIn(data: any) {


    //   this.isLoggedIn$ = of(this.authService.getLoggedIn());
    this.isLoggedIn$ = of(data);

    if (data == true) {
      this.checkRoles();
      this.userName = this.keycloakService.getUsername();
    }

  }

  login() {
    this.keycloakService.login();
  }

  register() {
    this.keycloakService.register();
  }


  logout(url: any = this.document.location.origin) {
    console.log(url);
    this.keycloakService.logout(url);
  }

  checkRoles() {

    let userRoles: string[] = this.keycloakService.getUserRoles(true);
    let baseRoles = this.basicKeycloakRoles;
    userRoles = userRoles.filter((el) => !baseRoles.includes(el));

    if (userRoles.length > 0) {
      //   this.router.navigateByUrl(`user`);
      this.hasRoles = true;
      if (this.keycloakService.isUserInRole('adminGlobal')) {
        this.isAdmin = true;
      }
    } else {
      if (this.router.url != '/user') {
        // this.router.navigateByUrl(`user`);
      }
    }

  }

  scrollToTop() {
    this.viewportScroller.scrollToPosition([0, 0]);
  }

  showSubmenuDemoHandler() {

    if (this.miniVariant) {
      this.changeMini(!this.miniVariant);
    }
    this.showSubmenuDemo = !this.showSubmenuDemo;

  }

  showSubmenuUserHandler() {

    if (this.miniVariant) {
      this.changeMini(!this.miniVariant);
    }
    this.showSubmenuUser = !this.showSubmenuUser;

  }

  showSubmenuAdminHandler() {

    if (this.miniVariant) {
      this.changeMini(!this.miniVariant);
    }
    this.showSubmenuAdmin = !this.showSubmenuAdmin;

  }

  showSubmenuSecurityHandler() {

    if (this.miniVariant) {
      this.changeMini(!this.miniVariant);
    }
    this.showSubmenuSecurity = !this.showSubmenuSecurity;

  }


  changeMini(b: boolean) {
    this.miniVariant = b;

    if (b = true) {
      this.showSubSubMenu = false;
      this.showSubSubDemo = false;
      this.showSubSecurity = false;
      this.showSubmenuUser = false;
      this.showSubmenuDemo = false;
      this.showSubmenuAdmin = false;
      this.showSubmenuSecurity = false;
    }
  }


  hasActiveLinkChild(type: any): boolean {


    let parentElement: any = null;

    if (type == 'matListItemUser') {
      parentElement = this.matListItemUser;
    }
    if (type == 'matListItemDemo') {
      parentElement = this.matListItemDemo;
    }
    if (type == 'matListItemAdmin') {
      parentElement = this.matListItemAdmin;
    }

    if (parentElement != undefined) {

      if (parentElement.nativeElement != undefined) {


        const childLinks = parentElement.nativeElement.querySelectorAll('a');

        if (childLinks != undefined) {
          const hasActiveLink = Array.from(childLinks).some((link: any) =>
            link.classList.contains('active-link')
          );
          return hasActiveLink;
        }
      }
    }

    return false;
  }

  isLinkActive(route: string): boolean {
    return this.router.isActive(route, true);
  }

}
