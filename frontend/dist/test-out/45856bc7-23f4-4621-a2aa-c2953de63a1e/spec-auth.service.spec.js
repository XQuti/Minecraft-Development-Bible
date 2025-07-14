import {
  HttpClientTestingModule,
  HttpTestingController,
  init_testing as init_testing2
} from "./chunk-4J2RBVF3.js";
import {
  AuthService,
  init_auth_service
} from "./chunk-DKNR2WOF.js";
import {
  TestBed,
  init_esm,
  init_testing,
  of
} from "./chunk-T6WWCSSA.js";
import "./chunk-TTULUY32.js";

// src/app/services/auth.service.spec.ts
init_testing();
init_testing2();
init_auth_service();
init_esm();
describe("AuthService", () => {
  let service;
  let httpMock;
  const mockUser = {
    id: 1,
    email: "test@example.com",
    username: "testuser",
    avatarUrl: void 0,
    provider: "local",
    roles: ["USER"]
  };
  const mockAuthResponse = {
    token: "jwt-token",
    user: mockUser
  };
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
    service.authToken = null;
  });
  it("should be created", () => {
    expect(service).toBeTruthy();
  });
  describe("login", () => {
    it("should redirect to OAuth provider", () => {
      spyOn(service, "redirectToOAuth").and.callFake((provider) => {
        expect(provider).toBe("google");
      });
      service.login("google");
      expect(service.redirectToOAuth).toHaveBeenCalledWith("google");
    });
    it("should handle invalid provider", () => {
      spyOn(console, "error");
      service.login("invalid");
      expect(console.error).toHaveBeenCalledWith("Invalid OAuth provider:", "invalid");
    });
  });
  describe("logout", () => {
    it("should clear token and call logout endpoint", () => {
      service.authToken = "jwt-token";
      service.logout().subscribe((response) => {
        expect(response).toBeDefined();
        expect(service.authToken).toBeNull();
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
      expect(req.request.method).toBe("POST");
      expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
      req.flush({ message: "Logged out successfully" });
    });
    it("should clear token even if logout endpoint fails", () => {
      service.authToken = "jwt-token";
      service.logout().subscribe((response) => {
        expect(response).toBeDefined();
        expect(service.authToken).toBeNull();
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
      req.flush("Server error", { status: 500, statusText: "Internal Server Error" });
    });
  });
  describe("getCurrentUser", () => {
    it("should return current user when token exists", () => {
      service.authToken = "jwt-token";
      service.getCurrentUser().subscribe((user) => {
        expect(user).toEqual(mockUser);
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      expect(req.request.method).toBe("GET");
      expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
      req.flush(mockUser);
    });
    it("should return null when no token exists", () => {
      service.authToken = null;
      service.getCurrentUser().subscribe((user) => {
        expect(user).toBeNull();
      });
      httpMock.expectNone("http://localhost:8080/api/auth/me");
    });
    it("should handle getCurrentUser error", () => {
      service.authToken = "jwt-token";
      const logoutSpy = spyOn(service, "logout").and.returnValue(of({ message: "Logged out" }));
      service.getCurrentUser().subscribe({
        next: (user) => {
          expect(user).toBeNull();
        },
        error: () => {
        }
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      req.error(new ErrorEvent("Network error"), { status: 401, statusText: "Unauthorized" });
      expect(logoutSpy).toHaveBeenCalled();
    });
  });
  describe("getToken", () => {
    it("should return token from memory", () => {
      service.authToken = "jwt-token";
      expect(service.getToken()).toBe("jwt-token");
    });
    it("should return null when no token exists", () => {
      service.authToken = null;
      expect(service.getToken()).toBeNull();
    });
  });
  describe("isAuthenticated", () => {
    it("should return true when token exists", () => {
      service.authToken = "jwt-token";
      expect(service.isAuthenticated()).toBe(true);
    });
    it("should return false when no token exists", () => {
      service.authToken = null;
      expect(service.isAuthenticated()).toBe(false);
    });
  });
  describe("handleAuthCallback", () => {
    it("should handle auth callback with valid token", () => {
      spyOn(service, "getTokenFromCookie").and.returnValue("jwt-token");
      service.authToken = "jwt-token";
      service.handleAuthCallback();
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      expect(req.request.method).toBe("GET");
      req.flush(mockUser);
    });
    it("should handle auth callback and logout on error", () => {
      spyOn(service, "getTokenFromCookie").and.returnValue("invalid-token");
      service.authToken = "invalid-token";
      const logoutSpy = spyOn(service, "logout").and.returnValue(of({ message: "Logged out" }));
      service.handleAuthCallback();
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      req.error(new ErrorEvent("Network error"));
      expect(logoutSpy).toHaveBeenCalled();
    });
  });
});
//# sourceMappingURL=spec-auth.service.spec.js.map
