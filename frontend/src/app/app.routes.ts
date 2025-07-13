import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/pages/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'tutorials',
    loadComponent: () => import('./components/pages/tutorials/module-list.component').then(m => m.ModuleListComponent)
  },
  {
    path: 'tutorials/modules/:moduleId',
    loadComponent: () => import('./components/pages/tutorials/module-list.component').then(m => m.ModuleListComponent)
  },
  {
    path: 'tutorials/modules/:moduleId/lessons/:lessonId',
    loadComponent: () => import('./components/pages/tutorials/lesson-view.component').then(m => m.LessonViewComponent)
  },
  {
    path: 'forums',
    loadComponent: () => import('./components/pages/forums/thread-list.component').then(m => m.ThreadListComponent)
  },
  {
    path: 'forums/threads/:id',
    loadComponent: () => import('./components/pages/forums/thread-view.component').then(m => m.ThreadViewComponent)
  },
  {
    path: 'auth/callback',
    loadComponent: () => import('./components/auth/auth-callback.component').then(m => m.AuthCallbackComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
