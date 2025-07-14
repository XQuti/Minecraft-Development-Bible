import {
  HttpClient,
  HttpHeaders,
  init_http
} from "./chunk-M27OVNWH.js";
import {
  BehaviorSubject,
  Injectable,
  __decorate,
  catchError,
  init_core,
  init_esm,
  init_operators,
  init_tslib_es6,
  of,
  retry,
  tap,
  throwError,
  timeout
} from "./chunk-36OBKH4Z.js";
import {
  __esm
} from "./chunk-TTULUY32.js";

// src/app/services/auth.service.ts
var AuthService;
var init_auth_service = __esm({
  "src/app/services/auth.service.ts"() {
    "use strict";
    init_tslib_es6();
    init_core();
    init_http();
    init_esm();
    init_operators();
    AuthService = class AuthService2 {
      http;
      API_URL = "http://localhost:8080/api";
      TOKEN_KEY = "auth_token";
      REQUEST_TIMEOUT = 1e4;
      // 10 seconds
      currentUserSubject = new BehaviorSubject(null);
      currentUser$ = this.currentUserSubject.asObservable();
      constructor(http) {
        this.http = http;
        this.loadUserFromToken();
      }
      loadUserFromToken() {
        const token = this.getToken();
        if (token) {
          this.getCurrentUser().subscribe({
            error: (error) => {
              console.warn("Failed to load user from stored token:", error);
              this.removeToken();
            }
          });
        }
      }
      getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
      }
      setToken(token) {
        if (!token || token.trim() === "") {
          console.error("Attempted to set empty or invalid token");
          return;
        }
        localStorage.setItem(this.TOKEN_KEY, token);
      }
      removeToken() {
        localStorage.removeItem(this.TOKEN_KEY);
      }
      isAuthenticated() {
        const token = this.getToken();
        return !!token && token.trim() !== "";
      }
      getCurrentUser() {
        const token = this.getToken();
        if (!token) {
          return of(null);
        }
        const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
        return this.http.get(`${this.API_URL}/auth/me`, { headers }).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), tap((user) => {
          if (user) {
            this.currentUserSubject.next(user);
          }
        }), catchError((error) => {
          console.error("Error fetching current user:", error);
          if (error.status === 401 || error.status === 403) {
            console.warn("Authentication failed, logging out user");
            this.logout();
          } else if (error.status === 0) {
            console.error("Network error - server may be unavailable");
          } else {
            console.error("Unexpected error:", error.message);
          }
          return of(null);
        }));
      }
      login(provider) {
        if (!provider || provider !== "google" && provider !== "github") {
          console.error("Invalid OAuth provider:", provider);
          return;
        }
        try {
          window.location.href = `${this.API_URL.replace("/api", "")}/oauth2/authorization/${provider}`;
        } catch (error) {
          console.error("Error redirecting to OAuth provider:", error);
        }
      }
      handleAuthCallback(token) {
        if (!token || token.trim() === "") {
          console.error("Invalid token received in auth callback");
          return;
        }
        this.setToken(token);
        this.getCurrentUser().subscribe({
          next: (user) => {
            if (user) {
              console.log("User authenticated successfully:", user.email);
            }
          },
          error: (error) => {
            console.error("Failed to fetch user after auth callback:", error);
            this.logout();
          }
        });
      }
      logout() {
        const token = this.getToken();
        this.removeToken();
        this.currentUserSubject.next(null);
        if (token) {
          const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
          return this.http.post(`${this.API_URL}/auth/logout`, {}, { headers }).pipe(timeout(this.REQUEST_TIMEOUT), catchError((error) => {
            console.warn("Error during server logout:", error);
            return of({ message: "Logged out locally" });
          }));
        }
        return of({ message: "Logged out successfully" });
      }
      getAuthHeaders() {
        const token = this.getToken();
        if (!token) {
          throw new Error("No authentication token available");
        }
        return new HttpHeaders().set("Authorization", `Bearer ${token}`);
      }
      // Helper method for authenticated requests
      getAuthenticatedHeaders() {
        return this.getAuthHeaders();
      }
      // Helper method to handle API errors consistently
      handleApiError(error) {
        let errorMessage = "An unexpected error occurred";
        if (error.error && typeof error.error === "object") {
          const apiError = error.error;
          errorMessage = apiError.message || errorMessage;
          if (apiError.errors) {
            console.error("Validation errors:", apiError.errors);
          }
        } else if (error.message) {
          errorMessage = error.message;
        }
        console.error("API Error:", {
          status: error.status,
          message: errorMessage,
          url: error.url
        });
        return throwError(() => new Error(errorMessage));
      }
      static ctorParameters = () => [
        { type: HttpClient }
      ];
    };
    AuthService = __decorate([
      Injectable({
        providedIn: "root"
      })
    ], AuthService);
  }
});

export {
  AuthService,
  init_auth_service
};
//# sourceMappingURL=chunk-GILDE4VG.js.map
