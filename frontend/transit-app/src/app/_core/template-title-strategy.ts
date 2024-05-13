import { bootstrapApplication, Title } from '@angular/platform-browser';
import { RouterModule, RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TemplatePageTitleStrategy extends TitleStrategy {
  constructor(private readonly title: Title) {
    super();
  }

  override updateTitle(routerState: RouterStateSnapshot) {
    const title = this.buildTitle(routerState);
    if (title !== undefined) {

      if (title !== "") {
      this.title.setTitle(`TRANSIT | ${title}`);
      }
      else{
        this.title.setTitle(`TRANSIT`);

      }

    }
  }
}
