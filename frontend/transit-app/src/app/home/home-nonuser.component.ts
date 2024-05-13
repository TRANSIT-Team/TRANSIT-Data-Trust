import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {ActivatedRoute} from "@angular/router";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogInfoComponent} from "../dialog/dialog-info.component";

@Component({
  selector: 'app-home-nonuser',
  templateUrl: './home-nonuser.component.html',
  styleUrls: ['./home-nonuser.component.css']
})
export class HomeNonuserComponent implements OnInit {

  privacypolicy: boolean = false;
  imprint: boolean = false;

  constructor(private readonly keycloakService: KeycloakService, private route: ActivatedRoute, public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.route.fragment.subscribe((fragment: any) => {
      if (fragment && fragment === 'imprint') {
        this.imprint = true;
      }
      if (fragment && fragment === 'privacy-policy') {
        this.privacypolicy = true;
      }
    });

  }

  showImprint() {
    this.imprint = true;
    this.privacypolicy = false;
  }

  showPrivacyPolicy() {
    this.imprint = false;
    this.privacypolicy = true;
  }


}
