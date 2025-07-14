import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  const mockUser: User = {
    id: 1,
    email: 'test@example.com',
    username: 'testuser',
    avatarUrl: undefined,
    provider: 'local',
    roles: ['USER']
  };

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'logout'
    ], {
      currentUser$: of(mockUser)
    });

    await TestBed.configureTestingModule({
      imports: [NavbarComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: {} },
            params: of({}),
            queryParams: of({})
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    
    // Setup spy return values
    mockAuthService.logout.and.returnValue(of({ message: 'Logged out successfully' }));
    mockAuthService.login.and.returnValue(undefined);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with currentUser$ observable', () => {
    expect(component.currentUser$).toBeDefined();
    
    component.currentUser$.subscribe(user => {
      expect(user).toEqual(mockUser);
    });
  });

  it('should call authService.login with provider', () => {
    component.login('github');
    expect(mockAuthService.login).toHaveBeenCalledWith('github');

    component.login('google');
    expect(mockAuthService.login).toHaveBeenCalledWith('google');
  });

  it('should call authService.logout', () => {
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
  });

  it('should handle null user in currentUser$ observable', () => {
    // Update the spy to return null user
    Object.defineProperty(mockAuthService, 'currentUser$', {
      value: of(null)
    });
    
    component = new NavbarComponent(mockAuthService);
    
    component.currentUser$.subscribe(user => {
      expect(user).toBeNull();
    });
  });

  it('should render correctly with user data', () => {
    fixture.detectChanges();
    
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('nav')).toBeTruthy();
    expect(compiled.textContent).toContain('Minecraft Dev Bible');
  });

  it('should have navigation links', () => {
    fixture.detectChanges();
    
    const compiled = fixture.nativeElement as HTMLElement;
    const tutorialsLink = compiled.querySelector('a[routerLink="/tutorials"]');
    const forumsLink = compiled.querySelector('a[routerLink="/forums"]');
    
    expect(tutorialsLink).toBeTruthy();
    expect(forumsLink).toBeTruthy();
    expect(tutorialsLink?.textContent?.trim()).toBe('Tutorials');
    expect(forumsLink?.textContent?.trim()).toBe('Forums');
  });
});