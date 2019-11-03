package edu.udacity.java.nano.chat;

import com.alibaba.fastjson.JSON;
import edu.udacity.java.nano.chat.model.Message;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

// https://www.baeldung.com/java-websockets

//@ServerEndPoint
//the container ensures availability of the class as a WebSocket server listening to a specific client
@Component
@ServerEndpoint("/chat/{username}")
public class WebSocketChatServer {

    /**
     * All chat sessions
     * Using the thread-safe map to save the set of online sessions
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    /**
     * Open connection, 1) add session, 2) add user.
     *
     * @param username: WebSocket support the parameters
     * @Open the method invoked by the container when a new Websocket connection is initiated
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        //System.out.println("WebSocketChatServer --> onOpen() : username:" + username);
        onlineSessions.put(session.getId(), session);
        broadcast(Message.jsonStr(username, "has joined Chat Room", Message.ENTER, onlineSessions.size()));
    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     *
     * @param jsonStr sent from Client
     * @param session
     * @OnMessagge received information from the Websocket container when a message is sent to the endpoint
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr, @PathParam("username") String username) {
        //System.out.println("WebSocketChatServer --> onMessage() : username:" + username);
        //System.out.println("String jsonStr: "+jsonStr);
        Message message = JSON.parseObject(jsonStr, Message.class);
        //System.out.println(message.toString());
        broadcast(Message.jsonStr(message.getUsername(), message.getMessage(), Message.SPEAK, onlineSessions.size()));
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     *
     * @param session
     * @param username
     * @OnClose method be invoked when the websocket connection closes
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        //System.out.println("WebSocketChatServer --> onClose() : username:" + username);
        onlineSessions.remove(session.getId());
        //broadcast(username + " has left the Chat Room");
        broadcast(Message.jsonStr(username, "has left the Chat Room", Message.LEAVE, onlineSessions.size()));
    }

    /**
     * Print exception.
     *
     * @param session
     * @OnError invoked when a problem with communication occurred
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("Error has happened!");
        error.printStackTrace();
    }

    /**
     * Send messages in a session
     */
    private static void sendMessage(Session session, String msg) {
        // System.out.println(">> sendMessage() : " + msg);
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast messages
     */
    private static void broadcast(String msg) {
        onlineSessions.forEach((id, session) -> {
            sendMessage(session, msg);
        });
    }
}
