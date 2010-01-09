package org.googlecode.userapi;

public class ChangesHistory implements Comparable<ChangesHistory> {
    private int messagesCount;
    private int friendsCount;
    private int photosCount;

    public ChangesHistory() {
        messagesCount = 0;
        friendsCount = 0;
        photosCount = 0;
    }

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

    /**
     * Compares this changesHistory with newer one from server.
     *
     * @param that newer changesHistory.
     * @return -1 as specified changesHistory hasn't new changes, 1 - has got new changes, 0 - nothing changed.
     */
    @Override
    public int compareTo(ChangesHistory that) {
        if (that == null)
            return 1;

        if (this.messagesCount == that.messagesCount && this.friendsCount == that.friendsCount &&
                this.photosCount == that.photosCount)
            return 0;

        if (this.messagesCount < that.messagesCount || this.friendsCount < that.friendsCount ||
                this.photosCount < that.photosCount)
            return -1;
        else
            return 1;
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
