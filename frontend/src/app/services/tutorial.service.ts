import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, timeout, retry } from 'rxjs/operators';
import { TutorialModule, TutorialLesson } from '../models/tutorial.model';

@Injectable({
  providedIn: 'root'
})
export class TutorialService {
  private readonly API_URL = 'http://localhost:8080/api/tutorials';
  private readonly REQUEST_TIMEOUT = 10000; // 10 seconds

  constructor(private http: HttpClient) {}

  getModules(): Observable<TutorialModule[]> {
    return this.http.get<TutorialModule[]>(`${this.API_URL}/modules`).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching tutorial modules:', error);
        return this.handleError(error);
      })
    );
  }

  getModule(moduleId: number): Observable<TutorialModule> {
    if (!moduleId || moduleId <= 0) {
      throw new Error('Invalid module ID');
    }

    return this.http.get<TutorialModule>(`${this.API_URL}/modules/${moduleId}`).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error(`Error fetching tutorial module ${moduleId}:`, error);
        return this.handleError(error);
      })
    );
  }

  getModuleLessons(moduleId: number): Observable<TutorialLesson[]> {
    if (!moduleId || moduleId <= 0) {
      throw new Error('Invalid module ID');
    }

    return this.http.get<TutorialLesson[]>(`${this.API_URL}/modules/${moduleId}/lessons`).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error(`Error fetching lessons for module ${moduleId}:`, error);
        return this.handleError(error);
      })
    );
  }

  getLesson(lessonId: number): Observable<TutorialLesson> {
    if (!lessonId || lessonId <= 0) {
      throw new Error('Invalid lesson ID');
    }

    return this.http.get<TutorialLesson>(`${this.API_URL}/lessons/${lessonId}`).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error(`Error fetching tutorial lesson ${lessonId}:`, error);
        return this.handleError(error);
      })
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unexpected error occurred';
    
    if (error.status === 0) {
      errorMessage = 'Network error - please check your connection';
    } else if (error.status === 404) {
      errorMessage = 'Tutorial content not found';
    } else if (error.status >= 500) {
      errorMessage = 'Server error - please try again later';
    } else if (error.error && error.error.message) {
      errorMessage = error.error.message;
    }
    
    console.error('Tutorial Service Error:', {
      status: error.status,
      message: errorMessage,
      url: error.url
    });
    
    throw new Error(errorMessage);
  }
}