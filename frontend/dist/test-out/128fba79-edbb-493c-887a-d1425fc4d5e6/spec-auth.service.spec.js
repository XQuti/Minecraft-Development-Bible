import {
  HttpClientTestingModule,
  HttpTestingController,
  init_testing as init_testing2
} from "./chunk-MROCUD6J.js";
import {
  AuthService,
  init_auth_service
} from "./chunk-GILDE4VG.js";
import "./chunk-M27OVNWH.js";
import {
  TestBed,
  init_testing
} from "./chunk-36OBKH4Z.js";
import "./chunk-TTULUY32.js";

// src/app/services/auth.service.spec.ts
init_testing();
init_testing2();
init_auth_service();
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
  });
  it("should be created", () => {
    expect(service).toBeTruthy();
  });
  describe("login", () => {
    it("should redirect to OAuth provider", () => {
      const mockLocation = { href: "" };
      Object.defineProperty(window, "location", {
        value: mockLocation,
        writable: true
      });
      service.login("google");
      expect(mockLocation.href).toBe("http://localhost:8080/oauth2/authorization/google");
    });
    it("should handle invalid provider", () => {
      spyOn(console, "error");
      service.login("invalid");
      expect(console.error).toHaveBeenCalledWith("Invalid OAuth provider:", "invalid");
    });
  });
  describe("logout", () => {
    it("should clear local storage and call logout endpoint", () => {
      localStorage.setItem("auth_token", "jwt-token");
      service.logout().subscribe((response) => {
        expect(response).toBeDefined();
        expect(localStorage.getItem("auth_token")).toBeNull();
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
      expect(req.request.method).toBe("POST");
      expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
      req.flush({ message: "Logged out successfully" });
    });
    it("should clear local storage even if logout endpoint fails", () => {
      localStorage.setItem("auth_token", "jwt-token");
      service.logout().subscribe((response) => {
        expect(response).toBeDefined();
        expect(localStorage.getItem("auth_token")).toBeNull();
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/logout");
      req.flush("Server error", { status: 500, statusText: "Internal Server Error" });
    });
  });
  describe("getCurrentUser", () => {
    it("should return current user when token exists", () => {
      localStorage.setItem("auth_token", "jwt-token");
      service.getCurrentUser().subscribe((user) => {
        expect(user).toEqual(mockUser);
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      expect(req.request.method).toBe("GET");
      expect(req.request.headers.get("Authorization")).toBe("Bearer jwt-token");
      req.flush(mockUser);
    });
    it("should return null when no token exists", () => {
      service.getCurrentUser().subscribe((user) => {
        expect(user).toBeNull();
      });
      httpMock.expectNone("http://localhost:8080/api/auth/me");
    });
    it("should handle getCurrentUser error", () => {
      localStorage.setItem("auth_token", "jwt-token");
      service.getCurrentUser().subscribe((user) => {
        expect(user).toBeNull();
      });
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      req.flush("Unauthorized", { status: 401, statusText: "Unauthorized" });
    });
  });
  describe("getToken", () => {
    it("should return token from localStorage", () => {
      localStorage.setItem("auth_token", "jwt-token");
      expect(service.getToken()).toBe("jwt-token");
    });
    it("should return null when no token exists", () => {
      expect(service.getToken()).toBeNull();
    });
  });
  describe("isAuthenticated", () => {
    it("should return true when token exists", () => {
      localStorage.setItem("auth_token", "jwt-token");
      expect(service.isAuthenticated()).toBe(true);
    });
    it("should return false when no token exists", () => {
      expect(service.isAuthenticated()).toBe(false);
    });
  });
  describe("handleAuthCallback", () => {
    it("should handle auth callback with valid token", () => {
      service.handleAuthCallback("jwt-token");
      expect(localStorage.getItem("auth_token")).toBe("jwt-token");
      const req = httpMock.expectOne("http://localhost:8080/api/auth/me");
      expect(req.request.method).toBe("GET");
      req.flush(mockUser);
    });
    it("should handle invalid token in callback", () => {
      spyOn(console, "error");
      service.handleAuthCallback("");
      expect(console.error).toHaveBeenCalledWith("Invalid token received in auth callback");
      expect(localStorage.getItem("auth_token")).toBeNull();
    });
  });
});
//# sourceMappingURL=spec-auth.service.spec.js.map
