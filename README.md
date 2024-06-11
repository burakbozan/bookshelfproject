Bookshelf Projects
<!---
Idempotent operation
Making the peration idempotent ensures safety of retry operation, mitigating unintended side effects or inconsistent results of a retry.

Context and problem
In distributed systems, particularly those involving microservices and APIs, operations often need to be retried due to transient failures, network issues, or other disruptions. However, retrying operations that alter system state can lead to duplicate transactions, inconsistent data, and unpredictable system behavior. This poses a significant challenge when ensuring system reliability and consistency across multiple service calls that may not always succeed on the first attempt.

Solution
The idempotent operation pattern ensures that operations can be safely repeated without changing the end result after their initial application. An operation is idempotent if it can be performed multiple times with the same outcome as if it was performed once. This is vital for operations that might be retried due to failures or in cases when multiple similar requests are received due to client retries or network delays.

Each operation or event sent by a producer includes a unique identifier, often generated based on the event’s content but incorporating unique logic to prevent duplication across systems. This ensures that even if an event is sent multiple times to guarantee delivery at least once, the consumers can still recognize and disregard that event’s duplicates based on the unique identifier.

This solution comes with some computational overhead, making this pattern sensible only for business flows where fixing idempotency is absolutely crucial.

Considerations
Implementing the idempotent operation pattern requires attention to several factors:

Unique identifiers: Generating unique identifiers using robust algorithms or systems like UUIDs for each operation that the client sends to prevent any overlap or duplication.

Resource overhead: Tracking operations and storing results increases resource usage, impacting storage and potential performance.

Operation time window: The decision on how long to keep records of operations and their results depends on the typical duration of expected retries and operational requirements.

Concurrency and synchronization: Managing concurrent requests with the same identifier necessitates careful synchronization to ensure that each operation is processed only once, possibly involving database locks or atomic operations.

Testing for idempotency: Testing is necessary to confirm that operations are truly idempotent under various scenarios, including repeated and concurrent executions.

Saga
The Saga design pattern is a technique used to ensure data consistency across services in distributed transaction scenarios. A saga consists of sequence of transactions that update each service and produce a message or event to initiate the next transaction. If a step in the sequence fails, the saga executes compensating transactions that undo the previous transactions.

Context and problem
Transactions must adhere to the ACID properties, which stands for Atomicity, Consistency, Isolation, and Durability.

Atomicity: Means that a set of operations must either occur as a whole or not at all.

Consistency: Ensures that a transaction brings data from one valid state to another valid state.

Isolation: Guarantees that concurrent transactions produce the same data state as sequentially executed transactions.

Durability: Ensures that committed transactions remain committed even in case of system failure or power outage.

In multi-service architecture, each service’s datastore is responsible for ensuring the ACID properties within its own transactions. A datastore-per-component model offers many advantages in microservices architecture, such as encapsulating domain data, allowing each service to use its own data store and schema, and be insulated from other services’ failures.

Yet, maintaining consistency across multiple services can be challenging and requires a cross-service transaction management strategy. Such strategy can comprise special approach for achieving ACID transactions across services, like distributed transactions. Distributed transactions, such as the two-phase commit (2PC) protocol, require all participants in a transaction to commit or roll back before the transaction can proceed. However, some participant implementations, such as NoSQL databases and message brokering, do not support this model.

Solution
The Saga pattern is an alternative to distributed transactions way to manage transactions using a sequence of local transactions. Each local transaction is an atomic work effort carried out by a participant in the saga. After each local transaction updates the database, it publishes a message or event to trigger the next local transaction in the saga. If a local transaction fails, the saga executes a series of compensating transactions that undo the changes made by the preceding local transactions.

There are two main saga implementation approaches: choreography and orchestration. Each approach has its own set of challenges and technologies to coordinate the workflow.

The figure below illustrates the difference in events coordination between services in choreography and orchestration approaches:

Choreography vs. Orchestration

Choreography is a way to coordinate sagas where participants exchange events without a centralized point of control. In this approach, each local transaction publishes domain events that trigger local transactions in other services. Choreography’s benefits:

Is good for simple workflows that require few participants and don’t need coordination logic.

Doesn’t require additional service implementation and maintenance.

Doesn’t introduce a single point of failure since the responsibilities are distributed across the saga participants.

Challenges related to choreography:

The workflow can become confusing when adding new steps, as it’s difficult to track which saga participants listen to which commands.

A risk of cyclic dependency between saga participants because they have to consume each other’s commands.

Integration testing is difficult because all services must be running to simulate a transaction.

Orchestration is a way to coordinate sagas where a centralized controller tells the saga participants what local transactions to execute. The orchestrator executes saga requests, stores and interprets the states of each task, and handles failure recovery with compensating transactions. Orchestration’s benefits:

Is good for complex workflows involving many participants or new participants added over time.

Is suitable when there is control over every participant in the process, and control over the flow of activities.

Doesn’t introduce cyclical dependencies, because the orchestrator unilaterally depends on the saga participants.

Saga participants don’t need to know about commands for other participants, which simplifies business logic.

Challenges related to orchestration:

An implementation of coordination logic requires the additional design complexity.

There is an additional point of failure because the orchestrator manages the complete workflow.

Considerations
When implementing the Saga pattern, keep in mind the following points:

Introduction of the Saga pattern could be challenging as it requires a new way of coordinating transactions and ensuring data consistency across multiple microservices.

Debugging the Saga pattern is particularly hard, and it becomes more complex as the number of participants increases.

Data cannot be rolled back as each saga participant commits changes to its local databases.

The implementation must be capable of handling transient failures and provide idempotence to reduce side effects and ensure data consistency. Idempotence means that the same operation can be repeated multiple times without changing the initial result.

Observability should be implemented to monitor and track the saga workflow.

The lack of participant data isolation imposes durability challenges, and the saga implementation must include countermeasures to reduce anomalies.

The following anomalies can happen without proper measures: lost updates, dirty reads, and fuzzy/nonrepeatable reads.

The following countermeasures are suggested to reduce or prevent anomalies: semantic lock, commutative updates, pessimistic view, reread value, and version file.

Circuit breaker
The circuit breaker software architecture pattern, inspired by the electrical circuit breaker, is a design pattern aiming to mitigate failures of remote calls in distributed environments.

Context and problem
In a distributed environment it is common for systems to make remote calls to downstream APIs. Such remote calls may fail due to transient faults. These include network issues, slow or unresponsive dependencies, service outages, etc., that could lead to cascading failures in upstream components, also to timeouts and retry storms. In such an environment, it is crucial to safeguard system functionality and prevent cascading failures that can have a widespread impact. The circuit breaker pattern mitigates these challenges by providing a mechanism to detect failures and respond to them.

Solution
The circuit breaker pattern is designed to enhance the resilience of distributed systems by introducing a mechanism that can be triggered when certain failure conditions are met. When the circuit breaker “trips,” it prevents further requests or calls to a failing component, resource, or downstream API. This allows the system to gracefully degrade, avoiding further load on a struggling system and enabling recovery.

The figure illustrates service interactions through circuit breaker in different scenarious. The top scenario visualises normal service operation with closed circuit breaker, while two other scenarious show failure in downstream service operation (open and half-open circuit breaker):

Circuit breaker

Circuit breaker states
The circuit breaker operates in the following states: closed, open and half-open.

Closed: The circuit breaker in this state allows requests to flow through to the target component. It monitors these requests for potential failures.

Open: When the failure rate or response time of the target component exceeds a predefined threshold, the circuit breaker transitions to the open state. In this state, all further requests are blocked.

Half-open: After a certain period of time, the circuit breaker transitions to the half-open state, allowing a limited number of requests to pass through. If these requests are successful, the circuit breaker returns to the closed state, allowing normal operations to resume. If the tests fail, it returns to the open state.

The diagram below illustrates transitions between circuit breaker states, based on successes or failures of requests to downstream API:

States of the circuit breaker

Implementation advantages
Implementing the circuit breaker pattern provides the following key advantages:

Fault tolerance: By preventing further requests to a failing component, the pattern isolates the issue, allowing the rest of the system to continue functioning. This increases the overall fault tolerance of the system.

Performance: The circuit breaker pattern can significantly improve system performance by avoiding repeated requests to a component that is known to be failing or slow. This reduces resource contention and latency.

Reduced downtime: Systems employing this pattern can recover faster from failures, as they don’t wait for timeouts or continue to bombard a non-responsive downstream component.

Self-healing: The pattern promotes self-healing behaviour by automatically transitioning from the open to the half-open state after a specified time, testing whether the failed component has recovered.

Monitoring and insights: Implementing the circuit breaker pattern provides valuable insights into the stability and performance of external dependencies. This information can be used to make informed decisions and conduct proactive maintenance.

Considerations
The typical components where circuit breaker is recommended include the following:

Microservices architecture: Pattern helps microservices gracefully handle failures in service-to-service communication, preventing cascading failures that could disrupt the entire system.

API integrations: In applications that interact with external APIs, a circuit breaker can protect against prolonged downtime or slow responses from these third-party APIs.

Database connections: Pattern is valuable for managing connections to databases, preventing database-related issues from affecting the entire system.

Circuit breaker implementation at ING
Implementations of the circuit breaker pattern at ING are present in the following:

The Merak ING framework for API development; see the related API SDK documentation.

The Touchpoint Sidecar.

Circuit breaker is also an integral feature of the Finagle HTTP client that provides two modules that can act as circuit breakers:

Fail fast: A session-driven circuit breaker.

Failure accrual: A request-driven circuit breaker.

Rate limiting
Rate limiting means setting a maximum limit on the number of API requests that can be made within a given duration.

Context and problem
Limits can be set in a contract (like a rate-limiting SLA) or defined in a rate limiting policy.

Rate limiting is also a security measure to protect a service from malicious or excessive usage, by limiting the number of requests over a given period of time.

Solution
You can use a rate limiting pattern to help avoid or minimise throttling errors related to throttling limits. The most common rate limiting technique is the token bucket algorithm.

In the example below, 33.3% of the processing requests coming from clients 1 and 2 is dropped due to a throttled database quota. Service instances 1 and 2 only allow for 600 out of the 900 incoming requests to pass and the rest is dropped:

Rate limiting example

Considerations
Rate limiting is applicable or particularly helpful in the following scenarios:

Overload prevention.

QoS/SLA requirements on data sources and services.

Large-scale repetitive automated tasks such as batch processing.

Lowering your traffic and potentially improving throughput by reducing the number of records sent to a service over a given period.

Reducing traffic compared to a naive retry on error strategy.

Reducing memory consumption by dequeuing records only when there is capacity to process them.

API gateway
An API gateway is the architecture pattern that outlines the concept of a single point of entry for clients to access multiple backend services. An API gateway implementations typically provide a unified interface to consume APIs, and it handles common functionalities like authentication, authorisation, rate limiting, caching, logging, and monitoring. An API gateway can also perform additional tasks such as routing, load balancing, transformation, and aggregation of API responses.

An API gateway can help improve the reliability of a service-oriented architecture by decoupling clients from backend services. This way, the clients do not need to know the details of each service, such as its location, protocol, or version. The API gateway can also abstract away the complexity of the backend services, and thus provide a consistent and simplified API for the clients. This can reduce the network latency, bandwidth consumption, and error rate of the client-server communication.

Solution
In the relation to Well-Architected Reliability Framework, the API gateway represents a pattern that provides reliable mechanisms to expose the API of a service. It supports foundational functions and management of change, failure, and demand.

Foundation: An API gateway can ensure that clients and backend services have a secure and stable connection, and that the network infrastructure can handle the traffic volume and variability.

Change management: An API gateway can enable the backend services to evolve independently, without breaking the client contracts. It can also facilitate the deployment of new features, bug fixes, and performance improvements by allowing the API gateway to route the traffic to different versions of the services.

Failure management: An API gateway can improve the resilience of the system by detecting and isolating failures, and by implementing fallback strategies, such as retries, timeouts, and use of circuit breakers. It can also provide visibility and feedback on the health and performance of the backend services, by collecting and exposing metrics and logs.

Demand management: An API gateway can help manage the demand and capacity of the system by implementing throttling, caching, and batching policies. It can also optimise the resource utilisation and efficiency of the backend services, by transforming and aggregating the API responses.

Gateway routing
One of the main functions of an API gateway is to route the incoming requests from the clients to the appropriate backend services, and to route the outgoing responses from the services back to the clients. The following functions of API gateway are involved in routing:

Request mapping: An API gateway can map the request URI, method, headers, and body to the corresponding service URI, method, headers, and body, based on predefined rules or configurations.

Service discovery: An API gateway can discover the location and availability of the backend services, based on service registry, DNS, or load balancer.

Load balancing: An API gateway can distribute the load among multiple instances of the same service, based on load balancing algorithms like round-robin, least connections, or weighted random.

Versioning: An API gateway can route the request to different versions of the same service based on versioning strategies, such as URI, header, or query parameter.

Gateway offloading
A further function of an API gateway is to offload some tasks from the backend services and perform them at the edge of the network. This can improve the performance, scalability, and security of the system. Tasks that an API gateway can offload include the following:

Authentication: An API gateway can authenticate the client requests by verifying the credentials, tokens, or certificates, and by rejecting the unauthorised or invalid requests. For example, an API gateway can verify the JWT token in the authorisation header, and check its signature, expiration, and scope.

Rate limiting: An API gateway can limit the number of requests that a client can make in a given period by applying quotas, limits, or policies, and by throttling or rejecting the excess requests. For example, an API gateway can limit the number of requests per second, per minute, or per hour, based on the client IP, API key, or endpoint.

Caching: An API gateway can cache the responses from the backend services, by storing them in a local or distributed cache, and by serving them from the cache for subsequent requests. This can reduce the network latency, bandwidth consumption, and server load.

Implementation of API gateway at ING
The Touchpoint API Gateway is built on top of Nginx. For more details, consult the API Gateway documentation on The Forge.

Caching
Recommendation 01-011

Select the appropriate caching strategy for your workload.

Caching is a method to speed up computer systems by temporarily storing data in a quick-access area, called a “cache”. This improves system performance and efficiency, especially where fast data retrieval is crucial.

Caching patterns
Caching patterns vary in how they handle data storage and retrieval to balance performance, consistency, and resource efficiency; see the following summary on them:

Cache-aside: Cache-aside is a common caching pattern where the application is responsible for reading and writing data to the cache. The application first checks the cache for the data, and if it’s not found, it reads the data from the data store and then writes it to the cache. In this case, the cache contains only data that is requested by the application, which helps keep the cache small.

Read-through: Read-through is a caching pattern where application does not interact directly with the cache. Instead, it delegates all read operations to the cache. If the data is not found in the cache, the cache retrieves it from the data store and adds it to the cache.

Write-through and write-back patterns manage how data is simultaneously or subsequently written to cache and storage, focusing on immediate data consistency or enhanced write performance, respectively.

Strategies like read-through, write-around, and lazy loading prioritise efficient data retrieval, loading data into cache based on access patterns or demand.

Eager loading pre-fetches data for faster access, while TTL caching controls data freshness by defining expiration times.

These patterns need to be customised when applied to specific use cases, depending on the application’s performance needs, data volatility, and the importance of real-time accuracy.

Considerations on when to use caching
In practice, implementing caching is a balancing act that considers both business objectives (such as cost, performance, scalability) and technical constraints (including the nature of data, system architecture). Deciding to use or avoid caching, and determining the best caching strategy, involves a thoughtful design approach that aligns with these factors.

Caching is advised: displaying account summary
This scenario involves an iOS widget provided by ING’s mobile app, where users frequently check their account summaries. These summaries include account balances, recent transactions, and periodic statements. Caching is advised for the following reasons:

High read frequency: Users often check their account summaries, but the underlying data, such as the account balance and recent transactions, doesn’t change every minute. Caching account summaries can significantly reduce the load on the back-end systems, resulting in faster response times.

Enhancing user experience: Quick access to account summaries is essential for a positive user experience, especially when the data is accessed frequently.

Data stability: Data elements like account balances and recent transactions are relatively stable and don’t change constantly. Temporarily caching this data (for example, for a few minutes) can be advantageous.

Caching is not advised: processing real-time transactions
This scenario involves processing real-time transactions, such as funds transfers, bill payments, or loan repayments. In such a case, caching is not advised, for the following reasons:

Need for real-time data accuracy: Transactions, including transfers and payments, require current balance information to avoid issues like overdrafts or transaction failures.

High data volatility: In this scenario, account balances and transaction statuses change rapidly. Caching could lead to the use of outdated information, increasing the risk of financial errors.

Regulatory and compliance concerns: At ING, regulatory requirements demand accurate and real-time processing of transactional data, rendering caching unsuitable for this purpose.

Additional indicators
There are additional indicators that can help determine whether caching is a good fit for a particular use case.

Expected cache-hit ratio
A high expected load probably leads to a high expected cache hit ratio, but there are other reasons this can happen. The general rules are as follows:

If the cache hit ratio is high, caching makes sense.

If it is low (< 30% for example), do not cache.

If the ratio is approaching 100%, one should take a look at longer cache periods and prioritise the objects highly to avoid them getting pushed out of the cache.

Expected response time
If a service’s response takes a long time, the first action is to upgrade the provider that provides the service operation. However, this is not always easy, and in those cases, it is good idea to cache it. The general rule is that response time can be a reason to cache, but only if the cached object will be used again.

Expected load
The expected (peak) load for back-end services ranges from a few calls per minute to more than 100 calls per second. The usual metric for load for service operations is transactions per second, abbreviated TPS. 1 TPS is equal to an average of 1 call every second in a peak hour. The general rules are as follows:

The higher the load, the more sense it makes to cache.

If the load exceeds 5 TPS, cache.

If the load is less than 1 TPS, do not cache.

If the load is in-between, consider not caching, unless there are other reasons to do so.

Required actuality, change frequency, and change triggers
Some data is required to be as actual as possible, others can be a minute-old, an hour-old, or even a day-old. Other aspects of relevance are the frequency the data can change in the back-end, and the way that change is triggered. Real actuality and caching are incompatible, if changes in the system of record can go unnoticed by the consumer application. Note that ‘as actual as possible’ can also mean a few seconds, which can still make it appropriate to use caching if other aspects justify it. If changes in the system of record are rare, it can still be actual enough when cached.

Differing caching times can also cause issues. If two objects are related (for example, the balance and the transactions), then if these objects are not cached at the same time, they can get out of sync. In some cases, this can call for shared caching, where multiple columns use the same cached objects, in order to provide a consistent view. However, shared caching is to be avoided whenever possible. The general rules are as follows:

If the consumer application triggers a change in a system of record, objects that are cached for the affected provider need to be invalidated. The safest way is to do a node-wide invalidation of all objects cached for the specific user. On a large scale, this does not affect the total load.

If the provider (SOR) data hardly ever changes, caching is easier.

Caching technologies
Selecting the best cache for distributed applications depends on various factors, including the specific requirements of the application, the complexity of the data, scalability needs and the environment.

Caching technologies that are available at ING within IPC and Public Cloud Foundation (Nebula), along with their pros and cons, are summarised below.

KaaS (Apache Cassandra)

Apache Cassandra is known for its scalability and fault tolerance, and it includes caching mechanisms to improve database performance.

In Cassandra, data writing involves storing information in a specific partition. This partition is then replicated across multiple nodes in the cluster, enhancing fault tolerance and high availability. The write process initially targets the node responsible for the partition, followed by replication to other nodes.

Reading data in Cassandra is more complex. It requires coordinating among several nodes to locate and retrieve the data. This coordination, necessary for read operations, is generally slower and more complex than writing, especially when the data is spread out over many nodes.

Cassandra is particularly optimised for high-volume write throughput. It is designed to manage substantial write loads and can scale horizontally to accommodate increased write demands.

However, this scalability presents challenges for read operations. As the number of nodes in the cluster increases, the complexity and potential latency of read operations also rise, due to the more extensive coordination and data retrieval efforts required.

Keyspaces as a Service is a managed service for Cassandra, provided in IPC.

Redis

Redis is an in-memory data structure store, used as a database, cache, and message broker. It supports data structures such as strings, hashes, lists, sets, also a variety of caching strategies, though some (like write-back and read-through) require additional logic or configuration.

Starting with Redis 7.4, Redis is dual-licensed under the source-available Redis Source Available License (RSALv2) and Server Side Public License (SSPLv1) licenses. Prevous versions were licensed under the open-source BSD license.

Pros:

Performance: Redis is extremely fast, offering high throughput and low latency.

Scalability: Easily scalable, supporting clustering for higher availability.

Data structures: Supports a variety of data structures for complex applications.

Persistence: Offers options for durability, ensuring data is not lost.

Cons:

Memory cost: Being in-memory, it can be more expensive in terms of memory usage.

Redis is not available in IPC as a managed service.

Apache Ignite

Apache Ignite is a distributed database, caching, and processing platform for transactional, analytical, and streaming workloads. Natively support most caching strategies.

Apache Ignite is licensed under the Apache License 2.0. This is a permissive free software license, allowing for the use, modification, and distribution of the software for any purpose without concern for royalties.

Pros:

Versatility: Serves as a database and a caching solution.

SQL support: Provides SQL support with in-memory speeds.

Scalability: Highly scalable and fault-tolerant.

Cons:

Complex setup: More complex to set up compared to simpler caching solutions.

Resource intensive: Requires significant resources for larger setups.

Apache Ignite is not available in IPC as a managed service.

Memcached

Memcached is a high-performance, distributed memory object caching system, intended for use in speeding up dynamic web applications by alleviating database load. Being a simpler key-value store, it inherently supports lazy loading and TTL, but requires external implementation or additional logic for other caching strategies.

Memcached is open-source and is under the Revised BSD license. Similar to Redis, the Revised BSD license is very permissive, allowing for wide use with minimal restrictions.

Pros:

Simplicity: Easy to set up and use.

Speed: Offers fast access to cached data.

Memory efficiency: Uses memory efficiently for caching.

Cons:

No persistence: Does not offer data persistence.

Limited sata structures: Primarily supports simple key-value storage.

Scalability: Less advanced clustering and scalability options compared to Redis.

Memcached is not available in IPC as a managed service.

Hazelcast

Hazelcast is an in-memory computing platform that provides distributed data structures and computing utilities. It natively supports most caching strategies.

The Hazelcast Community Edition is under Apache License 2.0. This is a permissive open-source license, allowing for free use and distribution. The Enterprise Edition is under commercial license, requires purchasing a license and typically offers additional features and support.

Pros:

High availability: Offers clustering and partitioning for high availability.

Data structures: Supports a wide range of distributed data structures.

Compute capabilities: Provides distributed computing capabilities like distributed java.util.{Queue, Set, List, Map}.

Cons:

Complexity: Can be complex to set up and manage.

Memory intensive: Similar to Redis, it can be memory-intensive.

-->
