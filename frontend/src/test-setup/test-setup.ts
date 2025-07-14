// Global test setup for Angular testing environment
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

// Initialize the Angular testing environment once
let testBedInitialized = false;

export function initializeTestBed() {
  if (!testBedInitialized) {
    getTestBed().initTestEnvironment(
      BrowserDynamicTestingModule,
      platformBrowserDynamicTesting(),
    );
    testBedInitialized = true;
  }
}

// Initialize test environment immediately
initializeTestBed();