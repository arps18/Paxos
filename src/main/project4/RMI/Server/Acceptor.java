package main.project4.RMI.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Acceptor extends Remote {

  Boolean prepare(String proposalId, Operation operation) throws RemoteException;

  void accept(String proposalId, Operation proposalValue) throws RemoteException;
}
