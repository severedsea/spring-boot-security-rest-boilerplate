# Spring Boot + Spring Security + REST Boilerplate
###### Note: Sole purpose of this project is for personal use. But feel free to comment and use this as a reference for your own project. 
### Features: 
- Spring Boot
- Spring Security
	* Custom token-based authentication filter
	* Database token storage
	* BCryptPasswordEncoder
	* CORS filter configuration
- Spring MVC
	* DTO - Use of @Validated for DTO validation using domain objects (Without the need to create separate DTOs)
	* JSON Marshalling - Use of @JSONView for JSON marshalling of domain objects based on endpoints
	* Exception handling - Use of @ControllerAdvice for exception handling
	* Messages - i18n support
- Spring Data
	* JPA
	* Hibernate
	* MySQL 
- Groovy
- Gradle
- Flyway Database Migration
- Package-by-feature approach