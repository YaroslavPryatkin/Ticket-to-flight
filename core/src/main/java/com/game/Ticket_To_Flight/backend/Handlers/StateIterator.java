package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData.State;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

public class StateIterator {
    private final GameData gameData;
    private final DataChangesCreator dataChangesCreator;
    private final TurnOrderIterator turnOrderIterator;
    private final LowLevelHandlerBack.Flags flags;
    private int round = 1;

    public StateIterator(
        GameData gameData,
        DataChangesCreator dataChangesCreator,
        LowLevelHandlerBack.Flags flags
    ){
        this.gameData= gameData;
        turnOrderIterator = new TurnOrderIterator(gameData);
        this.dataChangesCreator = dataChangesCreator;
        this.flags = flags;
    }

    public boolean nextState(Boolean hasPassed){
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
            playerStateToPlayerState(State.AUCTION, null, hasPassed);
        }
        else if (gameData.currentState == GameData.State.AUCTION) {
            playerStateToPlayerState(State.ABILITIES, null, hasPassed);
        }
        else if (gameData.currentState == GameData.State.ABILITIES) {
            playerStateToPlayerState(State.PLANES, 1, hasPassed);
        }
        else if (gameData.currentState == GameData.State.PLANES) {
             playerStateToPlayerState(State.AIRLINES, 2, hasPassed);
        }
        else if (gameData.currentState == GameData.State.AIRLINES) {
            playerStateToNonPlayerState(State.EVENT, hasPassed);
        }
        else if (gameData.currentState == GameData.State.EVENT) {
             nonPlayerStateToPlayerState(State.FLIGHTS, 3);
        }
        else if (gameData.currentState == GameData.State.FLIGHTS) {
            playerStateToNonPlayerState(State.INCOME, hasPassed);
        }
        else if (gameData.currentState == GameData.State.INCOME) {
            nonPlayerStateToNonPlayerState(State.TAXES);
        }
        else if (gameData.currentState == GameData.State.TAXES) {
            nonPlayerStateToNonPlayerState(State.WORLD_UPDATE);
            ++round;
            dataChangesCreator.resetAllAbilities();
            dataChangesCreator.resetActionPoints();
            dataChangesCreator.setCurrentRound(round);
            return round <= StaticGameData.amountOfRounds;
        }
        else {
             throw new IllegalArgumentException("Unknown game state");
        }
        return true;
    }

    /**
     * if not everybody had passed, sets next player. Else moves to the next state.
     * @param nextState state for transfer to
     * @param ability ability to be the first
     * @param hasPassed crutch
     */
    private void playerStateToPlayerState(State nextState, Integer ability, boolean hasPassed){
        if(hasPassed)
            dataChangesCreator.addHasPassed();

        Integer nextPlayer = turnOrderIterator.getNextPlayer();
        if(nextPlayer != null && !(hasPassed && gameData.currentPlayer.equals(nextPlayer))){
            dataChangesCreator.setCurrentPLayer(nextPlayer);
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
        }
        else {
            dataChangesCreator.removeAllPassed();
            dataChangesCreator.setCurrentState(nextState);
            turnOrderIterator.reset(ability);
            dataChangesCreator.setCurrentPLayer(turnOrderIterator.getNextPlayer());
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
        }
    }

    private void playerStateToNonPlayerState(State nextState, boolean hasPassed){
        if(hasPassed)
            dataChangesCreator.addHasPassed();

        Integer nextPlayer = turnOrderIterator.getNextPlayer();
        if(nextPlayer != null && !(hasPassed && gameData.currentPlayer.equals(nextPlayer))){
            dataChangesCreator.setCurrentPLayer(nextPlayer);
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
        }
        else {
            dataChangesCreator.removeAllPassed();
            dataChangesCreator.setCurrentState(nextState);
            dataChangesCreator.setCurrentPLayer(-1);
            flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.NO_PLAYER_STAGE;
        }
    }

    private void nonPlayerStateToPlayerState(State nextState, Integer ability){
        dataChangesCreator.setCurrentState(nextState);
        turnOrderIterator.reset(ability);
        dataChangesCreator.setCurrentPLayer(turnOrderIterator.getNextPlayer());
        flags.currentPlayerState = LowLevelHandlerBack.Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
    }

    private void nonPlayerStateToNonPlayerState(State nextState){
        dataChangesCreator.setCurrentState(nextState);
        dataChangesCreator.setCurrentPLayer(-1);
    }

}
