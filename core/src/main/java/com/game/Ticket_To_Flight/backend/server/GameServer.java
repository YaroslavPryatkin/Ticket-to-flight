package com.game.Ticket_To_Flight.backend.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.io.IOException;

public class GameServer {
    private final Server server;
    private final LowLevelHandler llh;

    public GameServer(LowLevelHandler llh) {
        this.llh = llh;
        server = new Server();

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
