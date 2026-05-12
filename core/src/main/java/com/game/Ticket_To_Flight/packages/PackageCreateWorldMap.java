package com.game.Ticket_To_Flight.packages;

public class PackageCreateWorldMap {
    public String mapTextureName;
    public float worldWidth;
    public float worldHeight;

    public PackageCreateWorldMap() {
    }

    public PackageCreateWorldMap(String mapTextureName, float worldWidth, float worldHeight) {
        this.mapTextureName = mapTextureName;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
}
