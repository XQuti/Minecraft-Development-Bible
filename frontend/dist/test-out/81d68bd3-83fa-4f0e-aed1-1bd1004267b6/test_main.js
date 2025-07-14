import {
  MockPlatformLocation,
  init_testing as init_testing2
} from "./chunk-6CFRQRYO.js";
import {
  BrowserModule,
  PlatformLocation,
  getDOM,
  init_browser_D_u_fknz,
  init_common,
  init_platform_browser,
  platformBrowser
} from "./chunk-5EW7VPBW.js";
import {
  APP_ID,
  COMPILER_OPTIONS,
  ChangeDetectionScheduler,
  ChangeDetectionSchedulerImpl,
  Compiler,
  CompilerConfig,
  CompilerFactory,
  DOCUMENT,
  FactoryTarget,
  Inject,
  Injectable,
  Injector,
  NgModule,
  ResourceLoader,
  TestComponentRenderer,
  Version,
  ViewEncapsulation,
  core_exports,
  createPlatformFactory,
  getTestBed,
  init_compiler,
  init_core,
  init_testing,
  internalProvideZoneChangeDetection,
  ɵɵngDeclareClassMetadata,
  ɵɵngDeclareFactory,
  ɵɵngDeclareInjectable,
  ɵɵngDeclareInjector,
  ɵɵngDeclareNgModule
} from "./chunk-T6WWCSSA.js";
import "./chunk-TTULUY32.js";

// node_modules/@angular/build/src/builders/karma/polyfills/init_test_bed.js
init_testing();

// node_modules/@angular/platform-browser-dynamic/fesm2022/testing.mjs
init_core();
init_core();

// node_modules/@angular/platform-browser-dynamic/fesm2022/platform-browser-dynamic.mjs
init_core();
init_core();
init_compiler();
init_platform_browser();
var VERSION = new Version("19.2.14");
var COMPILER_PROVIDERS = [{
  provide: Compiler,
  useFactory: () => new Compiler()
}];
var JitCompilerFactory = class {
  _defaultOptions;
  /** @internal */
  constructor(defaultOptions) {
    const compilerOptions = {
      defaultEncapsulation: ViewEncapsulation.Emulated
    };
    this._defaultOptions = [compilerOptions, ...defaultOptions];
  }
  createCompiler(options = []) {
    const opts = _mergeOptions(this._defaultOptions.concat(options));
    const injector = Injector.create({
      providers: [COMPILER_PROVIDERS, {
        provide: CompilerConfig,
        useFactory: () => {
          return new CompilerConfig({
            defaultEncapsulation: opts.defaultEncapsulation,
            preserveWhitespaces: opts.preserveWhitespaces
          });
        },
        deps: []
      }, opts.providers]
    });
    return injector.get(Compiler);
  }
};
function _mergeOptions(optionsArr) {
  return {
    defaultEncapsulation: _lastDefined(optionsArr.map((options) => options.defaultEncapsulation)),
    providers: _mergeArrays(optionsArr.map((options) => options.providers)),
    preserveWhitespaces: _lastDefined(optionsArr.map((options) => options.preserveWhitespaces))
  };
}
function _lastDefined(args) {
  for (let i = args.length - 1; i >= 0; i--) {
    if (args[i] !== void 0) {
      return args[i];
    }
  }
  return void 0;
}
function _mergeArrays(parts) {
  const result = [];
  parts.forEach((part) => part && result.push(...part));
  return result;
}
var ResourceLoaderImpl = class _ResourceLoaderImpl extends ResourceLoader {
  get(url) {
    let resolve;
    let reject;
    const promise = new Promise((res, rej) => {
      resolve = res;
      reject = rej;
    });
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.responseType = "text";
    xhr.onload = function() {
      const response = xhr.response;
      let status = xhr.status;
      if (status === 0) {
        status = response ? 200 : 0;
      }
      if (200 <= status && status <= 300) {
        resolve(response);
      } else {
        reject(`Failed to load ${url}`);
      }
    };
    xhr.onerror = function() {
      reject(`Failed to load ${url}`);
    };
    xhr.send();
    return promise;
  }
  static \u0275fac = \u0275\u0275ngDeclareFactory({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _ResourceLoaderImpl,
    deps: null,
    target: FactoryTarget.Injectable
  });
  static \u0275prov = \u0275\u0275ngDeclareInjectable({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _ResourceLoaderImpl
  });
};
\u0275\u0275ngDeclareClassMetadata({
  minVersion: "12.0.0",
  version: "19.2.14",
  ngImport: core_exports,
  type: ResourceLoaderImpl,
  decorators: [{
    type: Injectable
  }]
});
var INTERNAL_BROWSER_DYNAMIC_PLATFORM_PROVIDERS = [{
  provide: COMPILER_OPTIONS,
  useValue: {
    providers: [{
      provide: ResourceLoader,
      useClass: ResourceLoaderImpl,
      deps: []
    }]
  },
  multi: true
}, {
  provide: CompilerFactory,
  useClass: JitCompilerFactory,
  deps: [COMPILER_OPTIONS]
}];
var platformBrowserDynamic = createPlatformFactory(platformBrowser, "browserDynamic", INTERNAL_BROWSER_DYNAMIC_PLATFORM_PROVIDERS);

// node_modules/@angular/platform-browser/fesm2022/testing.mjs
init_common();
init_testing2();
init_core();
init_core();
init_testing();
init_browser_D_u_fknz();
var DOMTestComponentRenderer = class _DOMTestComponentRenderer extends TestComponentRenderer {
  _doc;
  constructor(_doc) {
    super();
    this._doc = _doc;
  }
  insertRootElement(rootElId) {
    this.removeAllRootElementsImpl();
    const rootElement = getDOM().getDefaultDocument().createElement("div");
    rootElement.setAttribute("id", rootElId);
    this._doc.body.appendChild(rootElement);
  }
  removeAllRootElements() {
    if (typeof this._doc.querySelectorAll === "function") {
      this.removeAllRootElementsImpl();
    }
  }
  removeAllRootElementsImpl() {
    const oldRoots = this._doc.querySelectorAll("[id^=root]");
    for (let i = 0; i < oldRoots.length; i++) {
      getDOM().remove(oldRoots[i]);
    }
  }
  static \u0275fac = \u0275\u0275ngDeclareFactory({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _DOMTestComponentRenderer,
    deps: [{
      token: DOCUMENT
    }],
    target: FactoryTarget.Injectable
  });
  static \u0275prov = \u0275\u0275ngDeclareInjectable({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _DOMTestComponentRenderer
  });
};
\u0275\u0275ngDeclareClassMetadata({
  minVersion: "12.0.0",
  version: "19.2.14",
  ngImport: core_exports,
  type: DOMTestComponentRenderer,
  decorators: [{
    type: Injectable
  }],
  ctorParameters: () => [{
    type: void 0,
    decorators: [{
      type: Inject,
      args: [DOCUMENT]
    }]
  }]
});
var platformBrowserTesting = createPlatformFactory(platformBrowser, "browserTesting");
var BrowserTestingModule = class _BrowserTestingModule {
  static \u0275fac = \u0275\u0275ngDeclareFactory({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserTestingModule,
    deps: [],
    target: FactoryTarget.NgModule
  });
  static \u0275mod = \u0275\u0275ngDeclareNgModule({
    minVersion: "14.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserTestingModule,
    exports: [BrowserModule]
  });
  static \u0275inj = \u0275\u0275ngDeclareInjector({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserTestingModule,
    providers: [{
      provide: APP_ID,
      useValue: "a"
    }, internalProvideZoneChangeDetection({}), {
      provide: ChangeDetectionScheduler,
      useExisting: ChangeDetectionSchedulerImpl
    }, {
      provide: PlatformLocation,
      useClass: MockPlatformLocation
    }, {
      provide: TestComponentRenderer,
      useClass: DOMTestComponentRenderer
    }],
    imports: [BrowserModule]
  });
};
\u0275\u0275ngDeclareClassMetadata({
  minVersion: "12.0.0",
  version: "19.2.14",
  ngImport: core_exports,
  type: BrowserTestingModule,
  decorators: [{
    type: NgModule,
    args: [{
      exports: [BrowserModule],
      providers: [{
        provide: APP_ID,
        useValue: "a"
      }, internalProvideZoneChangeDetection({}), {
        provide: ChangeDetectionScheduler,
        useExisting: ChangeDetectionSchedulerImpl
      }, {
        provide: PlatformLocation,
        useClass: MockPlatformLocation
      }, {
        provide: TestComponentRenderer,
        useClass: DOMTestComponentRenderer
      }]
    }]
  }]
});

// node_modules/@angular/platform-browser-dynamic/fesm2022/testing.mjs
init_compiler();
var platformBrowserDynamicTesting = createPlatformFactory(platformBrowserDynamic, "browserDynamicTesting");
var BrowserDynamicTestingModule = class _BrowserDynamicTestingModule {
  static \u0275fac = \u0275\u0275ngDeclareFactory({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserDynamicTestingModule,
    deps: [],
    target: FactoryTarget.NgModule
  });
  static \u0275mod = \u0275\u0275ngDeclareNgModule({
    minVersion: "14.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserDynamicTestingModule,
    exports: [BrowserTestingModule]
  });
  static \u0275inj = \u0275\u0275ngDeclareInjector({
    minVersion: "12.0.0",
    version: "19.2.14",
    ngImport: core_exports,
    type: _BrowserDynamicTestingModule,
    imports: [BrowserTestingModule]
  });
};
\u0275\u0275ngDeclareClassMetadata({
  minVersion: "12.0.0",
  version: "19.2.14",
  ngImport: core_exports,
  type: BrowserDynamicTestingModule,
  decorators: [{
    type: NgModule,
    args: [{
      exports: [BrowserTestingModule]
    }]
  }]
});

// node_modules/@angular/build/src/builders/karma/polyfills/init_test_bed.js
getTestBed().initTestEnvironment(BrowserDynamicTestingModule, platformBrowserDynamicTesting(), {
  errorOnUnknownElements: true,
  errorOnUnknownProperties: true
});
/*! Bundled license information:

@angular/platform-browser-dynamic/fesm2022/platform-browser-dynamic.mjs:
@angular/platform-browser/fesm2022/testing.mjs:
@angular/platform-browser-dynamic/fesm2022/testing.mjs:
  (**
   * @license Angular v19.2.14
   * (c) 2010-2025 Google LLC. https://angular.io/
   * License: MIT
   *)

@angular/build/src/builders/karma/polyfills/init_test_bed.js:
  (**
   * @license
   * Copyright Google LLC All Rights Reserved.
   *
   * Use of this source code is governed by an MIT-style license that can be
   * found in the LICENSE file at https://angular.dev/license
   *)
*/
//# sourceMappingURL=test_main.js.map
