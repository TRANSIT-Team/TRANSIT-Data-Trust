import { Component, OnInit } from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogInfoComponent} from "../../dialog/dialog-info.component";

@Component({
  selector: 'app-home-anonym',
  templateUrl: './home-anonym.component.html',
  styleUrls: ['./home-anonym.component.css']
})
export class HomeAnonymComponent implements OnInit {

  constructor(private readonly keycloakService: KeycloakService,public dialog: MatDialog) {
  }

  ngOnInit(): void {
  }
  public login() {
    this.keycloakService.login();
  }

  public register() {
    this.keycloakService.register();
  }

  openInfoDialog(type: string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      type: type
    };
    const dialogRef = this.dialog.open(DialogInfoComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((result: any) => {

    });
  }
}
