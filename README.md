Smart Campus Sensor and Room Management API.


1. Overview
    It is a RESTful web service that was created as a part of the Smart Campus project at the university. Developed with an API powered by Java and JAX-RS (Jersey), it gives a strong back-end API to control campus rooms, add different IoT sensors (e.g., Temperature, CO2, Occupancy), and keep track of sensor readings. The application will be based on thread-safe in-memory data store and adhering to strict RESTful architectural requirements such as HATEOAS, standard HTTP status codes, and sub-resource locators.

2. Build and Launch Instructions.
    The minimum requirements to create and operate this project locally are Java JDK 1+, Maven, and a web server such as Apache Tomcat.

  Step-by-step Execution:
    1. Make a clone of the repository on your computer.
    2. Select your project in your IDE (e.g., NetBeans, IntelliJ, or Eclipse).
    3. Develop the project with Maven:
   ```bash
   mvn clean install
    4.Install the created .war file in your Tomcat server.

    5.When it is run through NetBeans, just right click on the project and run.

    6.The API will be available in the location: http://localhost:8080/smart-campus-api/

3. Sample cURL Commands
These are five API test commands that are used to test the basic functionality of the API. (Note: This is port 8080, but change it to another port in case your server uses a different port).

    1. API Discovery info:
    curl -X GET http://localhost:8080/smart-campus-api/

    Select Room: Choose a new room.<|human|>2. Add a New Room:
    curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms.
    -H "Content-Type: application/json" \
    -d "{"id":"LIB-301", "name":"Library Quiet Study", "capacity":50}"

    3. Get All Rooms:
    curl -X GET localhost:8080/smart-campus-api/api/v1/rooms.

    4. Add New Sensor (Connected to the Room):
    curl -X POST localhost:8080/smart-campus-api/api/v1/sensors.
    -H "Content-Type: application/json" \
    -d "{"id":"TEMP-001", "type":"Temperature", "status":"ACTIVE", "currentValue":22.5, "roomId":"LIB-301"}"

    5. Add New Reading to the Sensor:
    curl -X POST localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings.
    -H "Content-Type: application/json" \
    -d "{"id":"READ-001", "timestamp":1672531200000, "value":23.1}"

    
4. Conceptual Report
    Part 1: Service Architecture and Set-up.
    Q: How does a default lifecycle of a JAX-RS Resource class work? Does the run-time create a new instance on each incoming request or does it consider it a singleton? What effect does this have on in-memory data structures?
    The default is that JAX-RS resource classes are per-request instantiated. Every incoming HTTP request is created into a new instance and destroyed after responding to the request. Due to this, normal instance variables (such as a normal HashMap) will be lost between requests. In-memory structures should be handled by a Singleton class or declared to be static in order to avoid data loss. Moreover, we have to utilize thread-safe collections (such as ConcurrentHashMap) to avoid the race condition, in case multiple requests are being made to the API at the same time.

    Inquisition: What is the significance of the so-called Hypermedia (HATEOAS) provision being viewed as the feature of the advanced RESTful design? What does it do to help client developers?
    HATEOAS makes the API self-descriptive by ensuring that it provides navigation links to related resources in the API response. To client developers it removes the hardcoded endpoint URLs. Clients can follow dynamically given links within the JSON response, i.e. should the backend URL structures change the client application will not break down.

    Part 2: Room Management
    Q: What is the implication of returning a list of rooms by only the IDs, as opposed to returning the entire room objects?
    Sending back only IDs uses less network bandwidth, but requires the client to use several follow-up API calls (the N+1 problem) to get details, which adds load to the server. Full objects involve more bandwidth upfront, but enable the client to render data immediately. In our scenario, we have the advantage of returning back complete objects, which is advantageous to dashboard rendering.

    Q: Does your implementation of the DELETE operation make it idempotent? Provide justification.
    Yes, the DELETE /roomId operation is idempotent. When a client makes a DELETE request to a valid room, it is deleted and the response is 204 No Content. In case of the same request being made again, the server response is 404 Not Found. Although the status code is different, the system state is the same as before the initial request and adheres to the idempotency constraint.

    Part 3: Sensor Operations and Connection.
    Q: we use explicitly @Consumes(Mediatype.APPLICATION_JSON). Discuss the technical impact should a client transmit text/plain. What does JAX-RS do about this?
    When a client submits data that is not supported, it is intercepted by JAX-RS before it is sent to the resource method. The constraint of the form of the request, which is the @Consumes constraint, automatically rejects the request and sends an HTTP 415 Unsupported Media Type status as the default security and validation mechanism.

    Q: Compare query param filtering with path filtering (e.g., /sensors/type/CO2). What is the superiority of query parameter approach?
    The URLs (identifying resources (nouns)) should be modified by query parameters (filtering or modifying them), but the query parameters should be in the form of:?type=CO2, meaning that we are querying the main collection, only a subset of it. Although query parameters are optional and flexible (with multiple filters possible), hardcoded paths provide fixed routing that is not semantically compatible with searching dynamically.

    Part 4: Deep Nesting containing Sub-Resources.
    Q: Discuss architectural advantages of Sub-Resource Locator pattern. What does it do to cope with complexity?
    It imposes Single Responsibility Principle. Defining routes too deeply nested (such as /sensors/readings), within a single SensorResource, makes it a bloated God class. We delegate logic to SensorReadingResource by making SensorResource deal with sensor metadata, and SensorReadingResource deal with historical data. This modularity simplifies large APIs to read, maintain and test.