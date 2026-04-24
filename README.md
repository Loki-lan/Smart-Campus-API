# Smart-Campus-API

#### ***Project Overview***



**This system is a scalable RESTful API built as part of the university’s Smart Campus initiative. Developed with Java using JAX-RS (Jersey) and managed through Maven, it handles a large ecosystem of rooms and environmental sensors such as CO₂ and temperature monitors. The design follows solid RESTful principles, incorporating sub-resource locators for structured access to sensor logs and exception mapping to ensure consistent and reliable error handling.
**



#### ***Build \& Launch Instructions***



**To build and run this API locally, follow these steps:**



1. **Clone the Repository: git clone <https://github.com/Loki-lan/Smart-Campus-API.git>**
2. **Navigate to Directory: cd </Users/heshanialoka/Desktop/ALL/UNI/GitHub/Smart-Campus-API>**
3. **Build with Maven: mvn clean install**
4. **Run the Server: mvn exec:java .**
* &#x09;**The API will be accessible at: http://localhost:8080/api/v1**



#### ***Postman Testing***



***STEP 01 :***

* ***A JSON response containing metadata like the version ("v1") and links to your primary collections (rooms and sensors). This demonstrates HATEOAS for your report.***
* GET - http://localhost:8080/api/v1



***STEP 02 :***

* ***Creating a Room***
* POST - http://localhost:8080/api/v1/rooms
* Body - {"id": "ENG-LAB-402", "name": "3LA", "capacity": 20}



* GET - http://localhost:8080/api/v1/rooms/ENG-LAB-402
* ***The full JSON object.***



***STEP 03 :***

* ***Adding a Sensor***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "HUM-005", "type": "Humidity", "status": "ACTIVE", "roomId": "ENG-LAB-402"}



* ***Adding a sensor with a invalid roomId.***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "HUM-006", "type": "Humidity", "status": "ACTIVE", "roomId": "VOID-999"}



* GET - http://localhost:8080/api/v1/sensors?type=Humidity
* ***Only sensors matching that specific types are returned.***



***STEP 04 :***

* ***Updating the currentValue.***
* POST - http://localhost:8080/api/v1/sensors/HUM-005/readings
* Body - {"value": 45.2}
* GET -  http://localhost:8080/api/v1/sensors/HUM-005



* ***Updating the sensor status to "MAINTENANCE".***
* PUT - http://localhost:8080/api/v1/sensors/HUM-005
* Headers - Content-Type to application/json
* Body - {"id": "HUM-005", "type": "Humidity", "status": "MAINTENANCE", "roomId": "ENG-LAB-402"}



* ***Adding a reading to the "MAINTENANCE" sensor.***
* POST - http://localhost:8080/api/v1/sensors/HUM-005/readings
* Body - {"value": 50.0}
*Response: 403 Forbidden (Proves sensors in maintenance cannot record data)



***STEP 05 :***

* ***The sensor status returns as "ACTIVE".***
* PUT - http://localhost:8080/api/v1/sensors/HUM-005
* Headers - Content-Type to application/json
* Body - {"id": "HUM-005", "status": "ACTIVE", "roomId": "ENG-LAB-402"}



* ***Deleting the room with the ACTIVE sensor.***
* DELETE - http://localhost:8080/api/v1/rooms/ENG-LAB-402
* Response: 409 Conflict (Proves you cannot delete a room while it has an active sensor).



* ***Deleting the sensor first and then deleting the room after.***
* DELETE - http://localhost:8080/api/v1/sensors/HUM-005
* DELETE - http://localhost:8080/api/v1/rooms/ENG-LAB-402
* Response: 204 No Content (Successful clean deletion)

------------------------------------------------------------------------------------

**Part 01 - JAX-RS Resource Lifecycle
JAX-RS implements an automatic instantiation of resource classes on a per-request basis. This implies that a different object of the resource class is generated with each incoming HTTP request.

This implementation, however, does not have shared data stored within resource classes. Rather, there is a singleton pattern through SmartCampusStore.getInstance(). This makes sure that all requests operate on the same in-memory data.

This design avoids:

*Data loss (since data is not tied to request-scoped objects)
*Race conditions (because ConcurrentHashMap is used for thread safety)

So even though resources are per request, the data layer is shared and thread safe, which is essential for concurrent API usage.

2. HATEOAS / Hypermedia

Hypermedia (HATEOAS) implies that the responses have links that indicate what to do next.

Links such as:

/rooms
/sensors

This will be advantageous to clients since:

*They do not require hard coded URLs.
*The API is self discoverable.
*Any modifications to endpoints do not destroy clients provided that links are also modified.

This makes the API more developer friendly, dynamic, and flexible than the case of static documentation.

**Part 02 - 

1) Return of ID vs Full Objects

Returning only IDs:

 Advantage: Reduced payload (reduced bandwidth)
 Disadvantage: Needs further requests to obtain details.

Sending back complete objects (solution):

  Advantage: Lowers number of API calls.
  Advantage: More accessible to the clients.
  Disadvantage: Slightly bigger response size.

In the case of this system, it is preferable to send full room objects since it makes client side reasoning easier.

2) DELETE Idempotency

DELETE command is idempotent.

First DELETE → room is deleted.
Next DELETE room no longer exists → probably gives 404.

Repeated calls do not alter the ultimate state following the initial deletion meaning that it is idempotent.

**Part 03

1. @Consumes JSON Behavior

 API uses:

@Consumes(MediaType.APPLICATION_JSON)

If a client sends:

text/plain or application/xml

JAX-RS will:

Reject the request automatically
Return HTTP 415 Unsupported Media Type

This ensures API only processes valid JSON input.

2. QueryParam vs PathParam

You used:

/sensors?type=CO2

This is better than:

/sensors/type/CO2

Because:

Query parameters are designed for filtering/searching
They are optional and flexible
Multiple filters can be combined easily (?type=CO2&status=ACTIVE)

Path parameters should represent resource hierarchy, not filters.

**Part 04

1. Sub Resource Locator Benefits


Benefits:

Maintains clean and modular code.
Differentiates between concerns (sensor logic vs readings logic)
Enhances scalability of large APIs.

In its absence, it would be in a huge single class and this is difficult to sustain.

2. Data Consistency (Side Effect)

When a new reading is added:

It is stored in the readings list
The parent sensor’s currentValue is updated

This ensures:

The API always reflects the latest sensor state
No inconsistency between readings and sensor data

**Part 05

1. HTTP 422 vs 404

HTTP 422 is more accurate because:

The request format is valid JSON
But the data inside it is invalid (roomId doesn’t exist)

404 would imply the endpoint itself is wrong, which is not the case.

2. Security Risk of Stack Traces

Exposing stack traces can reveal:

Internal class names
File structure
Libraries used
Potential vulnerabilities

Attackers can use this information to exploit the system.

Global exception mapper prevents this by returning a generic 500 response, which is secure.

3. Logging via Filters

Using JAX-RS filters is better than manual logging because:

Centralized (no repetition in every method)
Automatically applied to all endpoints
Cleaner and maintainable

RequestResponseLoggingFilter cleanly logs:

Incoming request method + URI
Outgoing response status


