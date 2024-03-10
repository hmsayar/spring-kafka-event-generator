# Spring Boot Kafka Event Simulator
Event generator uses Java Faker to generate real-world-like predefined events and publishes them to kafka topic at random time intervals.
## Prerequisites

- Docker and Docker Compose

## Setup

1. Clone the repository
2. Navigate to project folder
3. Replace ```your_ip_address_not_localhost``` with your ip address in docker-compose and application.properties file.
4. ``docker-compose up -d``
5. Run the spring boot application.

## Usage

After running the application, you can interact with Kafka through various CLI commands. Here are some useful commands for common tasks:

### Accessing Kafka CLI

Access the Kafka CLI by entering the Kafka Docker container:

```
docker exec -it mykafka /bin/bash
```
List all topics
```
kafka-topics.sh --list --bootstrap-server localhost:9092
```
Get detailed info about topic
```
kafka-topics.sh --describe --topic mytopic --bootstrap-server localhost:9092
```
Consume messages and view which partition each message comes from and its keys
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic mytopic --from-beginning --property print.key=true --property print.partition=true
```