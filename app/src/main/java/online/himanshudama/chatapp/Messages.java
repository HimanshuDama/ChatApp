package online.himanshudama.chatapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Messages {

    public String messageText, senderUserName, type, messageImg;
    private Date createdAt;

    public Messages() {}



    public Messages(String messageText, String messageImg, Date createdAt, String senderUserName, String type ) {
        this.messageText = messageText;
        this.createdAt = createdAt;
        this.senderUserName = senderUserName;
        this.type = type;
        this.messageImg = messageImg;


    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderID) {
        this.senderUserName = senderID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageImg() {
        return messageImg;
    }

    public void setMessageImg(String messageImg) {
        this.messageImg = messageImg;
    }

}
