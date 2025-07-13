import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService } from '../../../services/forum.service';
import { AuthService } from '../../../services/auth.service';
import { ForumThread, ForumPost } from '../../../models/forum.model';

@Component({
  selector: 'app-thread-view',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="container mx-auto px-4 py-8" *ngIf="!loading; else loadingTemplate">
      <div class="max-w-4xl mx-auto">
        <!-- Breadcrumb -->
        <nav class="mb-6">
          <ol class="flex items-center space-x-2 text-sm text-gray-500">
            <li><a routerLink="/forums" class="hover:text-minecraft-green">Forums</a></li>
            <li>/</li>
            <li class="text-gray-700">{{ thread?.title }}</li>
          </ol>
        </nav>

        <!-- Thread Header -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6 border border-gray-200" *ngIf="thread">
          <h1 class="text-3xl font-bold text-minecraft-green mb-4">{{ thread.title }}</h1>
          
          <div class="flex items-center space-x-4 text-sm text-gray-500">
            <div class="flex items-center space-x-2">
              <img 
                [src]="thread.author.avatarUrl || '/assets/default-avatar.png'" 
                [alt]="thread.author.username"
                class="w-8 h-8 rounded-full"
              >
              <span class="font-medium">{{ thread.author.username }}</span>
            </div>
            <span>•</span>
            <span>{{ formatDate(thread.createdAt) }}</span>
            <span>•</span>
            <span>{{ posts.length }} replies</span>
          </div>
        </div>

        <!-- Posts -->
        <div class="space-y-4 mb-8">
          <div 
            *ngFor="let post of posts; let i = index" 
            class="bg-white rounded-lg shadow-md border border-gray-200"
          >
            <div class="p-6">
              <div class="flex items-start space-x-4">
                <img 
                  [src]="post.author.avatarUrl || '/assets/default-avatar.png'" 
                  [alt]="post.author.username"
                  class="w-10 h-10 rounded-full flex-shrink-0"
                >
                
                <div class="flex-1">
                  <div class="flex items-center justify-between mb-3">
                    <div class="flex items-center space-x-3">
                      <span class="font-medium text-minecraft-dark">{{ post.author.username }}</span>
                      <span class="text-sm text-gray-500">{{ formatDate(post.createdAt) }}</span>
                      <span *ngIf="i === 0" class="bg-minecraft-green text-white text-xs px-2 py-1 rounded">OP</span>
                    </div>
                    
                    <span class="text-sm text-gray-400">#{{ i + 1 }}</span>
                  </div>
                  
                  <div class="prose prose-sm max-w-none">
                    <div [innerHTML]="getFormattedContent(post.content)"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Reply Form -->
        <div *ngIf="authService.isAuthenticated()" class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
          <h3 class="text-lg font-semibold text-minecraft-dark mb-4">Post a Reply</h3>
          
          <form (ngSubmit)="createPost()" #postForm="ngForm">
            <div class="mb-4">
              <textarea
                name="content"
                [(ngModel)]="newPost.content"
                required
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-minecraft-green focus:border-transparent"
                placeholder="Write your reply..."
              ></textarea>
            </div>
            
            <div class="flex justify-end">
              <button
                type="submit"
                [disabled]="!postForm.valid || posting"
                class="px-6 py-2 bg-minecraft-green text-white rounded hover:bg-minecraft-dark disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                {{ posting ? 'Posting...' : 'Post Reply' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Login Prompt -->
        <div *ngIf="!authService.isAuthenticated()" class="bg-gray-50 rounded-lg p-6 text-center border border-gray-200">
          <p class="text-gray-600 mb-4">You need to be logged in to reply to this thread.</p>
          <a 
            href="/api/oauth2/authorization/google"
            class="inline-block bg-minecraft-green text-white px-6 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
          >
            Login to Reply
          </a>
        </div>
      </div>
    </div>

    <ng-template #loadingTemplate>
      <div class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-minecraft-green"></div>
      </div>
    </ng-template>

    <div *ngIf="error" class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <div class="bg-red-50 border border-red-200 rounded-lg p-4">
          <p class="text-red-600">{{ error }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .prose {
      color: #374151;
    }
    .prose p {
      margin-bottom: 1rem;
    }
    .prose code {
      background-color: #f3f4f6;
      padding: 0.125rem 0.25rem;
      border-radius: 0.25rem;
      font-size: 0.875em;
    }
    .prose pre {
      background-color: #1f2937;
      color: #f9fafb;
      padding: 1rem;
      border-radius: 0.5rem;
      overflow-x: auto;
      margin: 1rem 0;
    }
  `]
})
export class ThreadViewComponent implements OnInit {
  threadId!: number;
  thread: ForumThread | null = null;
  posts: ForumPost[] = [];
  loading = true;
  error: string | null = null;
  posting = false;
  
  newPost = {
    content: ''
  };

  constructor(
    private route: ActivatedRoute,
    private forumService: ForumService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.threadId = +params['id'];
      this.loadThreadData();
    });
  }

  private loadThreadData(): void {
    this.loading = true;
    this.error = null;

    // Load thread details and posts separately using forkJoin
    import('rxjs').then(({ forkJoin }) => {
      forkJoin({
        thread: this.forumService.getThread(this.threadId),
        posts: this.forumService.getThreadPosts(this.threadId)
      }).subscribe({
        next: ({ thread, posts }) => {
          this.thread = thread;
          this.posts = posts.content || [];
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load thread. Please try again later.';
          this.loading = false;
          console.error('Error loading thread:', error);
        }
      });
    });
  }

  createPost(): void {
    if (!this.newPost.content.trim()) {
      return;
    }

    this.posting = true;
    this.forumService.createPost(this.threadId, { content: this.newPost.content }).subscribe({
      next: (post) => {
        this.posts.push(post);
        this.newPost.content = '';
        this.posting = false;
      },
      error: (error) => {
        this.error = 'Failed to post reply. Please try again.';
        this.posting = false;
        console.error('Error creating post:', error);
      }
    });
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
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }

  getFormattedContent(content: string): string {
    // Basic markdown-like formatting
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/`(.*?)`/g, '<code>$1</code>')
      .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
      .replace(/\n\n/g, '</p><p>')
      .replace(/\n/g, '<br>')
      .replace(/^/, '<p>')
      .replace(/$/, '</p>')
      .replace(/<p><\/p>/g, '');
  }
}