package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.network.Network;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class LowLevelHandler {
    protected final GameData gameData;

    public LowLevelHandler(GameData gameData) {
        this.gameData = gameData;
    }

    public static class MessageAndConnection {
        public final Network.GameMessage message;
        public final Connection con;

        public MessageAndConnection(Connection con, Network.GameMessage message) {
            this.message = message;
            this.con = con;
        }
    }
    private Queue<MessageAndConnection> receiveMessageQueue = new ConcurrentLinkedQueue<>();
    private Queue<MessageAndConnection> sendMessageQueue = new ConcurrentLinkedQueue<>();

    /**
     * called by main thread
     */
    protected abstract void handleIncomingMessage(Connection con, Network.GameMessage message);

    protected void addMessage(Connection con, Network.GameMessage mes) {
        if (con != null && con.isConnected()) {
            sendMessageQueue.offer(new MessageAndConnection(con, mes));
        }
    }

    /**
     * called by network thread
     */
    public abstract void handleNewConnection(Connection con);

    public void clearWaitingMessages() {
        sendMessageQueue.clear();
    }
    public void clearIncomingMessages(){
        receiveMessageQueue.clear();
    }
    public void clearAllMessages(){
        clearWaitingMessages();
        clearIncomingMessages();
    }



    protected boolean sendAllWaitingMessages() {
        boolean res = true;
        MessageAndConnection mc;
        while ((mc = sendMessageQueue.poll()) != null) {
            if(mc.con != null && mc.con.isConnected()) {
                //System.out.println("Sending message " + mc.message.getClass() + " to "+ mc.con);
                mc.con.sendTCP(mc.message);
            }
            else{
                res = false;
            }
        }
        return res;
    }

    protected void handleAllIncomingMessages() {
        MessageAndConnection mc;
        while ((mc = receiveMessageQueue.poll()) != null) {
            handleIncomingMessage(mc.con, mc.message);
        }
    }

    public void receiveMessage(Connection con, Object o){
        if(o instanceof Network.GameMessage)
            receiveMessageQueue.offer(new MessageAndConnection(con,(Network.GameMessage) o));
    }

    public abstract boolean update();
}
