package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.CityType;
import com.game.Ticket_To_Flight.network.Network;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.game.Ticket_To_Flight.packages.PackageInitAirports;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class GameClient {
    private final Client client;
    private final MainClient mainClient;

    public GameClient() {
        client = new Client();
        mainClient = new MainClient();
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

    public MainClient getMainClient() {
        return mainClient;
    }

    // temporary function
    public void sendWorldMapPacket() {
        PackageCreateWorldMap mapPacket = new PackageCreateWorldMap("EuropeMap.png", 1920f, 1080f);
        mainClient.getPackage(mapPacket);

        // ... где-то в методе инициализации для теста ...

        CityType testCityType = null; // Если это класс, сделай new CityType(...), если enum — CityType.ТВОЕ_ЗНАЧЕНИЕ

        // 2. Создаем "Типы" аэропортов с ценами и гейтами
        AirportType regionalType = new AirportType(1, 500.0, 2, testCityType, "Региональный аэропорт");
        AirportType internationalType = new AirportType(2, 1500.0, 5, testCityType, "Международный хаб");

        List<Airport> testAirports = new ArrayList<>();

        testAirports.add(new Airport(1, regionalType, new Vector2(1050f, 480f)));

        testAirports.add(new Airport(2, regionalType, new Vector2(980f, 200f)));

        testAirports.add(new Airport(3, internationalType, new Vector2(1120f, 420f)));

        testAirports.add(new Airport(4, internationalType, new Vector2(1800f, 250f)));

        PackageInitAirports airportPacket = new PackageInitAirports(testAirports);

        mainClient.getPackage(airportPacket);
    }


}
