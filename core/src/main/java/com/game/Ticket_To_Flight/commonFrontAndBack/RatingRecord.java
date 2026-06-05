package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;

public class RatingRecord implements Comparable<RatingRecord> {
    int player;
    int rating;
    public Player getPlayer(GameData gameData){
        return gameData.players.get(player);
    }

    public int getRating(){
        return rating;
    }

    private RatingRecord(){
        player = 0;
        rating = 0;
    }
    public RatingRecord(Player player){
        this.player = player.getId();
        this.rating = player.income + player.money/2;
    }

    @Override
    public int compareTo(RatingRecord other) {
        int result = Integer.compare(other.getRating(), this.getRating());
        if (result == 0) {
            result = Integer.compare(this.player, other.player);
        }
        return result;
    }

}
