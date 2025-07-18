import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap, retry, timeout } from 'rxjs/operators';
import { User } from '../models/user.model';

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  errors?: { [key: string]: string };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api';
  private readonly REQUEST_TIMEOUT = 10000; // 10 seconds
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  // Store token in memory instead of localStorage for security
  private authToken: string | null = null;

  constructor(private http: HttpClient) {
    this.loadUserFromToken();
  }

  private loadUserFromToken(): void {
    // Try to get token from cookie on page load
    const token = this.getTokenFromCookie();
    if (token) {
      this.authToken = token;
      this.getCurrentUser().subscribe({
        error: (error) => {
          console.warn('Failed to load user from stored token:', error);
          this.removeToken();
        }
      });
    }
  }

  private getTokenFromCookie(): string | null {
    const name = 'auth_token=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) === ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) === 0) {
        return c.substring(name.length, c.length);
      }
    }
    return null;
  }

  getToken(): string | null {
    return this.authToken;
  }

  setToken(token: string): void {
    if (!token || token.trim() === '') {
      console.error('Attempted to set empty or invalid token');
      return;
    }
    this.authToken = token;
  }

  removeToken(): void {
    this.authToken = null;
    // Clear the cookie by setting it to expire
    document.cookie = 'auth_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && token.trim() !== '';
  }

  getCurrentUser(): Observable<User | null> {
    const token = this.getToken();
    if (!token) {
      return of(null);
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<User>(`${this.API_URL}/auth/me`, { headers }).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      tap(user => {
        if (user) {
          this.currentUserSubject.next(user);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching current user:', error);
        
        // Handle different error scenarios
        if (error.status === 401 || error.status === 403) {
          console.warn('Authentication failed, logging out user');
          this.logout().subscribe(); // Subscribe to ensure logout completes
        } else if (error.status === 0) {
          console.error('Network error - server may be unavailable');
        } else {
          console.error('Unexpected error:', error.message);
        }
        
        return of(null);
      })
    );
  }

  login(provider: 'google' | 'github'): void {
    if (!provider || (provider !== 'google' && provider !== 'github')) {
      console.error('Invalid OAuth provider:', provider);
      return;
    }
    
    this.redirectToOAuth(provider);
  }

  private redirectToOAuth(provider: string): void {
    try {
      window.location.href = `${this.API_URL.replace('/api', '')}/oauth2/authorization/${provider}`;
    } catch (error) {
      console.error('Error redirecting to OAuth provider:', error);
    }
  }

  handleAuthCallback(): void {
    // Token is now in cookie, just load user
    this.getCurrentUser().subscribe({
      next: (user) => {
        if (user) {
          console.log('User authenticated successfully:', user.email);
        }
      },
      error: (error) => {
        console.error('Failed to fetch user after auth callback:', error);
        this.logout().subscribe();
      }
    });
  }

  logout(): Observable<{ message: string }> {
    const token = this.getToken();
    
    // Clear local state immediately
    this.removeToken();
    this.currentUserSubject.next(null);
    
    // Notify server if token exists
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.post<{ message: string }>(`${this.API_URL}/auth/logout`, {}, { headers }).pipe(
        timeout(this.REQUEST_TIMEOUT),
        catchError((error: HttpErrorResponse) => {
          console.warn('Error during server logout:', error);
          // Don't throw error as local logout already succeeded
          return of({ message: 'Logged out locally' });
        })
      );
    }
    
    return of({ message: 'Logged out successfully' });
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    if (!token) {
      throw new Error('No authentication token available');
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // Helper method for authenticated requests
  getAuthenticatedHeaders(): HttpHeaders {
    return this.getAuthHeaders();
  }

  // Helper method to handle API errors consistently
  handleApiError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unexpected error occurred';
    
    if (error.error && typeof error.error === 'object') {
      const apiError = error.error as ApiError;
      errorMessage = apiError.message || errorMessage;
      
      // Log validation errors if present
      if (apiError.errors) {
        console.error('Validation errors:', apiError.errors);
      }
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    console.error('API Error:', {
      status: error.status,
      message: errorMessage,
      url: error.url
    });
    
    return throwError(() => new Error(errorMessage));
  }
}