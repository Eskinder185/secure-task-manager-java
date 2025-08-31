# SecureTask — Merge Notes

Your original classes have been merged into the Spring Boot project.

## Primary (Spring Boot) API
- Controllers, Security, JWT, Actuator, Swagger are from the Spring Boot starter in `com.esecure.securetask`.
- Endpoints: `/api/auth/login`, `/api/tasks`, `/actuator/health`, docs at `/swagger-ui.html`.

## Original Code (namespaced under `legacy`)
- Original services/controllers/utils were copied under `com.esecure.securetask.legacy.*` to avoid class-name conflicts.
- Models `Task.java` and `User.java` were replaced with your originals in the main package.
- `AVLTree.java` is added to `com.esecure.securetask.model`.

### Files merged
- model: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/model/Task.java
- model: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/model/User.java
- model: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/model/AVLTree.java
- service_legacy: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/legacy/service/AuthServiceLegacy.java
- service_legacy: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/legacy/service/RBACServiceLegacy.java
- service_legacy: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/legacy/service/TaskServiceLegacy.java
- controller_legacy: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/legacy/controller/TaskControllerLegacy.java
- utils_legacy: /mnt/data/securetask_merged/src/main/java/com/esecure/securetask/legacy/utils/JWTUtilLegacy.java

## How to integrate AVLTree with the Spring service
Edit `src/main/java/com/esecure/securetask/service/TaskService.java` and:
1. Add a field: `private final AVLTree<String> index = new AVLTree<>();` (adjust generic type/methods based on your implementation).
2. On `create`: insert `task.getTitle()` (or another key) into the index.
3. On `update`: if key changes, remove old then insert new.
4. On `delete`: remove key from index.
5. For search endpoints you add later, query via the AVL index for O(log n).

If you prefer to reuse your original logic:
- Look at `legacy.service.TaskServiceLegacy` and port methods into the primary `TaskService` (keep `@Service`).
- You can delete the `legacy` package once you've migrated logic.

## Build and run
- Set `APP_JWT_SECRET` (base64 256-bit key) in your shell, then:
```
mvn spring-boot:run
```
