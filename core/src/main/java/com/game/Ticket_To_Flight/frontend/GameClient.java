package com.game.Ticket_To_Flight.frontend;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.game.Ticket_To_Flight.network.Network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class GameClient {
    private final Client client;

    public GameClient() {
        client = new Client();
        Network.register(client);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {

            }
        });
        client.start();
    }

    public boolean connect(String clientName, InetAddress ipAddress) {
        if(client.isConnected()) {
            client.stop();
            client.start();
        }
        try{
            client.connect(Network.timeoutTime, ipAddress, Network.TCP_PORT, Network.UDP_PORT);
        }
        catch (IOException e){
            System.err.println("Server with ip = " + ipAddress.toString() + " was not found: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean connect(String clientName) {
        InetAddress ipAddress = client.discoverHost(Network.UDP_PORT, Network.timeoutTime);
        if(ipAddress == null) {
            System.err.println("Automatic server finding ended unsuccessfully.");
            return false;
        }
        return connect(clientName, ipAddress);
    }

    public boolean connect(String clientName, String ipAddress){
        try{
            return connect(clientName, InetAddress.getByName(ipAddress));
        }
        catch (UnknownHostException e){
            System.err.println("Server with ip = " + ipAddress + " was not found: " + e.getMessage());
            return false;
        }
    }


}
