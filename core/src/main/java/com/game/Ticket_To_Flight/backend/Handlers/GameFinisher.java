package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.RatingRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameFinisher {
    public static List<RatingRecord> getRating(GameData gameData){
        List<RatingRecord> res = new ArrayList<>();
        for(Player pl : gameData.players){
            res.add(new RatingRecord(pl));
        }
        Collections.sort(res);
        return res;
    }
}
