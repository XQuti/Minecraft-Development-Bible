import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ForumService } from './forum.service';
import { ForumThread, ForumPost, CreateThreadRequest, CreatePostRequest } from '../models/forum.model';

describe('ForumService', () => {
  let service: ForumService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8080/api/forums';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ForumService]
    });
    service = TestBed.inject(ForumService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getThreads', () => {
    it('should fetch threads without category', () => {
      const mockResponse = {
        content: [
          {
            id: 1,
            title: 'Test Thread',
            category: 'general',
            authorUsername: 'testuser',
            createdAt: '2024-01-01T00:00:00',
            updatedAt: '2024-01-01T00:00:00',
            postCount: 5,
            pinned: false
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0
      };

      service.getThreads(0, 20).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.content.length).toBe(1);
        expect(response.content[0].title).toBe('Test Thread');
      });

      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should fetch threads with category filter', () => {
      const category = 'general';
      const mockResponse = {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 20,
        number: 0
      };

      service.getThreads(0, 20, category).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20&category=${category}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle error when fetching threads', () => {
      service.getThreads(0, 20).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('createThread', () => {
    it('should create a new thread', () => {
      const createRequest: CreateThreadRequest = {
        title: 'New Thread',
        content: 'Thread content',
        category: 'general'
      };

      const mockResponse: ForumThread = {
        id: 1,
        title: 'New Thread',
        category: 'general',
        authorUsername: 'testuser',
        createdAt: '2024-01-01T00:00:00',
        updatedAt: '2024-01-01T00:00:00',
        postCount: 1,
        pinned: false
      };

      service.createThread(createRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.title).toBe('New Thread');
      });

      const req = httpMock.expectOne(`${baseUrl}/threads`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush(mockResponse);
    });

    it('should handle error when creating thread', () => {
      const createRequest: CreateThreadRequest = {
        title: '',
        content: 'Thread content',
        category: 'general'
      };

      service.createThread(createRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/threads`);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('getThreadPosts', () => {
    it('should fetch posts for a thread', () => {
      const threadId = 1;
      const mockPosts: ForumPost[] = [
        {
          id: 1,
          content: 'First post',
          authorUsername: 'testuser',
          createdAt: '2024-01-01T00:00:00'
        },
        {
          id: 2,
          content: 'Second post',
          authorUsername: 'testuser2',
          createdAt: '2024-01-01T01:00:00'
        }
      ];

      service.getThreadPosts(threadId).subscribe(posts => {
        expect(posts).toEqual(mockPosts);
        expect(posts.length).toBe(2);
        expect(posts[0].content).toBe('First post');
      });

      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle error when thread not found', () => {
      const threadId = 999;

      service.getThreadPosts(threadId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('createPost', () => {
    it('should create a new post', () => {
      const threadId = 1;
      const createRequest: CreatePostRequest = {
        content: 'New post content'
      };

      const mockResponse: ForumPost = {
        id: 3,
        content: 'New post content',
        authorUsername: 'testuser',
        createdAt: '2024-01-01T02:00:00'
      };

      service.createPost(threadId, createRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.content).toBe('New post content');
      });

      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush(mockResponse);
    });

    it('should handle error when creating post without authentication', () => {
      const threadId = 1;
      const createRequest: CreatePostRequest = {
        content: 'New post content'
      };

      service.createPost(threadId, createRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('error handling', () => {
    it('should handle network errors', () => {
      service.getThreads(0, 20).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      req.error(new ErrorEvent('Network error'));
    });
  });
});