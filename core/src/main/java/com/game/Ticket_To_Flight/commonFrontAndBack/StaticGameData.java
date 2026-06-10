package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * static class
 */
public class StaticGameData {
    public static SetHolder<AirportType> airportTypes = new SetHolder<AirportType>();
    public static SetHolder<AirlineType> airlineTypes = new SetHolder<AirlineType>();
    public static SetHolder<PlaneType> planeTypes = new SetHolder<PlaneType>();
    public static SetHolder<CityType> cityTypes = new SetHolder<CityType>();
    public static SetHolder<PassengerType> passengerTypes = new SetHolder<PassengerType>();
    public static SetHolder<WorldEventType> worldEventTypes = new SetHolder<WorldEventType>();
    public static SetHolder<AbilityType> abilityTypes = new SetHolder<AbilityType>();
    public static Integer maxActionsPerTurn = 5;
    public static Integer maxAmountOfShares = 20;
    public static Integer amountOfRounds = 1;
    public static Integer minimalAuctionBetIncrease = 40;
    public static Integer plusMoneyPerShare = 300;
    public static Integer minusIncomePerShare = 100;

    public static Integer abilityActionPointsIncrease = 2;
    public static Double abilityIncomeMultiplier = 1.2;

    private StaticGameData(){}

    private static boolean jsonDownloaded = false;
    private static String jsonFolder = "assets/StaticData";
    private static List<SetHolder<? extends Identifiable>> staticHolder = List.of(
        cityTypes,
        airlineTypes,
        airportTypes,
        passengerTypes,
        planeTypes,
        abilityTypes
    );
    private static List<String> jsonNames = List.of(
        "CityTypes.json",
        "AirlineTypes.json",
        "AirportTypes.json",
        "PassengerTypes.json",
        "PlaneTypes.json",
        "AbilityTypes.json"
    );
    private static List<Class<? extends Identifiable>> staticClasses = List.of(
        CityType.class,
        AirlineType.class,
        AirportType.class,
        PassengerType.class,
        PlaneType.class,
        AbilityType.class
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
                System.err.println("File not found: [" + path + "]");
                return;
            }
            CollectionType setType = mapper.getTypeFactory().constructCollectionType(Set.class, clazz);
            Set<T> loadedData = mapper.readValue(file, setType);
            holder.clear();
            holder.addAll(loadedData);
            // System.out.println("Successfully downloaded json [" + clazz.getSimpleName() + "]: " + holder.size());

        } catch (IOException e) {
            System.err.println("Error during json parsing for [" + path + "]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
