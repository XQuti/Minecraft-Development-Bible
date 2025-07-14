import {
  ForumService,
  init_forum_service
} from "./chunk-2VOR6OUE.js";
import {
  HttpClientTestingModule,
  HttpTestingController,
  init_testing as init_testing2
} from "./chunk-MROCUD6J.js";
import "./chunk-VOIJPMP3.js";
import "./chunk-M27OVNWH.js";
import {
  TestBed,
  init_testing
} from "./chunk-36OBKH4Z.js";
import {
  __spreadProps,
  __spreadValues
} from "./chunk-TTULUY32.js";

// src/app/services/forum.service.spec.ts
init_testing();
init_testing2();
init_forum_service();
describe("ForumService", () => {
  let service;
  let httpMock;
  const baseUrl = "http://localhost:8080/api/forums";
  const mockUser = {
    id: 1,
    username: "testuser",
    email: "test@example.com",
    avatarUrl: void 0,
    provider: "local",
    roles: ["USER"]
  };
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
  it("should be created", () => {
    expect(service).toBeTruthy();
  });
  describe("getThreads", () => {
    it("should fetch threads without category", () => {
      const mockResponse = {
        content: [
          {
            id: 1,
            title: "Test Thread",
            author: mockUser,
            isPinned: false,
            isLocked: false,
            postCount: 5,
            lastActivity: "2024-01-01T00:00:00",
            createdAt: "2024-01-01T00:00:00",
            updatedAt: "2024-01-01T00:00:00"
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      };
      service.getThreads(0, 20).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.content.length).toBe(1);
        expect(response.content[0].title).toBe("Test Thread");
      });
      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      expect(req.request.method).toBe("GET");
      req.flush(mockResponse);
    });
    it("should fetch threads with search query", () => {
      const searchQuery = "test";
      const mockResponse = {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 20,
        number: 0,
        first: true,
        last: true
      };
      service.getThreads(0, 20, searchQuery).subscribe((response) => {
        expect(response).toEqual(mockResponse);
      });
      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20&search=${searchQuery}`);
      expect(req.request.method).toBe("GET");
      req.flush(mockResponse);
    });
    it("should handle error when fetching threads", () => {
      service.getThreads(0, 20).subscribe({
        next: () => fail("should have failed"),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });
      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      req.flush("Server Error", { status: 500, statusText: "Internal Server Error" });
    });
  });
  describe("createThread", () => {
    it("should create a new thread", () => {
      const createRequest = {
        title: "New Thread",
        content: "Thread content"
      };
      const mockResponse = {
        id: 1,
        title: "New Thread",
        author: mockUser,
        isPinned: false,
        isLocked: false,
        postCount: 1,
        lastActivity: "2024-01-01T00:00:00",
        createdAt: "2024-01-01T00:00:00",
        updatedAt: "2024-01-01T00:00:00"
      };
      service.createThread(createRequest).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.title).toBe("New Thread");
      });
      const req = httpMock.expectOne(`${baseUrl}/threads`);
      expect(req.request.method).toBe("POST");
      expect(req.request.body).toEqual(createRequest);
      req.flush(mockResponse);
    });
    it("should handle error when creating thread", () => {
      const createRequest = {
        title: "",
        content: "Thread content"
      };
      service.createThread(createRequest).subscribe({
        next: () => fail("should have failed"),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });
      const req = httpMock.expectOne(`${baseUrl}/threads`);
      req.flush("Bad Request", { status: 400, statusText: "Bad Request" });
    });
  });
  describe("getThreadPosts", () => {
    it("should fetch posts for a thread", () => {
      const threadId = 1;
      const mockThread = {
        id: threadId,
        title: "Test Thread",
        author: mockUser,
        isPinned: false,
        isLocked: false,
        postCount: 2,
        lastActivity: "2024-01-01T01:00:00",
        createdAt: "2024-01-01T00:00:00"
      };
      const mockPosts = [
        {
          id: 1,
          content: "First post",
          author: mockUser,
          thread: mockThread,
          createdAt: "2024-01-01T00:00:00"
        },
        {
          id: 2,
          content: "Second post",
          author: __spreadProps(__spreadValues({}, mockUser), { id: 2, username: "testuser2" }),
          thread: mockThread,
          createdAt: "2024-01-01T01:00:00"
        }
      ];
      const mockResponse = {
        content: mockPosts,
        totalElements: 2,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      };
      service.getThreadPosts(threadId).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.content.length).toBe(2);
        expect(response.content[0].content).toBe("First post");
      });
      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts?page=0&size=20`);
      expect(req.request.method).toBe("GET");
      req.flush(mockResponse);
    });
    it("should handle error when thread not found", () => {
      const threadId = 999;
      service.getThreadPosts(threadId).subscribe({
        next: () => fail("should have failed"),
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });
      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts?page=0&size=20`);
      req.flush("Not Found", { status: 404, statusText: "Not Found" });
    });
  });
  describe("createPost", () => {
    it("should create a new post", () => {
      const threadId = 1;
      const createRequest = {
        content: "New post content"
      };
      const mockThread = {
        id: threadId,
        title: "Test Thread",
        author: mockUser,
        isPinned: false,
        isLocked: false,
        postCount: 1,
        lastActivity: "2024-01-01T02:00:00",
        createdAt: "2024-01-01T00:00:00"
      };
      const mockResponse = {
        id: 3,
        content: "New post content",
        author: mockUser,
        thread: mockThread,
        createdAt: "2024-01-01T02:00:00"
      };
      service.createPost(threadId, createRequest).subscribe((response) => {
        expect(response).toEqual(mockResponse);
        expect(response.content).toBe("New post content");
      });
      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      expect(req.request.method).toBe("POST");
      expect(req.request.body).toEqual(createRequest);
      req.flush(mockResponse);
    });
    it("should handle error when creating post without authentication", () => {
      const threadId = 1;
      const createRequest = {
        content: "New post content"
      };
      service.createPost(threadId, createRequest).subscribe({
        next: () => fail("should have failed"),
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });
      const req = httpMock.expectOne(`${baseUrl}/threads/${threadId}/posts`);
      req.flush("Unauthorized", { status: 401, statusText: "Unauthorized" });
    });
  });
  describe("error handling", () => {
    it("should handle network errors", () => {
      service.getThreads(0, 20).subscribe({
        next: () => fail("should have failed"),
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });
      const req = httpMock.expectOne(`${baseUrl}/threads?page=0&size=20`);
      req.error(new ErrorEvent("Network error"));
    });
  });
});
//# sourceMappingURL=spec-forum.service.spec.js.map
