package com.game.Ticket_To_Flight.backend.Handlers;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData.State;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.ArrayList;
import java.util.List;

public class StateIterator {
    private final GameData gameData;
    private final DataChangesCreator dataChangesCreator;
    private final TurnOrderIterator turnOrderIterator;
    private final LowLevelHandlerBack.Flags flags;
    private final LowLevelHandlerBack llh;
    private int round = 1;

    public StateIterator(
        GameData gameData,
        DataChangesCreator dataChangesCreator,
        LowLevelHandlerBack.Flags flags,
        LowLevelHandlerBack llh
    ){
        this.gameData= gameData;
        turnOrderIterator = new TurnOrderIterator(gameData);
        this.dataChangesCreator = dataChangesCreator;
        this.flags = flags;
        this.llh = llh;
    }

    public boolean nextState(){
        llh.applyAndSendDataChanges(); // sending changes from main logic
        removeAllDisconnectedPlayers();
        if(gameData.currentState == GameData.State.NO_STATE){
            nonPlayerStateToNonPlayerState(State.WORLD_UPDATE);
             dataChangesCreator.setCurrentRound(round);
             flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.NO_PLAYER_STAGE;
             if(round == 1){
                 dataChangesCreator.resetAllAbilities();
                 dataChangesCreator.resetActionPoints();
                 dataChangesCreator.setDefaultTurnOrder();
             }
        }
        else if(gameData.currentState == GameData.State.WORLD_UPDATE) {
            nonPlayerStateToPlayerState(State.INVESTMENTS, null);
        }
        else if (gameData.currentState == GameData.State.INVESTMENTS) {
            playerStateToPlayerState(State.AUCTION, null);
        }
        else if (gameData.currentState == GameData.State.AUCTION) {
            // finished auction, which changes turn order, will not brake it since that means all
            // players have passed and nextPlayer() will return null anyway
            playerStateToPlayerState(State.ABILITIES, null);
        }
        else if (gameData.currentState == GameData.State.ABILITIES) {
            playerStateToPlayerState(State.PLANES, 1);
        }
        else if (gameData.currentState == GameData.State.PLANES) {
             playerStateToPlayerState(State.AIRLINES, 2);
        }
        else if (gameData.currentState == GameData.State.AIRLINES) {
            //playerStateToNonPlayerState(State.EVENT);
            playerStateToPlayerState(State.FLIGHTS, 3);
        }
        else if (gameData.currentState == GameData.State.EVENT) {
             nonPlayerStateToPlayerState(State.FLIGHTS, 3);
        }
        else if (gameData.currentState == GameData.State.FLIGHTS) {
            playerStateToNonPlayerState(State.INCOME_AND_TAXES);
        }
        else if (gameData.currentState == GameData.State.INCOME_AND_TAXES) {
            nonPlayerStateToNonPlayerState(State.WORLD_UPDATE);
            ++round;
            dataChangesCreator.resetAllAbilities();
            dataChangesCreator.resetActionPoints();
            dataChangesCreator.setCurrentRound(round);
        }
        else {
             throw new IllegalArgumentException("Unknown game state");
        }
        llh.applyAndSendDataChanges(); // sending changes in current player and game state
        return round <= StaticGameData.amountOfRounds;
    }

    /**
     * if not everybody had passed, sets next player. Else moves to the next state.
     * @param nextState state for transfer to
     * @param ability ability to be the first
     */
    private void playerStateToPlayerState(State nextState, Integer ability){
        Integer nextPlayer = turnOrderIterator.getNextPlayer();
        if(nextPlayer != null){ // at least one not passed player
            dataChangesCreator.setCurrentPLayer(nextPlayer);
        }
        else { // everybody passed
            System.out.println("Changing state player to player");
            dataChangesCreator.removeAllPassed();
            dataChangesCreator.setCurrentState(nextState);
            turnOrderIterator.reset(ability);
            dataChangesCreator.setCurrentPLayer(turnOrderIterator.getFirstId());
        }
        flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
    }

    private void playerStateToNonPlayerState(State nextState){
        Integer nextPlayer = turnOrderIterator.getNextPlayer();
        if(nextPlayer != null){
            dataChangesCreator.setCurrentPLayer(nextPlayer);
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
        }
        else {
            System.out.println("Changing state player to non player");
            dataChangesCreator.removeAllPassed();
            dataChangesCreator.setCurrentState(nextState);
            dataChangesCreator.setCurrentPLayer(-1);
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.NO_PLAYER_STAGE;
        }
    }

    private void nonPlayerStateToPlayerState(State nextState, Integer ability){
        System.out.println("Changing state non player to player");
        dataChangesCreator.setCurrentState(nextState);
        turnOrderIterator.reset(ability);
        dataChangesCreator.setCurrentPLayer(turnOrderIterator.getFirstId());
        flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
    }

    private void nonPlayerStateToNonPlayerState(State nextState){
        System.out.println("Changing state non player to non player");
        dataChangesCreator.setCurrentState(nextState);
        dataChangesCreator.setCurrentPLayer(-1);
    }

    private void removeAllDisconnectedPlayers(){
        List<Integer> playersToRemove = new ArrayList<>();
        for(Player pl : gameData.players){
            int id = pl.getId();
            Connection con  = llh.int2con.get(id);
            if(con != null && !con.isConnected()){
                llh.con2int.remove(con);
                llh.int2con.remove(id);
                playersToRemove.add(pl.getId());
            }
            else if(con == null){
                playersToRemove.add(pl.getId());
            }
        }

        if(!playersToRemove.isEmpty()) {
            for (Integer id : playersToRemove) {
                dataChangesCreator.removeAllAirlinesFromThePlayer(id);
            }
            llh.applyAndSendDataChanges();
            for (Integer id : playersToRemove){
                int indInTurn = dataChangesCreator.removePlayerAndGetTurnInd(id);
                turnOrderIterator.removePlayer(indInTurn);
            }
            llh.applyAndSendDataChanges();
        }
    }

}
