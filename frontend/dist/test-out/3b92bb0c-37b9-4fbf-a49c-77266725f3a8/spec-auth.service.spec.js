import {
  HttpClientTestingModule,
  HttpTestingController,
  init_testing as init_testing2
} from "./chunk-4J2RBVF3.js";
import {
  AuthService,
  init_auth_service
} from "./chunk-R3RS3RWC.js";
import {
  TestBed,
  init_esm,
  init_testing,
  of
} from "./chunk-T6WWCSSA.js";
import {
  __async,
  __commonJS
} from "./chunk-TTULUY32.js";

// src/app/services/auth.service.spec.ts
var require_auth_service_spec = __commonJS({
  "src/app/services/auth.service.spec.ts"(exports) {
    init_testing();
    init_testing2();
    init_auth_service();
    init_esm();
    describe("AuthService", () => {
      let service;
      let testableService;
      let httpMock;
      const mockUser = {
        id: 1,
        email: "test@example.com",
        username: "testuser",
        avatarUrl: void 0,
        provider: "local",
        roles: ["USER"]
      };
      beforeEach(() => __async(null, null, function* () {
        yield TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers: [AuthService]
        }).compileComponents();
        service = TestBed.inject(AuthService);
        testableService = service;
        httpMock = TestBed.inject(HttpTestingController);
      }));
      afterEach(() => {
        if (service && testableService) {
          testableService.authToken = null;
        }
        if (typeof document !== "undefined") {
          document.cookie = "auth_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        }
        if (httpMock) {
          httpMock.verify();
        }
      });
      it("should be created", () => {
        expect(service).toBeTruthy();
      });
      describe("login", () => {
        it("should redirect to OAuth provider", () => {
          spyOn(testableService, "redirectToOAuth").and.callFake((provider) => {
            expect(provider).toBe("google");
          });
          service.login("google");
          expect(testableService.redirectToOAuth).toHaveBeenCalledWith("google");
        });
        it("should handle invalid provider", () => {
          spyOn(console, "error");
          service.login("invalid");
          expect(console.error).toHaveBeenCalledWith("Invalid OAuth provider:", "invalid");
        });
      });
      describe("logout", () => {
        it("should clear token and call logout endpoint", () => {
          testableService.authToken = "jwt-token";
          service.logout().subscribe((response) => {
            expect(response).toBeDefined();
            expect(testableService.authToken).toBeNull();
          });
          const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
          expect(req.request.method).toBe("POST");
          expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
          req.flush({ message: "Logged out successfully" });
        });
        it("should clear token even if logout endpoint fails", () => {
          testableService.authToken = "jwt-token";
          service.logout().subscribe((response) => {
            expect(response).toBeDefined();
            expect(testableService.authToken).toBeNull();
          });
          const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
          req.flush("Server error", { status: 500, statusText: "Internal Server Error" });
        });
      });
      describe("getCurrentUser", () => {
        it("should return current user when token exists", () => {
          testableService.authToken = "jwt-token";
          service.getCurrentUser().subscribe((user) => {
            expect(user).toEqual(mockUser);
          });
          const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
          expect(req.request.method).toBe("GET");
          expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
          req.flush(mockUser);
        });
        it("should return null when no token exists", () => {
          testableService.authToken = null;
          service.getCurrentUser().subscribe((user) => {
            expect(user).toBeNull();
          });
          httpMock.expectNone("http://localhost:8080/api/auth/me");
        });
        it("should handle getCurrentUser error", (done) => {
          testableService.authToken = "jwt-token";
          const logoutSpy = spyOn(service, "logout").and.returnValue(of({ message: "Logged out" }));
          service.getCurrentUser().subscribe({
            next: (user) => {
              expect(user).toBeNull();
              setTimeout(() => {
                expect(logoutSpy).toHaveBeenCalled();
                done();
              }, 10);
            },
            error: () => {
              fail("Should not reach error handler, service handles errors internally");
              done();
            }
          });
          const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
          req.error(new ErrorEvent("Network error"), { status: 401, statusText: "Unauthorized" });
        });
      });
      describe("getToken", () => {
        it("should return token from memory", () => {
          testableService.authToken = "jwt-token";
          expect(service.getToken()).toBe("jwt-token");
        });
        it("should return null when no token exists", () => {
          testableService.authToken = null;
          expect(service.getToken()).toBeNull();
        });
      });
      describe("isAuthenticated", () => {
        it("should return true when token exists", () => {
          testableService.authToken = "jwt-token";
          expect(service.isAuthenticated()).toBe(true);
        });
        it("should return false when no token exists", () => {
          testableService.authToken = null;
          expect(service.isAuthenticated()).toBe(false);
        });
      });
      describe("handleAuthCallback", () => {
        it("should handle auth callback with valid token", () => {
          spyOn(testableService, "getTokenFromCookie").and.returnValue("jwt-token");
          testableService.authToken = "jwt-token";
          service.handleAuthCallback();
          const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
          expect(req.request.method).toBe("GET");
          req.flush(mockUser);
        });
        it("should handle auth callback and logout on error", (done) => {
          spyOn(testableService, "getTokenFromCookie").and.returnValue("invalid-token");
          testableService.authToken = "invalid-token";
          const logoutSpy = spyOn(service, "logout").and.returnValue(of({ message: "Logged out" }));
          service.handleAuthCallback();
          const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
          req.error(new ErrorEvent("Network error"), { status: 401, statusText: "Unauthorized" });
          setTimeout(() => {
            expect(logoutSpy).toHaveBeenCalled();
            done();
          }, 10);
        });
      });
    });
  }
});
export default require_auth_service_spec();
//# sourceMappingURL=spec-auth.service.spec.js.map
