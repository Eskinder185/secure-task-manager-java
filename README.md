SecureTask (Spring Boot)

SecureTask is a small Spring Boot API with JWT login, role-based access, and a Community Issue Reporter (simple web form with photo + address).

What’s included

Authentication: login to get a JWT, use it for protected task endpoints.

Tasks API: basic create/read/update/delete for tasks.

Issue Reporter: public form to submit neighborhood issues; optional photo.

Docs & health: interactive API docs and a health check.

How to run (high level)

Install Java 17 and Maven on your machine.

Set an environment variable named APP_JWT_SECRET to a random base64 key.

Start the Spring Boot app from the project folder (the one that contains pom.xml).

Open the app in your browser.

Where to go

API Docs (Swagger UI): open the “swagger” page at port 8080.

Health check: open the “actuator/health” page at port 8080.

Public report form: open the “/report” page at port 8080.

Using it

Log in at the auth endpoint to get a token, then include the token as a Bearer token for protected routes.

Submit issues either through the web form or by sending form data to the issues endpoints.

View recent reports via the public feed endpoint.

Address and location (optional)

Reporters can type Street, City, State, and ZIP.

If only an address is provided, the server can look up coordinates.

If only GPS is provided (Use my location), the server can look up the address.

To enable this, set the geocoding provider and an email contact in your environment.

For offline demos, you can disable geocoding.

Rate limiting

Anonymous issue submissions are limited by default (per IP, short time window).

You can change the window and limit with environment settings.

Notes

If the app doesn’t start because of a plugin error, make sure the project uses the Spring Boot parent in the Maven configuration or that the Spring Boot Maven plugin version is set.

If reverse geocoding is slow or blocked, provide a contact email in the geocoding settings, or disable geocoding for local testing.
