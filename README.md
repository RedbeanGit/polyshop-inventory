# Inventory

## Description

The Inventory microservice is responsible for maintaining the list of available products, storing only the quantity and identifier for each product. It was developed as part of a course project on creating microservices using RabbitMQ for communication between services.

## Technologies Used

- Programming Language: Java with Spring Boot
- Database: MongoDB
- Message Queue: RabbitMQ
- Containerization: Docker

## Installation and Configuration

1. Clone the GitHub repository:

```bash
git clone git@github.com:RedbeanGit/polyshop-inventory.git
```

2. Install Docker and Docker Compose on your machine if you haven't already. You can follow the installation instructions on Docker's official website: https://docs.docker.com/get-docker/ and https://docs.docker.com/compose/install/.

3. Navigate to the Inventory microservice directory:

```bash
cd polyshop-inventory
```

4. Launch Docker Compose to start the necessary containers:

```bash
docker-compose up -d
```

**Now you can choose to run the Inventory service inside a docker container or directly on your host.**

### Running with docker

5. Build the Docker image for the microservice using the provided Dockerfile:

```bash
docker build -t polyshop-inventory .
```

6. Run the container from the image you have just builded:

```bash
docker run --name polyshop_inventory polyshop-inventory
```

### Running on host

5. Start Spring Boot application:

```bash
./mvnw spring-boot:run
```

## API

List of routes/API endpoints available for this microservice:

- **GET** /products : Retrieves all products.
- **GET** /products/{productId} : Retrieves a specific product.
- **POST** /products : Creates a new product.
- **PUT** /products/{productId} : Updates a specific product.

## Message Queue

The Inventory microservice listens for messages from the Order microservice and sends events upon successful or unsuccessful stock updates.
