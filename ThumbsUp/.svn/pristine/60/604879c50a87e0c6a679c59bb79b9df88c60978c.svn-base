package cs735_project;

import cs735_project.operations.*;

public abstract class Operation implements java.io.Serializable{

    public static Operation publishStatus(String content){
        if(content.isEmpty()){
            throw new IllegalArgumentException();
        }
        return new PublishStatusOperation(content);
    }

    public static Operation follow(Long otherUserId){
        if(otherUserId == null){
            throw new IllegalArgumentException();
        }
        return new FollowOperation(otherUserId);
    }

    public static Operation unfollow(Long otherUserId){
        if(otherUserId == null){
            throw new IllegalArgumentException();
        }
        return new UnFollowOperation(otherUserId);
    }

    public static Operation likeStatus(Long userId, Long statusId){
        if(userId == null && statusId == null){
            throw new IllegalArgumentException();
        }
        return new LikeOperation(userId, statusId);
    }

    public static Operation unlikeStatus(Long userId, Long statusId){
        if(userId == null && statusId == null){
            throw new IllegalArgumentException();
        }
        return new UnlikeOperation(userId, statusId);
    }

    public static Operation showAllStatus(){
        return new ShowAllStatusOperation();
    }
}
