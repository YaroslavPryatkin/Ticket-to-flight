package com.game.Ticket_To_Flight.commonFrontAndBack;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LowLevelHandler {

    private final ExecutorService parsingPool = Executors.newFixedThreadPool(4);


    public final Queue<GameData.DataChanges> changesQueue = new ConcurrentLinkedQueue<>();

    private final GameData gameData;

    public LowLevelHandler(GameData gameData) {
        this.gameData = gameData;
    }

    public void update() {
        if (changesQueue.isEmpty()) return;

        gameData.acquireWriteLock();
        try {
            while (!changesQueue.isEmpty()) {
                GameData.DataChanges changes = changesQueue.poll();
                if (changes != null) {
                    gameData.applyChanges(changes);
                }
            }
        } finally {
            gameData.releaseWriteLock();
        }
    }

    public void processIncomingDTO(DTOHandler.DataChangesDTO dto) {
        parsingPool.submit(() -> {
            gameData.acquireReadLock();
            try {
                Object parsedObject = DTOHandler.fromDTO(dto, gameData);

                if (parsedObject instanceof GameData.DataChanges) {
                    GameData.DataChanges changes = (GameData.DataChanges) parsedObject;
                    changesQueue.add(changes);
                }
            } catch (Exception e) {
                System.err.println("DTO parsing error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                gameData.releaseReadLock();
            }
        });
    }

    public void shutdown() {
        parsingPool.shutdown();
    }
}
