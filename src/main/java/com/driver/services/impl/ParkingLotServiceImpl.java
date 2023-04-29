package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        //add a new parking lot to the database
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLotRepository1.save(parkingLot);

        return parkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        //create a new spot in the parkingLot with given id
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();

        //the spot type should be the next biggest type in case the number of wheels are not 2 or 4, for 4+ wheels, it is others
        Spot spot = new Spot();
        spot.setPricePerHour(pricePerHour);
        if((numberOfWheels != 2) || (numberOfWheels != 4)) {
            spot.setSpotType(SpotType.FOUR_WHEELER);
        } else if (numberOfWheels > 4) {
            spot.setSpotType(SpotType.OTHERS);
        }
        else if (numberOfWheels == 2) {
            spot.setSpotType(SpotType.TWO_WHEELER);
        }
        else if (numberOfWheels == 4) {
            spot.setSpotType(SpotType.FOUR_WHEELER);
        }

        spot.setReservationList(new ArrayList<>());

        List<Spot> spotList = parkingLot.getSpotList();
        if (spotList == null) {
            spotList = new ArrayList<>();
        }
        spotList.add(spot);
        spot.setOccupied(false);
        spot.setParkingLot(parkingLot);

        parkingLot.setSpotList(spotList);
        parkingLotRepository1.save(parkingLot);

        return spot;
    }

    @Override
    public void deleteSpot(int spotId) {
        //delete a spot from given parking lot
//        Spot spot = spotRepository1.findById(spotId).get();
//        spot.setOccupied(false);
//        ParkingLot parkingLot = spot.getParkingLot();
//
//        parkingLot.getSpotList().remove(spot);
//
//        parkingLotRepository1.save(parkingLot);
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        //update the details of a spot
        Spot spot = null;
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        List<Spot> spotList = parkingLot.getSpotList();

        for (Spot spot1 : spotList) {
            if(spot1.getId() == spotId) {
                spot = spot1;
                spot1.setPricePerHour(pricePerHour);
                spotRepository1.save(spot1);
                break;
            }
        }
        return spot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        //delete a parkingLot
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
