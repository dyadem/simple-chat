package common;

import java.rmi.*;

public interface ChatInterface extends Remote {
    String getName() throws RemoteException;

    void send(String msg) throws RemoteException;

    void setClient(ChatInterface c) throws RemoteException;

    ChatInterface getClient() throws RemoteException;
}
