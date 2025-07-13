import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div class="min-h-screen flex items-center justify-center bg-secondary-50">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto mb-4"></div>
        <p class="text-secondary-600">Completing authentication...</p>
      </div>
    </div>
  `
})
export class AuthCallbackComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      if (error) {
        console.error('Authentication error:', error);
        this.router.navigate(['/'], { queryParams: { error: 'auth_failed' } });
        return;
      }

      if (token) {
        this.authService.handleAuthCallback(token);
        this.router.navigate(['/']);
      } else {
        console.error('No token received in callback');
        this.router.navigate(['/'], { queryParams: { error: 'no_token' } });
      }
    });
  }
}