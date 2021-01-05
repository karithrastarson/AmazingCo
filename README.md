# AmazingCo

## Environment requirements
* Java 15
* Docker Compose
* Maven

## How to run
Navigate to the root of the project and then:
1. Run ``docker-compose up`` 
   to create the necessary containers
2.  Run ``mvn spring-boot:run`` to run service
3.  Query the endpoints

## Currently available endpoints
* ``GET /tree`` Returns the entire tree
* ``GET /tree/{id}`` Returns a specific
  node from the tree
* ``PUT /tree/{id}/:move`` moves a node. A
  node must be provided in the requestBody with
    the new value of the parentId
* ``POST /tree/{id}`` creates a new node under the
node with the id provided
    

## Technical stack
* MySQL is used for persisting the nodes
* Google Guava Loading Cache for storing
  cache for more speedy access to tree
* Spring Boot is used as a service framework
* Maven is used as a dependency framework