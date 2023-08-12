package main.project4.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Learner extends Remote {
  void learn(String proposalId, Operation acceptedValue) throws RemoteException;
}
