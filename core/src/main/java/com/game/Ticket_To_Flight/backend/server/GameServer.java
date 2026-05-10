package com.game.Ticket_To_Flight.backend.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.network.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private final Server server;

    public GameServer() {
        server = new Server();

        Network.register(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("The new client connected: " + connection.getRemoteAddressTCP());
            }

            @Override
            public void received(Connection connection, Object object) {

            }
        });
    }

    public void start() {
        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
            server.start();
            System.out.println("Server launched!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
