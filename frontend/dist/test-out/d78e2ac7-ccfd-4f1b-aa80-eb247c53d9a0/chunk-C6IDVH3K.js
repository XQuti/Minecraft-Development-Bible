import {
  NoPreloading,
  ROUTER_CONFIGURATION,
  ROUTER_PROVIDERS,
  ROUTES,
  Router,
  RouterModule,
  RouterOutlet,
  afterNextNavigation,
  init_router_Dwfin5Au,
  init_router_module_DTJgGWLd,
  withPreloading
} from "./chunk-2UTAPEJQ.js";
import {
  init_testing as init_testing2,
  provideLocationMocks
} from "./chunk-6CFRQRYO.js";
import {
  Component,
  FactoryTarget,
  Injectable,
  NgModule,
  TestBed,
  ViewChild,
  core_exports,
  init_core,
  init_testing,
  signal,
  ɵɵngDeclareClassMetadata,
  ɵɵngDeclareComponent,
  ɵɵngDeclareFactory,
  ɵɵngDeclareInjectable,
  ɵɵngDeclareInjector,
  ɵɵngDeclareNgModule
} from "./chunk-T6WWCSSA.js";
import {
  __async,
  __esm
} from "./chunk-TTULUY32.js";

// node_modules/@angular/router/fesm2022/testing.mjs
var RouterTestingModule, RootFixtureService, RootCmp, RouterTestingHarness;
var init_testing3 = __esm({
  "node_modules/@angular/router/fesm2022/testing.mjs"() {
    "use strict";
    init_core();
    init_core();
    init_testing();
    init_router_Dwfin5Au();
    init_router_module_DTJgGWLd();
    init_testing2();
    RouterTestingModule = class _RouterTestingModule {
      static withRoutes(routes, config) {
        return {
          ngModule: _RouterTestingModule,
          providers: [{
            provide: ROUTES,
            multi: true,
            useValue: routes
          }, {
            provide: ROUTER_CONFIGURATION,
            useValue: config ? config : {}
          }]
        };
      }
      static \u0275fac = \u0275\u0275ngDeclareFactory({
        minVersion: "12.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RouterTestingModule,
        deps: [],
        target: FactoryTarget.NgModule
      });
      static \u0275mod = \u0275\u0275ngDeclareNgModule({
        minVersion: "14.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RouterTestingModule,
        exports: [RouterModule]
      });
      static \u0275inj = \u0275\u0275ngDeclareInjector({
        minVersion: "12.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RouterTestingModule,
        providers: [ROUTER_PROVIDERS, provideLocationMocks(), withPreloading(NoPreloading).\u0275providers, {
          provide: ROUTES,
          multi: true,
          useValue: []
        }],
        imports: [RouterModule]
      });
    };
    \u0275\u0275ngDeclareClassMetadata({
      minVersion: "12.0.0",
      version: "19.2.14",
      ngImport: core_exports,
      type: RouterTestingModule,
      decorators: [{
        type: NgModule,
        args: [{
          exports: [RouterModule],
          providers: [ROUTER_PROVIDERS, provideLocationMocks(), withPreloading(NoPreloading).\u0275providers, {
            provide: ROUTES,
            multi: true,
            useValue: []
          }]
        }]
      }]
    });
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
      static \u0275fac = \u0275\u0275ngDeclareFactory({
        minVersion: "12.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RootFixtureService,
        deps: [],
        target: FactoryTarget.Injectable
      });
      static \u0275prov = \u0275\u0275ngDeclareInjectable({
        minVersion: "12.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RootFixtureService,
        providedIn: "root"
      });
    };
    \u0275\u0275ngDeclareClassMetadata({
      minVersion: "12.0.0",
      version: "19.2.14",
      ngImport: core_exports,
      type: RootFixtureService,
      decorators: [{
        type: Injectable,
        args: [{
          providedIn: "root"
        }]
      }]
    });
    RootCmp = class _RootCmp {
      outlet;
      routerOutletData = signal(void 0);
      static \u0275fac = \u0275\u0275ngDeclareFactory({
        minVersion: "12.0.0",
        version: "19.2.14",
        ngImport: core_exports,
        type: _RootCmp,
        deps: [],
        target: FactoryTarget.Component
      });
      static \u0275cmp = \u0275\u0275ngDeclareComponent({
        minVersion: "14.0.0",
        version: "19.2.14",
        type: _RootCmp,
        isStandalone: true,
        selector: "ng-component",
        viewQueries: [{
          propertyName: "outlet",
          first: true,
          predicate: RouterOutlet,
          descendants: true
        }],
        ngImport: core_exports,
        template: '<router-outlet [routerOutletData]="routerOutletData()"></router-outlet>',
        isInline: true,
        dependencies: [{
          kind: "directive",
          type: RouterOutlet,
          selector: "router-outlet",
          inputs: ["name", "routerOutletData"],
          outputs: ["activate", "deactivate", "attach", "detach"],
          exportAs: ["outlet"]
        }]
      });
    };
    \u0275\u0275ngDeclareClassMetadata({
      minVersion: "12.0.0",
      version: "19.2.14",
      ngImport: core_exports,
      type: RootCmp,
      decorators: [{
        type: Component,
        args: [{
          template: '<router-outlet [routerOutletData]="routerOutletData()"></router-outlet>',
          imports: [RouterOutlet]
        }]
      }],
      propDecorators: {
        outlet: [{
          type: ViewChild,
          args: [RouterOutlet]
        }]
      }
    });
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

export {
  RouterTestingModule,
  init_testing3 as init_testing
};
/*! Bundled license information:

@angular/router/fesm2022/testing.mjs:
  (**
   * @license Angular v19.2.14
   * (c) 2010-2025 Google LLC. https://angular.io/
   * License: MIT
   *)
*/
//# sourceMappingURL=chunk-C6IDVH3K.js.map
