package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies;

import com.badlogic.gdx.graphics.Color;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;

public class FlightInteractionStrategy implements MapInteractionStrategy {
    private Airport selectedDepartureAirport = null;
    private final LowLevelHandlerFront llh;

    private final float NORMAL_ALPHA = 1.0f;
    private final float SELECTED_ALPHA = 0.4f;

    public FlightInteractionStrategy(LowLevelHandlerFront llh) {
        this.llh = llh;
    }

    private Color getTransparentColor(Color originalColor, float alpha) {
        return new Color(originalColor.r, originalColor.g, originalColor.b, alpha);
    }

    @Override
    public void onAirportClicked(Airport airport) {
        if (selectedDepartureAirport == null) {
            selectedDepartureAirport = airport;
            //airport.setColor(getTransparentColor(airport.getColor(), SELECTED_ALPHA));
            System.out.println("Selected departure: " + airport.getCityName());
        }
        else if (selectedDepartureAirport != airport) {
         //   selectedDepartureAirport.setColor(getTransparentColor(selectedDepartureAirport.getColor(), NORMAL_ALPHA));

            selectedDepartureAirport = airport;
           // airport.setColor(getTransparentColor(airport.getColor(), SELECTED_ALPHA));
            System.out.println("Changed departure to: " + airport.getCityName());
        }
    }

    @Override
    public void onAirlineClicked(Airline airline) {
        if (selectedDepartureAirport == null) {
            System.out.println("Please select departure airport first!");
            return;
        }

        if (airline.getPortA() == selectedDepartureAirport || airline.getPortB() == selectedDepartureAirport) {

            //airline.setColor(getTransparentColor(airline.getColor(), SELECTED_ALPHA));
            System.out.println("Flight route selected!");

            // llh.sendFlightRequest(selectedDepartureAirport.getId(), airline.getId());

            //selectedDepartureAirport.setColor(getTransparentColor(selectedDepartureAirport.getColor(), NORMAL_ALPHA));
            selectedDepartureAirport = null;

        }
        else {
            System.out.println("This airline is not connected to the selected airport.");
        }
    }

    @Override
    public void onEmptyMapClicked(float worldX, float worldY) {
        if (selectedDepartureAirport != null) {
            //selectedDepartureAirport.setColor(getTransparentColor(selectedDepartureAirport.getColor(), NORMAL_ALPHA));
            selectedDepartureAirport = null;
            System.out.println("Selection cleared.");
        }
    }
}
