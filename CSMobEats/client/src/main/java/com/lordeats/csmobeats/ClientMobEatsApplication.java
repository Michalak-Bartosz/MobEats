package com.lordeats.csmobeats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@Slf4j
public class ClientMobEatsApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new SpringApplicationBuilder(ClientMobEatsApplication.class).web(WebApplicationType.NONE).run(args);

        var client = new StandardWebSocketClient();
        var stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new StringMessageConverter());

        var connection = stompClient.connect("ws://localhost:8080/chat", new StompSessionHandler() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectHeaders) {
                session.subscribe("/topic/messages",this);
                session.subscribe("user/queue/registrationName", this);

            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("Communication error: ", exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("Transport layer error: ", exception);
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                if("/topic/messages".equals(headers.getDestination())){
                    log.info(payload + "");
                }else if("user/queue/registrationName".equals(headers.getDestination())){
                    log.info("Users on chat: " + payload + "");
                }
            }
        });

        var sc = new Scanner(System.in);
        var s = "";

        log.info("Podaj nazwe użytkownika: ");
        s = sc.nextLine();
        connection.get().send("/app/name", s);
        log.info("ChatData");

        do {
            s = sc.nextLine();
            connection.get().send("/app/chat", s);
        }while(!sc.nextLine().startsWith("q!"));
    }

}
