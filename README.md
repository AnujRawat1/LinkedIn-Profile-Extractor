# LinkedIn Profile Extractor

A Spring Boot application that extracts comprehensive LinkedIn profile data using the LinkedIn Voyager API.

## Features

- Extract full LinkedIn profiles including:
  - Basic information (name, headline, location, about, profile image)
  - Work experience with company details
  - Education history
  - Skills
  - Certifications
  - Languages
- RESTful API with proper error handling
- Swagger/OpenAPI documentation

## Architecture

The application follows a layered architecture:

```
Controller → Service → HttpClient → LinkedIn Voyager API → Parser → DTO → Response
```

### Key Components

- **ProfileController**: REST API endpoint for profile extraction
- **ProfileService**: Business logic orchestration
- **LinkedInHttpClient**: HTTP client for LinkedIn Voyager API
- **LinkedInProfileParser**: Parses Voyager API JSON responses into DTOs
- **LinkedInUrlUtil**: URL validation and username extraction
- **GlobalExceptionHandler**: Centralized error handling

## Prerequisites

- Java 21+
- Maven 3.8+
- LinkedIn account (for authentication cookies)

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/your-username/LinkedinProfileExtractor.git
cd LinkedinProfileExtractor
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

### 3. Add your LinkedIn authentication cookies

Edit `.env` and add your LinkedIn session cookies:

```bash
# LinkedIn Voyager API Configuration
# Get these from your browser's LinkedIn session
LI_AT=your_li_at_cookie_value
JSESSIONID=your_jsessionid_value
```

#### How to get the authentication cookies:

1. Log into LinkedIn in your browser
2. Open Developer Tools (F12)
3. Go to **Application** tab → **Cookies** → `https://www.linkedin.com`
4. Find and copy these two cookies:
   - `li_at` - Main authentication cookie
   - `JSESSIONID` - Session ID for LinkedIn session
5. Paste the values into your `.env` file

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Endpoint**: POST http://localhost:8080/api/v1/profiles

## API Usage

### Extract Profile

**POST** `/api/v1/profiles`

Extracts LinkedIn profile data from a profile URL.

**Request Body:**
```json
{
  "profileUrl": "https://www.linkedin.com/in/john-doe/"
}
```

**Example using curl:**
```bash
curl -X POST http://localhost:8080/api/v1/profiles \
  -H "Content-Type: application/json" \
  -d '{"profileUrl": "https://www.linkedin.com/in/john-doe/"}'
```

**Response:**
```json
{
  "success": true,
  "message": "Profile extracted successfully",
  "username": "john-doe",
  "data": {
    "firstName": "John",
    "lastName": "Doe",
    "headline": "Senior Software Engineer",
    "location": "Bengaluru, India",
    "about": "Backend engineer",
    "profilePictureUrl": "https://example.com/profile.jpg",
    "experience": [
      {
        "title": "Senior Software Engineer",
        "company": "ABC Technologies",
        "location": "Bengaluru"
      }
    ],
    "education": [
      {
        "school": "ABC University",
        "degree": "B.Tech",
        "fieldOfStudy": "Computer Science"
      }
    ],
    "skills": ["Java", "Spring Boot"],
    "certifications": [
      {
        "name": "AWS Certified Developer",
        "authority": "AWS",
        "licenseNumber": null
      }
    ],
    "languages": []
  }
}
```

## Error Handling

The API uses standard HTTP status codes:

- **200**: Success
- **400**: Invalid LinkedIn URL
- **401**: Authentication failed (missing or invalid cookies)
- **404**: Profile not found
- **429**: Rate limit exceeded
- **500**: Internal server error

Error response format:
```json
{
  "timestamp": "2024-08-29T00:00:00",
  "status": 400,
  "error": "INVALID_LINKEDIN_URL",
  "message": "URL must be a valid LinkedIn profile URL"
}
```

## Testing

Run the test suite:
```bash
mvn test
```

## Technology Stack

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web** (REST API)
- **Spring Validation** (Request validation)
- **Jackson** (JSON processing)
- **Lombok** (Code generation)
- **java-dotenv** (Environment variable management)
- **SpringDoc OpenAPI** (API documentation)
- **JUnit 5** (Testing)

## Authentication

This application uses LinkedIn's Voyager API, which requires cookie-based authentication:

- **li_at cookie**: LinkedIn session authentication cookie
- **JSESSIONID cookie**: Session ID for LinkedIn session
- Cookies expire periodically and need to be refreshed
- Never commit cookies to version control
- Keep them secure as they provide access to your LinkedIn account

## Limitations

- Requires manual cookie extraction from browser
- Cookies expire and need periodic refresh
- Rate limits apply (LinkedIn may throttle requests)
- Voyager API is internal and may change without notice
- Only works with public LinkedIn profiles

## Security Considerations

- Never commit `.env` file or credentials to git
- The `.env` file is already in `.gitignore`
- The cookies provide access to your LinkedIn account
- Implement rate limiting in production
- Consider implementing session management for cookie refresh

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/anuj/LinkedinProfileExtractor/
│   │   ├── client/          # HTTP clients
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── exception/       # Custom exceptions
│   │   ├── parser/          # Response parsers
│   │   ├── service/         # Business logic
│   │   └── util/            # Utilities
│   └── resources/
│       └── application.yaml # Application configuration
└── test/
    └── java/com/anuj/LinkedinProfileExtractor/
        └── util/            # Utility tests
```

### Building the project

```bash
mvn clean install
```

### Running with Maven

```bash
mvn spring-boot:run
```

### Running the JAR directly

```bash
mvn clean package
java -jar target/LinkedinProfileExtractor-0.0.1-SNAPSHOT.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is for educational purposes. Use responsibly and respect LinkedIn's Terms of Service.

## Disclaimer

This application uses LinkedIn's internal Voyager API. This may violate LinkedIn's Terms of Service. Use at your own risk. The authors are not responsible for any consequences of using this software.
