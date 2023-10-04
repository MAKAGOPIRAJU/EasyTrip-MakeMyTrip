package com.driver.controllers;

import com.driver.controllers.AirportRepository;
import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
//import com.driver.repositories.AirportRepository;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AirportService {
    AirportRepository airportRepositoryObj = new AirportRepository();

    public void addAirport(Airport airport) {
        airportRepositoryObj.addAirport(airport);
    }

    public String getLargestAirportName() {
        List<Airport> list = airportRepositoryObj.getAllAirports();
        list.sort((a, b) -> {
            if (a.getNoOfTerminals() == b.getNoOfTerminals())
                return a.getAirportName().compareTo(b.getAirportName());

            return b.getNoOfTerminals() - a.getNoOfTerminals();
        });

        if (list.isEmpty())
            return null;

        return list.get(0).getAirportName();
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){
        List<Flight> list = airportRepositoryObj.getAllFlights();

        double shortestDuration = Double.MAX_VALUE;

        for (Flight flight : list)
            if (flight.getFromCity() == fromCity && flight.getToCity() == toCity)
                if (flight.getDuration() < shortestDuration)
                    shortestDuration = flight.getDuration();

        if (shortestDuration == Double.MAX_VALUE) return -1;

        return shortestDuration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int count = 0;
        Airport airport = airportRepositoryObj.getAirportByName(airportName);
        List<Flight> list = airportRepositoryObj.getAllFlights();

        for (Flight flight : list)
            if (flight.getFlightDate() == date && (flight.getFromCity() == airport.getCity() || flight.getToCity() == airport.getCity()))
                count += airportRepositoryObj.getPassengersByFlight(flight.getFlightId()).size();

        return count;
    }

    public int calculateFlightFare(Integer flightId) {
        return 3000 + 50 * airportRepositoryObj.getPassengersByFlight(flightId).size();
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        Flight flight = airportRepositoryObj.getFlightById(flightId);
        Passenger passenger = airportRepositoryObj.getPassengerById(passengerId);

        if (flight == null || passenger == null )
            return "FAILURE";

        List<Integer> passengerList = airportRepositoryObj.getPassengersByFlight(flightId);
        if (passengerList.contains(passengerId) || passengerList.size() == flight.getMaxCapacity())
            return "FAILURE";

        airportRepositoryObj.bookTicket(flightId, passengerId);

        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        Flight flight = airportRepositoryObj.getFlightById(flightId);
        Passenger passenger = airportRepositoryObj.getPassengerById(passengerId);

        if (flight == null || passenger == null)
            return "FAILURE";

        List<Integer> passengerList = airportRepositoryObj.getPassengersByFlight(flightId);
        if (!passengerList.contains(passengerId))
            return "FAILURE";

        airportRepositoryObj.cancelTicket(flightId, passengerId);

        return "SUCCESS";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count = 0;
        List<Flight> flights = airportRepositoryObj.getAllFlights();

        for (Flight flight : flights)
            if (airportRepositoryObj.getPassengersByFlight(flight.getFlightId()).contains(passengerId))
                ++count;

        return count;
    }

    public String addFlight(Flight flight) {
        airportRepositoryObj.addFlight(flight);

        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        Flight flight = airportRepositoryObj.getFlightById(flightId);
        if (flight == null)
            return null;

        List<Airport> airportList = airportRepositoryObj.getAllAirports();

        for (Airport airport : airportList)
            if (airport.getCity() == flight.getFromCity())
                return airport.getAirportName();

        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int size =  airportRepositoryObj.getPassengersByFlight(flightId).size();

        return 3000 * size + 50 * size * (size - 1) / 2;
    }

    public String addPassenger(Passenger passenger) {
        airportRepositoryObj.addPassenger(passenger.getPassengerId(), passenger);

        return "SUCCESS";
    }
}