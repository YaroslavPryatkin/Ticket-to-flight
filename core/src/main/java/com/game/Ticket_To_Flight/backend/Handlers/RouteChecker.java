package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.RouteDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.Map;

public class RouteChecker {
    private final GameData gameData;
    private final DataChangesCreator dataChangesCreator;
    private Route rt = null;

    public RouteChecker(GameData gameData, DataChangesCreator dataChangesCreator){
        this.gameData= gameData;
        this.dataChangesCreator = dataChangesCreator;
    }

    public boolean downloadAndCheckDTO(RouteDTO dto){
        Airport st = gameData.airports.get(dto.getStartingPort());
        PlaneType plane = StaticGameData.planeTypes.get(dto.getPlane());
        if(st == null || plane == null ||
            gameData.players.get(gameData.currentPlayer).planes.getOrDefault(plane, 0) <= 0
        ) {rt = null; return false;}
        rt = new Route(plane, gameData, st);

        for(Integer lineId : dto.getLines()){

            Airport cur = rt.getCurrentAirport();
            for(Map.Entry<Integer, Integer> e : dto.getPassengers().get(cur.getId()).entrySet()){
                PassengerType type = StaticGameData.passengerTypes.get(e.getKey());
                if(type == null) {rt = null; return false;}
                for(int i=0;i<e.getValue();++i){
                    if(rt.addPassenger(type)!=null) {
                        rt = null;
                        return false;
                    }
                }
            }

            Airline line = gameData.airlines.get(lineId);
            if(line == null || rt.makeFlight(line)!=null) {
                rt = null;
                return false;
            }
        }
        return rt.canFinishRoute();
    }

    public void applyRT(){
        if(rt == null) return;
        dataChangesCreator.takePlane(rt.plane.getId());
        dataChangesCreator.removePassengers(rt.getBoardedPerPort());
        dataChangesCreator.incomeGainFromRoute(rt.getIncomeChange());
        rt = null;
    }

    public void clearRT(){
        rt = null;
    }
}
