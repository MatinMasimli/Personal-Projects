package cs735_project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ThumbsUpServer {

    public static final String System_Name = "System_ThumbsUp";

    private ThumbsUpLocal localSystem;
    private Registry registry;
    private static SocketServer socketSrv;

    /**
     * Creates a server for a given System
     */
    public ThumbsUpServer(ThumbsUpLocal system){
        this.localSystem = system;
    }

    public synchronized int start(int port) throws RemoteException {
        if (registry != null)
            throw new IllegalStateException("server already running");
        Registry reg;
        if (port > 0) {
            reg = LocateRegistry.getRegistry(port);
        } else if (port < 0) {
            port = -port;
            reg = LocateRegistry.createRegistry(port);
        } else {
            Random rand = new Random();
            int tries = 0;
            while (true) {
                port = 50000 + rand.nextInt(10000);
                try {
                    reg = LocateRegistry.createRegistry(port);
                    break;
                } catch (RemoteException e) {
                    if (++tries < 10 && e.getCause() instanceof java.net.BindException)
                        continue;
                    throw e;
                }
            }
        }
        reg.rebind(localSystem.name, localSystem);
        registry = reg;
        return port;
    }

    /**
     * Stops the server by removing the bank form the registry.  The bank is left exported.
     */
    public synchronized void stop() {
        if (registry != null) {
            try {
                registry.unbind(localSystem.name);
            } catch (Exception e) {
                Logger.getLogger("cs735_835").warning(String.format("unable to stop: %s%n", e.getMessage()));
            } finally {
                registry = null;
            }
        }
//        **************************************************
        socketSrv.stop();
    }


    public synchronized void printStatus() {

        try {
            Map<Long, Long[]> usersStats = localSystem.currentStats();
            if(usersStats.isEmpty()){
                System.out.println(" There is no user");
                System.out.println("--------------------");
                return;
            }

            System.out.println("Total Number of Users: " + usersStats.size());
            for (Map.Entry<Long, Long[]> e : usersStats.entrySet()) {

                System.out.println(String.format("userId %d -> Number of followers = %d , " +
                                "Number of followings = %d , Number of statuses = %d, " +
                                "Total Number of Likes = %d , Total Number of Unlikes = %d", e.getKey(),
                        e.getValue()[0], e.getValue()[1], e.getValue()[2], e.getValue()[3], e.getValue()[4] ));
            }
            System.out.println("--------------------");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        int port = 0;
        int socketPort = 0;
        if (args.length > 1)
            socketPort = Integer.parseInt(args[1]);
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        ThumbsUpLocal localSystem = new ThumbsUpLocal(System_Name);
        ThumbsUpServer server = new ThumbsUpServer(localSystem);

        //*******************
        socketSrv = new SocketServer(localSystem, socketPort);
        //**************
        socketPort = socketSrv.start();
        System.out.printf("Socket server running on port %d%n", socketPort );

        try {
            port = server.start(port);
            System.out.printf("server running on port %d%n", port);
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            exec.scheduleAtFixedRate(server::printStatus, 0, 30, TimeUnit.SECONDS);
        } catch (RemoteException e) {
            Throwable t = e.getCause();
            if (t instanceof java.net.ConnectException)
                System.err.println("unable to connect to registry: " + t.getMessage());
            else if (t instanceof java.net.BindException)
                System.err.println("cannot start registry: " + t.getMessage());
            else
                System.err.println("cannot start server: " + e.getMessage());
            UnicastRemoteObject.unexportObject(localSystem, false);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
