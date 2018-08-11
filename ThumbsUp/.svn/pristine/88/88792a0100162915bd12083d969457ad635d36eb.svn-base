package cs735_project;

import cs735_project.common.RemoteUser;
import cs735_project.common.RemoteThumbsUp;
import cs735_project.operations.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbsUpLocal extends UnicastRemoteObject implements RemoteThumbsUp {

    public final String name;

    public ThumbsUpLocal(String name) throws RemoteException {   // public is newly added here
        super();
        this.name = name;
    }

    @Override
    public Long createUser() throws RemoteException {
        RemoteUser newUser = Database.createUser();
        return ((User)newUser).getUserId();
    }

    @Override
    public Long removeUser(long userId) throws RemoteException {
        RemoteUser curUser = Database.removeUser(userId);
        return ((User)curUser).getUserId();
    }

    @Override
    public List<Long> getAllUsers() {
        return Database.getAllUsers();
    }

    @Override
    public RemoteUser getRemoteUser(long userId) throws RemoteException {
        return Database.getUser(userId);
    }

    public RemoteUser getLocalUser(long userId){
        return Database.getUser(userId);
    }

    @Override
    public BufferedUser getBufferedUser(long userId) throws RemoteException {
        RemoteUser remoteUser = Database.getUser(userId);
        return new BufferedUser(remoteUser);
    }

    @Override
    public Record requestOperation(long userId, Operation operation) throws RemoteException {
        RemoteUser remoteUser = Database.getUser(userId);

        if(operation instanceof FollowOperation){
            return remoteUser.follow(((FollowOperation) operation).getUserId());
        }

        else if(operation instanceof LikeOperation){
            return remoteUser.likeStatus(((LikeOperation) operation).getUserId(), ((LikeOperation) operation).getStatusId());
        }

        else if(operation instanceof PublishStatusOperation){
            return remoteUser.publishStatus(((PublishStatusOperation) operation).getContent());
        }

        else if(operation instanceof ShowAllStatusOperation){
            return remoteUser.showAllStatus();
        }

        else if(operation instanceof UnFollowOperation){
            return remoteUser.unfollow(((UnFollowOperation) operation).getUserId());
        }

        else if(operation instanceof UnlikeOperation){
            return remoteUser.unlikeStatus(((UnlikeOperation) operation).getUserId(), ((UnlikeOperation) operation).getStatusId());
        }

        return null;
    }

    @Override
    public Map<Long, Long[]> currentStats() throws RemoteException{
        return Database.returnStats();
    }

    @Override
    public String toString() {
        return "ThumbsUpLocal{" +
                "name='" + name + '\'' +
                '}';
    }
}
