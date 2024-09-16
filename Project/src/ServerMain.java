package Project.src;

import javax.swing.JFrame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

public class ServerMain{
    private ServerSocket serverSocket;
    private final ArrayList<ServerListener> clientListeners = new ArrayList<>();
    public final ArrayList<ObjectOutputStream> clientOutputStreams = new ArrayList<>();
    private final ArrayList<String> gamePlayerNames = new ArrayList<>();
    private final String hostName;
    int port;
    private boolean isRunning = false;
    private Socket socket;
    private ObjectOutputStream out;
    private HostPanel hostPanel;
    private CharacterSelectPanel characterSelectPanel;

    public ServerMain(int port, String hostName, HostPanel hostPanel, CharacterSelectPanel characterSelectPanel){
        this.port = port;
        this.hostName = hostName;
        this.hostPanel = hostPanel;
        this.characterSelectPanel = characterSelectPanel;
    }
    public void startServer() {
        isRunning = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started on Port: " + port);
            addClientToList(hostName);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client accepted: " + clientSocket.getInetAddress());

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                addClientOutputStream(out);
                ServerListener serverListener = new ServerListener(clientSocket, this, in, out);
                clientListeners.add(serverListener);
                new Thread(serverListener).start();
                System.out.println("SERVER HAS STARTED SUCCESSFULLY");
            }
        } catch (IOException e) {
            if (isRunning) {
                e.printStackTrace();
            }
        }
    }
    public void addClientOutputStream(ObjectOutputStream out) {
        clientOutputStreams.add(out);
    }

    public void removeClientOutputStream(ObjectOutputStream out) {
        clientOutputStreams.remove(out);
    }
    public void addClientToList(String clientName) {
        if(!gamePlayerNames.contains(clientName)){
            gamePlayerNames.add(clientName);
            for(ObjectOutputStream clientOut : clientOutputStreams){
                //CommandFromServer.notify_CLIENT_NAME(clientOut, clientName);
                broadcastMessage(4,clientName);
                broadcastMessage(8, hostName);
            }
            System.out.println("New Client Joined: " + clientName);
        }
        hostPanel.playerList_serverSide.add(clientName);
        hostPanel.updatePeopleList();
        System.out.println(gamePlayerNames);
        //hostPanel.verifyName();
    }

    public void removeClientFromList(String clientName) {
        if(gamePlayerNames.remove(clientName)) {
            for (ObjectOutputStream clientOut : clientOutputStreams) {
                //CommandFromServer.notify_CLIENT_DISCONNECTED(clientOut, clientName);
                broadcastMessage(5, clientName);
            }
        }
        hostPanel.playerList_serverSide.remove(clientName);
        hostPanel.updatePeopleList();
        System.out.println(gamePlayerNames);
    }

    public void characterTempChoose(String playerChoosing) {
        String[] characterChosenInfo = playerChoosing.split("-");
        System.out.println("Player " + characterChosenInfo[0] + " Has Chosen Character " + characterChosenInfo[1]);
        for(ObjectOutputStream clientOut : clientOutputStreams){
            //CommandFromServer.notify_CLIENT_NAME(clientOut, clientName);
            broadcastMessage(6, playerChoosing);
            System.out.println("Sent Successfully");
        }
        if(characterChosenInfo[1].equals("catB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[0][0] = characterChosenInfo[0];
            temp[0][1] = "NotMine";
            System.out.println("About to be Updated....");
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterChosenInfo[1].equals("indianWomanB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[1][0] = characterChosenInfo[0];
            temp[1][1] = "NotMine";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterChosenInfo[1].equals("whiteB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[2][0] = characterChosenInfo[0];
            temp[2][1] = "NotMine";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterChosenInfo[1].equals("frogB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[3][0] = characterChosenInfo[0];
            temp[3][1] = "NotMine";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterChosenInfo[1].equals("gandalfB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[4][0] = characterChosenInfo[0];
            temp[4][1] = "NotMine";
            characterSelectPanel.updateAvailability(temp);
        }
    }
    public void characterTempUNChoose(String playerChoosing) {
        String[] characterUNChosenInfo = playerChoosing.split("-");
        System.out.println("Player " + characterUNChosenInfo[0] + " Has UNChosen Character " + characterUNChosenInfo[1]);
        for(ObjectOutputStream clientOut : clientOutputStreams){
            //CommandFromServer.notify_CLIENT_NAME(clientOut, clientName);
            broadcastMessage(7, playerChoosing);// I fixed now...
            System.out.println("DEBUGCASE");
        }
        if(characterUNChosenInfo[1].equals("catB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[0][0] = "No_Player";
            temp[0][1] = "Available";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterUNChosenInfo[1].equals("indianWomanB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[1][0] = "No_Player";
            temp[1][1] = "Available";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterUNChosenInfo[1].equals("whiteB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[2][0] = "No_Player";
            temp[2][1] = "Available";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterUNChosenInfo[1].equals("frogB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[3][0] = "No_Player";
            temp[3][1] = "Available";
            characterSelectPanel.updateAvailability(temp);
        }
        else if(characterUNChosenInfo[1].equals("gandalfB")) {
            Object[][] temp = characterSelectPanel.FINAL_ARRAY;
            temp[4][0] = "No_Player";
            temp[4][1] = "Available";
            characterSelectPanel.updateAvailability(temp);
        }
    }

    public synchronized void broadcastMessage(int values, String name){
        switch (values){
            case 1:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_START_GAME(out, name);
                    }}
                break;
            case 2:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_DONE_WITH_CARD_SELECTION(out, name);
                    }}
                break;
            case 3:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_HOST_DISCONNECTED(out, name);
                    }}
                break;
            case 4:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_CLIENT_NAME(out, name);
                    }}
                break;
            case 5:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_CLIENT_DISCONNECTED(out, name);
                    }}
                break;
            case 6:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        String[] characterCombined = name.split("-");
                        CommandFromServer.notify_CHARACTER_SELECTION(out, characterCombined[0], characterCombined[1]);
                    }}
                break;
            case 7:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        String[] characterCombined = name.split("-");
                        CommandFromServer.notify_CHARACTER_UNSELECTION(out, characterCombined[0], characterCombined[1]);
                    }}
                break;
            case 8:
                synchronized (clientOutputStreams){
                        ObjectOutputStream out = clientOutputStreams.get(clientOutputStreams.size()-1);
                        CommandFromServer.notify_HOST_NAME(out, name);
                    }
                break;
            case 9:
                synchronized (clientOutputStreams){
                    for(ObjectOutputStream out: clientOutputStreams){
                        CommandFromServer.notify_ALL_DONE_WITH_CHARACTER_SELECTION(out);
                    }
                }
                break;
        }
    }
    public void stopServer(){
        if(serverSocket != null && !serverSocket.isClosed()) {
            try {
                isRunning = false;
                serverSocket.close();
                for(ServerListener listener : clientListeners){
                    listener.stopListening();
                }
                clientListeners.clear();
                clientOutputStreams.clear();
                gamePlayerNames.clear();
                System.out.println("Server Stopped");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //CommandFromServer.notify_HOST_DISCONNECTED(out, hostName);
        broadcastMessage(3, hostName);
        hostPanel.clearPeopleList();
    }
    public ObjectOutputStream getOut(){
        return out;
    }
    public Socket getSocket(){
        return socket;
    }
    public void setOut(ObjectOutputStream out){
        this.out = out;
    }
    public void setSocket(Socket socket){
        this.socket = socket;
    }
    public ArrayList<String> getGamePlayerNames(){
        return gamePlayerNames;
    }
    public void setCharacterSelectPanel(CharacterSelectPanel characterSelectPanel){
        this.characterSelectPanel = characterSelectPanel;
    }


}