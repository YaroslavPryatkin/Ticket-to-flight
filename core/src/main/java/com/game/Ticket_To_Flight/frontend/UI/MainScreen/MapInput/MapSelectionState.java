package com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;

public class MapSelectionState {
    private Integer selectedAirportId;
    private Integer selectedAirlineId;
    private Integer selectedPassengerTypeId;
    private Integer activeFlightAirportId;
    private Integer firstFlightAirport;

    private Route currentRoute;

    public void setCurrentRoute(Route route) {
        this.currentRoute = route;
    }

    public boolean isAirlineInRoute(Airline airline) {
        return airline != null && currentRoute != null && currentRoute.getLines().contains(airline);
    }

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

    public void clearRouteHighlights() {
        clearFlightSelection();
        firstFlightAirport = null;
        activeFlightAirportId = null;
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
        return airport != null &&
            ((selectedAirportId != null && selectedAirportId.equals(airport.getId())) ||
                (activeFlightAirportId != null && activeFlightAirportId.equals(airport.getId())));
    }

    public void setActiveFlightAirport(Airport airport) {
        activeFlightAirportId = airport == null ? null : airport.getId();
    }

    public void setFirstFlightAirport(Airport airport) {
        firstFlightAirport = airport == null ? null : airport.getId();
    }

    public boolean isActiveFlightAirport(Airport airport) {
        return airport != null && activeFlightAirportId != null && activeFlightAirportId.equals(airport.getId());
    }

    public boolean isAirportFirst(Airport airport) {
        if (airport == null || firstFlightAirport == null) return false;

        return firstFlightAirport.equals(airport.getId());
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
