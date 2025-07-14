import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SearchableForumThread {
  id: number;
  title: string;
  content: string;
  category: string;
  authorUsername: string;
  createdAt: string;
  updatedAt: string;
  postCount: number;
  isPinned: boolean;
  isLocked: boolean;
  tags: string[];
}

export interface SearchableForumPost {
  id: number;
  content: string;
  threadId: number;
  threadTitle: string;
  authorUsername: string;
  createdAt: string;
  updatedAt: string;
  category: string;
}

export interface SearchResults<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private apiUrl = 'http://localhost:8080/api/search';

  constructor(private http: HttpClient) {}

  searchThreads(
    query?: string,
    category?: string,
    author?: string,
    page: number = 0,
    size: number = 20,
    sortBy: string = 'createdAt',
    sortDir: string = 'desc'
  ): Observable<SearchResults<SearchableForumThread>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (query && query.trim()) {
      params = params.set('query', query.trim());
    }
    if (category && category.trim()) {
      params = params.set('category', category.trim());
    }
    if (author && author.trim()) {
      params = params.set('author', author.trim());
    }

    return this.http.get<SearchResults<SearchableForumThread>>(`${this.apiUrl}/threads`, { params });
  }

  searchPosts(
    query: string,
    category?: string,
    page: number = 0,
    size: number = 20,
    sortBy: string = 'createdAt',
    sortDir: string = 'desc'
  ): Observable<SearchResults<SearchableForumPost>> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (category && category.trim()) {
      params = params.set('category', category.trim());
    }

    return this.http.get<SearchResults<SearchableForumPost>>(`${this.apiUrl}/posts`, { params });
  }

  searchThreadsByCategory(
    category: string,
    page: number = 0,
    size: number = 20
  ): Observable<SearchResults<SearchableForumThread>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResults<SearchableForumThread>>(
      `${this.apiUrl}/threads/category/${encodeURIComponent(category)}`,
      { params }
    );
  }

  searchThreadsByAuthor(
    author: string,
    page: number = 0,
    size: number = 20
  ): Observable<SearchResults<SearchableForumThread>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResults<SearchableForumThread>>(
      `${this.apiUrl}/threads/author/${encodeURIComponent(author)}`,
      { params }
    );
  }
}