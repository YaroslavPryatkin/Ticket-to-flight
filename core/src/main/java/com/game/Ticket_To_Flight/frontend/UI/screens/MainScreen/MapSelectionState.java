package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

public class MapSelectionState {
    private Integer selectedAirportId;
    private Integer selectedAirlineId;
    private Integer selectedPassengerTypeId;

    public void selectAirport(Airport airport) {
        Integer nextAirportId = airport == null ? null : airport.getId();
        if (selectedAirportId == null || !selectedAirportId.equals(nextAirportId)) {
            selectedAirlineId = null;
            selectedPassengerTypeId = null;
        }
        selectedAirportId = nextAirportId;
    }

    public void selectPassengerType(PassengerType passengerType) {
        selectedPassengerTypeId = passengerType == null ? null : passengerType.getId();
    }

    public void selectAirline(Airline airline) {
        selectedAirlineId = airline == null ? null : airline.getId();
    }

    public void clearFlightSelection() {
        selectedAirportId = null;
        selectedAirlineId = null;
        selectedPassengerTypeId = null;
    }

    public Integer getSelectedAirportId() {
        return selectedAirportId;
    }

    public Integer getSelectedAirlineId() {
        return selectedAirlineId;
    }

    public Integer getSelectedPassengerTypeId() {
        return selectedPassengerTypeId;
    }

    public boolean isAirportSelected(Airport airport) {
        return airport != null && selectedAirportId != null && selectedAirportId.equals(airport.getId());
    }

    public boolean isAirlineSelected(Airline airline) {
        return airline != null && selectedAirlineId != null && selectedAirlineId.equals(airline.getId());
    }

    public boolean isPassengerTypeSelected(PassengerType passengerType) {
        return passengerType != null && selectedPassengerTypeId != null && selectedPassengerTypeId.equals(passengerType.getId());
    }

    public boolean hasCompleteFlightSelection() {
        return selectedAirportId != null && selectedAirlineId != null && selectedPassengerTypeId != null;
    }
}
