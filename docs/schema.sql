-- Database schema for Minecraft Development Bible

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    provider VARCHAR(20) NOT NULL, -- 'google' or 'github'
    provider_id VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(provider, provider_id)
);

-- User roles table (for many-to-many relationship)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tutorial modules table
CREATE TABLE tutorial_modules (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tutorial lessons table
CREATE TABLE tutorial_lessons (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT, -- Markdown content
    video_url TEXT,
    display_order INTEGER NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    module_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES tutorial_modules(id) ON DELETE CASCADE
);

-- Forum threads table
CREATE TABLE forum_threads (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author_id BIGINT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Forum posts table
CREATE TABLE forum_posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    thread_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (thread_id) REFERENCES forum_threads(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_provider ON users(provider, provider_id);
CREATE INDEX idx_tutorial_modules_order ON tutorial_modules(display_order);
CREATE INDEX idx_tutorial_lessons_module ON tutorial_lessons(module_id, display_order);
CREATE INDEX idx_forum_threads_created ON forum_threads(created_at DESC);
CREATE INDEX idx_forum_threads_pinned ON forum_threads(is_pinned DESC, updated_at DESC);
CREATE INDEX idx_forum_posts_thread ON forum_posts(thread_id, created_at ASC);

-- Sample data for development
INSERT INTO tutorial_modules (title, description, display_order) VALUES
('Getting Started with Paper API', 'Learn the basics of creating Minecraft server plugins with the Paper API', 1),
('Advanced Plugin Development', 'Dive deeper into advanced plugin development techniques', 2),
('Database Integration', 'Learn how to integrate databases with your Minecraft plugins', 3);

INSERT INTO tutorial_lessons (title, content, display_order, module_id) VALUES
('Setting up your Development Environment', '# Setting up your Development Environment

Welcome to the first lesson of the Minecraft Development Bible! In this lesson, we''ll set up everything you need to start developing Paper plugins.

## Prerequisites

- Java 17 or higher
- IntelliJ IDEA or Eclipse
- Basic Java knowledge

## Step 1: Download Paper

First, download the latest Paper server jar from [PaperMC](https://papermc.io/downloads).

## Step 2: Create your first plugin

Let''s create a simple "Hello World" plugin...', 1, 1),

('Understanding the Plugin Lifecycle', '# Understanding the Plugin Lifecycle

Every Paper plugin follows a specific lifecycle. Understanding this is crucial for effective plugin development.

## Plugin States

1. **Loading** - Plugin is being loaded by the server
2. **Enabling** - Plugin is being enabled and initialized
3. **Running** - Plugin is active and handling events
4. **Disabling** - Plugin is being shut down

## Key Methods

- `onLoad()` - Called when plugin is loaded
- `onEnable()` - Called when plugin is enabled
- `onDisable()` - Called when plugin is disabled', 2, 1),

('Working with Events', '# Working with Events

Events are the backbone of Minecraft plugin development. They allow your plugin to respond to things happening in the game world.

## Event Basics

Events in Paper are based on the Bukkit event system. Here''s how to listen for events:

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    player.sendMessage("Welcome to the server!");
}
```

## Common Events

- PlayerJoinEvent
- PlayerQuitEvent
- BlockBreakEvent
- EntityDamageEvent', 3, 1);

-- Insert a sample user (this would normally be created through OAuth)
INSERT INTO users (username, email, provider, provider_id, avatar_url) VALUES
('admin', 'admin@minecraftdevbible.com', 'github', '12345', 'https://github.com/identicons/admin.png');

-- Insert user role
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ADMIN'),
(1, 'USER');

-- Insert sample forum thread
INSERT INTO forum_threads (title, author_id) VALUES
('Welcome to the Minecraft Development Bible Forum!', 1),
('Questions about Paper API', 1);

-- Insert sample forum posts
INSERT INTO forum_posts (content, author_id, thread_id) VALUES
('Welcome everyone! This is the official forum for the Minecraft Development Bible. Feel free to ask questions, share your projects, and help fellow developers.', 1, 1),
('I''m having trouble with my first plugin. Can someone help me understand how events work?', 1, 2),
('Events are triggered when something happens in the game. You need to create an event listener to handle them. Check out the tutorial on Working with Events!', 1, 2);