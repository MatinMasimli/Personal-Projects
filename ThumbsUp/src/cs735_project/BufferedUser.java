package cs735_project;

import cs735_project.common.RemoteUser;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BufferedUser implements Serializable{

    private RemoteUser bufferUser;
    private Long userId;
    private AtomicLong likeCnt = new AtomicLong();
    private AtomicLong unlikeCnt = new AtomicLong();
    private List<RemoteUser> followedUsersList = new ArrayList<RemoteUser>();
    private List<RemoteUser> followingUsersList = new ArrayList<RemoteUser>();
    private List<Status> myStatusList = new ArrayList<>();
    private AtomicLong statusIdGenerator = new AtomicLong();
    private AtomicBoolean isFollowing = new AtomicBoolean(false);
    private AtomicBoolean publishedStatus = new AtomicBoolean( false);

    public BufferedUser(RemoteUser user) throws RemoteException{
        bufferUser = user;
        userId = bufferUser.getUserId();
        if(isFollowing.get() && publishedStatus.get()) {
            myStatusList = user.showAllMyStatusList();
            followedUsersList = user.allFollowList();
            followingUsersList = user.allOfYourFollowersList();
        }
    }

    public long userId(){
        return userId;
    }

    public void follow(Long otherUserId){
        Database.follow(this.userId, otherUserId);
        isFollowing.set(true);
    }

    public void unfollow(Long otherUserId){
        Database.unfollow(this.userId, otherUserId);
    }

    public void likeStatus(Long userId, Long statusId){
    //    String response = "Status Not Found";
        int myStatusListSize = myStatusList.size();

        for(int i = 0; i < myStatusListSize; i++){

            long likeCounts = myStatusList.get(i).getLikeCount().get();
            long unlikeCounts = myStatusList.get(i).getLikeCount().get();
            if(likeCounts < unlikeCounts){
                long diff = unlikeCounts - likeCounts;
                while(diff > 0){
                    likeCnt.incrementAndGet();
                    diff--;
                }

            }

        }
    }

    public void unlikeStatus(Long userId, Long statusId){
       for(Status remoteStatus : myStatusList){

           long likeCounts = remoteStatus.getLikeCount().get();
           long unlikeCounts = remoteStatus.getUnlikeCount().get();
           if(likeCounts > unlikeCounts){
               long diff = likeCounts - unlikeCounts;
               while(diff > 0){
                   unlikeCnt.incrementAndGet();
                   diff--;
               }
           }

       }
    }

    public void publishStatus(String statusText) throws RemoteException{
        Database.publishStatus(this.getUserId(), statusText);
        publishedStatus.set(true);
        System.out.println("Buffers Status: " + statusText);
    }

    public void showAllStatus(){
        List<String> statuses = Database.showAllStatuses(this.userId);
        statuses.forEach(status -> System.out.println(status.toString()));
    }

    public void sync() throws RemoteException{
        for(Status remoteStatus : myStatusList){
            long likeCounts = remoteStatus.getLikeCount().get();
            long unlikeCounts = remoteStatus.getUnlikeCount().get();

            if(likeCounts < unlikeCounts){
                while(likeCnt.getAndDecrement() != 0){
                    remoteStatus.like();
                }
            }
            else if(likeCounts > unlikeCounts){
                while(unlikeCnt.getAndDecrement() != 0){
                    remoteStatus.unlike();
                }
            }

        }
    }

    public Long getUserId() {
        return userId;
    }
}
