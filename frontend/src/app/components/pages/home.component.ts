import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-minecraft-light to-white">
      <!-- Hero Section -->
      <section class="container mx-auto px-4 py-16">
        <div class="max-w-4xl mx-auto text-center">
          <h1 class="text-5xl md:text-6xl font-bold text-minecraft-green mb-6">
            Minecraft Development Bible
          </h1>
          <p class="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            Master the art of Minecraft development with comprehensive tutorials, 
            community support, and hands-on learning experiences.
          </p>
          
          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <a 
              routerLink="/tutorials"
              class="bg-minecraft-green text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-minecraft-dark transition-colors duration-200 shadow-lg"
            >
              Start Learning
            </a>
            <a 
              routerLink="/forums"
              class="bg-white text-minecraft-green border-2 border-minecraft-green px-8 py-3 rounded-lg text-lg font-semibold hover:bg-minecraft-green hover:text-white transition-colors duration-200 shadow-lg"
            >
              Join Community
            </a>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="container mx-auto px-4 py-16">
        <div class="max-w-6xl mx-auto">
          <h2 class="text-3xl font-bold text-center text-minecraft-dark mb-12">
            Everything You Need to Build Amazing Minecraft Plugins
          </h2>
          
          <div class="grid md:grid-cols-3 gap-8">
            <!-- Feature 1 -->
            <div class="bg-white rounded-lg shadow-lg p-6 text-center hover:shadow-xl transition-shadow duration-300">
              <div class="w-16 h-16 bg-minecraft-green rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-minecraft-dark mb-3">Comprehensive Tutorials</h3>
              <p class="text-gray-600">
                Step-by-step guides covering everything from basic plugin setup to advanced Paper API features.
              </p>
            </div>

            <!-- Feature 2 -->
            <div class="bg-white rounded-lg shadow-lg p-6 text-center hover:shadow-xl transition-shadow duration-300">
              <div class="w-16 h-16 bg-minecraft-green rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-minecraft-dark mb-3">Active Community</h3>
              <p class="text-gray-600">
                Connect with fellow developers, ask questions, and share your creations in our supportive forum.
              </p>
            </div>

            <!-- Feature 3 -->
            <div class="bg-white rounded-lg shadow-lg p-6 text-center hover:shadow-xl transition-shadow duration-300">
              <div class="w-16 h-16 bg-minecraft-green rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-minecraft-dark mb-3">Modern Development</h3>
              <p class="text-gray-600">
                Learn the latest best practices using Paper API, modern Java features, and industry-standard tools.
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- Learning Path Section -->
      <section class="bg-gray-50 py-16">
        <div class="container mx-auto px-4">
          <div class="max-w-4xl mx-auto">
            <h2 class="text-3xl font-bold text-center text-minecraft-dark mb-12">
              Your Learning Journey
            </h2>
            
            <div class="space-y-8">
              <!-- Step 1 -->
              <div class="flex items-center space-x-6">
                <div class="w-12 h-12 bg-minecraft-green text-white rounded-full flex items-center justify-center font-bold text-lg flex-shrink-0">
                  1
                </div>
                <div class="flex-1">
                  <h3 class="text-xl font-semibold text-minecraft-dark mb-2">Setup Your Environment</h3>
                  <p class="text-gray-600">
                    Learn how to set up your development environment with IntelliJ IDEA, Java, and the Paper API.
                  </p>
                </div>
              </div>

              <!-- Step 2 -->
              <div class="flex items-center space-x-6">
                <div class="w-12 h-12 bg-minecraft-green text-white rounded-full flex items-center justify-center font-bold text-lg flex-shrink-0">
                  2
                </div>
                <div class="flex-1">
                  <h3 class="text-xl font-semibold text-minecraft-dark mb-2">Create Your First Plugin</h3>
                  <p class="text-gray-600">
                    Build a simple "Hello World" plugin and understand the basic structure of Minecraft plugins.
                  </p>
                </div>
              </div>

              <!-- Step 3 -->
              <div class="flex items-center space-x-6">
                <div class="w-12 h-12 bg-minecraft-green text-white rounded-full flex items-center justify-center font-bold text-lg flex-shrink-0">
                  3
                </div>
                <div class="flex-1">
                  <h3 class="text-xl font-semibold text-minecraft-dark mb-2">Master Advanced Features</h3>
                  <p class="text-gray-600">
                    Dive deep into events, commands, permissions, and database integration to create powerful plugins.
                  </p>
                </div>
              </div>
            </div>

            <div class="text-center mt-12">
              <a 
                routerLink="/tutorials"
                class="bg-minecraft-green text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-minecraft-dark transition-colors duration-200 shadow-lg"
              >
                Begin Your Journey
              </a>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="container mx-auto px-4 py-16">
        <div class="max-w-4xl mx-auto text-center">
          <h2 class="text-3xl font-bold text-minecraft-dark mb-6">
            Ready to Start Building?
          </h2>
          <p class="text-xl text-gray-600 mb-8">
            Join thousands of developers who are already creating amazing Minecraft experiences.
          </p>
          
          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <a 
              routerLink="/tutorials"
              class="bg-minecraft-green text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-minecraft-dark transition-colors duration-200 shadow-lg"
            >
              View Tutorials
            </a>
            <a 
              href="https://github.com/PaperMC/Paper"
              target="_blank"
              rel="noopener noreferrer"
              class="bg-gray-800 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-gray-700 transition-colors duration-200 shadow-lg"
            >
              Paper API Docs
            </a>
          </div>
        </div>
      </section>
    </div>
  `
})
export class HomeComponent {}