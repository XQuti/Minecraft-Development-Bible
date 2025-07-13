# Minecraft Development Bible - Frontend UI

A modern Angular web application for the Minecraft Development Bible platform, providing an intuitive interface for forums, tutorials, and user authentication.

## Technologies Used

- **Angular 20.1.0** - Modern web application framework
- **TypeScript** - Type-safe JavaScript development
- **Tailwind CSS** - Utility-first CSS framework
- **RxJS** - Reactive programming with observables
- **Angular Router** - Client-side navigation
- **Angular Forms** - Reactive form handling
- **Angular HTTP Client** - HTTP communication with backend
- **Jasmine & Karma** - Unit testing framework
- **Angular Testing Utilities** - Component and service testing

## Getting Started

### Prerequisites

- Node.js 18+ and npm
- Angular CLI 20.1.0 or higher
- Backend API running on `http://localhost:8080`

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MDB/frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   
   Update `src/environments/environment.ts` if needed:
   ```typescript
   export const environment = {
     production: false,
     apiUrl: 'http://localhost:8080/api'
   };
   ```

4. **Start the development server**
   ```bash
   ng serve
   ```

The application will be available at `http://localhost:4200/`

### Building for Production

1. **Build the application**
   ```bash
   ng build --configuration production
   ```

2. **Serve the built application**
   ```bash
   # Using a simple HTTP server
   npx http-server dist/minecraft-dev-bible-frontend
   ```

## Testing

### Current Test Status

⚠️ **Known Issue**: Tests currently fail due to TypeScript compilation errors. The test structure and logic are in place, but type mismatches and missing properties need to be resolved.

### Running Tests

```bash
# Attempt to run tests (currently failing due to TypeScript compilation errors)
ng test

# Run tests without watch mode
ng test --watch=false

# Build application (works successfully)
ng build --configuration production
```

### Test Structure

- **Service Tests**: Located alongside service files (`.spec.ts`)
  - `auth.service.spec.ts`: Authentication service tests (OAuth2 flow)
  - `forum.service.spec.ts`: Forum service HTTP client tests
  - `tutorial.service.spec.ts`: Tutorial service tests

- **Component Tests**: Located alongside component files
  - `navbar.component.spec.ts`: Navigation component tests
  - `thread-list.component.spec.ts`: Forum thread list tests

### Test Coverage (When Working)

The test suite is designed to cover:
- HTTP service calls with mocked responses using HttpClientTestingModule
- Component rendering and user interactions with TestBed
- Error handling and edge cases for API calls
- Authentication state management with Observable patterns

### Known Issues

1. **TypeScript Compilation Errors**: Type mismatches between test expectations and actual service/component APIs
2. **Missing Properties**: Some test files reference properties that don't exist in the actual components/services
3. **Observable Type Mismatches**: Tests expect synchronous values but services return Observables
4. **API Endpoint Mismatches**: Some tests use incorrect API endpoint URLs

### Fixing Tests

To resolve the test issues:
1. Update test files to match actual service method signatures
2. Fix Observable type handling in component tests
3. Correct API endpoint URLs to match backend configuration
4. Remove references to non-existent properties and methods

### Test Reports (When Working)

After running tests with coverage successfully, view the report:
- **Coverage Report**: `coverage/index.html`

## Project Structure

```
src/
├── app/
│   ├── components/          # UI components
│   │   ├── auth/           # Authentication components
│   │   ├── layout/         # Layout components (navbar, footer)
│   │   └── pages/          # Page components
│   │       ├── forums/     # Forum-related components
│   │       ├── tutorials/  # Tutorial components
│   │       └── home.component.ts
│   ├── models/             # TypeScript interfaces and types
│   ├── services/           # Angular services
│   │   ├── auth.service.ts
│   │   ├── forum.service.ts
│   │   └── tutorial.service.ts
│   ├── guards/             # Route guards
│   ├── interceptors/       # HTTP interceptors
│   └── app.routes.ts       # Application routing
├── assets/                 # Static assets
├── environments/           # Environment configurations
└── styles.css             # Global styles
```

## Key Features

### Authentication
- OAuth2 social login integration
- JWT token management
- Route protection with guards
- Automatic token refresh

### Forum System
- Threaded discussions
- Category filtering
- Pagination support
- Real-time updates
- Rich text posting

### Tutorial System
- Structured learning modules
- Progressive lesson navigation
- Code examples and snippets
- Interactive content

### Responsive Design
- Mobile-first approach
- Tailwind CSS utilities
- Adaptive layouts
- Touch-friendly interface

## Services

### AuthService
Handles user authentication and authorization:
```typescript
// Check authentication status
this.authService.isAuthenticated().subscribe(isAuth => {
  // Handle authentication state
});

// Get current user
this.authService.getCurrentUser().subscribe(user => {
  // Handle user data
});
```

### ForumService
Manages forum operations:
```typescript
// Get forum threads
this.forumService.getThreads(page, size, category).subscribe(threads => {
  // Handle threads data
});

// Create new thread
this.forumService.createThread(threadData).subscribe(thread => {
  // Handle created thread
});
```

### TutorialService
Handles tutorial content:
```typescript
// Get tutorial modules
this.tutorialService.getModules().subscribe(modules => {
  // Handle modules data
});

// Get lesson content
this.tutorialService.getLesson(lessonId).subscribe(lesson => {
  // Handle lesson data
});
```

## Component Testing

### Service Tests
Each service has comprehensive unit tests:
- HTTP request mocking with `HttpClientTestingModule`
- Error handling verification
- Observable stream testing
- Authentication flow testing

### Component Tests
Key components include basic rendering tests:
- Component creation verification
- Template rendering validation
- User interaction testing
- Service integration testing

Example test structure:
```typescript
describe('ThreadListComponent', () => {
  let component: ThreadListComponent;
  let fixture: ComponentFixture<ThreadListComponent>;
  let forumService: jasmine.SpyObj<ForumService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('ForumService', ['getThreads']);
    
    TestBed.configureTestingModule({
      imports: [ThreadListComponent],
      providers: [{ provide: ForumService, useValue: spy }]
    });
    
    fixture = TestBed.createComponent(ThreadListComponent);
    component = fixture.componentInstance;
    forumService = TestBed.inject(ForumService) as jasmine.SpyObj<ForumService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
```

## Styling

### Tailwind CSS
The application uses Tailwind CSS for styling:
- Utility-first approach
- Responsive design utilities
- Custom color palette
- Component-based styling

### Custom Styles
Global styles are defined in `src/styles.css`:
- CSS custom properties for theming
- Base component styles
- Utility classes

## Development Guidelines

### Code Style
- Follow Angular style guide
- Use TypeScript strict mode
- Implement proper error handling
- Use reactive programming patterns

### Component Development
- Keep components focused and single-purpose
- Use OnPush change detection when possible
- Implement proper lifecycle hooks
- Handle subscriptions properly (unsubscribe)

### Testing Best Practices
- Write tests for all services
- Test component public APIs
- Mock external dependencies
- Use descriptive test names
- Aim for good test coverage

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend CORS is configured for `http://localhost:4200`
   - Check browser developer tools for specific CORS errors

2. **Authentication Issues**
   - Verify JWT token format and expiration
   - Check OAuth2 configuration in backend
   - Ensure proper token storage and retrieval

3. **Build Errors**
   - Clear node_modules and reinstall: `rm -rf node_modules && npm install`
   - Check TypeScript version compatibility
   - Verify Angular CLI version

### Development Tools

Enable Angular DevTools for debugging:
1. Install Angular DevTools browser extension
2. Use Angular CLI analytics: `ng analytics on`
3. Enable source maps in development

## Deployment

### Docker Deployment
The project includes a Dockerfile for containerized deployment:

```bash
# Build the Docker image
docker build -t mdb-frontend .

# Run the container
docker run -p 80:80 mdb-frontend
```

### Static Hosting
For static hosting (Netlify, Vercel, etc.):
1. Build the application: `ng build --configuration production`
2. Deploy the `dist/` directory
3. Configure routing for SPA (single-page application)

## Contributing

1. Create a feature branch from `main`
2. Follow Angular coding standards
3. Write tests for new functionality
4. Ensure all tests pass: `ng test`
5. Build successfully: `ng build`
6. Submit a pull request

## Performance Optimization

- Lazy loading for route modules
- OnPush change detection strategy
- TrackBy functions for *ngFor loops
- Image optimization and lazy loading
- Bundle analysis with `ng build --stats-json`

## License

This project is licensed under the MIT License - see the LICENSE file for details.
