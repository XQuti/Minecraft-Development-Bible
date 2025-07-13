import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, timeout, retry } from 'rxjs/operators';
import { ForumThread, ForumPost, CreateThreadRequest, CreatePostRequest, PageResponse } from '../models/forum.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private readonly API_URL = 'http://localhost:8080/api/forums';
  private readonly REQUEST_TIMEOUT = 15000; // 15 seconds for forum operations

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getThreads(page: number = 0, size: number = 20, category?: string): Observable<PageResponse<ForumThread>> {
    // Validate parameters
    if (page < 0) page = 0;
    if (size < 1 || size > 100) size = 20;

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (category && category.trim() !== '') {
      params = params.set('category', category.trim());
    }
    
    return this.http.get<PageResponse<ForumThread>>(`${this.API_URL}/threads`, { params }).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching forum threads:', error);
        // Return a user-friendly error message
        throw new Error('Could not load forum threads. Please try again later.');
      })
    );
  }

  getThread(threadId: number): Observable<ForumThread> {
    if (!threadId || threadId <= 0) {
      throw new Error('Invalid thread ID');
    }

    return this.http.get<ForumThread>(`${this.API_URL}/threads/${threadId}`).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error(`Error fetching thread ${threadId}:`, error);
        throw new Error('Could not load thread. Please try again later.');
      })
    );
  }

  createThread(request: CreateThreadRequest): Observable<ForumThread> {
    if (!request || !request.title || request.title.trim() === '') {
      throw new Error('Thread title is required');
    }

    // Validate request
    const validatedRequest: CreateThreadRequest = {
      title: request.title.trim(),
      content: request.content ? request.content.trim() : undefined
    };

    try {
      const headers = this.authService.getAuthenticatedHeaders();
      return this.http.post<ForumThread>(`${this.API_URL}/threads`, validatedRequest, { headers }).pipe(
        timeout(this.REQUEST_TIMEOUT),
        catchError((error: HttpErrorResponse) => {
          console.error('Error creating forum thread:', error);
          throw new Error('Could not create thread. Please try again later.');
        })
      );
    } catch (error) {
      console.error('Authentication error when creating thread:', error);
      throw error;
    }
  }

  getThreadPosts(threadId: number, page: number = 0, size: number = 20): Observable<PageResponse<ForumPost>> {
    if (!threadId || threadId <= 0) {
      throw new Error('Invalid thread ID');
    }

    // Validate parameters
    if (page < 0) page = 0;
    if (size < 1 || size > 100) size = 20;

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<ForumPost>>(`${this.API_URL}/threads/${threadId}/posts`, { params }).pipe(
      timeout(this.REQUEST_TIMEOUT),
      retry(1),
      catchError((error: HttpErrorResponse) => {
        console.error(`Error fetching posts for thread ${threadId}:`, error);
        throw new Error('Could not load thread posts. Please try again later.');
      })
    );
  }

  createPost(threadId: number, request: CreatePostRequest): Observable<ForumPost> {
    if (!threadId || threadId <= 0) {
      throw new Error('Invalid thread ID');
    }

    if (!request || !request.content || request.content.trim() === '') {
      throw new Error('Post content is required');
    }

    // Validate request
    const validatedRequest: CreatePostRequest = {
      content: request.content.trim()
    };

    try {
      const headers = this.authService.getAuthenticatedHeaders();
      return this.http.post<ForumPost>(`${this.API_URL}/threads/${threadId}/posts`, validatedRequest, { headers }).pipe(
        timeout(this.REQUEST_TIMEOUT),
        catchError((error: HttpErrorResponse) => {
          console.error(`Error creating post in thread ${threadId}:`, error);
          throw new Error('Could not create post. Please try again later.');
        })
      );
    } catch (error) {
      console.error('Authentication error when creating post:', error);
      throw error;
    }
  }
}