import { User } from './user.model';

export interface ForumThread {
  id: number;
  title: string;
  content?: string;
  author: User;
  isPinned: boolean;
  isLocked: boolean;
  posts?: ForumPost[];
  postCount: number;
  lastActivity: string;
  createdAt: string;
  updatedAt?: string;
}

export interface ForumPost {
  id: number;
  content: string;
  author: User;
  thread: ForumThread;
  createdAt: string;
  updatedAt?: string;
}

export interface CreateThreadRequest {
  title: string;
  content?: string;
}

export interface CreatePostRequest {
  content: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}