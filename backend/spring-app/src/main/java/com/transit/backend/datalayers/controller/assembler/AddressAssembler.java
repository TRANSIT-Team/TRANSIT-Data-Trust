package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.AddressController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.assembler.helper.ZipStates;
import com.transit.backend.datalayers.controller.assembler.helper.Zipcodesgermanystates;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.repository.CompanyAddressRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class AddressAssembler extends AbstractAssembler<Address, AddressDTO, AddressController> {
	@Autowired
	private AddressMapper addressMapper;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	
	
	@Autowired
	private ZipStates zipStatesClazz;
	
	public AddressAssembler() {
		super(AddressController.class, AddressDTO.class);
		
		
	}
	
	@Override
	public AddressDTO toModel(Address entity) {
		
		var result = super.toModel(entity);
		//Set Flag if an Address is already used inside an Order
		result.setInOrderUsed(orderRepository.existsOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(entity.getId(), entity.getId(), entity.getId()));
		
		result.setCompanyAddress(companyAddressRepository.existsByAddress_Id(entity.getId()));
		
		if (entity.getLocationPoint() != null) {
			if (entity.getStreet() != null && (entity.getCity() != null || entity.getZip() != null)) {
				result.setLocationPoint(entity.getLocationPoint());
			} else if (entity.getStreet() == null && (entity.getCity() != null || entity.getZip() != null)) {
				//geoservice
				List<Point> tempPoints = new ArrayList<>();
				var tempPoint = new AtomicReference<>(new GeometryFactory().createPoint(new Coordinate(0, 0)));
				List<Zipcodesgermanystates> zipStates = zipStatesClazz.getZipStates();
				
				if (zipStates.isEmpty()) {
					result.setLocationPoint(zipStatesClazz.getGermanyMiddle());
					return result;
				}
				zipStates.forEach(zipState -> {
					if (entity.getZip() != null && zipState.getZipcode().equals(entity.getZip())) {
						if (entity.getCity() != null && zipState.getPlace().equals(entity.getCity())) {
							tempPoint.set(new GeometryFactory().createPoint(new Coordinate(zipState.getLongitude(), zipState.getLatitude())));
						} else {
							tempPoints.add(new GeometryFactory().createPoint(new Coordinate(zipState.getLongitude(), zipState.getLatitude())));
						}
					} else if (entity.getZip() == null && zipState.getPlace() != null) {
						if (zipState.getPlace().equals(entity.getCity())) {
							tempPoint.set(new GeometryFactory().createPoint(new Coordinate(zipState.getLongitude(), zipState.getLatitude())));
						}
					}
				});
				
				if (tempPoint.get().getCoordinate().getX() == 0) {
					if (tempPoints.isEmpty()) {
						result.setLocationPoint(zipStatesClazz.getGermanyMiddle());
					} else {
						result.setLocationPoint(new GeometryFactory().createGeometryCollection(GeometryFactory.toPointArray(tempPoints)).getInteriorPoint());
					}
				} else {
					result.setLocationPoint(tempPoint.get());
				}
				
				
			} else {
				Point tempPoint = zipStatesClazz.getGermanyMiddle();
				result.setLocationPoint(tempPoint);
			}
		}
		
		return result;
	}
	
	@Override
	public AbstractMapper<Address, AddressDTO> getMapper() {
		return this.addressMapper;
	}
	
	@Override
	public CollectionModel<AddressDTO> toCollectionModel(Iterable<? extends Address> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<AddressController> getControllerClass() {
		return AddressController.class;
	}
	
	
}

