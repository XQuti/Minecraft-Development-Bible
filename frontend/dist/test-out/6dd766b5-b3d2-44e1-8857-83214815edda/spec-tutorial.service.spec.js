import {
  HttpClientTestingModule,
  HttpTestingController,
  init_testing as init_testing2
} from "./chunk-4J2RBVF3.js";
import {
  HttpClient,
  Injectable,
  TestBed,
  __decorate,
  catchError,
  init_core,
  init_http,
  init_operators,
  init_testing,
  init_tslib_es6,
  retry,
  timeout
} from "./chunk-T6WWCSSA.js";
import {
  __async,
  __commonJS,
  __esm
} from "./chunk-TTULUY32.js";

// src/app/services/tutorial.service.ts
var TutorialService;
var init_tutorial_service = __esm({
  "src/app/services/tutorial.service.ts"() {
    "use strict";
    init_tslib_es6();
    init_core();
    init_http();
    init_operators();
    TutorialService = class TutorialService2 {
      http;
      API_URL = "http://localhost:8080/api/tutorials";
      REQUEST_TIMEOUT = 1e4;
      // 10 seconds
      constructor(http) {
        this.http = http;
      }
      getModules() {
        return this.http.get(`${this.API_URL}/modules`).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error("Error fetching tutorial modules:", error);
          return this.handleError(error);
        }));
      }
      getModule(moduleId) {
        if (!moduleId || moduleId <= 0) {
          throw new Error("Invalid module ID");
        }
        return this.http.get(`${this.API_URL}/modules/${moduleId}`).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error(`Error fetching tutorial module ${moduleId}:`, error);
          return this.handleError(error);
        }));
      }
      getModuleLessons(moduleId) {
        if (!moduleId || moduleId <= 0) {
          throw new Error("Invalid module ID");
        }
        return this.http.get(`${this.API_URL}/modules/${moduleId}/lessons`).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error(`Error fetching lessons for module ${moduleId}:`, error);
          return this.handleError(error);
        }));
      }
      getLesson(lessonId) {
        if (!lessonId || lessonId <= 0) {
          throw new Error("Invalid lesson ID");
        }
        return this.http.get(`${this.API_URL}/lessons/${lessonId}`).pipe(timeout(this.REQUEST_TIMEOUT), retry(1), catchError((error) => {
          console.error(`Error fetching tutorial lesson ${lessonId}:`, error);
          return this.handleError(error);
        }));
      }
      handleError(error) {
        let errorMessage = "An unexpected error occurred";
        if (error.status === 0) {
          errorMessage = "Network error - please check your connection";
        } else if (error.status === 404) {
          errorMessage = "Tutorial content not found";
        } else if (error.status >= 500) {
          errorMessage = "Server error - please try again later";
        } else if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
        console.error("Tutorial Service Error:", {
          status: error.status,
          message: errorMessage,
          url: error.url
        });
        throw new Error(errorMessage);
      }
      static ctorParameters = () => [
        { type: HttpClient }
      ];
    };
    TutorialService = __decorate([
      Injectable({
        providedIn: "root"
      })
    ], TutorialService);
  }
});

// src/app/services/tutorial.service.spec.ts
var require_tutorial_service_spec = __commonJS({
  "src/app/services/tutorial.service.spec.ts"(exports) {
    init_testing();
    init_testing2();
    init_tutorial_service();
    describe("TutorialService", () => {
      let service;
      let httpMock;
      const mockTutorialModule = {
        id: 1,
        title: "Getting Started",
        description: "Learn the basics of Minecraft development",
        order: 1,
        isPublished: true,
        createdAt: "2024-01-01T00:00:00Z",
        updatedAt: "2024-01-01T00:00:00Z",
        lessons: []
      };
      const mockTutorialLesson = {
        id: 1,
        title: "Introduction to Minecraft Modding",
        content: "This lesson covers the basics...",
        videoUrl: "https://example.com/video1",
        order: 1,
        isPublished: true,
        module: mockTutorialModule,
        createdAt: "2024-01-01T00:00:00Z",
        updatedAt: "2024-01-01T00:00:00Z"
      };
      beforeEach(() => __async(null, null, function* () {
        yield TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers: [TutorialService]
        }).compileComponents();
        service = TestBed.inject(TutorialService);
        httpMock = TestBed.inject(HttpTestingController);
      }));
      afterEach(() => {
        if (httpMock) {
          try {
            httpMock.verify();
          } catch (error) {
            console.warn("HttpMock verification failed:", error);
          }
        }
      });
      it("should be created", () => {
        expect(service).toBeTruthy();
      });
      describe("getModules", () => {
        it("should fetch all tutorial modules", () => {
          const mockModules = [mockTutorialModule];
          service.getModules().subscribe((modules) => {
            expect(modules).toEqual(mockModules);
            expect(modules.length).toBe(1);
            expect(modules[0].title).toBe("Getting Started");
          });
          const req = httpMock.expectOne("http://localhost:8080/api/tutorials/modules");
          expect(req.request.method).toBe("GET");
          req.flush(mockModules);
        });
        it("should handle error when fetching modules", () => {
          service.getModules().subscribe({
            next: () => fail("should have failed"),
            error: (error) => {
              expect(error.message).toBe("Server error - please try again later");
            }
          });
          const req = httpMock.expectOne("http://localhost:8080/api/tutorials/modules");
          req.flush("Server error", { status: 500, statusText: "Internal Server Error" });
        });
      });
      describe("getModule", () => {
        it("should fetch a specific module by ID", () => {
          const moduleId = 1;
          service.getModule(moduleId).subscribe((module2) => {
            expect(module2).toEqual(mockTutorialModule);
            expect(module2.id).toBe(moduleId);
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}`);
          expect(req.request.method).toBe("GET");
          req.flush(mockTutorialModule);
        });
        it("should handle 404 error for non-existent module", () => {
          const moduleId = 999;
          service.getModule(moduleId).subscribe({
            next: () => fail("should have failed"),
            error: (error) => {
              expect(error.message).toBe("Tutorial content not found");
            }
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}`);
          req.flush("Module not found", { status: 404, statusText: "Not Found" });
        });
      });
      describe("getModuleLessons", () => {
        it("should fetch lessons for a specific module", () => {
          const moduleId = 1;
          const mockLessons = [mockTutorialLesson];
          service.getModuleLessons(moduleId).subscribe((lessons) => {
            expect(lessons).toEqual(mockLessons);
            expect(lessons.length).toBe(1);
            expect(lessons[0].module.id).toBe(moduleId);
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}/lessons`);
          expect(req.request.method).toBe("GET");
          req.flush(mockLessons);
        });
        it("should handle empty lessons array", () => {
          const moduleId = 1;
          service.getModuleLessons(moduleId).subscribe((lessons) => {
            expect(lessons).toEqual([]);
            expect(lessons.length).toBe(0);
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}/lessons`);
          req.flush([]);
        });
      });
      describe("getLesson", () => {
        it("should fetch a specific lesson by ID", () => {
          const lessonId = 1;
          service.getLesson(lessonId).subscribe((lesson) => {
            expect(lesson).toEqual(mockTutorialLesson);
            expect(lesson.id).toBe(lessonId);
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/lessons/${lessonId}`);
          expect(req.request.method).toBe("GET");
          req.flush(mockTutorialLesson);
        });
        it("should handle 404 error for non-existent lesson", () => {
          const lessonId = 999;
          service.getLesson(lessonId).subscribe({
            next: () => fail("should have failed"),
            error: (error) => {
              expect(error.message).toContain("Tutorial content not found");
            }
          });
          const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/lessons/${lessonId}`);
          req.flush("Lesson not found", { status: 404, statusText: "Not Found" });
        });
      });
      describe("error handling", () => {
        it("should handle network errors", () => {
          service.getModules().subscribe({
            next: () => fail("should have failed"),
            error: (error) => {
              expect(error.message).toBe("Network error - please check your connection");
            }
          });
          const req = httpMock.expectOne("http://localhost:8080/api/tutorials/modules");
          req.error(new ErrorEvent("Network error"));
        });
        it("should handle server errors", () => {
          service.getModules().subscribe({
            next: () => fail("should have failed"),
            error: (error) => {
              expect(error.message).toBe("Server error - please try again later");
            }
          });
          const req = httpMock.expectOne("http://localhost:8080/api/tutorials/modules");
          req.flush("Server error", { status: 500, statusText: "Internal Server Error" });
        });
        it("should throw error for invalid module ID", () => {
          expect(() => service.getModule(0)).toThrowError("Invalid module ID");
          expect(() => service.getModule(-1)).toThrowError("Invalid module ID");
        });
        it("should throw error for invalid lesson ID", () => {
          expect(() => service.getLesson(0)).toThrowError("Invalid lesson ID");
          expect(() => service.getLesson(-1)).toThrowError("Invalid lesson ID");
        });
      });
    });
  }
});
export default require_tutorial_service_spec();
//# sourceMappingURL=spec-tutorial.service.spec.js.map
