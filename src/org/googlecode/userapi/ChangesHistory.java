package org.googlecode.userapi;

public class ChangesHistory {
    private int messagesCount;
    private int friendsCount;
    private int photosCount;

    public ChangesHistory(int messagesCount, int friendsCount, int photosCount) {
        this.messagesCount = messagesCount;
        this.friendsCount = friendsCount;
        this.photosCount = photosCount;
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    @Override
    public String toString() {
        return "ChangesHistory{" +
                "messagesCount=" + messagesCount +
                ", friendsCount=" + friendsCount +
                ", photosCount=" + photosCount +
                '}';
    }
}
