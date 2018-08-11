package cs735_project;

import cs735_project.common.RemoteStatus;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicLong;

public class Status  implements Serializable {

    private Long statusId;
    private String statusText;
    private Long userId;

    private AtomicLong likeCount;
    private AtomicLong unlikeCount;

    public Status(Long userId, Long statusId, String statusText) throws RemoteException{
        this.statusId = statusId;
        this.userId = userId;
        this.statusText = statusText;
        this.likeCount = new AtomicLong();
        this.unlikeCount = new AtomicLong();
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getStatusText() {
        return statusText;
    }

    public Long getUserId() {
        return userId;
    }

    public AtomicLong getLikeCount() {   // newly added exceptions & override
        return likeCount;
    }

    public AtomicLong getUnlikeCount() { // newly added exceptions & override
        return unlikeCount;
    }

    public void like() {
        likeCount.incrementAndGet();
    }

    public void unlike() {
        unlikeCount.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusId=" + statusId +
                ", statusText='" + statusText + '\'' +
                ", userId=" + userId +
                ", likeCount=" + likeCount +
                ", unlikeCount=" + unlikeCount +
                '}';
    }
}
