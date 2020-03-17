package com.lydon.test;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.*;
import java.util.*;

/**
 * @author liuyd
 */
@Slf4j
@Controller
public class GreetingController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 后台主动发送消息,所有订阅到/message/public的客户端都会受到此消息。
     * @param
     */
    @RequestMapping("/notifyHello")
    @ResponseBody
    public void notifyHello() {
        com.lydon.websocket.subscribe.Message message=new com.lydon.websocket.subscribe.Message();
        message.setContent("后台通知消息");
        message.setFrom("admin");
        System.out.println(message);
        simpMessagingTemplate.convertAndSend( "/message", message);
    }
    /**
     * 后台主动发送消息,局部订阅收到此消息。
     * @param
     */
    @RequestMapping("/sendToUser")
    @ResponseBody
    public void sendToUser(String userName) {
        com.lydon.websocket.subscribe.Message message=new com.lydon.websocket.subscribe.Message();
        message.setContent("后台通知消息");
        message.setFrom("admin");
        message.setTo(userName);
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/notice", message);
    }

    /**
     * 测试订阅
     * @param message
     * @param messageHeaders
     * @param destination
     * @param headers
     * @param id
     * @param body
     */
    @MessageMapping("/hello/{id}")
    public void hello(Message message,
                      MessageHeaders messageHeaders,
                      @Header("destination") String destination,
                      @Headers Map<String, Object> headers,
                      @DestinationVariable long id,
                      @Payload String body) {
        log.info("message:{}", message);
        log.info("messageHeaders:{}", messageHeaders);
        log.info("destination:{}", destination);
        log.info("headers:{}", headers);
        log.info("id:{}", id);
        log.info("body:{}", body);
    }


    /***  群消息   ***/

    /**
     * 主动返回消息。
     * @param message
     */
    @MessageMapping("/hello")
    public void hello(@Payload com.lydon.websocket.subscribe.Message message) {
        System.out.println(message);
        com.lydon.websocket.subscribe.Message returnMessage = new com.lydon.websocket.subscribe.Message();
        returnMessage.setContent("转发，" + message.getContent());
        simpMessagingTemplate.convertAndSend("/message/", returnMessage);
        //simpMessagingTemplate.convertAndSendToUser("tom","/notice", returnMessage);
    }

    /**
     * 使用注解的方式返回消息
     * @param message
     * @return
     */
    @MessageMapping("/hello1")
    @SendTo("/message/public")
    public com.lydon.websocket.subscribe.Message hello1(@Payload com.lydon.websocket.subscribe.Message message) {
        System.out.println(message);
        com.lydon.websocket.subscribe.Message returnMessage = new com.lydon.websocket.subscribe.Message();
        returnMessage.setContent("转发2，" + message.getContent());
        return returnMessage;
    }

    /***  点对点   ***/

    /**
     * 点对点发送消息。接收消息的人是从消息中获取的。
     * @param message
     * @param principal
     */
    @MessageMapping("/hello2")
    public void hello2(@Payload com.lydon.websocket.subscribe.Message message, Principal principal) {
        System.out.println(message);
        System.out.println(principal);
        com.lydon.websocket.subscribe.Message returnMessage = new com.lydon.websocket.subscribe.Message();
        returnMessage.setContent("转发3，" + message.getContent());
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/notice/msg", returnMessage);
    }

}
