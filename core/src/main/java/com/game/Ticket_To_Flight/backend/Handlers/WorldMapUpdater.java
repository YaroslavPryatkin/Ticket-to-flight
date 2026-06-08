package com.game.Ticket_To_Flight.backend.Handlers;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.io.Reader;
import java.util.*;

public class WorldMapUpdater {

    private final String presetsDirectory;
    private final GameData gameData;
    private final DataChangesCreator dataChangesCreator;
    private final Map<Integer, List<PrefixProbability>> passengerPrefixes = new HashMap<>();
    private final Random random = new Random();

    private final ObjectMapper mapper = new ObjectMapper();

    public WorldMapUpdater(String presetsDirectory, GameData gameData, DataChangesCreator dataChangesCreator) {
        this.presetsDirectory = presetsDirectory;
        this.gameData = gameData;
        this.dataChangesCreator = dataChangesCreator;
        loadPassengerProbabilities();
    }

    private static class PrefixProbability {
        final int type;
        final double prefixSum;

        PrefixProbability(int type, double prefixSum) {
            this.type = type;
            this.prefixSum = prefixSum;
        }
    }



    private void loadPassengerProbabilities() {
        String filePath = presetsDirectory + "/passengers.json";
        try (Reader reader = Gdx.files.internal(filePath).reader("UTF-8")) {
            JsonNode rootArray = mapper.readTree(reader);

            if (rootArray != null && rootArray.isArray()) {
                for (JsonNode node : rootArray) {
                    int airportId = node.get("airportId").asInt();
                    JsonNode probsNode = node.get("passengerProbability");

                    List<PrefixProbability> prefixes = new ArrayList<>();
                    double currentSum = 0.0;

                    Iterator<Map.Entry<String, JsonNode>> fields = probsNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        currentSum += entry.getValue().asDouble();
                        prefixes.add(new PrefixProbability(Integer.parseInt(entry.getKey()), currentSum));
                    }
                    passengerPrefixes.put(airportId, prefixes);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load passengers.json: " + e.getMessage());
        }
    }

    public boolean loadRound() {
        int roundNumber = gameData.roundNumber;
        String filePath = presetsDirectory + "round_" + roundNumber + ".json";
        try (Reader reader = Gdx.files.internal(filePath).reader("UTF-8")) {
            JsonNode root = mapper.readTree(reader);
            if (root == null) return false;
            Set<Integer> newlyAddedAirports = new HashSet<>();

            if (root.has("airports")) {
                parseAirports(root.get("airports"), newlyAddedAirports);
            }
            if (root.has("airlines")) {
                parseAirlines(root.get("airlines"));
            }
            if (root.has("planes")) {
                parsePlanes(root.get("planes"));
            }
            if (root.has("amountOfAirportsToAddPassengers")) {
                int amount = root.get("amountOfAirportsToAddPassengers").asInt();
                generatePassengers(amount, newlyAddedAirports);
            }

            return true;
        } catch (Exception e) {
            System.out.println("Preset for round " + roundNumber + " not found or skipped. Cause " + e.getMessage() );
            return false;
        }
    }

    private void parseAirports(JsonNode arrayNode, Set<Integer> newlyAdded) {
        if (!arrayNode.isArray()) return;

        for (JsonNode node : arrayNode) {
            int id = node.get("id").asInt();
            int type = node.get("type").asInt();
            if(StaticGameData.airportTypes.contains(type)) {
                int x = node.get("x").asInt();
                int y = node.get("y").asInt();
                String name = node.get("name").asText();

                dataChangesCreator.addAirport(id, type, x, y, name);
                newlyAdded.add(id);
            }
        }
    }

    private void parseAirlines(JsonNode arrayNode) {
        if (!arrayNode.isArray()) return;

        for (JsonNode node : arrayNode) {
            int type = node.get("type").asInt();
            if(StaticGameData.airlineTypes.contains(type))
                dataChangesCreator.addAirline(type, node.get("portA").asInt(), node.get("portB").asInt());
        }
    }

    private void parsePlanes(JsonNode arrayNode) {
        if (!arrayNode.isArray()) return;

        for (JsonNode node : arrayNode) {
            int type = node.get("type").asInt();
            if(StaticGameData.planeTypes.contains(type))
                dataChangesCreator.addAvailablePlanes(type, node.get("amount").asInt());
        }
    }

    private void generatePassengers(int iterations, Set<Integer> newlyAddedAirports) {
        List<Integer> validAirports = new ArrayList<>();

        for (Airport port : gameData.airports) {
            if (passengerPrefixes.containsKey(port.getId())) validAirports.add(port.getId());
        }
        for (Integer newId : newlyAddedAirports) {
            if (passengerPrefixes.containsKey(newId)) validAirports.add(newId);
        }

        if (validAirports.isEmpty()) return;


        Map<Integer, Double> currentWeights = new HashMap<>();
        double totalWeight = 0.0;

        for (Integer airportId : validAirports) {
            currentWeights.put(airportId, 1.0);
            totalWeight += 1.0;
        }


        for (int i = 0; i < iterations; i++) {
            if (totalWeight <= 0) break;


            double randomValue = random.nextDouble() * totalWeight;
            Integer selectedAirport = null;

            for (Map.Entry<Integer, Double> entry : currentWeights.entrySet()) {
                randomValue -= entry.getValue();
                if (randomValue <= 0) {
                    selectedAirport = entry.getKey();
                    break;
                }
            }
            if (selectedAirport == null) selectedAirport = validAirports.getLast();

            Integer passengerType = pickPassengerType(selectedAirport);
            if (passengerType != null) {
                dataChangesCreator.addPassenger(selectedAirport, passengerType);

                double oldWeight = currentWeights.get(selectedAirport);
                double newWeight = nextWeight(oldWeight);

                currentWeights.put(selectedAirport, newWeight);
                totalWeight += (newWeight - oldWeight);
            }
        }
    }

    private double nextWeight(double currentWeight) {

        if (currentWeight > 0.9) return 0.7;
        if (currentWeight > 0.6) return 0.5;
        if (currentWeight > 0.4) return 0.2;
        return 0.05;
    }

    private Integer pickPassengerType(int airportId) {
        List<PrefixProbability> prefixes = passengerPrefixes.get(airportId);
        if (prefixes == null || prefixes.isEmpty()) return null;

        double totalProb = prefixes.getLast().prefixSum;
        double randomValue = random.nextDouble() * totalProb;

        for (PrefixProbability p : prefixes) {
            if (randomValue <= p.prefixSum) {
                return p.type;
            }
        }

        return prefixes.getLast().type;
    }
}
