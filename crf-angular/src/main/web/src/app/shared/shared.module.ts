import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {AuthorizationService} from "./services/authorization.service";
import {CookieService} from "ngx-cookie-service";
import {TokenStorageService} from "./services/token-storage.service";
import {TopMenuLinkDirective} from "./directives/topmenu-link.directive";
import {TopMenuDropdownDirective} from "./directives/topmenu-dropdown.directive";
import {TopMenuAnchorToggleDirective} from "./directives/topmenu-anchor-toggle.directive";
import {TopMenuDirective} from "./directives/topmenu.directive";
import {CommonModule} from "@angular/common";
import {RouterModule} from "@angular/router";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {NavbarComponent} from "./components/navbar/navbar.component";
import {FormsModule} from "@angular/forms";
import {HorizontalMenuComponent} from "./components/horizontal-menu/horizontal-menu.component";
import {FooterComponent} from "./components/footer/footer.component";
import {NgbdSortableHeader} from "./directives/sortable.directive";
import {authInterceptorProvider, AuthInterceptorService} from "./services/auth-interceptor.service";
import {GridService} from "./services/grid.service";

@NgModule({
    declarations: [
        TopMenuDirective,
        TopMenuLinkDirective,
        TopMenuDropdownDirective,
        TopMenuAnchorToggleDirective,
        NgbdSortableHeader,
        HorizontalMenuComponent,
        NavbarComponent,
        FooterComponent
    ],

    imports: [
        RouterModule,
        CommonModule,
        NgbModule,
        HttpClientModule,
        FormsModule
    ],

    exports: [
        HorizontalMenuComponent,
        TopMenuDirective,
        NavbarComponent,
        FooterComponent,
        NgbdSortableHeader,
    ],

    providers: [
        GridService,
        AuthorizationService,
        CookieService,
        TokenStorageService,
        AuthInterceptorService,
        authInterceptorProvider
    ]
})

export class SharedModule {

}
