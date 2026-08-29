# LinkedIn Profile Extractor

A Spring Boot application that extracts comprehensive LinkedIn profile data using reverse-engineered Voyager API endpoints.

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
- Comprehensive test coverage

## Architecture

The application uses a clean architecture pattern:

```
Controller → Service → HttpClient → LinkedIn Voyager API → Parser → DTO → Response
```

### Key Components

- **ProfileController**: REST API endpoint for profile extraction
- **ProfileService**: Business logic orchestration
- **LinkedInHttpClient**: HTTP client for LinkedIn Voyager API
- **LinkedInProfileParser**: Parses Voyager API responses into DTOs
- **LinkedInUrlUtil**: URL validation and username extraction
- **GlobalExceptionHandler**: Centralized error handling

## Prerequisites

- Java 21+
- Maven 3.8+
- LinkedIn account (for authentication cookie)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/your-username/LinkedinProfileExtractor.git
cd LinkedinProfileExtractor
```

2. Configure environment variables:
```bash
cp .env.example .env
```

3. Edit `.env` and add your LinkedIn authentication cookie:
```bash
# OAuth Configuration (optional, for OAuth flow)
LINKEDIN_CLIENT_ID=your_client_id
LINKEDIN_CLIENT_SECRET=your_client_secret
LINKEDIN_REDIRECT_URI=http://localhost:8080/api/auth/linkedin/callback

# Voyager API Configuration (required)
# Get this from your browser's LinkedIn session (li_at cookie)
LINKEDIN_LI_AT_COOKIE=your_li_at_cookie_value
```

### How to get the li_at cookie:

1. Log into LinkedIn in your browser
2. Open Developer Tools (F12)
3. Go to **Application** tab → **Cookies** → `https://www.linkedin.com`
4. Find the `li_at` cookie and copy its value
5. Set it in your `.env` file

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Extract Profile

**POST** `/api/v1/profiles`

Extracts LinkedIn profile data from a profile URL.

**Request Body:**
```json
{
  "profileUrl": "https://www.linkedin.com/in/john-doe/"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile extracted successfully",
  "username": "john-doe",
  "data": {
    "name": "John Doe",
    "headline": "Senior Software Engineer",
    "location": "Bengaluru, India",
    "about": "Backend engineer",
    "profileImage": "https://example.com/profile.jpg",
    "experience": [
      {
        "title": "Senior Software Engineer",
        "company": "ABC Technologies",
        "companyUrl": "https://www.linkedin.com/company/12345",
        "location": "Bengaluru",
        "startDate": "2022-01",
        "endDate": null,
        "current": true,
        "description": "Building backend services."
      }
    ],
    "education": [
      {
        "school": "ABC University",
        "degree": "B.Tech",
        "fieldOfStudy": "Computer Science",
        "startDate": "2016",
        "endDate": "2020"
      }
    ],
    "skills": ["Java", "Spring Boot"],
    "certifications": [
      {
        "name": "AWS Certified Developer",
        "issuer": "AWS",
        "issueDate": "2021-06",
        "credentialUrl": "https://example.com/cert"
      }
    ],
    "languages": [
      {
        "name": "English",
        "proficiency": "Professional Working"
      }
    ]
  }
}
```

### OAuth Endpoints

**GET** `/api/auth/linkedin` - Initiates LinkedIn OAuth flow

**GET** `/api/auth/linkedin/callback` - OAuth callback handler

## Error Handling

The API uses standard HTTP status codes:

- **200**: Success
- **400**: Invalid LinkedIn URL
- **401**: Authentication failed
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

Run tests with coverage:
```bash
mvn test jacoco:report
```

## Technology Stack

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web**
- **Spring Validation**
- **Jackson** (JSON processing)
- **Lombok** (Code generation)
- **JUnit 5** (Testing)
- **Mockito** (Mocking)
- **SpringDoc OpenAPI** (API documentation)

## Authentication

This application uses LinkedIn's internal Voyager API, which requires cookie-based authentication:

- **li_at cookie**: LinkedIn session authentication cookie
- The cookie expires periodically and needs to be refreshed
- Never commit the cookie to version control
- Keep it secure as it provides access to your LinkedIn account

## Limitations

- Requires manual cookie extraction from browser
- Cookie expires and needs periodic refresh
- Rate limits apply (LinkedIn may throttle requests)
- Voyager API is undocumented and may change without notice
- Only works with public LinkedIn profiles

## Security Considerations

- Never commit `.env` file or credentials to git
- Use environment variables for sensitive data
- The li_at cookie provides access to your LinkedIn account
- Implement rate limiting in production
- Consider implementing session management for cookie refresh

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/anuj/LinkedinProfileExtractor/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── exception/       # Custom exceptions
│   │   ├── model/           # Response models
│   │   ├── parser/          # Response parsers
│   │   ├── service/         # Business logic
│   │   └── util/            # Utilities
│   └── resources/
│       └── application.yaml # Application configuration
└── test/
    └── java/com/anuj/LinkedinProfileExtractor/
        ├── parser/          # Parser tests
        └── util/            # Utility tests
```

### Building

```bash
mvn clean install
```

### Running with different profiles

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
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

This application uses reverse-engineered LinkedIn API endpoints. This may violate LinkedIn's Terms of Service. Use at your own risk. The authors are not responsible for any consequences of using this software.

## Future Enhancements

- [ ] Implement automatic cookie refresh mechanism
- [ ] Add caching for profile data
- [ ] Implement rate limiting
- [ ] Add support for company pages
- [ ] Implement batch profile extraction
- [ ] Add database persistence
- [ ] Implement user authentication
- [ ] Add monitoring and logging
