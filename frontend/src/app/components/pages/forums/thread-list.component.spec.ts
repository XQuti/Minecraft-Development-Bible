import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ThreadListComponent } from './thread-list.component';
import { ForumService } from '../../../services/forum.service';
import { AuthService } from '../../../services/auth.service';

describe('ThreadListComponent', () => {
  let component: ThreadListComponent;
  let fixture: ComponentFixture<ThreadListComponent>;
  let forumService: jasmine.SpyObj<ForumService>;
  let authService: jasmine.SpyObj<AuthService>;

  const mockThreadsResponse = {
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

  beforeEach(async () => {
    const forumServiceSpy = jasmine.createSpyObj('ForumService', ['getThreads', 'createThread']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'getCurrentUser']);

    await TestBed.configureTestingModule({
      imports: [
        ThreadListComponent,
        HttpClientTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: ForumService, useValue: forumServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ThreadListComponent);
    component = fixture.componentInstance;
    forumService = TestBed.inject(ForumService) as jasmine.SpyObj<ForumService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load threads on init', () => {
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));
    authService.isAuthenticated.and.returnValue(of(false));

    component.ngOnInit();

    expect(forumService.getThreads).toHaveBeenCalledWith(0, 20, undefined);
    expect(component.threads).toEqual(mockThreadsResponse.content);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading threads', () => {
    const errorResponse = { status: 500, message: 'Server Error' };
    forumService.getThreads.and.returnValue(throwError(() => errorResponse));
    authService.isAuthenticated.and.returnValue(of(false));

    component.ngOnInit();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Failed to load forum threads. Please try again.');
  });

  it('should filter threads by category', () => {
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));
    authService.isAuthenticated.and.returnValue(of(false));

    component.selectedCategory = 'general';
    component.loadThreads();

    expect(forumService.getThreads).toHaveBeenCalledWith(0, 20, 'general');
  });

  it('should create new thread when authenticated', () => {
    const newThread = {
      id: 2,
      title: 'New Thread',
      category: 'general',
      authorUsername: 'testuser',
      createdAt: '2024-01-01T01:00:00',
      updatedAt: '2024-01-01T01:00:00',
      postCount: 1,
      pinned: false
    };

    authService.isAuthenticated.and.returnValue(of(true));
    forumService.createThread.and.returnValue(of(newThread));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.createThreadForm.patchValue({
      title: 'New Thread',
      content: 'Thread content',
      category: 'general'
    });

    component.onCreateThread();

    expect(forumService.createThread).toHaveBeenCalledWith({
      title: 'New Thread',
      content: 'Thread content',
      category: 'general'
    });
    expect(component.showCreateForm).toBeFalse();
  });

  it('should handle error when creating thread', () => {
    const errorResponse = { status: 400, message: 'Bad Request' };
    authService.isAuthenticated.and.returnValue(of(true));
    forumService.createThread.and.returnValue(throwError(() => errorResponse));

    component.createThreadForm.patchValue({
      title: 'New Thread',
      content: 'Thread content',
      category: 'general'
    });

    component.onCreateThread();

    expect(component.error).toBe('Failed to create thread. Please try again.');
    expect(component.showCreateForm).toBeTrue();
  });

  it('should not show create form when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(of(false));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.ngOnInit();

    expect(component.isAuthenticated).toBeFalse();
  });

  it('should show create form when authenticated', () => {
    authService.isAuthenticated.and.returnValue(of(true));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.ngOnInit();

    expect(component.isAuthenticated).toBeTrue();
  });

  it('should validate form before creating thread', () => {
    authService.isAuthenticated.and.returnValue(of(true));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    // Form is invalid (empty title)
    component.createThreadForm.patchValue({
      title: '',
      content: 'Thread content',
      category: 'general'
    });

    component.onCreateThread();

    expect(forumService.createThread).not.toHaveBeenCalled();
  });

  it('should load more threads on pagination', () => {
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));
    authService.isAuthenticated.and.returnValue(of(false));

    component.currentPage = 1;
    component.loadThreads();

    expect(forumService.getThreads).toHaveBeenCalledWith(1, 20, undefined);
  });

  it('should reset form after successful thread creation', () => {
    const newThread = {
      id: 2,
      title: 'New Thread',
      category: 'general',
      authorUsername: 'testuser',
      createdAt: '2024-01-01T01:00:00',
      updatedAt: '2024-01-01T01:00:00',
      postCount: 1,
      pinned: false
    };

    authService.isAuthenticated.and.returnValue(of(true));
    forumService.createThread.and.returnValue(of(newThread));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.createThreadForm.patchValue({
      title: 'New Thread',
      content: 'Thread content',
      category: 'general'
    });

    component.onCreateThread();

    expect(component.createThreadForm.get('title')?.value).toBe('');
    expect(component.createThreadForm.get('content')?.value).toBe('');
    expect(component.createThreadForm.get('category')?.value).toBe('general');
  });
});