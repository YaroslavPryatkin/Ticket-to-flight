package com.game.Ticket_To_Flight.frontend;

import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.game.Ticket_To_Flight.frontend.UI.MainDrawer;

public class MainClient {
    private MainDrawer mainDrawer;

    public void setMainDrawer(MainDrawer mainDrawer) {
        this.mainDrawer = mainDrawer;
    }

    public void getPackage(Object packet) {
        if (packet instanceof PackageCreateWorldMap) {
            PackageCreateWorldMap mapPacket = (PackageCreateWorldMap) packet;
            mainDrawer.drawWorldMap(mapPacket);
        }


    }
}
