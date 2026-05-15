package com.game.Ticket_To_Flight.backend.server;

import com.game.Ticket_To_Flight.backend.LowLevelHandlerBack;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

public abstract class MainLoopBack {
    protected final GameData gameData = new GameData();
    protected final LowLevelHandlerBack llh;

    private volatile boolean isRunning = false;
    private Thread gameThread;

    protected MainLoopBack() {
        llh = new LowLevelHandlerBack(gameData, this);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        start();
    }

    private synchronized void start() {
        if (isRunning) return;

        isRunning = true;
        gameThread = new Thread(this::runLoop);
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void runLoop() {
        final double TARGET_FPS = 10.0;
        final long OPTIMAL_TIME = (long) (1_000_000_000 / TARGET_FPS);
        long lastLoopTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            lastLoopTime = now;

            llh.update();
            mainCycle();

            long timeTaken = System.nanoTime() - now;
            long timeLeft = OPTIMAL_TIME - timeTaken;

            if (timeLeft > 0) {
                try {
                    Thread.sleep(timeLeft / 1_000_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public synchronized void stop() {
        if (!isRunning) return;

        isRunning = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }

        llh.clearAllMessages();
        afterStop();
    }




    protected void afterStop(){};

    protected abstract void mainCycle();


}
