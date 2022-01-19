import { Directive, HostListener, ChangeDetectorRef, OnInit, OnDestroy, HostBinding, Input } from '@angular/core';
import { TopMenuLinkDirective } from './topmenu-link.directive';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appTopMenu]'
})

export class TopMenuDirective implements OnInit {

  protected navlinks: Array<TopMenuLinkDirective> = [];

  constructor() {
  }

  ngOnInit() {
  }

  public addLink(link: TopMenuLinkDirective): void {
    this.navlinks.push(link);
  }

  public closeOtherLinks(openLink: TopMenuLinkDirective): void {
    this.navlinks.forEach((link: TopMenuLinkDirective) => {
      if (link != openLink && (openLink.level.toString() === "1" || link.level === openLink.level)) {
        link.show = false;
      }
    });
  }

  @HostListener("mouseleave", ["$event"])
  onMouseOut(e: any) {
    this.navlinks.forEach((link: TopMenuLinkDirective) => {
      link.show = false;
    });
  }


}
