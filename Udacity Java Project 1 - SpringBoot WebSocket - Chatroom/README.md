# Chat Room
This chat room application is implemented with SpringBoot WebSocket and Thymleaf dependencies. Maven is used to manage all the significant dependencies.

## Background
WebSocket is a communication protocol that makes it possible to establish a two-way communication channel between a server and a client.

### Notice
Backend(Server Side) @ServerEndPoint("/chat/{username}") must matches Frontend:(Client Side) WebSocket URL: 'ws://localhost:8080/chat/`username`';

## Functions 
Users can join the Chat Room after entering their username `localhost:8080/` and send messages to the Chat Room, which will be seen by all participants. Also, they will see who join or leave the Chat Room. 

## Reference 
1.[A Guide to the Java API for WebSocket](https://www.baeldung.com/java-websockets)



