# Zuul Server

Run this app as a normal Spring Boot app. If you run from this project 
it will be on port 8765 (per the `application.yml`). 
Also run : [eureka](https://github.com/EGA-archive/ega-eureka-service)

Zuul has several functions:
* Integration with ELIXIR. The Zuul filter detects ELIXIR tokens, and injects EGA permissions in the REST call as a signed "X-Permissions" header.
* Traffic Shaping to protect the underlying resources
* Load balancing: automatically (via EUREKA) detect and add/remove available back end API services, and load balance between them. Also perform atomatic retries of REST calls.
* Integration: Make several back end microservices availabe on a single URL and Port
