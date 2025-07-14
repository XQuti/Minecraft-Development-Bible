import {
  ActivatedRoute,
  NoPreloading,
  ROUTER_CONFIGURATION,
  ROUTER_PROVIDERS,
  ROUTES,
  Router,
  RouterModule,
  RouterOutlet,
  afterNextNavigation,
  init_router,
  init_router2,
  init_router_module,
  withPreloading
} from "./chunk-XHN2NZZ7.js";
import {
  AuthService,
  init_auth_service
} from "./chunk-5GEQCHP4.js";
import "./chunk-UEYUA37I.js";
import {
  init_testing as init_testing2,
  provideLocationMocks
} from "./chunk-JVAGSH3T.js";
import {
  CommonModule,
  init_common
} from "./chunk-CQJUDIX6.js";
import {
  Component,
  FactoryTarget,
  Injectable,
  NgModule,
  TestBed,
  ViewChild,
  __decorate,
  core_exports,
  init_core,
  init_esm,
  init_testing,
  init_tslib_es6,
  of,
  signal,
  ɵɵngDeclareClassMetadata,
  ɵɵngDeclareComponent,
  ɵɵngDeclareFactory,
  ɵɵngDeclareInjectable,
  ɵɵngDeclareInjector,
  ɵɵngDeclareNgModule
} from "./chunk-65V46IUV.js";
import {
  __async,
  __commonJS,
  __esm
} from "./chunk-TTULUY32.js";

// src/app/components/layout/navbar.component.ts
var NavbarComponent;
var init_navbar_component = __esm({
  "src/app/components/layout/navbar.component.ts"() {
    "use strict";
    init_tslib_es6();
    init_core();
    init_common();
    init_router();
    init_auth_service();
    NavbarComponent = class NavbarComponent2 {
      authService;
      currentUser$;
      constructor(authService) {
        this.authService = authService;
        this.currentUser$ = this.authService.currentUser$;
      }
      ngOnInit() {
      }
      login(provider) {
        this.authService.login(provider);
      }
      logout() {
        this.authService.logout();
      }
      static ctorParameters = () => [
        { type: AuthService }
      ];
    };
    NavbarComponent = __decorate([
      Component({
        selector: "app-navbar",
        standalone: true,
        imports: [CommonModule, RouterModule],
        template: `
    <nav class="bg-white shadow-lg border-b border-secondary-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <!-- Logo and main navigation -->
          <div class="flex items-center">
            <a routerLink="/" class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-minecraft-green border-2 border-black"></div>
              <span class="text-xl font-bold text-secondary-900">Minecraft Dev Bible</span>
            </a>
            
            <div class="hidden md:ml-8 md:flex md:space-x-8">
              <a routerLink="/tutorials" 
                 routerLinkActive="text-primary-600 border-primary-600"
                 class="border-transparent text-secondary-500 hover:text-secondary-700 hover:border-secondary-300 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200">
                Tutorials
              </a>
              <a routerLink="/forums" 
                 routerLinkActive="text-primary-600 border-primary-600"
                 class="border-transparent text-secondary-500 hover:text-secondary-700 hover:border-secondary-300 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200">
                Forums
              </a>
            </div>
          </div>

          <!-- User menu -->
          <div class="flex items-center space-x-4">
            <div *ngIf="currentUser$ | async as user; else loginButtons" class="flex items-center space-x-3">
              <img [src]="user.avatarUrl || '/assets/default-avatar.png'" 
                   [alt]="user.username"
                   class="w-8 h-8 rounded-full border-2 border-secondary-300">
              <span class="text-sm font-medium text-secondary-700">{{ user.username }}</span>
              <button (click)="logout()" 
                      class="text-sm text-secondary-500 hover:text-secondary-700 transition-colors duration-200">
                Logout
              </button>
            </div>
            
            <ng-template #loginButtons>
              <div class="flex space-x-2">
                <button (click)="login('github')" 
                        class="btn-secondary text-sm">
                  <svg class="w-4 h-4 mr-2 inline" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 0C4.477 0 0 4.484 0 10.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0110 4.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.203 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.942.359.31.678.921.678 1.856 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0020 10.017C20 4.484 15.522 0 10 0z" clip-rule="evenodd"></path>
                  </svg>
                  GitHub
                </button>
                <button (click)="login('google')" 
                        class="btn-primary text-sm">
                  <svg class="w-4 h-4 mr-2 inline" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                    <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                    <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                    <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                  </svg>
                  Google
                </button>
              </div>
            </ng-template>
          </div>
        </div>
      </div>
    </nav>
  `
      })
    ], NavbarComponent);
  }
});

// node_modules/@angular/router/fesm2022/testing.mjs
var RouterTestingModule, RootFixtureService, RootCmp, RouterTestingHarness;
var init_testing3 = __esm({
  "node_modules/@angular/router/fesm2022/testing.mjs"() {
    "use strict";
    init_core();
    init_core();
    init_testing();
    init_router2();
    init_router_module();
    init_testing2();
    RouterTestingModule = class _RouterTestingModule {
      static withRoutes(routes, config) {
        return {
          ngModule: _RouterTestingModule,
          providers: [
            { provide: ROUTES, multi: true, useValue: routes },
            { provide: ROUTER_CONFIGURATION, useValue: config ? config : {} }
          ]
        };
      }
      static \u0275fac = \u0275\u0275ngDeclareFactory({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: _RouterTestingModule, deps: [], target: FactoryTarget.NgModule });
      static \u0275mod = \u0275\u0275ngDeclareNgModule({ minVersion: "14.0.0", version: "20.1.0", ngImport: core_exports, type: _RouterTestingModule, exports: [RouterModule] });
      static \u0275inj = \u0275\u0275ngDeclareInjector({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: _RouterTestingModule, providers: [
        ROUTER_PROVIDERS,
        provideLocationMocks(),
        withPreloading(NoPreloading).\u0275providers,
        { provide: ROUTES, multi: true, useValue: [] }
      ], imports: [RouterModule] });
    };
    \u0275\u0275ngDeclareClassMetadata({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: RouterTestingModule, decorators: [{
      type: NgModule,
      args: [{
        exports: [RouterModule],
        providers: [
          ROUTER_PROVIDERS,
          provideLocationMocks(),
          withPreloading(NoPreloading).\u0275providers,
          { provide: ROUTES, multi: true, useValue: [] }
        ]
      }]
    }] });
    RootFixtureService = class _RootFixtureService {
      fixture;
      harness;
      createHarness() {
        if (this.harness) {
          throw new Error("Only one harness should be created per test.");
        }
        this.harness = new RouterTestingHarness(this.getRootFixture());
        return this.harness;
      }
      getRootFixture() {
        if (this.fixture !== void 0) {
          return this.fixture;
        }
        this.fixture = TestBed.createComponent(RootCmp);
        this.fixture.detectChanges();
        return this.fixture;
      }
      static \u0275fac = \u0275\u0275ngDeclareFactory({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: _RootFixtureService, deps: [], target: FactoryTarget.Injectable });
      static \u0275prov = \u0275\u0275ngDeclareInjectable({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: _RootFixtureService, providedIn: "root" });
    };
    \u0275\u0275ngDeclareClassMetadata({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: RootFixtureService, decorators: [{
      type: Injectable,
      args: [{ providedIn: "root" }]
    }] });
    RootCmp = class _RootCmp {
      outlet;
      routerOutletData = signal(void 0, ...ngDevMode ? [{ debugName: "routerOutletData" }] : []);
      static \u0275fac = \u0275\u0275ngDeclareFactory({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: _RootCmp, deps: [], target: FactoryTarget.Component });
      static \u0275cmp = \u0275\u0275ngDeclareComponent({ minVersion: "14.0.0", version: "20.1.0", type: _RootCmp, isStandalone: true, selector: "ng-component", viewQueries: [{ propertyName: "outlet", first: true, predicate: RouterOutlet, descendants: true }], ngImport: core_exports, template: '<router-outlet [routerOutletData]="routerOutletData()"></router-outlet>', isInline: true, dependencies: [{ kind: "directive", type: RouterOutlet, selector: "router-outlet", inputs: ["name", "routerOutletData"], outputs: ["activate", "deactivate", "attach", "detach"], exportAs: ["outlet"] }] });
    };
    \u0275\u0275ngDeclareClassMetadata({ minVersion: "12.0.0", version: "20.1.0", ngImport: core_exports, type: RootCmp, decorators: [{
      type: Component,
      args: [{
        template: '<router-outlet [routerOutletData]="routerOutletData()"></router-outlet>',
        imports: [RouterOutlet]
      }]
    }], propDecorators: { outlet: [{
      type: ViewChild,
      args: [RouterOutlet]
    }] } });
    RouterTestingHarness = class {
      /**
       * Creates a `RouterTestingHarness` instance.
       *
       * The `RouterTestingHarness` also creates its own root component with a `RouterOutlet` for the
       * purposes of rendering route components.
       *
       * Throws an error if an instance has already been created.
       * Use of this harness also requires `destroyAfterEach: true` in the `ModuleTeardownOptions`
       *
       * @param initialUrl The target of navigation to trigger before returning the harness.
       */
      static create(initialUrl) {
        return __async(this, null, function* () {
          const harness = TestBed.inject(RootFixtureService).createHarness();
          if (initialUrl !== void 0) {
            yield harness.navigateByUrl(initialUrl);
          }
          return harness;
        });
      }
      /**
       * Fixture of the root component of the RouterTestingHarness
       */
      fixture;
      /** @internal */
      constructor(fixture) {
        this.fixture = fixture;
      }
      /** Instructs the root fixture to run change detection. */
      detectChanges() {
        this.fixture.detectChanges();
      }
      /** The `DebugElement` of the `RouterOutlet` component. `null` if the outlet is not activated. */
      get routeDebugElement() {
        const outlet = this.fixture.componentInstance.outlet;
        if (!outlet || !outlet.isActivated) {
          return null;
        }
        return this.fixture.debugElement.query((v) => v.componentInstance === outlet.component);
      }
      /** The native element of the `RouterOutlet` component. `null` if the outlet is not activated. */
      get routeNativeElement() {
        return this.routeDebugElement?.nativeElement ?? null;
      }
      navigateByUrl(url, requiredRoutedComponentType) {
        return __async(this, null, function* () {
          const router = TestBed.inject(Router);
          let resolveFn;
          const redirectTrackingPromise = new Promise((resolve) => {
            resolveFn = resolve;
          });
          afterNextNavigation(TestBed.inject(Router), resolveFn);
          yield router.navigateByUrl(url);
          yield redirectTrackingPromise;
          this.fixture.detectChanges();
          const outlet = this.fixture.componentInstance.outlet;
          if (outlet && outlet.isActivated && outlet.activatedRoute.component) {
            const activatedComponent = outlet.component;
            if (requiredRoutedComponentType !== void 0 && !(activatedComponent instanceof requiredRoutedComponentType)) {
              throw new Error(`Unexpected routed component type. Expected ${requiredRoutedComponentType.name} but got ${activatedComponent.constructor.name}`);
            }
            return activatedComponent;
          } else {
            if (requiredRoutedComponentType !== void 0) {
              throw new Error(`Unexpected routed component type. Expected ${requiredRoutedComponentType.name} but the navigation did not activate any component.`);
            }
            return null;
          }
        });
      }
    };
  }
});

// src/app/components/layout/navbar.component.spec.ts
var require_navbar_component_spec = __commonJS({
  "src/app/components/layout/navbar.component.spec.ts"(exports) {
    init_testing();
    init_esm();
    init_navbar_component();
    init_auth_service();
    init_router();
    init_testing3();
    describe("NavbarComponent", () => {
      let component;
      let fixture;
      let mockAuthService;
      const mockUser = {
        id: 1,
        email: "test@example.com",
        username: "testuser",
        avatarUrl: void 0,
        provider: "local",
        roles: ["USER"]
      };
      beforeEach(() => __async(null, null, function* () {
        const authServiceSpy = jasmine.createSpyObj("AuthService", [
          "login",
          "logout"
        ], {
          currentUser$: of(mockUser)
        });
        yield TestBed.configureTestingModule({
          imports: [NavbarComponent, RouterTestingModule],
          providers: [
            { provide: AuthService, useValue: authServiceSpy },
            {
              provide: ActivatedRoute,
              useValue: {
                snapshot: { params: {} },
                params: of({}),
                queryParams: of({})
              }
            }
          ]
        }).compileComponents();
        fixture = TestBed.createComponent(NavbarComponent);
        component = fixture.componentInstance;
        mockAuthService = TestBed.inject(AuthService);
      }));
      it("should create", () => {
        expect(component).toBeTruthy();
      });
      it("should initialize with currentUser$ observable", () => {
        expect(component.currentUser$).toBeDefined();
        component.currentUser$.subscribe((user) => {
          expect(user).toEqual(mockUser);
        });
      });
      it("should call authService.login with provider", () => {
        component.login("github");
        expect(mockAuthService.login).toHaveBeenCalledWith("github");
        component.login("google");
        expect(mockAuthService.login).toHaveBeenCalledWith("google");
      });
      it("should call authService.logout", () => {
        component.logout();
        expect(mockAuthService.logout).toHaveBeenCalled();
      });
      it("should handle null user in currentUser$ observable", () => {
        Object.defineProperty(mockAuthService, "currentUser$", {
          value: of(null)
        });
        component = new NavbarComponent(mockAuthService);
        component.currentUser$.subscribe((user) => {
          expect(user).toBeNull();
        });
      });
      it("should render correctly with user data", () => {
        fixture.detectChanges();
        const compiled = fixture.nativeElement;
        expect(compiled.querySelector("nav")).toBeTruthy();
        expect(compiled.textContent).toContain("Minecraft Dev Bible");
      });
      it("should have navigation links", () => {
        fixture.detectChanges();
        const compiled = fixture.nativeElement;
        const tutorialsLink = compiled.querySelector('a[routerLink="/tutorials"]');
        const forumsLink = compiled.querySelector('a[routerLink="/forums"]');
        expect(tutorialsLink).toBeTruthy();
        expect(forumsLink).toBeTruthy();
        expect(tutorialsLink?.textContent?.trim()).toBe("Tutorials");
        expect(forumsLink?.textContent?.trim()).toBe("Forums");
      });
    });
  }
});
export default require_navbar_component_spec();
/*! Bundled license information:

@angular/router/fesm2022/testing.mjs:
  (**
   * @license Angular v20.1.0
   * (c) 2010-2025 Google LLC. https://angular.io/
   * License: MIT
   *)
*/
//# sourceMappingURL=spec-navbar.component.spec.js.map
