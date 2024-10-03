package Project.src;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientMain{

    private ConnectPanel connectPanel;
    private WaitingForHostPanel waitingForHostPanel;
    private ClientListener clientListener;
    private Socket socket;
    private ObjectOutputStream out;
    public ArrayList<String> gamePlayerNames_ClientSide = new ArrayList<>();
    public ArrayList<String> Final_gamePlayerNames_ClientSide = new ArrayList<>();
    private String clientName;
    private CharacterSelectPanel characterSelectPanel;
    public String cardPanel_Client_Side;
    private ChatPanel chatPanel;
    public ArrayList<Player> playerArrayList_client = new ArrayList<Player>();
    private GamePanel gamePanel;
    public ArrayList<String> charactersInLevel_client = new ArrayList<>();


    public ClientMain(String clientName, CharacterSelectPanel characterSelectPanel, ChatPanel chatPanel, GamePanel gamePanel){
        this.clientName = clientName;
        this.characterSelectPanel = characterSelectPanel;
        this.chatPanel = chatPanel;
        this.gamePanel = gamePanel;
    }
    //Boolean temp = characterSelectPanel.available.get(0);
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1920,1040);
            frame.setContentPane(new ConnectPanel(frame));
            frame.setVisible(true);
        });
    }
    public ClientMain getClientMain(){
        return this;
    }
    public void setName(String name){
        this.clientName = name;
        CommandFromClient.notify_CLIENT_NAME_VERIFY(out, clientName);
    }
    public void setConnectPanel(ConnectPanel connectPanel){
        this.connectPanel = connectPanel;
    }
    public ConnectPanel getConnectPanel(){
        return connectPanel;
    }
    public void setWaitingForHostPanel(WaitingForHostPanel waitingForHostPanel){
        this.waitingForHostPanel = waitingForHostPanel;
    }
    public WaitingForHostPanel getWaitingForHostPanel(){
        return waitingForHostPanel;
    }
    public void setcharacterSelectPanel(CharacterSelectPanel characterSelectPanel){
        this.characterSelectPanel = characterSelectPanel;
    }
    public void setChatPanel(ChatPanel chatPanel){
        this.chatPanel = chatPanel;
    }

    public CharacterSelectPanel getCharacterSelectPanel() {
        return characterSelectPanel;
    }
    public ChatPanel getChatPanel(){ return chatPanel; }

    public boolean connectToServer(String ipAddress, String clientName){
        if(socket != null && !socket.isClosed()){
            System.out.println("Already Connected");
        }
        try{
            socket = new Socket(ipAddress, 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            clientListener = new ClientListener(socket, this);
            new Thread(clientListener).start();
            gamePlayerNames_ClientSide.add(clientName);
            connectPanel.playerListClientSide.add(clientName);
            CommandFromClient.notify_CLIENT_NAME(out, clientName);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to Connect to Server", "Connection Error",JOptionPane.ERROR_MESSAGE);
            connectPanel.confirmButton.setEnabled(true);
            connectPanel.confirmButton.setBackground(Color.BLACK);
            connectPanel.confirmButton.setForeground(Color.white);
            return false;
        }
    }

    public ArrayList<String> clearPlayerNames(ArrayList<String> gamePlayerNamesClientSide) {
        Iterator<String> iterator = gamePlayerNamesClientSide.iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            if(name == null || name.isEmpty()){
                iterator.remove();
            }
        }
        return gamePlayerNamesClientSide;
    }

    public void addHostToList(String hostName){
        if(!gamePlayerNames_ClientSide.contains(hostName)){
            gamePlayerNames_ClientSide.add(0,hostName);
        }
        if(!connectPanel.playerListClientSide.contains(hostName)){
            connectPanel.playerListClientSide.add(0,hostName);
            connectPanel.updatePlayerList();
        }
        System.out.println(gamePlayerNames_ClientSide);
    }
    public void addClientToList(String clientName) {
        if(!gamePlayerNames_ClientSide.contains(clientName)){
            gamePlayerNames_ClientSide.add(clientName);
        }
        if(!connectPanel.playerListClientSide.contains(clientName)){
            connectPanel.playerListClientSide.add(clientName);
            connectPanel.updatePlayerList();
        }
        System.out.println(gamePlayerNames_ClientSide);
        //connectPanel.verifyName();
    }
    public void removeClientFromList(String clientName) {
        gamePlayerNames_ClientSide.remove(clientName);
        connectPanel.playerListClientSide.remove(clientName);
        connectPanel.updatePlayerList();
        System.out.println(gamePlayerNames_ClientSide);
    }
    public void tempFinalAndMessage(String nameAndMessage) {
        String[] finalNameAndMessage = nameAndMessage.split("-");
        System.out.println("Player " + finalNameAndMessage[0] + " Has Sent Message: " + finalNameAndMessage[1]);
        if(!(finalNameAndMessage[0].equals(connectPanel.nameTextField.getText()))) {
            chatPanel.handleIncomingMessage(finalNameAndMessage[0], finalNameAndMessage[1]);
        }
    }
    public void fix_List(String processedList) {
        Final_gamePlayerNames_ClientSide.clear();
        String[] stringList = processedList.split("-");
        for(int i=0;i<stringList.length;i++){
            Final_gamePlayerNames_ClientSide.add(stringList[i]);
        }
        connectPanel.updatePlayerList();
    }

    public void handleNewList(ArrayList<Player> playerArrayList1) {
        if(!playerArrayList_client.isEmpty()) {
            playerArrayList_client.clear();
        }
        playerArrayList_client.addAll(playerArrayList1);
        System.out.println("Success, Got PlayerArrayList (C)");
        System.out.println(playerArrayList_client.size());
        for(int i=0;i<playerArrayList_client.size();i++){
            System.out.println(playerArrayList_client.get(i).getPlayerName());
            System.out.println(playerArrayList_client.get(i).getPlayerID());
        }
    }
    public void processLCName(String lcName) {
        if(!(lcName.equals(connectPanel.nameTextField.getText()))){
            gamePanel.GUILevelCardsClient();
        }
    }
    public void processInterClick(String nameClicked) {
        if(gamePanel.current_player == Final_gamePlayerNames_ClientSide.indexOf(nameClicked)){
            gamePanel.playerGameView(gamePanel.current_player);
        }
    }

    public void characterTempChoose(String playerChoosing) {
        String[] characterChosenInfo = playerChoosing.split("-");
        System.out.println("Player " + characterChosenInfo[0] + " Has Chosen Character " + characterChosenInfo[1]);
        if(!(characterChosenInfo[0].equals(connectPanel.nameTextField.getText()))){
            if(characterChosenInfo[1].equals("catB")) {
                Object[][] temp = characterSelectPanel.FINAL_ARRAY;
                temp[0][0] = characterChosenInfo[0];
                temp[0][1] = "NotMine";
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
            }}
    }
    public void characterTempUNChoose(String playerChoosing) {
        String[] characterUNChosenInfo = playerChoosing.split("-");
        System.out.println("Player " + characterUNChosenInfo[0] + " Has UNChosen Character " + characterUNChosenInfo[1]);
        if(!(characterUNChosenInfo[0].equals(connectPanel.nameTextField.getText()))){
            System.out.println("RUNNING THIS NOOOO!");
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
            }} else{
            System.out.println("BYPASSED BECAUSE CLIENT RECEIVING SAME");
        }
    }
    public void finalCharacterMethod() {
        charactersInLevel_client.add(characterSelectPanel.characterSelected);
        CommandFromClient.notifyFinalCharacter(getOut(), connectPanel.nameTextField.getText(), characterSelectPanel.characterSelected);
    }
    public void handleFinalCharacter(String fCharacter) {
        System.out.println(fCharacter);
        if(fCharacter.contains("-")) {
            String fCharacterTemp[] = fCharacter.split("-");
            System.out.println("FINAL DEBUG" + fCharacterTemp);
            if (!(fCharacterTemp[0].equals(connectPanel.nameTextField.getText()))) {
                charactersInLevel_client.add(fCharacterTemp[1]);
            }
        }
        else{
            charactersInLevel_client.add((fCharacter));
        }
    }
    public void makeCardChosenAccessible(String arrayListCardPanel) {
        cardPanel_Client_Side = arrayListCardPanel;
    }
    public ArrayList<String> getGamePlayerNames_ClientSide(){
        return gamePlayerNames_ClientSide;
    }
    public ArrayList<String> getFinal_gamePlayerNames_ClientSide(){
        return Final_gamePlayerNames_ClientSide;
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
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}