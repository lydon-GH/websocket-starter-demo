package com.lydon.test;

import com.sun.security.auth.UserPrincipal;
import com.lydon.websocket.annotation.TgWebSocketInterceptor;
import com.lydon.websocket.subscribe.WebSocketInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Map;

/**
 * @author liuyd
 */
@TgWebSocketInterceptor
public class MyWebSocketInterceptor implements WebSocketInterceptor {

    /**
     * 绑定user到websocket conn上
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("----------------------:");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                System.out.println(raw);
            }
            System.out.println(">>>>>>>>>>>>>>");
            String username = accessor.getFirstNativeHeader("username");
            String token = accessor.getFirstNativeHeader("token");

            System.out.println("token:" + token);

            if (StringUtils.isEmpty(username)) {
                return null;
            }
            // 绑定user
            Principal principal = new UserPrincipal(username);
            accessor.setUser(principal);
        }
        return message;
    }
}
