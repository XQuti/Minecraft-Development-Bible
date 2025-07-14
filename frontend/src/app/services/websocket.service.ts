import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { ForumThread, ForumPost, CreateThreadRequest, CreatePostRequest } from '../models/forum.model';

export interface ForumUpdate {
  type: 'thread' | 'post' | 'activity';
  data: ForumThread | ForumPost | ForumActivity;
  timestamp: number;
}

export interface ForumActivity {
  threadId: number;
  activity: string;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private connected = new BehaviorSubject<boolean>(false);
  private forumUpdates = new BehaviorSubject<ForumUpdate | null>(null);

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {},
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected');
      this.connected.next(true);
      this.subscribeToForumUpdates();
    };

    this.client.onDisconnect = () => {
      console.log('WebSocket disconnected');
      this.connected.next(false);
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame);
    };
  }

  connect(): void {
    if (!this.client.active) {
      this.client.activate();
    }
  }

  disconnect(): void {
    if (this.client.active) {
      this.client.deactivate();
    }
  }

  isConnected(): Observable<boolean> {
    return this.connected.asObservable();
  }

  getForumUpdates(): Observable<ForumUpdate | null> {
    return this.forumUpdates.asObservable();
  }

  private subscribeToForumUpdates(): void {
    // Subscribe to new threads
    this.client.subscribe('/topic/forum/threads', (message: IMessage) => {
      const thread = JSON.parse(message.body);
      this.forumUpdates.next({
        type: 'thread',
        data: thread,
        timestamp: Date.now()
      });
    });

    // Subscribe to new posts
    this.client.subscribe('/topic/forum/posts', (message: IMessage) => {
      const post = JSON.parse(message.body);
      this.forumUpdates.next({
        type: 'post',
        data: post,
        timestamp: Date.now()
      });
    });
  }

  subscribeToThread(threadId: number): void {
    if (this.client.connected) {
      this.client.subscribe(`/topic/forum/thread/${threadId}`, (message: IMessage) => {
        const post = JSON.parse(message.body);
        this.forumUpdates.next({
          type: 'post',
          data: post,
          timestamp: Date.now()
        });
      });

      this.client.subscribe(`/topic/forum/thread/${threadId}/activity`, (message: IMessage) => {
        const activity = JSON.parse(message.body);
        this.forumUpdates.next({
          type: 'activity',
          data: activity,
          timestamp: Date.now()
        });
      });
    }
  }

  sendNewThread(thread: CreateThreadRequest): void {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/forum/thread/new',
        body: JSON.stringify(thread)
      });
    }
  }

  sendNewPost(post: CreatePostRequest): void {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/forum/post/new',
        body: JSON.stringify(post)
      });
    }
  }

  sendUserActivity(threadId: number, activity: string): void {
    if (this.client.connected) {
      const activityData: ForumActivity = {
        threadId,
        activity,
        timestamp: Date.now()
      };
      this.client.publish({
        destination: '/app/forum/activity',
        body: JSON.stringify(activityData)
      });
    }
  }
}