# API Usage Examples

This document provides examples of how to use the Platform Assistant API.

## Prerequisites

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. Set your environment variables:
   ```bash
   export OPENAI_API_KEY="your-openai-api-key"
   export GITHUB_PERSONAL_ACCESS_TOKEN="your-github-token"
   ```

## Basic Chat Example

### Using curl

```bash
# Simple chat request
curl -X POST http://localhost:8080/api/chatbot/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello! Can you help me search for Spring Boot repositories on GitHub?"
  }'
```

### Using JavaScript (Node.js)

```javascript
const axios = require('axios');

async function chatWithBot() {
  try {
    const response = await axios.post('http://localhost:8080/api/chatbot/chat', {
      message: 'Hello! Can you help me search for Spring Boot repositories on GitHub?'
    });
    
    console.log('Bot response:', response.data.message);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

chatWithBot();
```

### Using Python

```python
import requests
import json

def chat_with_bot():
    url = "http://localhost:8080/api/chatbot/chat"
    payload = {
        "message": "Hello! Can you help me search for Spring Boot repositories on GitHub?"
    }
    
    try:
        response = requests.post(url, json=payload)
        response.raise_for_status()
        
        data = response.json()
        print(f"Bot response: {data['message']}")
        
    except requests.exceptions.RequestException as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    chat_with_bot()
```

## Chat with Context Example

```bash
# Chat with additional context
curl -X POST "http://localhost:8080/api/chatbot/chat/context?context.user=developer&context.project=spring-boot-app" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Create a new issue in my repository for a bug I found"
  }'
```

## Health Check

```bash
# Check if the service is running
curl http://localhost:8080/api/chatbot/health
```

## Example Conversations

### GitHub Repository Search

**User:** "Search for repositories about machine learning"

**Bot:** "I can help you search for machine learning repositories on GitHub! I have access to GitHub's search functionality through the MCP server. 

To search for repositories, I'll need to know:
1. What specific type of machine learning repositories you're looking for (e.g., Python, JavaScript, tutorials, frameworks)
2. Any specific criteria like language, stars, or recent activity

Would you like me to search for general machine learning repositories, or do you have specific criteria in mind?"

### Creating GitHub Issues

**User:** "Create an issue in my repository about a bug"

**Bot:** "I can help you create a GitHub issue! To create an issue, I'll need some information:

1. **Repository details**: What's the owner and repository name? (e.g., 'username/repo-name')
2. **Issue title**: What should we call this issue?
3. **Issue description**: Can you describe the bug in detail?
4. **Labels**: Any specific labels you'd like to add (e.g., 'bug', 'high-priority')?

Once you provide these details, I can create the issue for you using GitHub's API through the MCP server."

### Code Security Analysis

**User:** "Check the security of my repository"

**Bot:** "I can help you analyze the security of your repository! I have access to GitHub's code security features through the MCP server.

To perform a security analysis, I'll need:
1. **Repository details**: Owner and repository name
2. **What to check**: 
   - Secret scanning alerts
   - Code security vulnerabilities
   - Dependency vulnerabilities
   - Security advisories

Would you like me to check for secret scanning alerts and security vulnerabilities in your repository? Just provide the repository name and I'll get started!"

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK`: Successful request
- `400 Bad Request`: Invalid request format
- `500 Internal Server Error`: Server error

Example error response:
```json
{
  "message": "An error occurred while processing your request.",
  "success": false
}
```

## Rate Limiting

- The API respects GitHub's rate limits
- OpenAI API calls are subject to OpenAI's rate limits
- Consider implementing client-side rate limiting for production use

## Best Practices

1. **Provide Context**: Use the context endpoint when you have additional information that might help the bot
2. **Be Specific**: When asking for GitHub operations, provide specific details like repository names, issue titles, etc.
3. **Handle Errors**: Always check the response status and handle errors gracefully
4. **Secure Tokens**: Never expose your API tokens in client-side code 