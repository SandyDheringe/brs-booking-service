package com.brsbooking.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class MessageBroker {

    private final JmsTemplate jmsTemplate;


    @Autowired
    public MessageBroker(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendBookingMessage(String destination, BookingMessage bookingMessage) {
        jmsTemplate.convertAndSend(destination, bookingMessage);
        System.out.println("Sent message: " + bookingMessage);
    }
}
