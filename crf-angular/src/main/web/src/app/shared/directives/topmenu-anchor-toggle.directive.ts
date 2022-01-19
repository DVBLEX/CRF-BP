import { Directive, HostListener, Inject } from '@angular/core';

import { TopMenuLinkDirective } from "./topmenu-link.directive";

@Directive({
  selector: '[appTopMenuAnchorToggle]'
})
export class TopMenuAnchorToggleDirective {
  protected navlink: TopMenuLinkDirective;

  constructor(
    @Inject(TopMenuLinkDirective) navlink: TopMenuLinkDirective
  ) {
    this.navlink = navlink;
  }

  // @HostListener("click", ["$event"])
  // onClick() {
  //   this.navlink.toggle();
  // }

  @HostListener("mouseenter", ["$event"])
  onMouseOver(e: any) {
    this.navlink.openDropdown();

  }

  // @HostListener("mouseleave", ["$event"])
  // onMouseOut(e: any) {
  //   this.navlink.toggle();
  // }

}
