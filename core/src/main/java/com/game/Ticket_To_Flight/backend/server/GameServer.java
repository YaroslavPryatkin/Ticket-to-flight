package com.game.Ticket_To_Flight.backend.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.io.IOException;

public class GameServer {
    private final Server server;


    public GameServer(LowLevelHandler llh) {
        FirewallManager.ensureFirewallRule(Network.TCP_PORT, Network.UDP_PORT);
        server = new Server(Network.writeBufferSize, Network.objectBufferSize);

        Network.register(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                llh.handleNewConnection(connection);
            }

            @Override
            public void received(Connection connection, Object object) {
                llh.receiveMessage(connection, object);
            }

            @Override
            public void disconnected(Connection connection) {llh.handleDisconnection(connection);}
        });
        start();
    }

    void start() {
        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
            server.start();
            //System.out.println("Server launched!");
        } catch (IOException e) {
            System.err.println("FATAL: Failed to start server: " + e.getMessage());
            throw new RuntimeException("Server startup failed", e);
        }
    }

    public void stop(){
        server.stop();
    }
}
