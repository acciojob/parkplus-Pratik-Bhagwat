package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.
        User user = null;
        ParkingLot parkingLot = null;
        Reservation reservation = new Reservation();


        user = userRepository3.findById(userId).get();
        parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        reservation.setUser(user);

        //?checking if we didnt get any parkinglot or user by the given ids then return the exception.
        if (user == null && parkingLot == null) {
            reservation.setSpot(null);

            reservationRepository3.save(reservation);

            throw new Exception("Cannot make reservation");
        }

        //?getting the all spot list form the given parkingLot.
        List<Spot> spotList = parkingLot.getSpotList();
        int minimumCost = Integer.MAX_VALUE;

        Spot spotReq = null;

        //?here checking in the spot list if any spot is available or not to reserve.
        for (Spot spot : spotList) {
            if (!spot.getOccupied()) {

                //?and if the spot is free to reserve then simply checking the cost of the spot is minimum or not.
                if ((getNumberOfWheels(spot.getSpotType()) >= numberOfWheels) && ((spot.getPricePerHour() * timeInHours) < minimumCost)) {
                    minimumCost = spot.getPricePerHour() * timeInHours;
                    spotReq = spot;
                }
            }
    }
        //?and if we didnt find any spot that is free or unequipped then throw the given error
        if (spotReq == null) {
            reservation.setSpot(null);
            reservationRepository3.save(reservation);
            throw new Exception("Cannot make reservation");
        }

        //?setting all the remaining attributes.
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(spotReq);

        user.getReservationList().add(reservation);
        spotReq.getReservationList().add(reservation);

        spotReq.setOccupied(true);
        userRepository3.save(user);
        spotRepository3.save(spotReq);

        return reservation;
}
private int getNumberOfWheels(SpotType spotType) {
        if(spotType.equals(SpotType.TWO_WHEELER))
            return 2;
        else if(spotType.equals(SpotType.FOUR_WHEELER))
            return 4;
        return Integer.MAX_VALUE;
    }
}
