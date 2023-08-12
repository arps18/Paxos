# Implementation of Paxos

During the implementation phase, the main focus was on constructing a distributed system that could tolerate faults, using the Paxos consensus algorithm. This involved integrating different roles like Proposers, Acceptors, and Learners, ensuring that they all agreed on the proposed values. The challenge included simulating failures in the system and then making it recover, which required careful synchronization to maintain consensus attempts. A critical aspect was keeping events in the correct order across different phases of the protocol—prepare, accept, and learn.

This hands-on experience provided deep insights into the complexities of ensuring fault tolerance, establishing communication, and implementing consensus algorithms within distributed systems. It helped me understand the delicate balance needed between reliability and availability.

## System Requirements

- Java Development Kit (JDK) 11

## How to Use

1. To start the server, run the following command: <br/>
   `java -jar project4/RMI/server/Server <port> <remoteObject>` <br/>
   > Replace `<port>` with the desired port number on which the server will provide remote methods for the client. Also, replace `<remoteObject>` with the chosen remote object registry name, through which the client can access the server's methods. For instance: `java -cp project_4.jar project4/RMI/server/PaxosServerCreator 5001 KeyStoreServer`

2. To initiate the client, execute the following command: <br/>
   `java -jar project4/RMI/client/Client <hostname> <port> <remoteObject>` <br/>
   > Replace `<serverAddress>` with the server's IP address or hostname, and `<serverPort>` with the port number on which the server exposes remote objects. In this project, the serverPort can be set as `localhost`. For example: `java -cp project_4.jar project4/RMI/client/Client localhost 5001 KeyStoreServer`

3. The initial step involves pre-populating the key store with values. Following this, the system will perform 5 operations including GET, DELETE, and PUT.

4. The client will prompt you to input the desired operation along with the Key and Value, as per the chosen operation. If the input is incorrect, it will ask you to re-enter the data. The server processes the operation and returns a response to the client indicating the success of the operation. If "EXIT" is entered as the operation, the client will exit the system.

5. Available Client Operation inputs: GET <KEY>, PUT <KEY> <VALUE>, DELETE <KEY>
   Expected Output: The returned value for the given Key is: <VALUE>, Key: <KEY> has been added with Value: <VALUE>, Value for Key: <KEY> has been deleted.