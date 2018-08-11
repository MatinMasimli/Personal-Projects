package cs735_project;

import cs735_project.common.RemoteStatus;

import java.io.Serializable;
import java.util.List;

public class Record implements Serializable {

    private static final long serialVersionUID = 8642569678725441074L;

    static final String PUBLISH_STATUS_TYPE = "user %s published status %s";
    static final String FOLLOW_STATUS_TYPE = " user %s follows user %s";
    static final String UNFOLLOW_STATUS_TYPE = "user %s unfollows user %s";
    static final String STATUS_TYPE = "%s";

    private String message;
    private List<String> statusList;
    private StringBuilder stringBuilder = new StringBuilder();

    Record(String message){
        this.message = message;
    }

    Record(List<String> statusList){
        this.statusList = statusList;
    }

    @Override
    public String toString() {
       if(statusList != null){
            for(String status : statusList){
                stringBuilder.append(status).append("\n");
            }
            return stringBuilder.toString();
        }
        else {
            return message;
        }
    }
}


