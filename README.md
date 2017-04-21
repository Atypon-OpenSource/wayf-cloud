# WAYF Cloud
## Introduction
The where-are-you-from (WAYF) service is intended to reduce, or eliminate where possible, the friction that users/researchers experience as they try to access content on different publisher platforms where they are presented with a very large number of access options (e.g login, signup, institutional access, Social Logins etc). This goal is achieved by establishing an architecture and methods where content providers can access information about the user's browsing/authentication history and make intelligent decisions about the options which will allow the users to access their content, thus reducing the access options to a minimal set that apply for the given user profile.

## Solution Overview
### API
The wayf-cloud is accessible via a RESTful API. The inbound requests are serviced with [vert.x](http://vertx.io), a lightweight event-driven framework focused on throughput. Vert.x listens for inbound requests on an event loop. These events are immediately consumed and moved off of the event loop, freeing it up to accept more requests. After processing, the response is able to be written on any available thread. Due to powerful design but small footprint, vert.x is able to process thousands of requests per second.

### Reactive Back-end
Each client request is processed asynchronously on the back-end via the use of [ReactiveX](http://reactivex.io/). ReactiveX provides a functional framework for processing requests via observable streams. Asynchronous processing and concurrency are central to the library's implmentation. This abstracts away low-level threading and concurrency issues while exposing useful hooks in deciding where to execute logic. For instance, long running IO bound work, such as database reads, can be performed in an unbounded IO threadpool. Work that is more CPU intensive can be performed in a threadpool optimized for the machine's processor. The end result is an implemantation that is concise, easy to read, and maximizes performance.

### Graph Database
Due to the organic and interconnected nature of the underlying dataset, [Neo4J](https://neo4j.com/product/) is leveraged as the persistence layer. Not only are discrete data objects stored and retrievable, the relationships between them are persisted as well. This allows for a holistic understanding of a user and their data.

## What's Next
* Continue adding feature to fully support the system's requirements
* Mature exsiting architectural patterns
* Secure the solution
* Expand test coverage
* Document API (likely with Swagger)
