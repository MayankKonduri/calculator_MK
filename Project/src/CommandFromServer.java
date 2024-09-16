package Project.src;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;


public class CommandFromServer implements Serializable
{
    public static final String START_GAME = "START_GAME:";
    public static void notify_START_GAME(ObjectOutputStream out, String hostName){
        sendMessage(out, START_GAME + hostName);
    }
    public static final String DONE_WITH_CARD_SELECTION = "DONE_WITH_CARD_SELECTION:";
    public static void notify_DONE_WITH_CARD_SELECTION(ObjectOutputStream out, String hostName) {
        sendMessage(out, DONE_WITH_CARD_SELECTION + hostName);
    }
    public static final String CHARACTER_SELECTION = "CHARACTER_SELECTION:";
    public static void notify_CHARACTER_SELECTION(ObjectOutputStream out, String playerName, String characterName){
    sendMessage(out, CHARACTER_SELECTION + playerName + "-" + characterName);
}
    public static final String CHARACTER_UNSELECTION = "CHARACTER_UNSELECTION:";
    public static void notify_CHARACTER_UNSELECTION(ObjectOutputStream out, String playerName, String characterName){
        sendMessage(out, CHARACTER_UNSELECTION + playerName + "-" + characterName);
    }
    public static final String DONE_WITH_CHARACTER_SELECTION = "DONE_WITH_CHARACTER_SELECTION:";
    public static void notify_DONE_WITH_CHARACTER_SELECTION(ObjectOutputStream out, String playerName, String characterName) {
        sendMessage(out, DONE_WITH_CHARACTER_SELECTION + playerName + characterName);
    }
    public static final String ALL_DONE_WITH_CHARACTER_SELECTION = "ALL_DONE_WITH_CHARACTER_SELECTION:";
    public static void notify_ALL_DONE_WITH_CHARACTER_SELECTION(ObjectOutputStream out){
        sendMessage(out, ALL_DONE_WITH_CHARACTER_SELECTION);
    }
    public static final String HOST_DISCONNECTED = "HOST_DISCONNECTED:";
    public static void  notify_HOST_DISCONNECTED(ObjectOutputStream out, String hostName){
        sendMessage(out, HOST_DISCONNECTED + hostName);
    }
    public static final String HOST_NAME = "HOST_NAME:";
    public static void notify_HOST_NAME(ObjectOutputStream out, String playerName){
        sendMessage(out, HOST_NAME + playerName);
    }
    public static final String CLIENT_NAME = "CLIENT_NAME:";
    public static void notify_CLIENT_NAME(ObjectOutputStream out, String clientName){
        sendMessage(out, CLIENT_NAME + clientName);
    }
    public static final String CLIENT_DISCONNECTED = "CLIENT_DISCONNECTED:";
    public static void notify_CLIENT_DISCONNECTED(ObjectOutputStream out, String clientName) {
        sendMessage(out, CLIENT_DISCONNECTED + clientName);
    }


    public static void sendMessage(ObjectOutputStream out, String message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}