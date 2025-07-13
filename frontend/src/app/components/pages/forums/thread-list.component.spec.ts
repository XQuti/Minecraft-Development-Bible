import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
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
        author: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          avatarUrl: undefined,
          provider: 'local',
          roles: ['USER']
        },
        isPinned: false,
        isLocked: false,
        postCount: 5,
        lastActivity: '2024-01-01T00:00:00',
        createdAt: '2024-01-01T00:00:00',
        updatedAt: '2024-01-01T00:00:00'
      }
    ],
    totalElements: 1,
    totalPages: 1,
    size: 20,
    number: 0,
    first: true,
    last: true
  };

  beforeEach(async () => {
    const forumServiceSpy = jasmine.createSpyObj('ForumService', ['getThreads', 'createThread']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated']);

    await TestBed.configureTestingModule({
      imports: [
        ThreadListComponent,
        HttpClientTestingModule,
        FormsModule,
        RouterModule.forRoot([])
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
    authService.isAuthenticated.and.returnValue(false);

    component.ngOnInit();

    expect(forumService.getThreads).toHaveBeenCalled();
    expect(component.threads).toEqual(mockThreadsResponse.content);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading threads', () => {
    const errorResponse = { status: 500, message: 'Server Error' };
    forumService.getThreads.and.returnValue(throwError(() => errorResponse));
    authService.isAuthenticated.and.returnValue(false);

    component.ngOnInit();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Failed to load forum threads. Please try again later.');
  });

  it('should create new thread when authenticated', () => {
    const newThread = {
      id: 2,
      title: 'New Thread',
      author: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        avatarUrl: undefined,
        provider: 'local',
        roles: ['USER']
      },
      isPinned: false,
      isLocked: false,
      postCount: 1,
      lastActivity: '2024-01-01T01:00:00',
      createdAt: '2024-01-01T01:00:00',
      updatedAt: '2024-01-01T01:00:00'
    };

    authService.isAuthenticated.and.returnValue(true);
    forumService.createThread.and.returnValue(of(newThread));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.newThread = {
      title: 'New Thread',
      content: 'Thread content'
    };

    component.createThread();

    expect(forumService.createThread).toHaveBeenCalledWith({
      title: 'New Thread',
      content: 'Thread content'
    });
    expect(component.showCreateForm).toBeFalse();
  });

  it('should handle error when creating thread', () => {
    const errorResponse = { status: 400, message: 'Bad Request' };
    authService.isAuthenticated.and.returnValue(true);
    forumService.createThread.and.returnValue(throwError(() => errorResponse));

    component.newThread = {
      title: 'New Thread',
      content: 'Thread content'
    };

    component.createThread();

    expect(component.error).toBe('Failed to create thread. Please try again.');
    expect(component.creating).toBeFalse();
  });

  it('should validate form before creating thread', () => {
    authService.isAuthenticated.and.returnValue(true);
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    // Form is invalid (empty title)
    component.newThread = {
      title: '',
      content: 'Thread content'
    };

    component.createThread();

    expect(forumService.createThread).not.toHaveBeenCalled();
  });

  it('should reset form after successful thread creation', () => {
    const newThread = {
      id: 2,
      title: 'New Thread',
      author: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        avatarUrl: undefined,
        provider: 'local',
        roles: ['USER']
      },
      isPinned: false,
      isLocked: false,
      postCount: 1,
      lastActivity: '2024-01-01T01:00:00',
      createdAt: '2024-01-01T01:00:00',
      updatedAt: '2024-01-01T01:00:00'
    };

    authService.isAuthenticated.and.returnValue(true);
    forumService.createThread.and.returnValue(of(newThread));
    forumService.getThreads.and.returnValue(of(mockThreadsResponse));

    component.newThread = {
      title: 'New Thread',
      content: 'Thread content'
    };

    component.createThread();

    expect(component.newThread.title).toBe('');
    expect(component.newThread.content).toBe('');
  });
});