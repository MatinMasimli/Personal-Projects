package test;

import cs735_project.Record;
import cs735_project.ThumbsUpLocal;
import cs735_project.common.RemoteThumbsUp;
import cs735_project.common.RemoteUser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.testng.Assert.*;

public class UserSuite {

    RemoteThumbsUp remoteThumbsUp;
    ThumbsUpLocal server;

    @BeforeMethod
    void setup() throws RemoteException {
        server = new ThumbsUpLocal("TestBank");
        remoteThumbsUp = (RemoteThumbsUp) ThumbsUpLocal.toStub(server);
    }

    @AfterMethod
    void teardown() throws RemoteException{
        ThumbsUpLocal.unexportObject(server, false);
    }

    @Test(description = "RemoteUser operation sequential, follow vs unfollow")
    public void test1() throws RemoteException {
        int numUsers = 3;
        int numStatuses = 2;
        List<RemoteUser> users = new ArrayList<>(numUsers);
        List<Record> publishStatuses = new ArrayList<>(numStatuses * numUsers);

        for(int i = 0; i < numUsers; i++){
            users.add(remoteThumbsUp.getRemoteUser(server.createUser()));
        }

        for(RemoteUser user : users){
            for(int i = 0; i < numStatuses; i++){
                publishStatuses.add(user.publishStatus(" status " + i));
            }
        }

        publishStatuses.forEach(status -> System.out.println(status.toString()));
        System.out.println("------------");

        users.get(0).follow((users.get(1)).getUserId());
        users.get(1).follow((users.get(0)).getUserId());
        users.get(0).follow((users.get(2)).getUserId());

        assertEquals(users.get(0).showAllPublicStatus().size(), 4);

        assertEquals(users.get(0).allFollowList().size(), 2);
        users.get(0).unfollow((users.get(1)).getUserId());
        assertEquals(users.get(0).allFollowList().size(), 1);
        assertEquals(users.get(0).showAllPublicStatus().size(), 2);

        assertEquals(users.get(0).allOfYourFollowersList().size(), 1);  // followers of 0
        assertEquals(users.get(2).allOfYourFollowersList().size(), 1);  // followers of 2
        assertEquals(users.get(1).allOfYourFollowersList().size(), 0);  // followers of 1

        users.get(0).unfollow((users.get(2)).getUserId());
        users.get(2).follow((users.get(0)).getUserId());
        users.get(2).follow((users.get(1)).getUserId());
        assertEquals(users.get(0).showAllPublicStatus().size(), 0);

        assertEquals(users.get(2).allFollowList().size(), 2);
        assertEquals(users.get(0).allFollowList().size(), 0);


    }

    @Test(description = "RemoteUser operation sequential, like vs unlike")
    public void test2() throws RemoteException {
        int numUsers = 4;
        int numStatuses = 2;
        List<RemoteUser> users = new ArrayList<>(numUsers);
        List<Record> publishStatuses = new ArrayList<>(numStatuses * numUsers);

        for(int i = 0; i < numUsers; i++){
            users.add(remoteThumbsUp.getRemoteUser(server.createUser()));
        }

        for(RemoteUser user : users){
            for(int i = 0; i < numStatuses; i++){
                publishStatuses.add(user.publishStatus(" status " + i));
            }
        }

        publishStatuses.forEach(status -> System.out.println(status.toString()));
        System.out.println("------------");

        for(int i = 0; i < numUsers - 1; i++){
            users.get(i).follow((users.get(i+1)).getUserId());
            if(i + 1 == numUsers - 1){
                users.get(i + 1).follow((users.get(0)).getUserId());
            }
            System.out.println(users.get(i).getUserId() + " : " + users.get(i+1).getUserId());
        }

        System.out.println("********");
        for(int i = numUsers - 1; i > 0; i--){
            System.out.println(users.get(i).getUserId() + " : " + users.get(i-1).getUserId());
            users.get(i).follow((users.get(i-1)).getUserId());
            if(i - 1 == 0){
                users.get(i-1).follow((users.get(numUsers - 1)).getUserId());
            }
        }

        System.out.println("********");
        for(int i = 0; i < numUsers; i++){
            assertEquals(users.get(i).allFollowList().size(), 2);
            assertEquals(users.get(i).allOfYourFollowersList().size(), 2);
        }
    }

    @Test(description = "RemoteUser operation sequential, like vs unlike")
    public void test3() throws Exception {
        long start = System.nanoTime();

        int numUsers = 1000;
    //    int numStatuses = 3;

        List<RemoteUser> users = new ArrayList<>(numUsers);
        List<Record> publishStatuses = new ArrayList<>();

        for(int i = 0; i < numUsers; i++){
            users.add(remoteThumbsUp.getRemoteUser(server.createUser()));
        }

        for(int i = 1; i < numUsers; i++){
            users.get(i).follow((users.get(0)).getUserId());
        }

        users.get(0).publishStatus("status: " + 0);

        users.get(1).likeStatus((users.get(0)).getUserId(), 1L);
        users.get(2).unlikeStatus((users.get(0)).getUserId(), 1L);
        users.get(3).likeStatus((users.get(0)).getUserId(), 1L);
        users.get(4).unlikeStatus((users.get(0)).getUserId(), 1L);

        assertEquals(users.get(0).showAllMyStatusList().get(0).getLikeCount().get(), 2);
        assertEquals(users.get(0).showAllMyStatusList().get(0).getUnlikeCount().get(), 2);

        long endTime = System.nanoTime() - start;
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime);
        System.out.println("sequential endTime: " + duration);
    }


    @Test(description = "RemoteUser operation parallel, like vs unlike")
    public void test6() throws Exception {
        long start = System.nanoTime();
        int numUsers = 11;
        int numThreads = 5;
        List<RemoteUser> users = new ArrayList<>(numUsers);
        List<Future<String>> futures = new java.util.ArrayList<>(numUsers);
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);

        for(int i = 0; i < numUsers; i++){
            users.add(remoteThumbsUp.getRemoteUser(server.createUser()));
        }

        users.get(0).publishStatus("status: " + 0);

        for(int i = 1; i < numUsers; i++){
            users.get(i).follow((users.get(0)).getUserId());
        }

        for(int i = 0; i < numThreads; i++) {
            Future<String> future = exec.submit(() -> {
                for(int j = 1; j < numUsers; j++){
                    if(j % 2 == 0){
                        users.get(j).likeStatus((users.get(0)).getUserId(), 1L);
                    }
                    else{
                        users.get(j).unlikeStatus((users.get(0)).getUserId(), 1L);
                    }
                }
                return null;
            });

            futures.add(future);
        }

        exec.shutdown();
        for (Future<String> f : futures) {
            f.get();
        }

        assertEquals(users.get(0).showAllMyStatusList().get(0).getLikeCount().get(), 25);   //11,5(25) ;  // 5,5(10);
        assertEquals(users.get(0).showAllMyStatusList().get(0).getUnlikeCount().get(), 25);

        exec.awaitTermination(5, TimeUnit.SECONDS);

        long endTime = System.nanoTime() - start;
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime);
        System.out.println("parallel endTime: " + duration);
    }


    @Test(description = "RemoteUser operation parallel, multiple statuses published with " +
            "more threads and users, like vs unlike")
    public void test4() throws Exception {
        int numUsers = 61;
        int numThreads = 100;
        int numStatus = 10;
        List<RemoteUser> users = new ArrayList<>(numUsers);
        List<Future<String>> futures = new java.util.ArrayList<>(numUsers);
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);

        for(int i = 0; i < numUsers; i++){
            users.add(remoteThumbsUp.getRemoteUser(server.createUser()));
        }

        for(int i = 0; i < numStatus; i++){
            users.get(0).publishStatus("status: " + i);
        }

        assertEquals(users.get(0).showAllMyStatusList().get(0).getLikeCount().get(), 0);
        assertEquals(users.get(0).showAllMyStatusList().get(0).getUnlikeCount().get(), 0);

        for(int i = 1; i < numUsers; i++){
            users.get(i).follow((users.get(0)).getUserId());
        }

        assertEquals(users.get(0).allOfYourFollowersList().size(), numUsers - 1);

        for(int i = 0; i < numThreads; i++) {
            Future<String> future = exec.submit(() -> {
                for (int j = 1; j < numUsers; j++) {
                    if (j < 11) {
                        users.get(j).likeStatus((users.get(0)).getUserId(), 1L);
                    } else if (j < 31) {
                        users.get(j).likeStatus((users.get(0)).getUserId(), 3L);
                    } else {
                        users.get(j).unlikeStatus((users.get(0)).getUserId(), 5L);
                    }
                }
                return null;
            });

            futures.add(future);
        }

        exec.shutdown();
        for (Future<String> f : futures) {
            f.get();
        }

        assertEquals(users.get(0).showAllMyStatusList().get(0).getLikeCount().get(), 1000);
        assertEquals(users.get(0).showAllMyStatusList().get(2).getLikeCount().get(), 2000);
        assertEquals(users.get(0).showAllMyStatusList().get(4).getUnlikeCount().get(), 3000);
    }

}
