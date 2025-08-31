SecureTask – Spring Boot Starter

A small Spring Boot API for SecureTask with JWT login, roles, validation, Actuator health, and Swagger UI. It also includes a simple Community Issue Reporter (photo + address) so anyone can submit reports from a clean web form.

What you get

/api/auth/login → get a JWT (demo user: admin / password)

/api/tasks → CRUD (secured by JWT + RBAC)

/api/issues → create/search/update community issues (optional photo)

/report → one-page public form (mobile-friendly, no login)

Swagger docs → /swagger-ui.html

Health check → /actuator/health

Run it
1) Set a JWT secret

Linux/macOS

head -c 32 /dev/urandom | base64
# copy the output, then:
export APP_JWT_SECRET="PASTE_BASE64_KEY"


Windows PowerShell

$bytes = New-Object byte[] 32; (New-Object System.Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes); [Convert]::ToBase64String($bytes)
# copy the output, then:
setx APP_JWT_SECRET "PASTE_BASE64_KEY"
# open a new terminal so the var is available

2) Start the app
mvn spring-boot:run
# open http://localhost:8080/swagger-ui.html
# open http://localhost:8080/actuator/health

Quick tour
Auth (demo)
POST /api/auth/login
Content-Type: application/json

{ "username": "admin", "password": "password" }


Use the returned accessToken as Authorization: Bearer <token> for secured endpoints.

Tasks API (JWT required)

GET /api/tasks

POST /api/tasks

PUT /api/tasks/{id}

DELETE /api/tasks/{id}

Tip: This is where you can plug in your AVL tree: keep a Map<id, Task> for storage and maintain an AVL index (e.g., on title) for O(log n) search. Update both in create/update/delete.

Community Issue Reporter (no login)
Use the web form

Open http://localhost:8080/report

Fill Title + Address (or click Use my location)

Optional: add a photo

Submit 🎉

Endpoints

POST /api/issues (multipart: meta JSON + photo)

POST /api/issues/simple (form fields: title, description, street, city, state, postalCode, latitude, longitude, assignedGroup, photo)

GET /api/issues?status=&postalCode=&streetContains=&nearLat=&nearLng=&radiusMeters=

PATCH /api/issues/{id}/status (e.g., NEW → IN_PROGRESS → DONE)

GET /api/issues/feed.json (public JSON feed)

Address-first UX (friendlier than raw GPS)

Users can type Street / City / State / ZIP.

If only address is provided, the server geocodes to lat/lng.

If only GPS is provided (Use my location), the server reverse-geocodes the address.

Search supports postalCode and streetContains filters.

Geocoding config (recommended)
# pick a provider (Nominatim is default)
export APP_GEOCODE_PROVIDER=NOMINATIM
export NOMINATIM_BASE=https://nominatim.openstreetmap.org
export NOMINATIM_EMAIL=you@example.com   # (note the spelling)


Provide a contact email to respect Nominatim’s usage policy.
For offline/local demos: export APP_GEOCODE_PROVIDER=NONE

Handy examples (PowerShell)
# 1) get a token
$body = '{"username":"admin","password":"password"}'
$TOKEN = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $body).accessToken

# 2) create an issue with photo (multipart)
$meta = '{"title":"Pothole on 5th Ave","description":"Large pothole near stop sign","latitude":33.7489,"longitude":-84.3900,"assignedGroup":"roads"}'
$fields = @{ meta = $meta; photo = Get-Item "C:\path\to\photo.jpg" }
Invoke-RestMethod -Uri "http://localhost:8080/api/issues" -Method Post -Headers @{Authorization="Bearer $TOKEN"} -Form $fields

# 3) public feed (no auth)
Invoke-RestMethod -Uri "http://localhost:8080/api/issues/feed.json" -Method Get

Notes & defaults

JWT: HS256 via jjwt, expects base64 key in APP_JWT_SECRET.

Rate limiting (anonymous issue submits): default 10 requests / 10 minutes / IP.
Tune with:

export RATE_LIMIT_WINDOW_SEC=600
export RATE_LIMIT_MAX=10


CORS is on for simple cross-site posts; lock it down for production.

Project layout
src/main/java/com/esecure/securetask/...   # API, security, services
src/main/resources/application.yml         # configuration
src/test/java/...                          # tests


Legacy code (if any) moved under src/main/java/com/esecure/securetask/legacy/* for reference.

Troubleshooting

spring-boot:run not found → add Boot parent in pom.xml or pin the plugin version (3.3.3).

Reverse geocoding slow/fails → set APP_GEOCODE_PROVIDER=NONE to disable, or provide NOMINATIM_EMAIL.

429 Too Many Requests on /api/issues/simple → hit the rate limit; adjust RATE_LIMIT_* env vars.
