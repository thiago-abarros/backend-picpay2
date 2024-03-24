<h1 align="center">
  PicPay Backend Senior Challenge
</h1>

> **Important note:** this project was made originally by [@Giuliana Bezerra](https://github.com/giuliana-bezerra), on her video [Picpay simplificado com Java e Spring Boot!](https://youtu.be/YcuscoiIN14), this repository is only for study reasons, thank you in advance!

A web email service project made in Spring for the PicPay Challenge, for studying architectural patterns and notification services.

# Technologies

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)
- [Docker Compose](https://docs.docker.com/compose/)
- [H2](https://www.h2database.com/html/main.html)

# How to run

- Clone the git repository
```
git clone https://github.com/thiago-abarros/backend-picpay2.git
```
- Run Kafka:
```
docker-compose up
```
- Run the Spring Boot Application
- Access application in `http://localhost:8080`.

# Architecture
![Architectural Drawing](.github/Architectural%20Drawing.png)
![Activity Diagram](.github/Activity%20Diagram.png)

# Architectural Patterns and Explanations

## Synchronous and asynchronous processes

### Definition of Synchronous Processes

Synchronous execution means the first task in a program must finish processing before moving on to executing the next task whereas asynchronous execution means a second task can begin executing in parallel, without waiting for an earlier task to finish.

> **Example:** You want a burger and decide to go to McDonald's. After you order the burger at the counter, you are told to wait as your burger is prepared. In this synchronous situation, you are stuck at the counter until you are served your burger.

Here's a diagrammatic view of how synchronous processing works:
![Synchronous Processing](.github/Synchronous%20Processing.png)

### Definition of Asynchronous Processes

Asynchronous processing provides a means of distributing the processing that is required by an application between systems in an intercommunication environment. Unlike distributed transaction processing, however, the processing is asynchronous.

In distributed transaction processing, a session is held by two transactions for the period of a “conversation” between them, and requests and replies can be directly correlated.

In asynchronous processing, the processing is ``independent of the sessions on which requests are sent and replies are received``. No direct correlation can be made between a request and a reply, and no assumptions can be made about the timing of the reply.

> **Example:** You want a burger and decide to go to Five Guys. You go to the counter and order a burger. Five Guys gives you a buzzer that will notify you once your burger is ready. In this asynchronous situation, you have more freedom while you wait.

Here's a diagrammatic view of how asynchronous processing works:
![Asynchronous Processing](.github/Asynchronous%20Processing.png)

In general, asynchronous processing is applicable to any situation in which it is **not necessary or desirable to tie up local resources while a remote request is being processed**.

Asynchronous processing is not suitable for applications that involve synchronized changes to local and remote resources; for example, it cannot be used to process simultaneous linked updates to data split between two systems.

### Usage in the application

When we work with something unstable, the ideal is to work with an **``asynchronous procedure``**. Because if we make our transaction creation dependent on a procedure that is unstable, there will probably be ``many rollbacks`` in the application, we will have rollbacks happening very commonly. 

And that's why in this solution, the icing on the cake is to put in an ``asynchronous notification service`` so that the transaction is created and the notification is **eventually sent**. We don't need to stop creating the transaction because we can't create the notification.

This is different from the authentication service, we can only consolidate the transaction if it responds ok, so we will maintain the **``authorizing service synchronously``**, that is, we will wait for it to respond to consolidate the transaction.

## API

- http :8080/transaction value=100.0 payer=1 payee=200
```
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 05 Mar 2024 19:07:52 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "createdAt": "2024-03-05T16:07:50.749774",
    "id": 20,
    "payee": 2,
    "payer": 1,
    "value": 100.0
}
```

- http :8080/transaction
```
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 05 Mar 2024 19:08:13 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

[
    {
        "createdAt": "2024-03-05T16:07:50.749774",
        "id": 20,
        "payee": 2,
        "payer": 1,
        "value": 100.0
    }
]
```