import {
  AuthService,
  init_auth_service
} from "./chunk-VOIJPMP3.js";
import {
  HttpClient,
  HttpParams,
  init_http
} from "./chunk-M27OVNWH.js";
import {
  Injectable,
  __decorate,
  catchError,
  init_core,
  init_operators,
  init_tslib_es6,
  retry,
  timeout
} from "./chunk-36OBKH4Z.js";
import {
  __esm
} from "./chunk-TTULUY32.js";

// src/app/services/forum.service.ts
var ForumService;
var init_forum_service = __esm({
  "src/app/services/forum.service.ts"() {
    "use strict";
    init_tslib_es6();
    init_core();
    init_http();
    init_operators();
    init_auth_service();
    ForumService = class ForumService2 {
      http;
      authService;
      API_URL = "http://localhost:8080/api/forums";
      REQUEST_TIMEOUT = 15e3;
      // 15 seconds for forum operations
      constructor(http, authService) {
        this.http = http;
        this.authService = authService;
      }
      getThreads(page = 0, size = 20, category) {
        if (page < 0)
          page = 0;
        if (size < 1 || size > 100)
          size = 20;
        let params = new HttpParams().set("page", page.toString()).set("size", size.toString());
        if (category && category.trim() !== "") {
          params = params.set("category", category.trim());
        }
        return this.http.get(`${this.API_URL}/threads`, { params }).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error("Error fetching forum threads:", error);
          throw new Error("Could not load forum threads. Please try again later.");
        }));
      }
      getThread(threadId) {
        if (!threadId || threadId <= 0) {
          throw new Error("Invalid thread ID");
        }
        return this.http.get(`${this.API_URL}/threads/${threadId}`).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error(`Error fetching thread ${threadId}:`, error);
          throw new Error("Could not load thread. Please try again later.");
        }));
      }
      createThread(request) {
        if (!request || !request.title || request.title.trim() === "") {
          throw new Error("Thread title is required");
        }
        const validatedRequest = {
          title: request.title.trim(),
          content: request.content ? request.content.trim() : void 0
        };
        try {
          const headers = this.authService.getAuthenticatedHeaders();
          return this.http.post(`${this.API_URL}/threads`, validatedRequest, { headers }).pipe(timeout(this.REQUEST_TIMEOUT), catchError((error) => {
            console.error("Error creating forum thread:", error);
            throw new Error("Could not create thread. Please try again later.");
          }));
        } catch (error) {
          console.error("Authentication error when creating thread:", error);
          throw error;
        }
      }
      getThreadPosts(threadId, page = 0, size = 20) {
        if (!threadId || threadId <= 0) {
          throw new Error("Invalid thread ID");
        }
        if (page < 0)
          page = 0;
        if (size < 1 || size > 100)
          size = 20;
        const params = new HttpParams().set("page", page.toString()).set("size", size.toString());
        return this.http.get(`${this.API_URL}/threads/${threadId}/posts`, { params }).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error(`Error fetching posts for thread ${threadId}:`, error);
          throw new Error("Could not load thread posts. Please try again later.");
        }));
      }
      createPost(threadId, request) {
        if (!threadId || threadId <= 0) {
          throw new Error("Invalid thread ID");
        }
        if (!request || !request.content || request.content.trim() === "") {
          throw new Error("Post content is required");
        }
        const validatedRequest = {
          content: request.content.trim()
        };
        try {
          const headers = this.authService.getAuthenticatedHeaders();
          return this.http.post(`${this.API_URL}/threads/${threadId}/posts`, validatedRequest, { headers }).pipe(timeout(this.REQUEST_TIMEOUT), catchError((error) => {
            console.error(`Error creating post in thread ${threadId}:`, error);
            throw new Error("Could not create post. Please try again later.");
          }));
        } catch (error) {
          console.error("Authentication error when creating post:", error);
          throw error;
        }
      }
      static ctorParameters = () => [
        { type: HttpClient },
        { type: AuthService }
      ];
    };
    ForumService = __decorate([
      Injectable({
        providedIn: "root"
      })
    ], ForumService);
  }
});

export {
  ForumService,
  init_forum_service
};
//# sourceMappingURL=chunk-2VOR6OUE.js.map
