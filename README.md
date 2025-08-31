# SecureTask — Spring Boot Starter

This is a minimal Spring Boot API for your SecureTask project with JWT auth, RBAC, validation, actuator, and Swagger UI.

## Quick start

1) Generate a base64 secret (Linux/macOS):
```bash
head -c 32 /dev/urandom | base64
```
On Windows (PowerShell):
```powershell
$bytes = New-Object byte[] 32; (New-Object System.Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes); [Convert]::ToBase64String($bytes)
```

2) Run:
```bash
export APP_JWT_SECRET="PASTE_BASE64_KEY"
./mvnw spring-boot:run
# or: mvn spring-boot:run
```

3) Swagger UI: http://localhost:8080/swagger-ui.html  
Health: http://localhost:8080/actuator/health

## Auth demo
- POST /api/auth/login  { "username":"admin", "password":"password" }
- Use returned Bearer token for /api/tasks

## Integrate your AVL Tree
Replace the in-memory map in `TaskService` with your AVL index. E.g., keep a map<id,Task> for storage and an AVL index on title for fast search. Update methods to keep both structures in sync.

## Notes
- This starter uses `jjwt` (HS256) and expects a base64 key in `APP_JWT_SECRET`.
- Exposes `/api/tasks` (CRUD), `/api/auth/login`, `/actuator/health`, and Swagger docs.


---

## Project Layout (clean)
- `src/main/java/com/esecure/securetask/...` — Spring Boot API, security, services
- `src/main/resources/application.yml` — configuration (uses `APP_JWT_SECRET`)
- `src/test/java/...` — tests
- `legacy/` code has been moved under `src/main/java/com/esecure/securetask/legacy/*` for reference
- Removed old top-level folders: `controller/`, `service/`, `utils/`, `model/`, `logs/`, `Main`


## Community Issue Reporter (geo + photos) — MVP
### Endpoints
- `POST /api/issues` (multipart): `meta` (JSON) + optional `photo` → creates an issue
- `GET /api/issues?status=&nearLat=&nearLng=&radiusMeters=`: search (public read)
- `PATCH /api/issues/{id}/status`: update status (`NEW`, `IN_PROGRESS`, `DONE`)
- `GET /api/issues/feed.json`: public map feed (recent issues with lat/lng/status)

### Example (PowerShell)
```powershell
# login (get token)
$body = '{"username":"admin","password":"password"}'
$TOKEN = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $body).accessToken

# create issue (multipart)
$meta = '{"title":"Pothole on 5th Ave","description":"Large pothole near stop sign","latitude":33.7489,"longitude":-84.3900,"assignedGroup":"roads"}'
$fields = @{
  meta = $meta
  photo = Get-Item "C:\path\to\photo.jpg"
}
Invoke-RestMethod -Uri "http://localhost:8080/api/issues" -Method Post -Headers @{Authorization="Bearer $TOKEN"} -Form $fields

# public feed
Invoke-RestMethod -Uri "http://localhost:8080/api/issues/feed.json" -Method Get
```


## Easy Reporting (no login)
- Visit **/report** to use a simple form (auto-fill location).
- Anonymous submissions are allowed and rate-limited (defaults: 10 per 10 minutes per IP; configurable with `RATE_LIMIT_*` env vars).
- CORS is enabled for basic cross-site posting (you can host a static form elsewhere).

**Test locally**
- Open: `http://localhost:8080/report`
- or POST form-data to `/api/issues/simple` with fields: `title`, `description`, `latitude`, `longitude`, optional `assignedGroup`, and optional file `photo`.


## Address‑first reporting (friendlier UX)
- Reporters can enter **Street, City, State, ZIP** instead of coordinates.
- If only address is provided, the server **geocodes** it (Nominatim/OpenStreetMap).
- If only GPS is provided (Use my location), the server **reverse‑geocodes** to fill the address.
- Search now supports `?postalCode=` and `?streetContains=` in addition to radius searches.

**Config (recommended):**
```
APP_GEOCODE_PROVIDER=NOMINATIM
NOMINATIM_BASE=https://nominatim.openstreetmap.org
NOMINATIN_EMAIL=you@example.com
```
(Provide a contact email to respect Nominatim usage policy. For local dev or offline demos, set `APP_GEOCODE_PROVIDER=NONE` to disable network calls.)
