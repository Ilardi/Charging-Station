package com.project.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.model.ChargingStation;

@Repository
public interface ChargingStationRepository extends MongoRepository<ChargingStation,Integer>{
    
	@Query("{'AddressInfo.StateOrProvince': ?0}")
	List<ChargingStation> findByAddressInfoStateOrProvince(String stateOrProvince);

}
