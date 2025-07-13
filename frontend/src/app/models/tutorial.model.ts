export interface TutorialModule {
  id: number;
  title: string;
  description: string;
  order: number;
  isPublished: boolean;
  lessons?: TutorialLesson[];
  createdAt: string;
  updatedAt?: string;
}

export interface TutorialLesson {
  id: number;
  title: string;
  content: string; // Markdown content
  videoUrl?: string;
  order: number;
  isPublished: boolean;
  module: TutorialModule;
  createdAt: string;
  updatedAt?: string;
}