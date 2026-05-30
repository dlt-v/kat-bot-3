### KatBot Deployment Guide

KatBot3 now uses a registry-based workflow:

1. Maven builds and pushes Docker images with Jib, that's the manual way to do it.
2. Additionally, GitHub Actions automatically builds and publishes images for `release/*` branches.
3. The VPS pulls the published image from Docker Hub and runs it with Docker Compose.

---

### Prerequisites

Here's what's needed to make sure everything works:

1. Git
2. Java 17
3. Maven (IntelliJ has it already, so optional maybe)
4. Docker (logged in)
5. Access to the GitHub repository (duh)

Right now the built images are public so pulling from the VPS does not require a token.

---

### Local Development Setup

1. Clone the repository.

```bash
git clone <your-repo-url>
cd kat-bot-3
```

2. Open the project in your editor and copy or create your local environment file. (check `docker/.env.example` for reference)

3. Verify the project compiles locally.

```bash
mvn -DskipTests clean compile
```

4. Build and push the Docker image manually when needed.

```bash
mvn -Pprod -DskipTests clean compile jib:build
```

This uses the `prod` profile from `pom.xml` and pushes the image to `deltaveee/kat-bot:<version>`.

---

### GitHub Actions Release Pipeline

GitHub Actions handles the release workflow automatically.

Trigger rules:

1. A pull request targeting `release/*` runs a validation job.
2. A push to `release/*` runs the publish job.

What the workflow does:

1. Checks out the repository.
2. Sets up Java 17.
3. For pull requests, runs a full Maven verify build, including tests.
4. For pushes, logs in to Docker Hub using GitHub Secrets.
5. Builds the image with Jib.
6. Pushes the image to Docker Hub.

Required GitHub Secrets:

- `DOCKERHUB_USERNAME` — Docker Hub username.
- `DOCKERHUB_TOKEN` — a Docker Hub access token with read & write permission.

---

### VPS Setup

1. Install Docker if it is not already installed.


2. Copy the repository files you need to the VPS.

   At minimum, keep the `docker-compose.yml` file and a parent-directory `.env` file matching the compose file's `env_file: ../.env` path.

3. Update the compose file to use the published image instead of building locally.

   Use a line like this for the `image` field in `docker-compose.yml`:

```yaml
image: deltaveee/kat-bot:1.2.0
```

4. Pull the latest image and start the containers.

```bash
docker compose pull
docker compose up -d
```

5. When a newer image is published, repeat the same pull and restart commands.

```bash
docker compose pull
docker compose up -d
```

---

### Manual Release Checklist

For consistency, it's better to create a PR to a `release/*` branch and let GitHub Actions handle the publishing. But just in case:

1. Make sure the code is ready on a `release/*` branch.
2. Push the branch or merge into it.
3. Let GitHub Actions publish the image.
4. On the VPS, run:

```bash
docker compose pull
docker compose up -d
```

---

### Notes

- Prefer version tags like `1.2.0` for deployment in yamls.
- Avoid using `latest` for production unless you want the VPS to always track the newest push.
- Don't store secrets in the repository? Duh?
