import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService } from '../../../services/forum.service';
import { AuthService } from '../../../services/auth.service';
import { ForumThread } from '../../../models/forum.model';

@Component({
  selector: 'app-thread-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <div>
            <h1 class="text-4xl font-bold text-minecraft-green mb-2">Community Forum</h1>
            <p class="text-gray-600">Ask questions, share knowledge, and connect with other developers.</p>
          </div>
          
          <button 
            *ngIf="authService.isAuthenticated()"
            (click)="showCreateForm = !showCreateForm"
            class="bg-minecraft-green text-white px-6 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
          >
            {{ showCreateForm ? 'Cancel' : 'New Thread' }}
          </button>
          
          <a 
            *ngIf="!authService.isAuthenticated()"
            href="/api/oauth2/authorization/google"
            class="bg-minecraft-green text-white px-6 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
          >
            Login to Post
          </a>
        </div>

        <!-- Create Thread Form -->
        <div *ngIf="showCreateForm" class="bg-white rounded-lg shadow-md p-6 mb-8 border border-gray-200">
          <h3 class="text-xl font-semibold text-minecraft-dark mb-4">Create New Thread</h3>
          <form (ngSubmit)="createThread()" #threadForm="ngForm">
            <div class="mb-4">
              <label for="title" class="block text-sm font-medium text-gray-700 mb-2">Title</label>
              <input
                type="text"
                id="title"
                name="title"
                [(ngModel)]="newThread.title"
                required
                maxlength="200"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-minecraft-green focus:border-transparent"
                placeholder="Enter thread title..."
              >
            </div>
            
            <div class="mb-4">
              <label for="content" class="block text-sm font-medium text-gray-700 mb-2">Content</label>
              <textarea
                id="content"
                name="content"
                [(ngModel)]="newThread.content"
                required
                rows="6"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-minecraft-green focus:border-transparent"
                placeholder="Describe your question or topic..."
              ></textarea>
            </div>
            
            <div class="flex justify-end space-x-3">
              <button
                type="button"
                (click)="showCreateForm = false; resetForm()"
                class="px-4 py-2 text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors duration-200"
              >
                Cancel
              </button>
              <button
                type="submit"
                [disabled]="!threadForm.valid || creating"
                class="px-4 py-2 bg-minecraft-green text-white rounded hover:bg-minecraft-dark disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                {{ creating ? 'Creating...' : 'Create Thread' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Thread List -->
        <div class="space-y-4" *ngIf="!loading; else loadingTemplate">
          <div 
            *ngFor="let thread of threads" 
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 border border-gray-200"
          >
            <div class="p-6">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="text-xl font-semibold text-minecraft-dark mb-2">
                    <a [routerLink]="['/forums/threads', thread.id]" class="hover:text-minecraft-green">
                      {{ thread.title }}
                    </a>
                  </h3>
                  
                  <div class="flex items-center space-x-4 text-sm text-gray-500 mb-3">
                    <div class="flex items-center space-x-2">
                      <img 
                        [src]="thread.author.avatarUrl || '/assets/default-avatar.png'" 
                        [alt]="thread.author.username"
                        class="w-6 h-6 rounded-full"
                      >
                      <span>{{ thread.author.username }}</span>
                    </div>
                    <span>•</span>
                    <span>{{ formatDate(thread.createdAt) }}</span>
                    <span>•</span>
                    <span>{{ thread.postCount || 0 }} replies</span>
                  </div>
                  
                  <p class="text-gray-600 line-clamp-2">{{ getThreadPreview(thread) }}</p>
                </div>
                
                <div class="ml-4 text-right">
                  <div class="text-sm text-gray-500">Last activity</div>
                  <div class="text-sm font-medium">{{ formatDate(thread.lastActivity || thread.createdAt) }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <div *ngIf="threads.length === 0" class="text-center py-12">
            <div class="text-gray-500 mb-4">
              <svg class="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
              </svg>
              <p class="text-lg">No threads yet</p>
              <p>Be the first to start a discussion!</p>
            </div>
          </div>
        </div>

        <ng-template #loadingTemplate>
          <div class="flex justify-center items-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-minecraft-green"></div>
          </div>
        </ng-template>

        <div *ngIf="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mt-6">
          <p class="text-red-600">{{ error }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .line-clamp-2 {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  `]
})
export class ThreadListComponent implements OnInit {
  threads: ForumThread[] = [];
  loading = true;
  error: string | null = null;
  showCreateForm = false;
  creating = false;
  
  newThread = {
    title: '',
    content: ''
  };

  constructor(
    private forumService: ForumService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadThreads();
  }

  private loadThreads(): void {
    this.forumService.getThreads().subscribe({
      next: (threadsResponse) => {
        // threadsResponse is already a PageResponse<ForumThread>, so access .content
        this.threads = threadsResponse.content || [];
        this.loading = false;
      },
      error: (error: any) => {
        this.error = 'Failed to load forum threads. Please try again later.';
        this.loading = false;
        console.error('Error loading threads:', error);
      }
    });
  }

  createThread(): void {
    if (!this.newThread.title.trim() || !this.newThread.content.trim()) {
      return;
    }

    this.creating = true;
    this.forumService.createThread({ title: this.newThread.title, content: this.newThread.content }).subscribe({
      next: (thread) => {
        this.threads.unshift(thread);
        this.showCreateForm = false;
        this.resetForm();
        this.creating = false;
      },
      error: (error) => {
        this.error = 'Failed to create thread. Please try again.';
        this.creating = false;
        console.error('Error creating thread:', error);
      }
    });
  }

  resetForm(): void {
    this.newThread = {
      title: '',
      content: ''
    };
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));
    
    if (diffInHours < 1) {
      return 'Just now';
    } else if (diffInHours < 24) {
      return `${diffInHours}h ago`;
    } else if (diffInHours < 168) { // 7 days
      const days = Math.floor(diffInHours / 24);
      return `${days}d ago`;
    } else {
      return date.toLocaleDateString();
    }
  }

  getThreadPreview(thread: ForumThread): string {
    // If thread has a content property, use it; otherwise use a placeholder
    return thread.content || 'Click to view thread content...';
  }
}