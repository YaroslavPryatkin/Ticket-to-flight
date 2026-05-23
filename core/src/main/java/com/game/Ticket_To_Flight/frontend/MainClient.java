package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.MainMenu.MainMenuRenderer;
import com.game.Ticket_To_Flight.frontend.UI.screens.ConnectionScreen.ConnectionRenderer;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.WorldMapRenderer;
import com.game.Ticket_To_Flight.network.Network;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront.Flags;

public class MainClient {
    private final Game myGame;
    private final GameData gameData = new GameData();
    private final LowLevelHandlerFront llh = new LowLevelHandlerFront(gameData, this);
    private WorldMapRenderer worldMapRenderer;
    private ConnectionRenderer connectionRenderer;

    public MainClient(Game gm){
        this.myGame = gm;
        this.connectionRenderer = new ConnectionRenderer(myGame, llh, this);
        this.myGame.setScreen(this.connectionRenderer);
    }

    public void mainCycleWithUpdate(float delta) {
        llh.update();
        gameData.acquireReadLock();
        mainCycle(delta);
        gameData.releaseReadLock();
    }

    private void mainCycle(float delta){
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.RUNNING) {
            if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                //this.myGame.setScreen(this.worldMapRenderer);
            } else if (gameData.currentState == GameData.State.INVESTMENTS && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawInvestmentWindow();
                    llh.flags.currentStateState = LowLevelHandlerFront.Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.AUCTION && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawAuctionWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
                worldMapRenderer.drawAuctionWindow();
            } else if (gameData.currentState == GameData.State.ABILITIES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawAbilitiesWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.PLANES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawPlaneWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.AIRLINES && llh.getMyId() == gameData.currentPlayer) {
                // mainDrawer.reDrawAirlinesWindow();
            } else if (gameData.currentState == GameData.State.EVENT && llh.getMyId() == gameData.currentPlayer) {
                //mainDrawer.eventWindow();
            } else if (gameData.currentState == GameData.State.FLIGHTS && llh.getMyId() == gameData.currentPlayer) {
                // smth
            } else if (gameData.currentState == GameData.State.INCOME && llh.getMyId() == gameData.currentPlayer) {
                // change Income
            } else if (gameData.currentState == GameData.State.TAXES && llh.getMyId() == gameData.currentPlayer) {
                // Taxes
            }
        }
        worldMapRenderer.renderNoLogic(delta);
    }


    public void mainPreparationCycle(float delta) {
        llh.update();
        gameData.acquireReadLock();
        GamePreparationStage();
        gameData.releaseReadLock();
    }


    private void GamePreparationStage() {
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL) {
            llh.connectToServer();
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.SEARCHING_FOR_SERVER) {
            connectionRenderer.showLoadingScreen("Searching for server");
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME) {
            if (llh.flags.joinGameResponse == null) {
                connectionRenderer.showNicknameInput();
            }
            else if(llh.flags.joinGameResponse == Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS){
                connectionRenderer.setInputIsPrinted(false);
                connectionRenderer.showNicknameInput();
            }

        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE){
            connectionRenderer.showLoadingScreen("Waiting for server response");
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_OTHER_PLAYERS_TO_JOIN){
            connectionRenderer.showLoadingScreen("Waiting for other players");
        }
    }

    public void changeScreenToRunning(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                worldMapRenderer = new WorldMapRenderer(MainClient.this);
                myGame.setScreen(worldMapRenderer);
            }
        });
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
