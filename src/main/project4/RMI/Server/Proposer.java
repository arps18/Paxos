package main.project4.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Proposer extends Remote {
  void propose(String proposalId, Operation proposalValue) throws RemoteException, InterruptedException;
}
