import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TutorialService } from '../../../services/tutorial.service';
import { TutorialLesson, TutorialModule } from '../../../models/tutorial.model';

@Component({
  selector: 'app-lesson-view',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mx-auto px-4 py-8" *ngIf="!loading; else loadingTemplate">
      <div class="max-w-4xl mx-auto">
        <!-- Breadcrumb -->
        <nav class="mb-6">
          <ol class="flex items-center space-x-2 text-sm text-gray-500">
            <li><a routerLink="/tutorials" class="hover:text-minecraft-green">Tutorials</a></li>
            <li>/</li>
            <li *ngIf="module"><a [routerLink]="['/tutorials/modules', module.id]" class="hover:text-minecraft-green">{{ module.title }}</a></li>
            <li>/</li>
            <li class="text-gray-700">{{ currentLesson?.title }}</li>
          </ol>
        </nav>

        <!-- Lesson Header -->
        <div class="mb-8" *ngIf="currentLesson">
          <h1 class="text-4xl font-bold text-minecraft-green mb-2">{{ currentLesson.title }}</h1>
          <p class="text-gray-600" *ngIf="module">Part of {{ module.title }}</p>
        </div>

        <!-- Video Section -->
        <div class="mb-8" *ngIf="currentLesson && currentLesson.videoUrl">
          <div class="aspect-video bg-gray-100 rounded-lg overflow-hidden">
            <iframe 
              [src]="getVideoEmbedUrl(currentLesson.videoUrl)" 
              class="w-full h-full"
              frameborder="0"
              allowfullscreen
              title="Tutorial Video">
            </iframe>
          </div>
        </div>

        <!-- Lesson Content -->
        <div class="prose prose-lg max-w-none mb-8" *ngIf="currentLesson">
          <div [innerHTML]="getFormattedContent(currentLesson.content)"></div>
        </div>

        <!-- Navigation -->
        <div class="flex justify-between items-center pt-8 border-t border-gray-200">
          <button 
            *ngIf="previousLesson"
            (click)="navigateToLesson(previousLesson.id)"
            class="flex items-center space-x-2 bg-gray-100 hover:bg-gray-200 px-4 py-2 rounded transition-colors duration-200"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
            <span>{{ previousLesson.title }}</span>
          </button>
          
          <a 
            [routerLink]="['/tutorials/modules', moduleId]"
            class="bg-minecraft-green text-white px-4 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
          >
            Back to Module
          </a>
          
          <button 
            *ngIf="nextLesson"
            (click)="navigateToLesson(nextLesson.id)"
            class="flex items-center space-x-2 bg-minecraft-green text-white px-4 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
          >
            <span>{{ nextLesson.title }}</span>
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
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
    .prose h1, .prose h2, .prose h3, .prose h4, .prose h5, .prose h6 {
      color: #1f2937;
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
    }
  `]
})
export class LessonViewComponent implements OnInit {
  moduleId!: number;
  lessonId!: number;
  module: TutorialModule | null = null;
  currentLesson: TutorialLesson | null = null;
  lessons: TutorialLesson[] = [];
  previousLesson: TutorialLesson | null = null;
  nextLesson: TutorialLesson | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private tutorialService: TutorialService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const moduleIdParam = params['moduleId'];
      const lessonIdParam = params['lessonId'];
      
      if (moduleIdParam && lessonIdParam) {
        this.moduleId = +moduleIdParam;
        this.lessonId = +lessonIdParam;
        this.loadLessonData();
      } else {
        this.error = 'Invalid lesson parameters.';
        this.loading = false;
      }
    });
  }

  private loadLessonData(): void {
    this.loading = true;
    this.error = null;

    // Load module info and lessons
    this.tutorialService.getModuleLessons(this.moduleId).subscribe({
      next: (lessons) => {
        this.lessons = lessons;
        this.currentLesson = lessons.find(l => l.id === this.lessonId) || null;
        
        if (this.currentLesson) {
          this.setupNavigation();
          this.loadModuleInfo();
        } else {
          this.error = 'Lesson not found.';
          this.loading = false;
        }
      },
      error: (error) => {
        this.error = 'Failed to load lesson. Please try again later.';
        this.loading = false;
        console.error('Error loading lesson:', error);
      }
    });
  }

  private loadModuleInfo(): void {
    this.tutorialService.getModules().subscribe({
      next: (modules: TutorialModule[]) => {
        this.module = modules.find((m: TutorialModule) => m.id === this.moduleId) || null;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading module info:', error);
        this.loading = false;
      }
    });
  }

  private setupNavigation(): void {
    const currentIndex = this.lessons.findIndex(l => l.id === this.lessonId);
    
    if (currentIndex > 0) {
      this.previousLesson = this.lessons[currentIndex - 1];
    }
    
    if (currentIndex < this.lessons.length - 1) {
      this.nextLesson = this.lessons[currentIndex + 1];
    }
  }

  navigateToLesson(lessonId: number): void {
    this.lessonId = lessonId;
    this.loadLessonData();
    
    // Update URL without reloading component
    window.history.replaceState({}, '', `/tutorials/modules/${this.moduleId}/lessons/${lessonId}`);
  }

  getVideoEmbedUrl(videoUrl: string): string {
    // Convert YouTube URLs to embed format
    if (videoUrl.includes('youtube.com/watch?v=')) {
      const videoId = videoUrl.split('v=')[1].split('&')[0];
      return `https://www.youtube.com/embed/${videoId}`;
    }
    if (videoUrl.includes('youtu.be/')) {
      const videoId = videoUrl.split('youtu.be/')[1].split('?')[0];
      return `https://www.youtube.com/embed/${videoId}`;
    }
    return videoUrl;
  }

  getFormattedContent(content: string): string {
    // Basic markdown-like formatting
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/`(.*?)`/g, '<code>$1</code>')
      .replace(/\n\n/g, '</p><p>')
      .replace(/\n/g, '<br>')
      .replace(/^/, '<p>')
      .replace(/$/, '</p>');
  }
}