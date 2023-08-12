package main.project4.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface KeyValueStore extends Remote {
  String put(String key, String value) throws RemoteException, InterruptedException;

  String get(String key) throws RemoteException, InterruptedException;
  String delete(String key) throws RemoteException, InterruptedException;

  Boolean containsKey(String key) throws RemoteException, InterruptedException;
}

