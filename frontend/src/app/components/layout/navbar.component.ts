import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
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
export class NavbarComponent {
  currentUser$: Observable<User | null>;

  constructor(private authService: AuthService) {
    this.currentUser$ = this.authService.currentUser$;
  }



  login(provider: 'google' | 'github'): void {
    this.authService.login(provider);
  }

  logout(): void {
    this.authService.logout();
  }
}