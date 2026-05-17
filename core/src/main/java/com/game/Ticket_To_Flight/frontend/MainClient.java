package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.network.Network;
import com.game.Ticket_To_Flight.frontend.UI.MainDrawer;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront.Flags;

public class MainClient {
    private final Game myGame;
    private final GameData gameData = new GameData();
    private final LowLevelHandlerFront llh = new LowLevelHandlerFront(gameData);
    private final MainDrawer mainDrawer;

    public MainClient(Game gm){
        this.myGame = gm;
        this.mainDrawer = new MainDrawer(myGame, this);
    }

    public void mainCycleWithUpdate(float delta){
        llh.update();
        gameData.acquireReadLock();
        mainCycle(delta);
    }


    private void mainCycle(float delta){
        if(llh.flags.gamePreparationsState != Flags.GamePreparationsState.RUNNING){
            GamePraparationStage();
        }
        else {
            if(gameData.currentState == GameData.State.WORLD_UPDATE){
                // mainDrawer.createWorldMap(this);
            }
            /*if (llh.getMyId() == gameData.currentPlayer) {
                if (gameData.currentState == GameData.State.INVESTMENTS) {
                    //ask for shares
                    mainDrawer.drawInvestmentWindow();
                }

                if (gameData.currentState == GameData.State.AUCTION) {
                    mainDrawer.drawAuctionWindow();
                }
            }*/


        }
    }


    private void GamePraparationStage(){
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL){
            llh.connectToServer();
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.SEARCHING_FOR_SERVER){
            //ui крутить колесико
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME){
            if(llh.flags.joinGameResponse == null) {

                //ui спросить имя
                llh.sendJoinRequest("test");
            }
            else if(llh.flags.joinGameResponse == Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS){
                //ui спросить игрока еще раз
                llh.sendJoinRequest("other name");
            }

        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE){
            //ui крутить колесико
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_OTHER_PLAYERS_TO_JOIN){
            //ui ждем игроков
        }
    }


    public GameData getGameData(){return gameData;}

    public LowLevelHandlerFront getLlh() {
        return this.llh;
    }

    /*
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

     */
}
