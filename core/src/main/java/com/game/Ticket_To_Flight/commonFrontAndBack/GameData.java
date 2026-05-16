package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.TemporarySetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameData {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void acquireReadLock() {
        lock.readLock().lock();
    }

    public void releaseReadLock() {
        lock.readLock().unlock();
    }

    public void acquireWriteLock() {
        lock.writeLock().lock();
    }

    public void releaseWriteLock() {
        lock.writeLock().unlock();
    }


    public static SetHolder<AirportType> airportTypes = new SetHolder<>();
    public static SetHolder<AirlineType> airlineTypes = new SetHolder<>();
    public static SetHolder<PlaneType> planeTypes = new SetHolder<>();
    public static SetHolder<CityType> cityTypes = new SetHolder<>();
    public static SetHolder<PassengerType> passengerTypes = new SetHolder<>();
    public static SetHolder<WorldEventType> worldEventTypes = new SetHolder<>();
    public static SetHolder<AbilityType> abilityTypes = new SetHolder<>();
    public static Integer maxActionsPerTurn = 5;
    public static Integer maxAmountOfShares = 20;

    private static boolean jsonDownloaded = false;
    private static String jsonFolder = "assets/StaticData";
    private static List<SetHolder<? extends Identifiable>> staticHolder = List.of(
        cityTypes,
        airlineTypes,
        airportTypes,
        passengerTypes,
        planeTypes
    );
    private static List<String> jsonNames = List.of(
        "CityTypes.json",
        "AirlineTypes.json",
        "AirportTypes.json",
        "PassengerTypes.json",
        "PlaneTypes.json"
    );
    private static List<Class<? extends Identifiable>> staticClasses = List.of(
        CityType.class,
        AirlineType.class,
        AirportType.class,
        PassengerType.class,
        PlaneType.class
    );

    public static void loadAllJsons() {
        if(jsonDownloaded) return;
        jsonDownloaded = true;
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < jsonNames.size(); i++) {
            String path = jsonFolder + File.separator + jsonNames.get(i);
            Class<? extends Identifiable> clazz = staticClasses.get(i);
            SetHolder<? extends Identifiable> holder = staticHolder.get(i);

            loadSingleJson(path, (SetHolder) holder, clazz, mapper);
        }
    }

    private static <T extends Identifiable> void loadSingleJson(String path, SetHolder<T> holder, Class<T> clazz, ObjectMapper mapper) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("File not found: " + path);
                return;
            }
            CollectionType setType = mapper.getTypeFactory().constructCollectionType(Set.class, clazz);
            Set<T> loadedData = mapper.readValue(file, setType);
            holder.clear();
            holder.addAll(loadedData);
            System.out.println("Successfully downloaded json [" + clazz.getSimpleName() + "]: " + holder.size());

        } catch (IOException e) {
            System.err.println("Error during json parsing for " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public enum State {
        WORLD_UPDATE,
        INVESTMENTS,
        AUCTION,
        ABILITIES,
        PLANES,
        AIRLINES,
        EVENT,
        FLIGHTS,
        INCOME,
        TAXES
    }
    public State currentState;
    public Player currentPlayer;

    public SetHolder<WorldEventType> worldEvents = new SetHolder<>();
    public SetHolder<Airport> airports = new SetHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public SetHolder<Player> players = new SetHolder<>();
    public SetHolder<Airline> availableAirlines = new SetHolder<>();
    public MapHolder<PlaneType, Integer> availablePlanes = new MapHolder<>(GameData.planeTypes);
    public MapHolder<Player, Integer> actionPoints = new MapHolder<>(players);
    public MapHolder<Player,Integer> amountOfShares = new MapHolder<>(players);


    public static class AirlineDTO extends Identifiable {
        private AirlineDTO(){super(0); type = 0; portA = 0; portB = 0; player = null;};
        private static final AtomicInteger idGenerator = new AtomicInteger(0);
        private final int type;
        private final int portA;
        private final int portB;
        private final Integer player;

        public AirlineDTO(Airline line){
            super(line.getId());
            this.type = line.type.getId();
            this.portA = line.portA.getId();
            this.portB = line.portB.getId();
            if(line.player!=null){
                this.player=line.player.getId();
            }
            else {
                this.player=null;
            }
        }

        /**
         * Should not be called anywhere except Low Level Handler
         */
        public AirlineDTO(AirlineType type, Airport portA, Airport portB) {
            super(idGenerator.incrementAndGet());
            this.type = type.getId();
            this.portA = portA.getId();
            this.portB = portB.getId();
            this.player = null;
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * UNSAFE
         */
        public AirlineDTO(Integer type, Integer portA, Integer portB) {
            super(idGenerator.incrementAndGet());
            this.type = type;
            this.portA = portA;
            this.portB = portB;
            this.player = null;
        }

        public Airline restore(SetHolder<Airport> lookUpAirports, SetHolder<Player> lookUpPlayers){
            AirlineType type = GameData.airlineTypes.get(this.type);
            Airport portA = lookUpAirports.get(this.portA);
            Airport portB = lookUpAirports.get(this.portB);
            if(type == null || portA == null || portB == null) return null;

            if(this.player==null)
                return new Airline(this.getId(), type, portA, portB, null);

            Player player = lookUpPlayers.get(this.player);
            if(player == null) return null;

            return new Airline(this.getId(), type, portA, portB, player);
        }
    }
    public static class AirportDTO extends Identifiable{
        private AirportDTO(){super(0); type = null; position = null; passengers = null; name = null;}
        private final Integer type;
        private final Vector2 position;
        private final Map<Integer, Integer> passengers;
        private final String name;
        public AirportDTO(Airport port) {
            super(port.getId());
            this.type = port.type.getId();
            this.position = port.position;
            this.passengers=new HashMap<>();
            for(Map.Entry<Integer,Integer> psg : port.passengers.entrySet()){
                this.passengers.put(psg.getKey(), psg.getValue());
            }
            this.name = port.airportName;
        }

        /**
         * Should not be called anywhere except Low Level Handler
         */
        public AirportDTO(int id, AirportType type, Vector2 position, String AirportName) {
            super(id);
            if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
            this.type = type.getId();
            this.position = position;
            this.name = AirportName;
            this.passengers = new MapHolder<>(GameData.passengerTypes);
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * UNSAFE
         */
        public AirportDTO(int id, Integer type, Vector2 position, String AirportName) {
            super(id);
            if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
            this.type = type;
            this.position = position;
            this.name = AirportName;
            this.passengers = new MapHolder<>(GameData.passengerTypes);
        }

        public Airport restore(){
            AirportType type = GameData.airportTypes.get(this.type);
            if(type == null) return null;
            MapHolder<PassengerType, Integer> passengers = new MapHolder<>(GameData.passengerTypes);
            try {
                passengers.putAll(this.passengers);
            }
            catch(Exception e){
                return null;
            }
            return new Airport(this.getId(), type, this.position, this.name);
        }
    }
    public static class PlayerDTO extends Identifiable{
        private PlayerDTO(){super(0); name = null; money = 0; income = 0; planes = null; airlines = null;}
        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        private final String name;
        private final double money;
        private final double income;
        private final Map<Integer, Integer> planes;
        private final Set<Integer> airlines;
        private Color color;

        public PlayerDTO(Player player) {
            super(player.getId());
            this.name = player.name;
            this.money = player.money;
            this.income = player.income;
            this.airlines = new HashSet<>();
            for(Airline line : player.airlines){
                this.airlines.add(line.getId());
            }
            this.planes = new HashMap<>();
            this.planes.putAll(player.planes);
        }

        public PlayerDTO(int id, String name, double money, double income, MapHolder<PlaneType, Integer> planes, SetHolder<Airline> airlines){
            super(id);
            this.name = name;
            this.money = money;
            this.income=income;
            this.airlines = new HashSet<>();
            for(Airline line : airlines){
                this.airlines.add(line.getId());
            }
            this.planes = new HashMap<>();
            this.planes.putAll(planes);
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * Creates player in default state
         */
        public PlayerDTO(String name){
            super( idGenerator.incrementAndGet());
            this.name = name;
            money = 0;
            income = 0;
            planes = new HashMap<>();
            airlines = new HashSet<>();
        }

        public Player restore(SetHolder<Airline> lookUpAirlines){
            SetHolder<Airline> lines = new SetHolder<>();
            for(Integer id : this.airlines ){
                Airline line = lookUpAirlines.get(id);
                if(line == null) return null;
                lines.add(line);
            }
            MapHolder<PlaneType, Integer> planes = new MapHolder<>(GameData.planeTypes);
            try {
                planes.putAll(this.planes);
            }
            catch(Exception e){
                return null;
            }
            return new Player(this.getId(), this.money, this.income, planes, lines, this.name, this.color);
        }
    }

    public static class DataChanges extends Identifiable {
        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        public  Set<PlayerDTO> playersToAdd = null;
        public  Set<Integer> playersToRemove= null;
        public  Set<AirportDTO> airportsToAdd= null;
        public  Set<Integer> airportsToRemove= null;
        public  Set<AirlineDTO> airlinesToAdd= null;
        public  Set<Integer> airlinesToRemove= null;
        public  Set<Integer> availableAirlinesToAdd= null;
        public  Set<Integer> availableAirlinesToRemove= null;


        public  GameData.State currentState= null;
        public  Integer currentPlayer= null;
        public  Set<Integer> newWorldEvents= null;


        public  Map<Integer, Integer> availablePlanesToRemove= null;
        public  Map<Integer, Integer> availablePlanesToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> airportPassengersToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> airportPassengersToRemove= null;


        public  Map<Integer, Double> playerMoneyChange= null;
        public  Map<Integer, Double> playerIncomeChange= null;
        public  Map<Integer, Integer> playerActionPointsChange= null;
        public  Map<Integer, Integer> playerAmountOfSharesChange= null;

        public  Map<Integer, Set<Integer>> playerAirlinesToAdd= null;
        public  Map<Integer, Set<Integer>> playerAirlinesToRemove= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToRemove= null;

        public DataChanges(){super(idGenerator.incrementAndGet());}

        /**
         * Merges DataChanges. "other" should not be used after merging
         * @param other - source
         * @return this
         */
        public DataChanges merge(DataChanges other) {
            if (other == null) return this;

            if (other.currentState != null) this.currentState = other.currentState;
            if (other.currentPlayer != null) this.currentPlayer = other.currentPlayer;

            this.newWorldEvents = SetHolder.merge(this.newWorldEvents, other.newWorldEvents);
            this.availableAirlinesToAdd = SetHolder.merge(this.availableAirlinesToAdd, other.availableAirlinesToAdd);
            this.availableAirlinesToRemove = SetHolder.merge(this.availableAirlinesToRemove, other.availableAirlinesToRemove);
            this.playersToAdd = SetHolder.merge(this.playersToAdd, other.playersToAdd);
            this.playersToRemove = SetHolder.merge(this.playersToRemove, other.playersToRemove);
            this.airportsToAdd = SetHolder.merge(this.airportsToAdd, other.airportsToAdd);
            this.airportsToRemove = SetHolder.merge(this.airportsToRemove, other.airportsToRemove);
            this.airlinesToAdd = SetHolder.merge(this.airlinesToAdd, other.airlinesToAdd);
            this.airlinesToRemove = SetHolder.merge(this.airlinesToRemove, other.airlinesToRemove);

            this.airportPassengersToAdd = MapHolder.merge(
                this.airportPassengersToAdd, other.airportPassengersToAdd, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.airportPassengersToRemove = MapHolder.merge(
                this.airportPassengersToRemove, other.airportPassengersToRemove, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.availablePlanesToRemove = MapHolder.merge(
                this.availablePlanesToRemove, other.availablePlanesToRemove, v -> v, DataChanges::sumIntOrNull);

            this.availablePlanesToAdd = MapHolder.merge(
                this.availablePlanesToAdd, other.availablePlanesToAdd, v -> v, DataChanges::sumIntOrNull);

            this.playerMoneyChange = MapHolder.merge(
                this.playerMoneyChange, other.playerMoneyChange, v -> v, DataChanges::sumDoubleOrNull);

            this.playerIncomeChange = MapHolder.merge(
                this.playerIncomeChange, other.playerIncomeChange, v -> v, DataChanges::sumDoubleOrNull);

            this.playerActionPointsChange = MapHolder.merge(
                this.playerActionPointsChange, other.playerActionPointsChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAmountOfSharesChange = MapHolder.merge(
                this.playerAmountOfSharesChange, other.playerAmountOfSharesChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAirlinesToAdd = MapHolder.merge(
                this.playerAirlinesToAdd, other.playerAirlinesToAdd, v->v,
                SetHolder::merge
            );

            this.playerAirlinesToRemove = MapHolder.merge(
                this.playerAirlinesToRemove, other.playerAirlinesToRemove, v->v,
                SetHolder::merge
            );

            this.playerPlanesToAdd = MapHolder.merge(
                this.playerPlanesToAdd, other.playerPlanesToAdd, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.playerPlanesToRemove = MapHolder.merge(
                this.playerPlanesToRemove, other.playerPlanesToRemove, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );
            return this;
        }

        private static Integer sumIntOrNull(Integer a, Integer b) {
            int valA = (a != null) ? a : 0;
            int valB = (b != null) ? b : 0;
            int res = valA + valB;
            return res == 0 ? null : res;
        }

        private static Double sumDoubleOrNull(Double a, Double b) {
            double valA = (a != null) ? a : 0;
            double valB = (b != null) ? b : 0;
            double res = valA + valB;
            return Math.abs(res) < 1e-9 ? null : res;
        }
    }

    public void applyChangesUnsafe(DataChanges changes){
        if (changes.currentState != null) this.currentState = changes.currentState;
        if (changes.currentPlayer != null) this.currentPlayer = players.get(changes.currentPlayer);
        worldEvents.clearAndAddAllFromLookUp(changes.newWorldEvents, GameData.worldEventTypes);
        airports.changeSetDTOI(changes.airportsToAdd, changes.airportsToRemove,
            (dto)->dto.restore());
        airlines.changeSetDTOI(changes.airlinesToAdd, changes.airlinesToRemove,
            (dto)->dto.restore(this.airports, this.players));
        players.changeSetDTOI(changes.playersToAdd, changes.playersToRemove,
            (dto)->dto.restore(this.airlines));
        availableAirlines.changeSetII(changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, this.airlines);
        availableAirlines.retainAll(airlines);

        availablePlanes.merge(Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
            (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
            (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
            (i)->0);

        actionPoints.merge(changes.playerActionPointsChange, (o)->o, (o, n)->n+o);
        actionPoints.removeAllRefsToNotExistingObjects();

        amountOfShares.merge(changes.playerAmountOfSharesChange, (o)->o, (o, n)->n+o);
        amountOfShares.removeAllRefsToNotExistingObjects();

        players.changeAsStructWithSetterInteger(Player::setIncome, Player::getIncome,
            changes.playerIncomeChange, (f, s)->f+s);
        players.changeAsStructWithSetterInteger(Player::setMoney, Player::getMoney,
            changes.playerMoneyChange, (f, s)->f+s);
        players.changeAsStructInteger((pl) -> pl,
            Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
            (cur, params)-> {
            cur.airlines.changeSetII(params.get(0), params.get(1), airlines,
                (line)->line.player=null,
                (line)->{line.player = cur; return line;});
            cur.airlines.retainAll(airlines);
        });
        players.changeAsStructInteger((pl) -> pl.planes,
            Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
            (f, s)-> f.merge(s,
                (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

        airports.changeAsStructInteger((pl) -> pl.passengers,
            Arrays.asList(changes.airportPassengersToAdd, changes.airportPassengersToRemove),
            (f, s)-> f.merge(s,
                (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

    }

    public boolean checkChanges(DataChanges changes){
        if( !GameData.worldEventTypes.containsAll(changes.newWorldEvents) ||
            !players.checkChangeSetTI(changes.playersToAdd, changes.playersToRemove) ||
            !airports.checkChangeSetTI(changes.airportsToAdd, changes.airportsToRemove) ||
            !airlines.checkChangeSetTI(changes.airlinesToAdd, changes.airlinesToRemove)
        ) return false;

        SetHolder<Airport> airportsTmp = TemporarySetHolder.generateTemporarySetHolder(
          airports, changes.airportsToAdd, changes.airportsToRemove,
            (dto)->dto.restore());
        SetHolder<Airline> airlinesTmp = TemporarySetHolder.generateTemporarySetHolder(
            airlines, changes.airlinesToAdd, changes.airlinesToRemove,
            (dto)->dto.restore(airportsTmp, this.players));
        SetHolder<Player> playersTmp = TemporarySetHolder.generateTemporarySetHolder(
            players, changes.playersToAdd, changes.playersToRemove,
            (dto)->dto.restore(airlinesTmp));

        if (changes.currentPlayer != null && !playersTmp.contains(changes.currentPlayer)) return false;

        if( !availableAirlines.checkChangeSetIILookUp(
            changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, airlinesTmp) ||
            !GameData.planeTypes.containsAllKeys(changes.availablePlanesToAdd) ||
            !availablePlanes.checkMergeElements(
                Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
                (params)->params.get(0)-params.get(1)>=0,
                (old, params)->old+params.get(0)-params.get(1)>=0,
                (i)->0
            ) ||
            !playersTmp.checkChangeAsStructInteger(Player::getMoney, changes.playerMoneyChange,
                (current, change) -> current + change >= 0) ||
            !playersTmp.containsAllKeys(changes.playerActionPointsChange) ||
            !playersTmp.containsAllKeys(changes.playerAmountOfSharesChange) ||
            !actionPoints.checkMergeElements(changes.playerActionPointsChange, null,
                (o, n)->o+n>=0 && o+n<=GameData.maxActionsPerTurn) ||
            !amountOfShares.checkMergeElements(changes.playerAmountOfSharesChange, null,
                (o, n)->o+n>=0 && o+n<=GameData.maxAmountOfShares) ||
            !playersTmp.checkChangeAsStructInteger((pl)->pl.airlines,
                Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
                (f,s)->f.checkChangeSetIILookUp(s.get(0), s.get(1), airlinesTmp)
            )||
            !playersTmp.checkChangeAsStructInteger((pl)->pl.planes,
                Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
                (f,s)->f.checkMergeElements(s,
                    (params)->params.get(0)-params.get(1)>=0,
                    (old, params)->old+params.get(0)-params.get(1)>=0,
                    (i)->0
                ) && GameData.planeTypes.containsAll(s.get(0).keySet())
            ) ||
            !airportsTmp.checkChangeAsStructInteger((port)->port.passengers,
                Arrays.asList(changes.airportPassengersToAdd, changes.airportPassengersToRemove),
                (f,s)->f.checkMergeElements(s,
                    (params)->params.get(0)-params.get(1)>=0,
                    (old, params)->old+params.get(0)-params.get(1)>=0,
                    (i)->0
                ) && GameData.passengerTypes.containsAll(s.get(0).keySet())
            )
        ) return false;


        return true;
    }

    public boolean applyChanges(DataChanges changes) {
        if (changes == null) return true;

        if(!checkChanges(changes)) return false;

        applyChangesUnsafe(changes);

        return true;
    }











    //returns null if requirements not met, otherwise returns total income
    public static Double checkLine(Airline line, MapHolder<PassengerType, Integer> passengers, PlaneType plane){
        if(!line.type.luxuryRange.contains(plane.luxury) ||
            !line.type.capacityRange.contains(plane.capacity) ||
            !plane.distRange.contains(line.getDistance()) ||
            !plane.gateRange.contains(line.type.gateA) ||
            !plane.gateRange.contains(line.type.gateB) ||
            line.player == null)
            return null;
        int amountOfPeople = 0;
        double solvencySum = 0;

        Iterator<Map.Entry<PassengerType, Integer>> id = MapHolder.viewAsEntrySet(passengers);
        Map.Entry<PassengerType, Integer> e;
        while((e=id.next()) != null) {
            PassengerType type = e.getKey();
            int count = e.getValue();
            if(!type.luxuryRange.contains(plane.luxury) ||
                !type.capacityRange.contains(plane.capacity) ||
                !type.yieldRange.contains(line.type.yield))
                return null;
            amountOfPeople += type.size * count;
            solvencySum += type.solvency * count;
        }

        if(plane.capacity< amountOfPeople) return null;

        return plane.price * solvencySum * line.type.yield;
    }

    //does not check if a player has a plane and does not add removing the plane from the player
    /*
    public static DataChanges checkRoute(Airport start, List<Airline> route, MapHolder<Airport, MapHolder<PassengerType, Integer>> passengers, PlaneType plane){
        if(start == null || route == null || passengers == null ||
            route.isEmpty() || passengers.isEmpty() || plane==null) return null;

        if(route.size() > plane.stations + 1) return null;

        double amountOfFuel = 0;
        for(Airline line : route)
            amountOfFuel += line.getDistance();
        if(plane.fuel < amountOfFuel) return null;

        DataChanges resultChanges = new DataChanges();
        resultChanges.playerIncomeChange = new HashMap<>();
        resultChanges.airportPassengersToRemove = new HashMap<>();

        Iterator<Airline> airlineIterator = route.listIterator();

        class BoardedPassengers{
            private final List<Passenger> psgs = new LinkedList<>();
            private final List<Integer> arpts = new LinkedList<>();
            private int currentPort = 0;

            public void nextPort() {currentPort++;}

            public void add(Passenger p) {
                psgs.add(p);
                arpts.add(currentPort);
            }

            //true if everything good
            public boolean removeArrivedAndCheck(Airport current) {
                ListIterator<Passenger> pIt = psgs.listIterator();
                ListIterator<Integer> aIt = arpts.listIterator();
                while (pIt.hasNext()) {
                    Passenger p = pIt.next();
                    Integer port = aIt.next();
                    if (p.doesAirportFit(current)) {
                        if(!p.type.stationsRange.contains(currentPort - port - 1))
                            return false;
                        pIt.remove();
                        aIt.remove();
                    }
                }
                return true;
            }

            public List<Passenger> getPassengers(){
                return psgs;
            }

            public boolean isEmpty(){
                return psgs.isEmpty();
            }
        }
        BoardedPassengers boardedPassengers = new BoardedPassengers();


        Airport currentAirport = start;
        while(airlineIterator.hasNext()){

            boardedPassengers.nextPort();
            if(!boardedPassengers.removeArrivedAndCheck(currentAirport)) return null;

            while(passengersIterator.hasNext()){
                Passenger psg = passengersIterator.next();
                if(psg.portFrom == currentAirport) {
                    boardedPassengers.add(psg);
                    resultChanges.passengersToRemove.add(psg.getId());
                }
                else
                    break;
            }

            Airline line = airlineIterator.next();

            Double checkRes = checkLine(line, boardedPassengers.getPassengers(), plane);
            if(checkRes == null) return null;

            resultChanges.playerIncomeChange.put(line.player.getId(), resultChanges.playerIncomeChange.get(line.player.getId()) + checkRes);

            currentAirport = line.getAnotherEnd(currentAirport);
            if(currentAirport==null) return null;
        }
        boardedPassengers.nextPort();
        if(!boardedPassengers.removeArrivedAndCheck(currentAirport) ||
            !boardedPassengers.isEmpty()) return null;
        return resultChanges;
    }
    */

}
