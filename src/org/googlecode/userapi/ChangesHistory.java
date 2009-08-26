package org.googlecode.userapi;

public class ChangesHistory {
    private long messagesCount;
    private long friendsCount;
    private long photosCount;

    public ChangesHistory(long messagesCount, long friendsCount, long photosCount) {
        this.messagesCount = messagesCount;
        this.friendsCount = friendsCount;
        this.photosCount = photosCount;
    }

    public long getMessagesCount() {
        return messagesCount;
    }

    public long getFriendsCount() {
        return friendsCount;
    }

    public long getPhotosCount() {
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
