package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.CityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.game.Ticket_To_Flight.packages.PackageInitAirlines;
import com.game.Ticket_To_Flight.packages.PackageInitAirports;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class GameClient {
    private final Client client;
    private final LowLevelHandler llh;

    public GameClient(LowLevelHandler llh) {
        this.llh = llh;
        client = new Client();
        Network.register(client);

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection){
                llh.handleNewConnection(connection);
            }
            @Override
            public void received(Connection connection, Object object) {
                llh.receiveMessage(connection, object);
            }
        });
        client.start();
    }

    public boolean connect(InetAddress ipAddress) {
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

    public boolean connect() {
        InetAddress ipAddress = client.discoverHost(Network.UDP_PORT, Network.timeoutTime);
        if(ipAddress == null) {
            System.err.println("Automatic server finding ended unsuccessfully.");
            return false;
        }
        return connect(ipAddress);
    }

    public boolean connect(String ipAddress){
        try{
            return connect(InetAddress.getByName(ipAddress));
        }
        catch (UnknownHostException e){
            System.err.println("Server with ip = " + ipAddress + " was not found: " + e.getMessage());
            return false;
        }
    }

}
