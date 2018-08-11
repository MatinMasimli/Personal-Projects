package cs735_project;

import cs735_project.common.RemoteStatus;
import cs735_project.common.RemoteUser;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class User extends UnicastRemoteObject implements RemoteUser, Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private boolean isActive;


    public User(Long userId) throws RemoteException{
        super();
        this.userId = userId;
        this.isActive = true;
    }

    @Override
    public Long getUserId() throws RemoteException{   // throws RemoteException is newly added
        return userId;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public Record publishStatus(String statusText) throws RemoteException {
        Database.publishStatus(this.userId, statusText);
        return new Record(String.format(Record.PUBLISH_STATUS_TYPE, userId, statusText));
    }

    @Override
    public Record follow(Long otherUserId) throws RemoteException {
        Database.follow(this.getUserId(), otherUserId);
        return new Record(String.format(Record.FOLLOW_STATUS_TYPE, userId ,otherUserId));
    }

    @Override
    public Record unfollow(Long otherUserId) throws RemoteException {
        Database.unfollow(this.userId, otherUserId);
        return new Record(String.format(Record.UNFOLLOW_STATUS_TYPE, userId, otherUserId));
    }

    @Override
    public Record likeStatus(Long userId, Long statusId) throws RemoteException {
        String status = Database.like(userId, statusId);
        return new Record(String.format(Record.STATUS_TYPE, status));
    }

    @Override
    public Record unlikeStatus(Long userId, Long statusId) throws RemoteException {
        String status = Database.unlike(userId, statusId);
        return new Record(String.format(Record.STATUS_TYPE, status));
    }

    @Override
    public Record showAllStatus() throws RemoteException {
        List<String> subStatusList = Database.showAllStatuses(this.getUserId());
        return new Record(subStatusList);
    }

    protected void deactivateAccount(){
        synchronized (this){
            isActive = false;
        }
    }

    protected boolean getIsActive(){
        return isActive;
    }
/*
     protected void updateFollower(User otherUser) throws RemoteException {
        if( otherUser != null && otherUser.getUserId() != 0 ){
            allOfYourFollowers.putIfAbsent(otherUser.getUserId(), otherUser);
        }
    }

    public Integer getNumOfAllFollowers(){
        return allOfYourFollowers.size();
    }

    public Integer getNumOfAllUsersYouFollow(){
        return followList.size();
    }

    public ConcurrentHashMap<Integer, Status> getStatusMap() {
        return statusMap;
    }

*/

    @Override
    public List<String> showAllPublicStatus() throws RemoteException{   // newly added method
        return Database.showAllStatuses(this.getUserId());
    }

    @Override
    public List<RemoteUser> allFollowList() throws RemoteException{   // newly added method
        return Database.allFollowList(this.getUserId());
    }

    @Override
    public List<RemoteUser> allOfYourFollowersList() throws RemoteException{   // newly added method
        return Database.allOfYourFollowerList(this.getUserId());
    }

    @Override
    public List<Status> showAllMyStatusList() throws RemoteException{
        return Database.showAllMyStatusList(this.getUserId());
    }

    public Long getUserId(User user) throws RemoteException{
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", isActive=" + isActive +
                '}';
    }
}
