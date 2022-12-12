<!-- markdownlint-configure-file {
  "MD013": {
    "code_blocks": false,
    "tables": false
  },
  "MD033": false,
  "MD041": false
} -->
# Java Spring MongoDB REST API Reference

This is a simple URL Shortener App.

[![Build](https://github.com/rrajesh1979/java-spring-mongo-rest-ref/actions/workflows/build.yml/badge.svg)](https://github.com/rrajesh1979/java-spring-mongo-rest-ref/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rrajesh1979_java-spring-mongo-rest-ref&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=rrajesh1979_java-spring-mongo-rest-ref)
![GitHub](https://img.shields.io/github/license/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub last commit](https://img.shields.io/github/last-commit/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub pull requests](https://img.shields.io/github/issues-pr/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub issues](https://img.shields.io/github/issues/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub contributors](https://img.shields.io/github/contributors/rrajesh1979/java-spring-mongo-rest-ref)
![GitHub watchers](https://img.shields.io/github/watchers/rrajesh1979/java-spring-mongo-rest-ref)
![Known Vulnerabilities](https://snyk.io/test/github/rrajesh1979/java-spring-mongo-rest-ref/badge.svg)

[Key Features](#key-features) •
[Getting Started](#getting-started) •
[Usage](#usage) •
[Contributing](#contributing) •
[License](#license)

## Key Features
This is a URL shortener REST API.
- [X] API is built using
  - Java 17
  - GraalVM native image
  - Spring Boot 3.0.0
  - Spring Data
- [X] MongoDB is used as the database
- [X] Bucket4j is used for rate limiting
  - [ ] Alternative using Resilience4j TBD
- [X] Spring Cache is used for caching
- [X] Gradle is used for dependency management
- [X] Swagger UI for API documentation
- [X] Testing
  - JUnit 5 tests
  - Mockito for mocking
  - Testcontainers for integration tests
  - [ ] Testing URL Redirect TBD
- [X] Quality Assurance
  - Jacoco for test coverage
  - SonarQube for code quality analysis
- [X] CI/CD
  - [X] GitHub Actions
  - [ ] Security scanning using Snyk
- [X] Containerization & Deployment
  - Docker
  - Docker Compose
  - jib plugin for building Docker images

## Getting Started
- Clone the repository
- Make sure pre-requisites are installed
  - Java 17+, GraalVM
  - MongoDB
  - Docker
- Build the application
  - `./gradlew build`
- Run the application
  - `./gradlew bootRun`
- Build the Docker image
  - `./gradlew jibCustom`
- Run the application in Docker
  - `docker-compose up`
  - This will bring up the application and MongoDB

## Usage
- Swagger UI
  - http://localhost:8080/swagger-ui/index.html
- Sample API calls are provided here. You can import in a tool such as Insomnia or Postman.
  - https://github.com/rrajesh1979/java-spring-mongo-rest-ref/blob/master/docs/rest-calls-insomnia.yaml
  
## Swagger UI
Swagger UI is available at http://localhost:8000/api/v1/swagger-ui/index.html
![alt text](https://github.com/rrajesh1979/java-spring-mongo-rest-ref/blob/master/docs/swagger-ui.png?raw=true)

## Build Status
[![Build](https://github.com/rrajesh1979/java-spring-mongo-rest-ref/actions/workflows/build.yml/badge.svg)](https://github.com/rrajesh1979/java-spring-mongo-rest-ref/actions/workflows/build.yml) 

## Code Quality
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rrajesh1979_java-spring-mongo-rest-ref&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=rrajesh1979_java-spring-mongo-rest-ref)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
![GitHub](https://img.shields.io/github/license/rrajesh1979/java-spring-mongo-rest-ref)
