package com.game.Tichet_To_Flight.backend.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.game.Tichet_To_Flight.network.Network;

import java.io.IOException;

public class GameServer {
    private Server server;

    public GameServer() {
        server = new Server();

        Network.register(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Новый игрок подключился: " + connection.getRemoteAddressTCP());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.CreateGameRequest) {
                    Network.CreateGameRequest req = (Network.CreateGameRequest) object;
                    System.out.println("Игрок " + req.hostName + " создает игру!");

                    Network.GameCreatedResponse res = new Network.GameCreatedResponse();
                    res.success = true;
                    connection.sendTCP(res);
                }
            }
        });
    }

    public void start() {
        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
            server.start();
            System.out.println("Сервер запущен!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
