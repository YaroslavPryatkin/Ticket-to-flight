package com.game.Tichet_To_Flight.frontend;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.game.Tichet_To_Flight.network.Network;

import java.io.IOException;

public class GameClient {
    private Client client;

    public GameClient() {
        client = new Client();
        Network.register(client);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.GameCreatedResponse) {
                    System.out.println("Подключение к игре прошло успешно!");
                }
            }
        });
    }

    public void connect(String ipAddress) {
        client.start();
        try {
            client.connect(5000, ipAddress, Network.TCP_PORT, Network.UDP_PORT);

            Network.CreateGameRequest req = new Network.CreateGameRequest();
            req.hostName = "Egor";
            client.sendTCP(req);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
