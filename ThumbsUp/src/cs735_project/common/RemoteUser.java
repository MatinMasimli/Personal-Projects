package cs735_project.common;

import cs735_project.Record;
import cs735_project.Status;

import java.rmi.RemoteException;
import java.util.List;

public interface RemoteUser extends java.rmi.Remote {
    Record publishStatus(String status) throws RemoteException;
    Record follow(Long otherUserId) throws RemoteException;
    Record unfollow(Long otherUserId) throws RemoteException;
    Record likeStatus(Long userId, Long statusId) throws RemoteException;
    Record unlikeStatus(Long userId, Long statusId) throws RemoteException;
    Record showAllStatus() throws RemoteException;

    // newly added method
    Long getUserId() throws RemoteException;
    List<RemoteUser> allFollowList() throws RemoteException;
    List<RemoteUser> allOfYourFollowersList() throws RemoteException;

    List<String> showAllPublicStatus() throws RemoteException;
    List<Status> showAllMyStatusList() throws RemoteException;
}
