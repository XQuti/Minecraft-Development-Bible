import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TutorialService } from '../../../services/tutorial.service';
import { TutorialModule } from '../../../models/tutorial.model';

@Component({
  selector: 'app-module-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-4xl font-bold text-minecraft-green mb-2">Tutorial Modules</h1>
        <p class="text-gray-600 mb-8">Learn Minecraft development step by step with our comprehensive tutorials.</p>
        
        <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3" *ngIf="!loading; else loadingTemplate">
          <div 
            *ngFor="let module of modules" 
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 border border-gray-200"
          >
            <div class="p-6">
              <h3 class="text-xl font-semibold text-minecraft-dark mb-3">{{ module.title }}</h3>
              <p class="text-gray-600 mb-4 line-clamp-3">{{ module.description }}</p>
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-500">Module {{ module.order }}</span>
                <a 
                  [routerLink]="['/tutorials/modules', module.id]"
                  class="bg-minecraft-green text-white px-4 py-2 rounded hover:bg-minecraft-dark transition-colors duration-200"
                >
                  Start Learning
                </a>
              </div>
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
    .line-clamp-3 {
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  `]
})
export class ModuleListComponent implements OnInit {
  modules: TutorialModule[] = [];
  loading = true;
  error: string | null = null;

  constructor(private tutorialService: TutorialService) {}

  ngOnInit(): void {
    this.loadModules();
  }

  private loadModules(): void {
    this.tutorialService.getModules().subscribe({
      next: (modules: TutorialModule[]) => {
        this.modules = modules;
        this.loading = false;
      },        error: (error: Error) => {
          this.error = 'Failed to load tutorial modules. Please try again later.';
        this.loading = false;
        console.error('Error loading modules:', error);
      }
    });
  }
}