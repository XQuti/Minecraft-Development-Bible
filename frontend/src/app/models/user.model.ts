export interface User {
  id: number;
  username: string;
  email: string;
  avatarUrl?: string;
  provider: string;
  roles: string[];
}

export interface AuthResponse {
  user: User;
  token: string;
}