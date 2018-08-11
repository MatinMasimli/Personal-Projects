package cs735_project;

import cs735_project.common.RemoteUser;
import cs735_project.common.RemoteThumbsUp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbsUpClient {

    private RemoteThumbsUp thumbsUp;
    private String hostName;
    private int port;

    public ThumbsUpClient(String service) throws RemoteException, NotBoundException, MalformedURLException{
        thumbsUp = (RemoteThumbsUp) java.rmi.Naming.lookup(service);
    }

    public ThumbsUpClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }
    public static void main(String[] args) throws Exception {
        ThumbsUpClient client;

        if( args[0].equals("socket")) {
            if(  args.length < 3 ) {
                System.err.println("Incorrect command line. Usage:\nsocket hostName portNumber" );
                System.exit(-1);
            }

            client = new ThumbsUpClient(args[1], Integer.parseInt(args[2]));
            client.startSocketProcessing();

            return;
        }

        String service = "rmi://" + args[0] + "/" + ThumbsUpServer.System_Name;
        client = new ThumbsUpClient(service);

        try {
            if (args.length == 1) {
                client.createUser();
                return;
            }

            long id = Long.parseLong(args[1]);
            if (id < 0) {
                client.removeUser(-id);
                return;
            }

            String type = (args.length > 2) ? args[2] : "R";
            switch (type) {
                case "0":
                    client.runTestsBuffered(id);
                    break;
                case "1":
                    client.runTestsOperation(id);    // add one more case for socket
                    break;
                default:
                    client.runTestsRemote(id);
            }
        } catch (IllegalArgumentException e) {
            System.out.printf("illegal argument exception: %s%n", e.getMessage());
        }
    }

    /* SOCKET */
    private void startSocketProcessing(){
        //Socket version
        try (
                Socket kkSocket = new Socket(hostName, port);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while (true) {

                System.out.println("Usage: cmdId userId otherUserId(if need) statusText (if need):" );
                System.out.println("cmdId: 0 - UnFollow, 1 - follow, 2 - publish, 3 - like, 4 - unlike, 5 - showStatus, 6 - create user. 7 - remove user, 8 - exit" );

                fromUser = stdIn.readLine();
                try {
                    if (fromUser != null) {
                        StringBuilder cmd = new StringBuilder();
                        String[] texts = fromUser.split(" ");

                        if (texts[0].equals("1")) {                       // Follow
                            //Format 1 userId otherId
                            cmd.append(SocketServer.CMD_FOLLOW);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            long otherId = Integer.valueOf(texts[2]);
                            cmd.append(userId);
                            cmd.append(":");
                            cmd.append(otherId);
                        } else if (texts[0].equals("0")) {                  // UnFollow
                            //Format 0 userId otherId
                            cmd.append(SocketServer.CMD_UNFOLLOW);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            long otherId = Integer.valueOf(texts[2]);
                            cmd.append(userId);
                            cmd.append(":");
                            cmd.append(otherId);
                        } else if (texts[0].equals("2")) {                  // publishStatus
                            //Format 2 userId (message in next line)
                            cmd.append(SocketServer.CMD_PUBLISH);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            cmd.append(userId);
                            cmd.append(":");
                            cmd.append(stdIn.readLine());
                        } else if (texts[0].equals("3")) {                  // likeStatus
                            //Format 3 userId otherId statusId
                            cmd.append(SocketServer.CMD_LIKE);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            long otherId = Integer.valueOf(texts[2]);
                            Integer statusId = Integer.valueOf(texts[3]);
                            cmd.append(userId);
                            cmd.append(":");
                            cmd.append(otherId);
                            cmd.append(":");
                            cmd.append(statusId);
                        } else if (texts[0].equals("4")) {                  // unlikeStatus
                            //Format 4 userId otherId statusId
                            cmd.append(SocketServer.CMD_UNLIKE);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            long otherId = Integer.valueOf(texts[2]);
                            Integer statusId = Integer.valueOf(texts[3]);
                            cmd.append(userId);
                            cmd.append(":");
                            cmd.append(otherId);
                            cmd.append(":");
                            cmd.append(statusId);
                        } else if (texts[0].equals("5")) {                  // showAllStatus
                            //Format 5 userId
                            cmd.append(SocketServer.CMD_SHOW);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            cmd.append(userId);
                        } else if (texts[0].equals("6")) {                  // create user
                            //Format 6
                            cmd.append(SocketServer.CMD_CREATE);
                        } else if (texts[0].equals("7")) {                  // remove user
                            //Format 7 userId
                            cmd.append(SocketServer.CMD_REMOVE);
                            cmd.append(":");
                            long userId = Integer.valueOf(texts[1]);
                            cmd.append(userId);
                        }
                        else if (texts[0].equals("8")) {                  // exit
                            //Format 8
                            break;
                        }
                        //Convert string buffer to string and pass it to the server
                        out.println(cmd.toString());
                    }
                }catch (Exception ex)
                {
                    System.out.println("Error in input:" + ex.toString() );
                    continue;
                }


                fromServer = in.readLine();
                if( fromServer == null ) continue;
                //no harm here
                // post process new line in server answer (so as based on socket protocol each line contains a whole command)
                fromServer = fromServer.replaceAll("SOCKETLINE", "\n");
                System.out.println("Server: " + fromServer);
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());

        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
    }

    private void runTestsRemote(long id) throws RemoteException {
        RemoteUser user = thumbsUp.getRemoteUser(id);
        System.out.println(user.showAllStatus());

        Scanner in = new Scanner(System.in);
        try{
            while(true){
                String operation = in.next();
                if(operation.equals("1")){                       // Follow
                    Long userId = in.nextLong();
                    System.out.println(user.follow(userId));
                }

                else if(operation.equals("2")){                  // UnFollow
                    Long userId = in.nextLong();
                    System.out.println(user.unfollow(userId));
                }

                else if(operation.equals("3")){                  // publishStatus
                    String status = in.nextLine();
                    System.out.println(user.publishStatus(status));
                }

                else if(operation.equals("4")){                  // likeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    System.out.println(user.likeStatus(userId, statusId));
                }

                else if(operation.equals("5")){                  // unlikeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    System.out.println(user.unlikeStatus(userId, statusId));
                }

                else if(operation.equals("6")){                  // showAllStatus
                    System.out.println(user.showAllStatus());
                }
            }
        }
        catch(java.util.NoSuchElementException e){
            System.err.println(e.getMessage());
        }
    }

    private void runTestsOperation(long id) throws RemoteException{
        Scanner in = new Scanner(System.in);
        try {
            while(true){
                String operation = in.next();
                if(operation.equals("1")){                       // Follow
                    Long userId = in.nextLong();
                    System.out.println(thumbsUp.requestOperation(id, Operation.follow(userId)));
                }

                else if(operation.equals("2")){                  // UnFollow
                    Long userId = in.nextLong();
                    System.out.println(thumbsUp.requestOperation(id, Operation.unfollow(userId)));
                }

                else if(operation.equals("3")){                  // publishStatus
                    String status = in.nextLine();
                    System.out.println(thumbsUp.requestOperation(id, Operation.publishStatus(status)));
                }

                else if(operation.equals("4")){                  // likeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    System.out.println(thumbsUp.requestOperation(id, Operation.likeStatus(userId, statusId)));
                }

                else if(operation.equals("5")){                  // unlikeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    System.out.println(thumbsUp.requestOperation(id, Operation.unlikeStatus(userId, statusId)));
                }

                else if(operation.equals("6")){                  // showAllStatus
                    System.out.println(thumbsUp.requestOperation(id, Operation.showAllStatus()));
                }
            }
        }
        catch(IllegalArgumentException e){
            System.err.println(e.getMessage());
        }
    }

    private void runTestsBuffered(long id) throws RemoteException{
        BufferedUser bufferedUser = thumbsUp.getBufferedUser(id);
        //System.out.println(bufferedUser.showAllStatus());
        Scanner in = new Scanner(System.in);
        try {
            while(true){
                String operation = in.next();

                if(operation.equals("1")){                       // Follow
                    Long userId = in.nextLong();
                    bufferedUser.follow(userId);
                }

                else if(operation.equals("2")){                  // UnFollow
                    Long userId = in.nextLong();
                    bufferedUser.unfollow(userId);
                }

                else if(operation.equals("3")){                  // publishStatus
                    String status = in.nextLine();
                    bufferedUser.publishStatus(status);
                }

                else if(operation.equals("4")){                  // likeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    bufferedUser.likeStatus(userId, statusId);
                }

                else if(operation.equals("5")){                  // unlikeStatus
                    Long userId = in.nextLong();
                    Long statusId = in.nextLong();
                    bufferedUser.unlikeStatus(userId, statusId);
                }
            }
        }
        catch(IllegalArgumentException e){

        }

    }

    private void removeUser(long id) throws RemoteException{
        System.out.printf("user closed with ID = %d%n", thumbsUp.removeUser(id));
    }

    private void createUser() throws RemoteException {
        System.out.printf("user created: %d%n", thumbsUp.createUser());
    }
}
