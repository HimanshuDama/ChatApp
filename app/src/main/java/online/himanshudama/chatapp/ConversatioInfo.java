package online.himanshudama.chatapp;

public class ConversatioInfo {

    public Integer unreadCount;

    public ConversatioInfo() {}

    public ConversatioInfo(Integer unreadCount) {
        this.unreadCount = unreadCount;

    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
}
