package main.project4.RMI.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The PaxosServerCreator class is responsible for creating and binding the Paxos servers
 * within the RMI registry. It also configures the acceptors and learners for each server.
 */
public class PaxosServerCreator {

  public static void main(String[] args) {
    try {
      int serverNumber = 5;
      try {

        if (args.length != 2) {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
          System.out.println("Time : " + simpleDateFormat.format(System.currentTimeMillis()) + " - Usage: java PaxosServer c");
          System.exit(1);
        }

        int portInput = Integer.parseInt(args[0]);
        String remoteObjectName = args[1];

        Server[] servers = new Server[serverNumber];

        for (int serverId = 0; serverId < serverNumber; serverId++) {
          int port = portInput + serverId;

          LocateRegistry.createRegistry(port);

          servers[serverId] = new Server(serverId);

          Registry registry = LocateRegistry.getRegistry(port);
          registry.rebind(remoteObjectName, servers[serverId]);

          System.out.println("Server " + serverId + " is running at port " + port);
        }
        scheduler(servers);

        for (int serverId = 0; serverId < serverNumber; serverId++) {
          Acceptor[] acceptors = new Acceptor[serverNumber];
          Learner[] learners = new Learner[serverNumber];
          for (int i = 0; i < serverNumber; i++) {
            acceptors[i] = servers[i];
            learners[i] = servers[i];
          }
          servers[serverId].setAcceptors(acceptors);
          servers[serverId].setLearners(learners);
        }

      } catch (Exception e) {
        System.err.println("Server exception: " + e.toString());
        e.printStackTrace();
      }
    } catch (Exception e) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
      System.out.println(
          "Time : " + simpleDateFormat.format(System.currentTimeMillis()) + " - Exception occurred while processing client with message" +
              e.getMessage());
    }
  }

  private static void scheduler(Server[] servers) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        ServerDrop(servers);
      }
    }, 10000, 100000);
  }

  private static void ServerDrop(Server[] servers)  {
    int id = (int) (Math.random() * servers.length);
    servers[id].setServerDown();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");
    System.out.println(simpleDateFormat.format(System.currentTimeMillis()) + " -- Server " + id + " is going down...!!");
  }
}
