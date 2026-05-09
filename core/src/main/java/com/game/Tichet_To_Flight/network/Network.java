package com.game.Tichet_To_Flight.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(CreateGameRequest.class);
        kryo.register(GameCreatedResponse.class);
    }

    public static class CreateGameRequest {
        public String hostName;
    }

    public static class GameCreatedResponse {
        public boolean success;
    }
}
