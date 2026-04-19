# Smart Campus Sensor & Room Management API

#1. Overview
    This is a RESTful web service developed for the university's "Smart Campus" initiative. Built using Java and JAX-RS (Jersey), it provides a robust backend API to manage campus rooms, register various IoT sensors (e.g., Temperature, CO2, Occupancy), and maintain historical sensor readings. The application uses a thread-safe in-memory data store and follows strict RESTful architectural constraints including HATEOAS, standard HTTP status codes, and sub-resource locators.

2. Build and Launch Instructions
    To build and run this project locally, you will need **Java JDK 11+**, **Maven**, and a web server like **Apache Tomcat** or **GlassFish**.

  Step-by-step Execution:
    1. Clone the repository to your local machine.
    2. Open the project in your IDE (e.g., NetBeans, IntelliJ, or Eclipse).
    3. Build the project using Maven:
   ```bash
   mvn clean install
    4.Deploy the generated .war file to your Tomcat/GlassFish server.

    5.If running via NetBeans, simply right-click the project and select Run.

    6.The API will be accessible at: http://localhost:8080/smart-campus-api/api/v1

3. Sample cURL Commands
Here are five commands to test the core functionality of the API. (Note: Adjust the port 8080 if your server uses a different port).

    1. View API Discovery Info:
    curl -X GET http://localhost:8080/smart-campus-api/api/v1/

    2. Create a New Room:
    curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
    -H "Content-Type: application/json" \
    -d '{"id":"LIB-301", "name":"Library Quiet Study", "capacity":50}'

    3. Get All Rooms:
    curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms

    4. Register a New Sensor (Linked to the Room):
    curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
    -H "Content-Type: application/json" \
    -d '{"id":"TEMP-001", "type":"Temperature", "status":"ACTIVE", "currentValue":22.5, "roomId":"LIB-301"}'

    
4. Conceptual Report
    Part 1: Service Architecture & Setup
    Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? How does this impact in-memory data structures?
    By default, JAX-RS resource classes are instantiated on a per-request basis. A new instance is created for every incoming HTTP request and destroyed after the response. Because of this, standard instance variables (like a regular HashMap) will be lost between requests. To prevent data loss, in-memory structures must be managed by a Singleton class or declared as static. Furthermore, we must use thread-safe collections (like ConcurrentHashMap) to prevent race conditions when multiple requests hit the API simultaneously.

    Q: Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design? How does this benefit client developers?
    HATEOAS ensures API responses include navigation links to related resources, making the API self-descriptive. For client developers, this eliminates the need to hardcode endpoint URLs. Clients can dynamically follow links provided in the JSON response, meaning if backend URL structures change, the client application won't break.

    Part 2: Room Management
    Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects?
    Returning only IDs consumes less network bandwidth initially but forces the client to make multiple subsequent API calls (the "N+1 problem") to fetch details, increasing server load. Returning full objects requires more upfront bandwidth but allows the client to render data immediately. In our case, returning full objects benefits dashboard rendering.

    Q: Is the DELETE operation idempotent in your implementation? Provide justification.
    Yes, the DELETE /{roomId} operation is idempotent. If a client sends a DELETE request for a valid room, it is removed, returning 204 No Content. If the exact same request is sent again, the server returns 404 Not Found. Despite the different status code, the system state remains unchanged after the first request, strictly satisfying the idempotency constraint.

    Part 3: Sensor Operations & Linking
    Q: We explicitly use @Consumes(MediaType.APPLICATION_JSON). Explain the technical consequences if a client sends text/plain. How does JAX-RS handle this?
    If a client sends data in an unsupported format, JAX-RS intercepts the request before it reaches the resource method. Based on the @Consumes constraint, it automatically rejects the request and returns an HTTP 415 Unsupported Media Type status, acting as a built-in security and validation layer.

    Q: Contrast @QueryParam filtering with path-based filtering (e.g., /sensors/type/CO2). Why is the query parameter approach superior?
    URL paths should identify specific resources (nouns), while query parameters modify or filter them. ?type=CO2 correctly signifies we are querying the main collection, just a subset of it. Query parameters are optional and flexible (allowing multiple filters), whereas hardcoded paths create rigid routing that doesn't semantically align with dynamic searching.

    Part 4: Deep Nesting with Sub-Resources
    Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does it help manage complexity?
    It enforces the Single Responsibility Principle. Defining deeply nested routes (like /sensors/{id}/readings) inside one SensorResource creates a bloated "god class." By delegating to SensorReadingResource, we separate logic: SensorResource handles sensor metadata, while SensorReadingResource handles historical data. This modularity makes large APIs easier to read, maintain, and test.