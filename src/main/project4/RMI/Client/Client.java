package main.project4.RMI.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;
import java.util.Scanner;
import main.project4.RMI.ProcessReq.processRequest;
import main.project4.RMI.Server.KeyValueStore;


public class Client {


  public static void main(String[] args) {
    try {

      if (args.length != 3) {
        System.out.println("Time : " + System.currentTimeMillis() + " - Usage: java PaxosClient c");
        System.exit(1);
      }
      String hostname = args[0];
      int port = Integer.parseInt(args[1]);
      String remoteObjectName = args[2];


      RMIClientSocketFactory clientSocketFactory = new RMIClientSocketFactory() {

        public Socket createSocket(String host, int port) throws IOException {
          Socket socket = new Socket();
          socket.connect(new InetSocketAddress(host, port), 5000); // 5 sec timeout
          return socket;
        }

        public ServerSocket createServerSocket(int port) throws IOException {
          return new ServerSocket(port);
        }
      };

      clientSocketFactory.createSocket(hostname, port);


      Random random = new Random();
      int addition = random.nextInt(4);
      Registry registry = LocateRegistry.getRegistry(hostname, port + addition, clientSocketFactory);
      KeyValueStore remoteObject = (KeyValueStore) registry.lookup(remoteObjectName);


      for (int i = 0; i < 10; i++) {
        System.out.println("Time : " + System.currentTimeMillis() + " Starting Pre-Populating of key values..");
        handleOperation("PUT key" + i + " value" + i, remoteObject);
        System.out.println("Time : " + System.currentTimeMillis() + " Pre-Population of key values completed!");
      }


      for (int i = 0; i < 5; i++) {
        System.out.println("Time : " + System.currentTimeMillis() + " Performing GET Operation..");
        handleOperation("GET key" + i, remoteObject);
        System.out.println("Time : " + System.currentTimeMillis() + " GET Operation complete!");
      }


      for (int i = 0; i < 5; i++) {
        System.out.println("Time : " + System.currentTimeMillis() + " Performing DELETE Operation..");
        handleOperation("DELETE key" + i, remoteObject);
        System.out.println("Time : " + System.currentTimeMillis() + " DELETE Operation complete!");
      }


      for (int i = 5; i < 10; i++) {
        System.out.println("Time : " + System.currentTimeMillis() + " Performing PUT Operation..");
        handleOperation("PUT key" + i + " value" + i, remoteObject);
        System.out.println("Time : " + System.currentTimeMillis() + " PUT Operation complete!");
      }


      while (true) {
        try {
          Scanner sc = new Scanner(System.in);
          System.out.println("Time : " + System.currentTimeMillis() + " - Enter the GET, PUT, DELETE operation string or enter EXIT to exit the client: ");
          String operation = sc.nextLine();
          addition = random.nextInt(4);
          registry = LocateRegistry.getRegistry(hostname, port + addition, clientSocketFactory);
          System.out.println("Server port - " + port + addition);
          remoteObject = (KeyValueStore) registry.lookup(remoteObjectName);
          if (operation.equalsIgnoreCase("EXIT"))
            break;
          else if (operation.startsWith("PUT ") || operation.startsWith("GET ") || operation.startsWith("DELETE ")) {
            handleOperation(operation, remoteObject);
          }
        } catch (RemoteException e) {
          System.out.println("Time : " + System.currentTimeMillis() + " - RemoteException occurred while processing client request");
        } catch (ServerNotActiveException se) {
          System.out.println("Time : " + System.currentTimeMillis() + " - ServerNotActiveException occurred while processing client request");
        } catch (Exception e) {
          System.out.println("Time : " + System.currentTimeMillis() + " - Exception occurred while processing client request with message" + e.getMessage());
        }
      }
    } catch (RemoteException e) {
      System.out.println("Time : " + System.currentTimeMillis() + " - RemoteException occurred while processing client registry");
    } catch (Exception e) {
      System.out.println("Time : " + System.currentTimeMillis() + " - Exception occurred while processing client with message" + e.getMessage());
    }
  }


  private static void handleOperation(String operation, KeyValueStore remoteObject)
      throws ServerNotActiveException, RemoteException, InterruptedException {
    System.out.println("Time : " + System.currentTimeMillis() + " Received operation : " + operation);
    processRequest response = processRequest(operation, remoteObject);
    String responseData;
    if (!response.status) {
      System.out.println("Time : " + System.currentTimeMillis() + " - Received malformed request of length " + operation.length());
      responseData = response.message;
    } else {
      responseData = response.value;
    }
    System.out.println("Time : " + System.currentTimeMillis() + " Response from server : " + responseData);
  }


  private static processRequest processRequest(String requestData, KeyValueStore remoteObject)
      throws RemoteException, InterruptedException {


    if (requestData.startsWith("PUT")) {
      String[] parts = requestData.split(" ");
      if (parts.length == 3) {
        String key = parts[1];
        String value = parts[2];
        remoteObject.put(key, value);
        return new processRequest(true, "PUT process successful", "Key:" + key + " added with the Value:" + value);
      } else {
        return new processRequest(false, "PUT operation failed due to malformed input", "");
      }
    }


    if (requestData.startsWith("GET")) {
      String[] parts = requestData.split(" ");
      if (parts.length == 2) {
        String key = parts[1];
        if (remoteObject.containsKey(key)) {
          String value = remoteObject.get(key);
          return new processRequest(true, "GET operation successful!", "Value returned for the given Key is : " + value);
        } else {
          return new processRequest(false, "Key not found in key store", "");
        }
      } else {
        return new processRequest(false, "GET operation failed due to malformed input", "");
      }
    }

    if (requestData.startsWith("DELETE")) {
      String[] parts = requestData.split(" ");
      if (parts.length == 2) {
        String key = parts[1];
        remoteObject.delete(key);
        return new processRequest(true, "DELETE operation successful!", "Value deleted for Key:" + key);
      } else {
        return new processRequest(false, "DELETE operation failed due to malformed input", "");
      }
    }
    return new processRequest(false, "Operation failed due to malformed input", "");
  }
}
