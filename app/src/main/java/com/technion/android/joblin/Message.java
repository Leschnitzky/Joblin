package com.technion.android.joblin;

import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private Timestamp timestamp;

    public Message() {}

    public Message(String sender,
                   String receiver,
                   String message,
                   Timestamp timestamp) {

        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message(Message message) {

        this.sender = message.sender;
        this.receiver = message.receiver;
        this.message = message.message;
        this.timestamp = message.timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String reciever) {
        this.receiver = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
