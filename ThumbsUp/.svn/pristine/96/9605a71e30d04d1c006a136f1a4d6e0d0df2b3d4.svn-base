package cs735_project.common;

import cs735_project.BufferedUser;
import cs735_project.Operation;
import cs735_project.Record;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RemoteThumbsUp extends Remote{
    Long createUser() throws RemoteException;
    Long removeUser(long accountNumber) throws RemoteException;
    List<Long> getAllUsers() throws RemoteException;
    RemoteUser getRemoteUser(long accountNumber) throws RemoteException;
    BufferedUser getBufferedUser(long accountNumber) throws RemoteException;
    Record requestOperation(long userId, Operation operation) throws RemoteException;
    Map<Long, Long[]> currentStats() throws RemoteException;
}
