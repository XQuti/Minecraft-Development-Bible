# Essential MCP Servers Collection

This directory contains curated collections of Model Context Protocol (MCP) servers for AI agents. These servers provide essential capabilities for development, integration, and automation tasks.

## Server Collections

### 1. Essential MCP Servers (`essential-mcp-servers.yaml`)
Core servers that provide fundamental capabilities:
- **Filesystem Server**: File system access and operations
- **SQLite Server**: Local database operations
- **PostgreSQL Server**: Production database access
- **GitHub Server**: Repository management and code access
- **Git Server**: Version control operations
- **Memory Server**: Context and conversation management
- **Brave Search Server**: Web search capabilities
- **Puppeteer Server**: Web automation and scraping
- **Fetch Server**: HTTP client for API interactions
- **Everything Server**: Fast file search (Windows)
- **Sequential Thinking Server**: Complex reasoning tasks
- **Time Server**: Temporal operations and scheduling

### 2. Community MCP Servers (`community-mcp-servers.yaml`)
Popular third-party integrations:
- **Docker Server**: Container management
- **Kubernetes Server**: Cluster operations
- **AWS Server**: Cloud service integration
- **Slack Server**: Team communication
- **Notion Server**: Documentation and notes
- **Jira Server**: Project management
- **Google Drive Server**: Cloud file storage
- **MongoDB Server**: NoSQL database operations
- **Redis Server**: Caching and session management
- **Elasticsearch Server**: Search and analytics
- **Sentry Server**: Error monitoring
- **Linear Server**: Issue tracking

### 3. Development MCP Servers (`development-mcp-servers.yaml`)
Development-focused tools:
- **ESLint Server**: Code quality analysis
- **Jest Server**: Testing framework integration
- **NPM Server**: Package management
- **Webpack Server**: Build tool integration
- **OpenAPI Server**: API documentation
- **GraphQL Server**: GraphQL operations
- **Dotenv Server**: Environment management
- **Log Parser Server**: Log analysis
- **Performance Server**: Performance monitoring
- **Security Scanner Server**: Security analysis
- **Prettier Server**: Code formatting
- **TypeScript Server**: TypeScript support

## Configuration

### Environment Variables
Many servers require environment variables for authentication and configuration:

```bash
# GitHub Integration
export GITHUB_TOKEN="your_github_token"

# Database Connections
export POSTGRES_CONNECTION_STRING="postgresql://user:password@localhost:5432/dbname"
export MONGODB_CONNECTION_STRING="mongodb://localhost:27017/dbname"
export REDIS_URL="redis://localhost:6379"

# Cloud Services
export AWS_ACCESS_KEY_ID="your_aws_key"
export AWS_SECRET_ACCESS_KEY="your_aws_secret"
export AWS_REGION="us-east-1"

# Communication Tools
export SLACK_BOT_TOKEN="xoxb-your-slack-token"
export NOTION_API_KEY="your_notion_key"

# Search and Monitoring
export BRAVE_API_KEY="your_brave_api_key"
export SENTRY_DSN="your_sentry_dsn"
```

### Path Configuration
Update file and directory paths in the YAML files to match your system:

```yaml
# Example: Update filesystem server path
- name: Filesystem Server
  command: npx
  args:
    - -y
    - "@modelcontextprotocol/server-filesystem"
    - "C:\\Users\\YourUser\\Projects"  # Windows path
    # - "/home/user/projects"          # Linux/Mac path
```

## Installation

Most servers can be installed on-demand using `npx -y` which automatically installs and runs the latest version. For frequently used servers, you may want to install them globally:

```bash
# Install essential servers globally
npm install -g @modelcontextprotocol/server-filesystem
npm install -g @modelcontextprotocol/server-sqlite
npm install -g @modelcontextprotocol/server-github
```

## Usage

1. Choose the appropriate server collection based on your needs
2. Update environment variables and paths in the YAML files
3. Configure your AI agent to use the selected MCP servers
4. Test the connections to ensure proper functionality

## Security Considerations

- Store sensitive environment variables securely (use `.env` files or system environment variables)
- Limit filesystem access to necessary directories only
- Use read-only database connections when possible
- Regularly rotate API keys and tokens
- Monitor server logs for unusual activity

## Troubleshooting

### Common Issues

1. **Package not found**: Ensure the package name is correct and available on npm
2. **Permission denied**: Check file system permissions for specified paths
3. **Authentication failed**: Verify environment variables are set correctly
4. **Connection timeout**: Check network connectivity and service availability

### Debugging

Enable debug logging by setting environment variables:
```bash
export DEBUG=mcp:*
export MCP_LOG_LEVEL=debug
```

## Contributing

To add new servers or improve existing configurations:
1. Research the server package and its requirements
2. Add appropriate configuration to the relevant YAML file
3. Update this README with server description and setup instructions
4. Test the configuration thoroughly

## Resources

- [Model Context Protocol Documentation](https://modelcontextprotocol.io/)
- [Official MCP Servers Repository](https://github.com/modelcontextprotocol/servers)
- [Awesome MCP Servers](https://github.com/punkpeye/awesome-mcp-servers)
- [MCP Server Store](https://www.mcpstore.org/)