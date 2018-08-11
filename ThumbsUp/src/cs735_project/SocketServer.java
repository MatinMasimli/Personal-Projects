package cs735_project;

import cs735_project.common.RemoteUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketServer {

    int port;

    public static final String MSG_CLOSE = "CLOSE";
    public static final String CMD_FOLLOW = "FLW";
    public static final String CMD_UNFOLLOW = "UFL";
    public static final String CMD_PUBLISH = "PBL";
    public static final String CMD_LIKE = "LIK";
    public static final String CMD_UNLIKE = "ULK";
    public static final String CMD_SHOW = "SHW";
    public static final String CMD_CREATE = "CRE";
    public static final String CMD_REMOVE = "RMV";


    boolean listening = true;

    ServerSocket serverSocket;

    ThumbsUpLocal localSystem;


    public SocketServer(ThumbsUpLocal localSystem, int port)
    {
        this.port = port;
        this.localSystem = localSystem;
    }


    public int start()
    {

        if( serverSocket != null ) throw  new IllegalStateException();

        int res = port;
        try  {

            serverSocket = new ServerSocket(port);

            res = serverSocket.getLocalPort();

            new Thread(() -> runSocketThread(serverSocket) ).start();
        } catch (IOException e) {

            System.err.println("Could not listen on Socket port " + port);
            System.exit(-1);
        }

        return  res;
    }

    public void stop()
    {
        listening = false;
        try {
            serverSocket.close();
        }catch (IOException ignore){}
    }

    public void runSocketThread(ServerSocket serverSocket){
        while (listening) {
            try{

                Socket socket = serverSocket.accept();
                new Thread( () ->
                        runSingleClient(socket)
                ).start();
            } catch (IOException ioex) {
                System.err.println("IOException during Socket accept: " + ioex.toString() );
                listening = false;
            }
        }
    }

    public void runSingleClient(Socket socket) {

        try (

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {

            System.out.println("New client connected from:" + socket.getRemoteSocketAddress().toString() );
            String inputLine, outputLine;


            while ((inputLine = in.readLine()) != null) {

                outputLine = processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals(MSG_CLOSE))
                    break;
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Connection with client closed. Reason:" + e.getMessage() );
        }
    }

    public String  processInput(String input)
    {
        if( input == null ) return  null;
        input = input.trim();


        String[] fields = input.split(":");
        //Check if line has correct format - at least 2 fields (command and userId) or 1 command for create user command
        if( fields.length < 2 && (fields.length == 1 && !fields[0].equals(CMD_CREATE) ) )
        {
//            Logger.getLogger("cs735_835").warning("len:" + fields.length + "[0]=" + fields[0] + "==" + (!fields[0].equals(CMD_CREATE)));
            Logger.getLogger("cs735_835").warning(String.format("Socket input has incorrect format. Input: %s", input));
            return "ERROR";
        }


        String cmd = fields[0];
        int userId = fields.length > 1 ? Integer.parseInt(fields[1]) : 0;
        RemoteUser usr = null, other;
        try {
            usr = localSystem.getLocalUser(userId);
        }catch (IllegalArgumentException ex){}

        Long otherId, statusId ;
        Record r;
        if( usr == null && fields.length > 1 )
        {
            return  "INCORRECT USER ID: "+ userId;
        }

        try {
            switch (cmd) {
                case CMD_FOLLOW:
                    otherId = Long.valueOf(fields[2]);
                    r = usr.follow(otherId);
                    return r.toString();
                case CMD_UNFOLLOW:
                    otherId = Long.valueOf(fields[2]);
                    r = usr.unfollow(otherId);
                    return r.toString();
                case CMD_PUBLISH:
                    String msg = fields[2];
                    r = usr.publishStatus(msg);
                    return r.toString();
                case CMD_LIKE:
                    otherId = Long.valueOf(fields[2]);
                    statusId = Long.valueOf(fields[3]);
                    r = usr.likeStatus( otherId, statusId );
                    return r.toString();
                case CMD_UNLIKE:
                    otherId = Long.valueOf(fields[2]);
                    statusId = Long.valueOf(fields[3]);
                    r = usr.unlikeStatus( otherId, statusId );
                    return r.toString();
                case CMD_SHOW:
                    r = usr.showAllStatus();
                    String s =  r.toString();
                    //So as socket protocol need 1 line for each answer - we have to replace each \n to something else
                    //Not sure about this line, found online but doing no harm
                    s = s.replaceAll("\n", "SOCKETLINE" );
                    return s;
                case CMD_CREATE:
                    long id = localSystem.createUser();
                    return String.format("user created: %d", id);
                case CMD_REMOVE:
                    return String.format("user closed with ID = %d%n", localSystem.removeUser(userId));
            }
        }catch (Exception ex)
        {
            String s = "Error while Socket processing :"  + ex.toString();
            Logger.getLogger("cs735_835").warning(s);
            ex.printStackTrace();
            return  s;
        }
        return input;
    }


}
