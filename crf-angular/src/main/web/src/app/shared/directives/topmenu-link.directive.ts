import {
  Directive, HostBinding, Inject, Input, OnInit, OnDestroy, Output, EventEmitter
} from '@angular/core';

import { TopMenuDirective } from "./topmenu.directive";


@Directive({
  selector: "[appTopMenulink]"
})
export class TopMenuLinkDirective implements OnInit, OnDestroy {

  @Input()
  public parent: string;

  @Input()
  public level: number;

  @HostBinding('class.show')
  @Input()
  get show(): boolean {
    return this._show;
  }
  set show(value: boolean) {
    this._show = value;
    if (value) {
      this.topNav.closeOtherLinks(this);
    }
  }

  protected _show: boolean;

  protected topNav: TopMenuDirective;

  public constructor(
    @Inject(TopMenuDirective) topNav: TopMenuDirective) {
    this.topNav = topNav;
  }

  public ngOnInit(): any {
    this.topNav.addLink(this);
  }

  public ngOnDestroy(): any {
  }

  // public toggle(): any {
  //   this.show = !this.show;
  // }

  public openDropdown(): any {
    this.show = true;
  }


}
