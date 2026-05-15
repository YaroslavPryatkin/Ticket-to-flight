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


    // temporary function
    /*public void sendWorldMapPacket() {
        PackageCreateWorldMap mapPacket = new PackageCreateWorldMap("EuropeMap.png", 1920f, 1080f);
        mainClient.getPackage(mapPacket);

        // --- 1. ТИПЫ АЭРОПОРТОВ И ГОРОДОВ ---
        CityType testCityType = null;
        AirportType regionalType = new AirportType(1, 500.0, 2, testCityType, "Региональный");
        AirportType internationalType = new AirportType(2, 1500.0, 5, testCityType, "Хаб");

        // --- 2. ПАССАЖИРЫ ---
        PassengerType tourists = new PassengerType(
            1, 1.5, 3,
            new ClosedInterval<>(1.0, 2.0), new ClosedInterval<>(10.0, 50.0),
            new ClosedInterval<>(1, 10), new ClosedInterval<>(1, 5), "Туристы"
        );

        PassengerType business = new PassengerType(
            2, 3.0, 1,
            new ClosedInterval<>(2.0, 3.0), new ClosedInterval<>(50.0, 150.0),
            new ClosedInterval<>(1, 5), new ClosedInterval<>(1, 3), "Бизнесмены"
        );

        // --- 3. АЭРОПОРТЫ ---
        Airport krakow = new Airport(1, regionalType, new Vector2(1050f, 480f), "Krakow");
        Airport naples = new Airport(2, regionalType, new Vector2(980f, 200f), "Naples");
        Airport budapest = new Airport(3, internationalType, new Vector2(1120f, 420f), "Budapest");
        Airport tbilisi = new Airport(4, internationalType, new Vector2(1800f, 250f), "Tbilisi");

        Passenger group1 = new Passenger(tourists, krakow, naples, testCityType);
        krakow.addPassengers(group1);
        Passenger group2 = new Passenger(tourists, krakow, tbilisi, testCityType);
        krakow.addPassengers(group2);
        Passenger group3 = new Passenger(business, budapest, naples, testCityType);
        budapest.addPassengers(group3);

        // Отправляем аэропорты
        List<Airport> testAirports = new ArrayList<>();
        testAirports.add(krakow);
        testAirports.add(naples);
        testAirports.add(budapest);
        testAirports.add(tbilisi);

        PackageInitAirports airportPacket = new PackageInitAirports(testAirports);
        mainClient.getPackage(airportPacket);

        // ==========================================
        // --- 4. НОВОЕ: ИГРОКИ И АВИАЛИНИИ ---
        // ==========================================

        // Создаем тестовых игроков (Я предполагаю, что у игрока есть пустой конструктор)
        Player playerYaroslav = new Player();
        playerYaroslav.name = "Yaroslav";
        playerYaroslav.money = 2000.0; // Богатый игрок

        Player player2 = new Player();
        player2.name = "Player 2";
        player2.money = 500.0;

        // Создаем типы авиалиний
        AirlineType cheapRoute = new AirlineType(
            1, 15.0, 1, 1,
            new ClosedInterval<>(1.0, 2.0), new ClosedInterval<>(50, 100),
            300.0, "Бюджетный маршрут"
        );

        AirlineType expensiveRoute = new AirlineType(
            2, 50.0, 2, 2,
            new ClosedInterval<>(3.0, 5.0), new ClosedInterval<>(150, 300),
            1200.0, "Элитный хаб-маршрут"
        );

        // Создаем сами линии (используем клиентский конструктор с ID)

        // 1. Свободная авиалиния (Krakow -> Naples), никто не купил
        Airline krakowNaples = new Airline(1, cheapRoute, krakow, naples, null);

        // 2. Купленная авиалиния (Budapest -> Tbilisi), принадлежит Ярославу
        Airline budapestTbilisi = new Airline(2, expensiveRoute, budapest, tbilisi, playerYaroslav);

        // 3. Свободная дорогая авиалиния (Krakow -> Budapest), для теста нехватки денег
        Airline krakowBudapest = new Airline(3, expensiveRoute, krakow, budapest, null);

        // Добавляем линии в список
        List<Airline> testAirlines = new ArrayList<>();
        testAirlines.add(krakowNaples);
        testAirlines.add(budapestTbilisi);
        testAirlines.add(krakowBudapest);

        // --- 5. ОТПРАВЛЯЕМ АВИАЛИНИИ В РЕНДЕР ---
        // Если у тебя еще нет отдельного пакета для авиалиний (PackageInitAirlines),
        // можешь временно передать их напрямую в рендерер через твой mainClient/mainDrawer:
        // mainDrawer.updateAirlines(testAirlines);

        // Но по-хорошему, нужно создать DTO-пакет:
        PackageInitAirlines airlinePacket = new PackageInitAirlines(testAirlines);
        mainClient.getPackage(airlinePacket);
    }
*/

}
