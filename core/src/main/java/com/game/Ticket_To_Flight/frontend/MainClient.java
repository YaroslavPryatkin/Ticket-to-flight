package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.CityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.game.Ticket_To_Flight.frontend.UI.MainDrawer;
import com.game.Ticket_To_Flight.packages.PackageInitAirlines;
import com.game.Ticket_To_Flight.packages.PackageInitAirports;

import java.util.ArrayList;
import java.util.List;

public class MainClient {
    private MainDrawer mainDrawer;
    private final GameData gameData = new GameData();
    private final LowLevelHandlerFront llh = new LowLevelHandlerFront(gameData);

    public void setMainDrawer(MainDrawer mainDrawer) {
        this.mainDrawer = mainDrawer;
    }

    public void sendWorldMapPacket() {
        PackageCreateWorldMap mapPacket = new PackageCreateWorldMap("EuropeMap.png", 1920f, 1080f);
        mainDrawer.drawWorldMap(mapPacket);

        // --- 1. ТИПЫ АЭРОПОРТОВ И ГОРОДОВ ---
        CityType testCityType = new CityType(1, "Krakow");
        AirportType regionalType = new AirportType(1, 500.0, 2, testCityType, "Региональный");
        AirportType internationalType = new AirportType(2, 1500.0, 5, testCityType, "Хаб");

        // --- 2. ПАССАЖИРЫ ---
        PassengerType tourists = new PassengerType(
            1, 1.5, 3,
            new ClosedInterval<>(1.0, 2.0), new ClosedInterval<>(10.0, 50.0),
            new ClosedInterval<>(1, 10), new ClosedInterval<>(1, 5), testCityType, "Туристы"
        );

        PassengerType business = new PassengerType(
            2, 3.0, 1,
            new ClosedInterval<>(2.0, 3.0), new ClosedInterval<>(50.0, 150.0),
            new ClosedInterval<>(1, 5), new ClosedInterval<>(1, 3), testCityType, "Бизнесмены"
        );

        // --- 3. АЭРОПОРТЫ ---
        Airport krakow = new Airport(1, regionalType, new Vector2(1050f, 480f), "Krakow");
        Airport naples = new Airport(2, regionalType, new Vector2(980f, 200f), "Naples");
        Airport budapest = new Airport(3, internationalType, new Vector2(1120f, 420f), "Budapest");
        Airport tbilisi = new Airport(4, internationalType, new Vector2(1800f, 250f), "Tbilisi");

        krakow.passengers.put(tourists, 2);

        // В Будапеште была 1 группа бизнесменов (бывшая group3):
        budapest.passengers.put(business, 1);

        // Отправляем аэропорты
        List<Airport> testAirports = new ArrayList<>();
        testAirports.add(krakow);
        testAirports.add(naples);
        testAirports.add(budapest);
        testAirports.add(tbilisi);

        PackageInitAirports airportPacket = new PackageInitAirports(testAirports);
        mainDrawer.drawAirports(airportPacket);

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
        mainDrawer.drawAirlines(airlinePacket);
    }
}
