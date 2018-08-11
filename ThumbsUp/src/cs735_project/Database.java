package cs735_project;

import cs735_project.common.RemoteStatus;
import cs735_project.common.RemoteUser;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Database {
    private static ConcurrentHashMap<Long, RemoteUser> usersMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Status> statusMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, Set<Long>> followList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Set<Long>> allOfYourFollowers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Set<Long>> userStatusMap = new ConcurrentHashMap<>();

    private static AtomicLong userIdGenerator = new AtomicLong();
    private static AtomicLong statusIdGenerator = new AtomicLong();

    public static RemoteUser createUser() throws RemoteException {
        Long userId = userIdGenerator.incrementAndGet();
        RemoteUser newUser = new User(userId);
        usersMap.putIfAbsent(userId, newUser);
        return newUser;
    }

    public static RemoteUser removeUser(Long userId){
        if(!usersMap.containsKey(userId)){
            throw new IllegalArgumentException();
        }
        RemoteUser currentUser = usersMap.remove(userId);
        ((User)currentUser).deactivateAccount();
        return currentUser;
    }

    public static RemoteUser getUser(Long userId){
        if(!usersMap.containsKey(userId)){
            throw new IllegalArgumentException();
        }
        return usersMap.get(userId);
    }

    public static List<Long> getAllUsers() {
        List<Long> allUsers = new ArrayList<>(usersMap.keySet());
        Collections.sort(allUsers);
        return allUsers;
    }

    public static void follow(Long userId1, Long userId2){
        if(userId1 != null && userId2 != null){
            Set<Long> subFollowList = followList.get(userId1);
            if (subFollowList == null) {
                subFollowList = new HashSet<>();
            }

            subFollowList.add(userId2);
            followList.put(userId1, subFollowList);

            Set<Long> subAllOfYourFollowersList = allOfYourFollowers.get(userId2);
            if (subAllOfYourFollowersList == null) {
                subAllOfYourFollowersList = new HashSet<>();
            }

            subAllOfYourFollowersList.add(userId1);
            allOfYourFollowers.put(userId2, subAllOfYourFollowersList);
        }
    }

    public static void unfollow(Long userId1, Long userId2){
        if(userId1 != null && userId2 != null){
            Set<Long> subFollowList = followList.get(userId1);
            if (subFollowList != null && subFollowList.contains(userId2)) {
                subFollowList.remove(userId2);
            }

            Set<Long> subAllOfYourFollowers = allOfYourFollowers.get(userId2);
            if (subAllOfYourFollowers != null && subAllOfYourFollowers.contains(userId1)) {
                subAllOfYourFollowers.remove(userId1);
            }
        }
    }

    public static void publishStatus(Long userId, String statusText) throws RemoteException{
        Long statusId = statusIdGenerator.incrementAndGet();

        Status status = new Status(userId, statusId, statusText);
        statusMap.putIfAbsent(statusId, status);

        Set<Long> statusIdList = userStatusMap.get(userId);
        if (statusIdList == null) {
            statusIdList = new HashSet<>();
        }

        statusIdList.add(statusId);
        userStatusMap.put(userId, statusIdList);
    }

    public static List<String> showAllStatuses(Long userId){
        List<String> subStatusList = new ArrayList<>();
        Set<Long> allFollowings = followList.get(userId);

        if(allFollowings != null && !allFollowings.isEmpty()){
            for(Long followerId : allFollowings){
                Set<Long> statusIdList = userStatusMap.get(followerId);
                if (statusIdList != null) {
                    for (Long statusId : statusIdList) {
                        Status status = (Status) statusMap.get(statusId);
                        if (status != null) {
                            subStatusList.add(status.toString());
                        }
                    }
                }
            }
        }

        return subStatusList;
    }

    public static String like(Long userId, Long statusId) throws RemoteException{
        String response = "Status Not Found";

        Set<Long> userStatuses = userStatusMap.get(userId);
        if (userStatuses != null && userStatuses.contains(statusId)) {
            Status status = statusMap.get(statusId);
            status.like();
            response = status.toString();
        }

        return response;
    }

    public static String unlike(Long userId, Long statusId) throws RemoteException{
        String response = "Status Not Found";

        Set<Long> userStatuses = userStatusMap.get(userId);
        if (userStatuses != null && userStatuses.contains(statusId)) {
            Status status = statusMap.get(statusId);
            status.unlike();
            response = status.toString();
        }

        return response;
    }

    public static Map<Long, Long[]> returnStats() throws RemoteException{
        Map<Long, Long[] > map = new HashMap<>();

        for(Long userId : usersMap.keySet()){
            Long [] values = new Long[5];

            Set<Long> userFollowList = followList.get(userId);
            values[0] = userFollowList == null ? 0L : userFollowList.size();

            Set<Long> userFollowingList = allOfYourFollowers.get(userId);
            values[1] = userFollowingList == null ? 0L : userFollowingList.size();

            Set<Long> statusIds = userStatusMap.get(userId);
            values[2] = statusIds == null ? 0L : statusIds.size();

            AtomicLong likeCount = new AtomicLong();
            AtomicLong unlikeCount = new AtomicLong();

            if (statusIds != null) {
                for (Long statusId : statusIds) {
                    Status remoteStatus = statusMap.get(statusId);
                    likeCount.addAndGet(((Status) remoteStatus).getLikeCount().get());
                    unlikeCount.addAndGet(((Status) remoteStatus).getUnlikeCount().get());
                }
            }

            values[3] = likeCount.get();
            values[4] = unlikeCount.get();
            map.put(userId, values);
        }

        return map;
    }


  public static List<RemoteUser> allFollowList(Long userId){   // new
      Set<Long> ifollowthem = followList.get(userId);
      List<RemoteUser> list = new ArrayList<>();

      for(Long id : ifollowthem){
          RemoteUser rUser = usersMap.get(id);
          list.add(rUser);
      }
      return list;
  }


  public static List<RemoteUser> allOfYourFollowerList(Long userId){   // new
      Set<Long> theyfollowme = allOfYourFollowers.get(userId);
      List<RemoteUser> list = new ArrayList<>();

      for(Long id : theyfollowme){
          RemoteUser rUser = usersMap.get(id);
          list.add(rUser);
      }
      return list;
  }

    public static List<Status> showAllMyStatusList(Long userId){
        List<Status> list = new ArrayList<>();

        Set<Long> statusIds = userStatusMap.get(userId);
        for(Long id : statusIds){
            list.add(statusMap.get(id));
        }

        return list;
    }


}
