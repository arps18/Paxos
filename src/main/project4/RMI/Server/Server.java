package main.project4.RMI.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends UnicastRemoteObject implements Proposer, Acceptor, Learner,
    KeyValueStore {
  private final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
  private final Map<String, Pair<String, Operation>> containsKey;
  private Acceptor[] acceptors;
  private Learner[] learners;
  private boolean serverStatus = false;
  private long serverDownTime = 0;
  private final int serverId;
  boolean isSuccess = false;
  int SERVER_DT=100;
  double div = 2.0;
  private final Map<String, Pair<Integer, Boolean>> stringPairMap;

  public Server(int serverId) throws RemoteException {
    this.serverId = serverId;
    this.containsKey = new HashMap<>();
    this.stringPairMap = new HashMap<>();
  }
  public void setAcceptors(Acceptor[] acceptors) throws RemoteException {
    this.acceptors = acceptors;
  }

  public void setLearners(Learner[] learners) throws RemoteException {
    this.learners = learners;
  }

  @Override
  public synchronized String put(String key, String value)
      throws RemoteException, InterruptedException {
    isSuccess = false;
    proposeOperation(new Operation("PUT", key, value));
    if (isSuccess)
      return "PUT operation successful for key - "+ key +" with value - "+value;
    else
      return "Error occurred during PUT operation for key - "+key;
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    if (keyValueStore.containsKey(key))
      return keyValueStore.get(key);
    return "No entry exist for they key - "+key;
  }

  @Override
  public synchronized String delete(String key) throws RemoteException, InterruptedException {
    isSuccess = false;
    proposeOperation(new Operation("DELETE", key, null));
    if (isSuccess)
      return "DELETE operation successful for key - "+ key;
    else
      return "Error occurred during DELETE operation for key - "+key;
  }

  @Override
  public Boolean containsKey(String key) throws RemoteException {
    return keyValueStore.containsKey(key);
  }


  private boolean checkAcceptorStatus() throws RemoteException {
    if(serverStatus) {
      long currentTime = System.currentTimeMillis() / 1000L;
      if(this.serverDownTime + SERVER_DT <= currentTime) {
        serverStatus = false;
        return false;
      }
      return true;
    }
    return false;
  }

  private void proposeOperation(Operation operation) throws RemoteException, InterruptedException {
    String proposalId = generateProposalId();
    propose(proposalId, operation);
  }


  @Override
  public synchronized Boolean prepare(String proposalId, Operation operation) throws RemoteException {
    if(checkAcceptorStatus()) {
      return null;
    }
    if(this.containsKey.containsKey(operation.key)) {
      if(Long.parseLong(this.containsKey.get(operation.key).getKey().split(":")[1]) >
          Long.parseLong(proposalId.split(":")[1])) {
        return false;
      }
    }
    this.containsKey.put(operation.key, new Pair<>(proposalId, operation));
    return true;
  }


  @Override
  public synchronized void accept(String proposalId, Operation proposalValue) throws RemoteException {
    if(checkAcceptorStatus()) {
      return;
    }

    if(this.containsKey.containsKey(proposalValue.key)) {
      if(Long.parseLong(this.containsKey.get(proposalValue.key).getKey().split(":")[1]) <=
          Long.parseLong(proposalId.split(":")[1])) {
        for(Learner learner : this.learners) {
          learner.learn(proposalId, proposalValue);
        }
      }
    }
  }

  @Override
  public synchronized void propose(String proposalId, Operation proposalValue)
      throws RemoteException {

    List<Boolean> prepareResponse = new ArrayList<>();
    for(Acceptor acceptor : this.acceptors) {
      Boolean res = acceptor.prepare(proposalId, proposalValue);
      prepareResponse.add(res);
    }
    int majority = 0;

    for(int i=0; i<5; i++) {
      if(prepareResponse.get(i) != null) {
        if(prepareResponse.get(i))
          majority += 1;
      }
    }

    if(majority >= Math.ceil(acceptors.length/ div)) {
      for(int i=0; i<5; i++) {
        if(prepareResponse.get(i) != null)
          this.acceptors[i].accept(proposalId, proposalValue);
      }
    }
  }


  @Override
  public synchronized void learn(String proposalId, Operation acceptedValue) throws RemoteException {

    if(!this.stringPairMap.containsKey(proposalId)) {
      this.stringPairMap.put(proposalId, new Pair<>(1, false));
    } else {
      Pair<Integer, Boolean> learnerPair = this.stringPairMap.get(proposalId);
      learnerPair.setKey(learnerPair.getKey()+1);
      if(learnerPair.getKey() >= Math.ceil(acceptors.length/ div) && !learnerPair.getValue()) {
        this.isSuccess = executeOperation(acceptedValue);
        learnerPair.setValue(true);
      }
      this.stringPairMap.put(proposalId, learnerPair);
    }
  }


  private String generateProposalId() throws RemoteException {
    return serverId + ":" + System.currentTimeMillis();
  }


  private boolean executeOperation(Operation operation) throws RemoteException {
    if (operation == null) return false;
    switch (operation.type) {
      case "PUT":
        keyValueStore.put(operation.key, operation.value);
        System.out.println(System.currentTimeMillis()+" - PUT Operation successful for Key:Value - " + operation.key + ":" + operation.value);
        return true;
      case "DELETE":
        if(keyValueStore.containsKey(operation.key)) {
          keyValueStore.remove(operation.key);
          System.out.println(System.currentTimeMillis()+" - DELETE Operation successful for Key - " + operation.key );
          return true;
        } else {
          System.out.println(System.currentTimeMillis()+" - DELETE Operation Failed for Key - " + operation.key );
          return false;
        }
      default:
        throw new IllegalArgumentException("Unknown operation type: " + operation.type);
    }
  }

  public void setServerDown() {
    this.serverStatus = true;
    this.serverDownTime = System.currentTimeMillis() / 1000L;
  }

}






