# Exchange Rate API

This project sets up an exchange rate provider api, provisioned with authentication, rate limiting, caching, and both REST and GraphQL interfaces.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architectural Highlights & Design Decisions](#architectural-highlights--design-decisions)
  - [Infrastructure Decoupling](#infrastructure-decoupling)
  - [Optimized Caching Strategy](#optimized-caching-strategy)
  - [Protocol Flexibility](#protocol-flexibility)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
- [How to Run the Application](#how-to-run-the-application)
  - [Option 1: Development with Nix](#option-1-development-environment-with-nix)
  - [Option 2: Deployment with Docker Compose](#option-2-deployment-with-docker-compose)
  - [Option 3: Local Development Dependencies](#option-3-local-development-dependencies)
- [API Usage & Documentation](#api-usage--documentation)
  - [Authentication](#authentication)
  - [API Endpoints](#api-endpoints)
  - [Rate Limiting](#rate-limiting)

## Tech Stack

*   **Language & Framework:** Java 23, Spring Boot
*   **Data & Database:** Spring Data JPA, PostgreSQL
*   **Caching:** Spring Cache, Caffeine, Redis
*   **Documentation:** SpringDoc OpenAPI (Swagger)
*   **Build & Environment:** Maven, Docker, Nix

---

## Architectural Highlights & Design Decisions

#### Infrastructure Decoupling

This API makes use of a PostgreSQL database and offers profile-based configuration for its caching layer, which is used for both exchange rate data and rate limiting.

-   **`cache-caffeine`:** An in-memory cache, ideal for simple, single-instance deployments.
-   **`cache-redis`:** A distributed cache, enabling horizontal scalability. With Redis, multiple API instances can share a consistent cache for both rates and rate limiting state.

This design decouples the application logic from its infrastructure, allowing it to adapt between single and multiple instance deployments.

#### Decoupled Data Providers

An interface is provided for data providers to implement, making swapping providers very easy.
There are currently two implemented options, [exchangerate.host](https://exchangerate.host/) and [frankfurter](https://frankfurter.dev/), and adding more is very easy.

#### Optimized Caching Strategy

-   Each time an exchange rate is requested from our API, a value is served from an internal cache.
-   If the cache is stale:
    1.  A single request is made to the configured external provider for rates against a base currency.
    2.  The response is used to calculate and cache all direct, inverse, and **cross-currency rates**.
    3.  The cache is populated with the full set of rates.

This strategy allows us to make at most **one request per minute** (the configurable cache TTL) to external APIs.

##### Example with Three Currencies

Given rates against a common base currency (e.g., EUR), we can derive all other rates.
-   **Known Rates:** `EUR → USD` and `EUR → GBP`

We can derive:
1.  **Inverse Rates**:
    -   $\( \text{USD → EUR} = \frac{1}{\text{EUR → USD}} \)$
2.  **Cross-Rate Calculation**:
    -   To find the rate for `USD → GBP`, the formula is:
        $\( \text{USD → GBP} = \frac{\text{EUR → GBP}}{\text{EUR → USD}} \)$

This principle allows the system to serve a wide variety of conversion requests while being extremely efficient with external network traffic.

#### Protocol Flexibility

This project offers both a REST and a GraphQL api, so users can choose what they like best.

**NOTE:** GraphiQL (GraphQL's graphical IDE) is broken on the spring boot version used, so /graphiql won't work. However, graphql queries still work fine.

---

## Getting Started

### Prerequisites

You'll need JDK23 and maven to run this project.

You will also need an instance of PostgreSQL and optionally, Redis. (These don't need to run on the same machine)

If you'd rather use a containerized solution, a docker image is provided, which compiles the project and runs a PostgreSQL and Redis instance.

### Configuration

You can configure some options through the project's application.properties file, namely:
- Which provider to use (use spring profiles for this)
- Which cache implementation to use (use spring profiles for this)
- How many requests are allowed in the rate limiting time window
- How big the rate limiting time window is

There is also a .env file with all the necessary environment variables for running the project.

You **need** to set these, or else things won't work.

The default values in the provided example work perfectly if you use docker, otherwise, feel free to change the instances URLs/Ports to whatever you need.

You will also need an exchangerate.host api key if you desire to use that provider. (ERHOST_APIKEY environment variable)

---

## How to Run the Application

Running the application involves two main steps: configuring your environment and then launching the application using your preferred method.

### 1. Configure Environment Variables

Before you can run the project, you must provide configuration through environment variables. These are used to set things like database connection details, API keys, and other secrets the application needs to function.

Remember that `.env` files should be listed in `.gitignore` and should never be committed to version control.

#### Loading the Variables

Many tools, like Docker Compose, will automatically detect and load the `.env` file. If your method doesn't, you'll need to load the variables into your shell session manually before running the application.

**For Linux/macOS (Bash/Zsh):**

You can `source` the file. This is the most common method.

```bash
# This command exports all variables defined in the .env file
set -a
source .env
set +a
```

**For Nushell:**

```nushell
open .env | from toml | load-env
```


### 2. Choose Your Runtime Environment

Once your environment variables are configured, choose one of the following options to run the application.

#### Option A: Development with Nix

A Nix flake is provided with all necessary dependencies, including a Java LSP for editors like Vim, Helix, or Emacs.

> **Note:** For a seamless experience, consider using [direnv](https://direnv.net/) with `nix develop`. It can automatically load environment variables from your `.env` file whenever you enter the directory.

1.  Enter the development environment:
    ```shell
    nix develop
    ```
2.  (If not using direnv) Load your environment variables as described in Step 1.
3.  Once inside the shell, run the application:
    ```shell
    mvn spring-boot:run
    ```

You will need to run backing services (like a database) separately, for example, using Docker, locally, or on a server.

#### Option B: Deployment with Docker Compose

The `docker-compose.yml` is configured to build the application image and run it alongside its required services. It automatically loads variables from the `.env` file in the project root.

1.  Ensure your `.env` file is created and populated.
2.  Start all services in detached mode:
    ```shell
    docker-compose up -d --build
    ```

#### Option C: Local Java & Maven

You can use your locally installed Java and Maven, provided they meet the version requirements specified in the project.

1.  Load your environment variables as described in Step 1.
2.  Run the application:
    ```shell
    mvn spring-boot:run
    ```

---

## API Usage & Documentation

### Authentication

Endpoints under `/api/v1/exchange/` are protected and require an API key provided in the `X-API-KEY` header. You can create a user and generate keys via the `/api/v1/auth/` endpoints.

### API Endpoints

#### REST API (Swagger)

An interactive Swagger UI is available for exploring and testing the REST API:
**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

#### GraphQL API

The GraphQL endpoint is available at `/graphql`. Use an API client to send `POST` requests with your queries.

### Rate Limiting

Authenticated endpoints are rate-limited using a sliding-window algorithm, applied per user.
The default values in `application.properties` are set low (e.g., 2 requests per 10 seconds) to easily demonstrate that the functionality is in place and working correctly.
