import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TutorialService } from './tutorial.service';
import { TutorialModule, TutorialLesson } from '../models/tutorial.model';

describe('TutorialService', () => {
  let service: TutorialService;
  let httpMock: HttpTestingController;

  const mockTutorialModule: TutorialModule = {
    id: 1,
    title: 'Getting Started',
    description: 'Learn the basics of Minecraft development',
    order: 1,
    isPublished: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    lessons: []
  };

  const mockTutorialLesson: TutorialLesson = {
    id: 1,
    title: 'Introduction to Minecraft Modding',
    content: 'This lesson covers the basics...',
    videoUrl: 'https://example.com/video1',
    order: 1,
    isPublished: true,
    module: mockTutorialModule,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TutorialService]
    });
    service = TestBed.inject(TutorialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getModules', () => {
    it('should fetch all tutorial modules', () => {
      const mockModules = [mockTutorialModule];

      service.getModules().subscribe(modules => {
        expect(modules).toEqual(mockModules);
        expect(modules.length).toBe(1);
        expect(modules[0].title).toBe('Getting Started');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/tutorials/modules');
      expect(req.request.method).toBe('GET');
      req.flush(mockModules);
    });

    it('should handle error when fetching modules', () => {
      service.getModules().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/tutorials/modules');
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getModule', () => {
    it('should fetch a specific module by ID', () => {
      const moduleId = 1;

      service.getModule(moduleId).subscribe(module => {
        expect(module).toEqual(mockTutorialModule);
        expect(module.id).toBe(moduleId);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTutorialModule);
    });

    it('should handle 404 error for non-existent module', () => {
      const moduleId = 999;

      service.getModule(moduleId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}`);
      req.flush('Module not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getModuleLessons', () => {
    it('should fetch lessons for a specific module', () => {
      const moduleId = 1;
      const mockLessons = [mockTutorialLesson];

      service.getModuleLessons(moduleId).subscribe(lessons => {
        expect(lessons).toEqual(mockLessons);
        expect(lessons.length).toBe(1);
        expect(lessons[0].module.id).toBe(moduleId);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}/lessons`);
      expect(req.request.method).toBe('GET');
      req.flush(mockLessons);
    });

    it('should handle empty lessons array', () => {
      const moduleId = 1;

      service.getModuleLessons(moduleId).subscribe(lessons => {
        expect(lessons).toEqual([]);
        expect(lessons.length).toBe(0);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/modules/${moduleId}/lessons`);
      req.flush([]);
    });
  });

  describe('getLesson', () => {
    it('should fetch a specific lesson by ID', () => {
      const lessonId = 1;

      service.getLesson(lessonId).subscribe(lesson => {
        expect(lesson).toEqual(mockTutorialLesson);
        expect(lesson.id).toBe(lessonId);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/lessons/${lessonId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTutorialLesson);
    });

    it('should handle 404 error for non-existent lesson', () => {
      const lessonId = 999;

      service.getLesson(lessonId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Tutorial content not found');
        }
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/tutorials/lessons/${lessonId}`);
      req.flush('Lesson not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('error handling', () => {
    it('should handle network errors', () => {
      service.getModules().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Network error');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/tutorials/modules');
      req.error(new ErrorEvent('Network error'));
    });

    it('should handle server errors', () => {
      service.getModules().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toContain('Server error');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/tutorials/modules');
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should throw error for invalid module ID', () => {
      expect(() => service.getModule(0)).toThrowError('Invalid module ID');
      expect(() => service.getModule(-1)).toThrowError('Invalid module ID');
    });

    it('should throw error for invalid lesson ID', () => {
      expect(() => service.getLesson(0)).toThrowError('Invalid lesson ID');
      expect(() => service.getLesson(-1)).toThrowError('Invalid lesson ID');
    });
  });
});