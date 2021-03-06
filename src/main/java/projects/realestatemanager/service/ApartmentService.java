package projects.realestatemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projects.realestatemanager.converter.ApartmentConverter;
import projects.realestatemanager.data.apartment.ApartmentSummary;
import projects.realestatemanager.domain.model.Apartment;
import projects.realestatemanager.domain.model.Building;
import projects.realestatemanager.domain.model.User;
import projects.realestatemanager.domain.repository.ApartmentRepository;
import projects.realestatemanager.domain.repository.BuildingRepository;
import projects.realestatemanager.exception.*;
import projects.realestatemanager.web.command.CreateApartmentCommand;
import projects.realestatemanager.web.command.EditApartmentCommand;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentConverter apartmentConverter;
    private final BuildingRepository buildingRepository;

    public List<ApartmentSummary> findAllApartments() {
        log.debug("Getting all apartments info");

        return apartmentRepository.findAll().stream()
                .map(apartmentConverter::from)
                .collect(Collectors.toList());

    }

    public void add(CreateApartmentCommand createApartmentCommand) {
        log.debug("Apartment data to be saved: {}", createApartmentCommand);
        Building building = buildingRepository.getOne(createApartmentCommand.getBuildingId());

        Apartment apartmentToAdd = apartmentConverter.from(createApartmentCommand, building);

        if (apartmentRepository.existsByFloorAndAreaAndBuildingAndWindowsDirection(apartmentToAdd.getFloor(), apartmentToAdd.getArea(), apartmentToAdd.getBuilding(), apartmentToAdd.getWindowsDirection())) {
            log.debug("Trying to add existing apartment");
            throw new ApartmentAlreadyExistExeption(
                    "Apartment already exists");
        }

        apartmentToAdd.setActive(true);
        apartmentToAdd.setCreationDate(LocalDate.now());
        apartmentToAdd.setPricePerSquareMeter((int) apartmentToAdd.getArea()/apartmentToAdd.getPrice());
        apartmentRepository.save(apartmentToAdd);
        log.debug("Added apartment: {}", apartmentToAdd);

    }

    public ApartmentSummary showApartmentById(Long id) {
        log.debug("Apartment id to find in DB: {}", id);

        Apartment apartmentToEdit = apartmentRepository.getOne(id);
        log.debug("Received apartment to edit: {}", apartmentToEdit);
        if (!apartmentRepository.existsById(id)) {
            log.debug("Trying to edit no existing apartment");
            throw new ApartmentNoExistingException(String.format("Apartment with id %s isn't existing", id));
        }
        return apartmentConverter.from(apartmentToEdit);
    }

    public boolean editApartment(EditApartmentCommand editApartmentCommand) {
        Long id = editApartmentCommand.getId();
        if (!apartmentRepository.existsById(id)) {
            log.debug("Trying to edit no existing apartment");
            throw new ApartmentNoExistingException(String.format("Apartment with id %s isn't existing", id));

        }
        Apartment apartmentToEdit = apartmentRepository.getOne(id);
        log.debug("Apartment to edit: {}", apartmentToEdit);
        apartmentToEdit = apartmentConverter.from(editApartmentCommand, apartmentToEdit);
        log.debug("Apartment edited: {}", apartmentToEdit);

        return true;
    }


    public boolean delete(Long id) {
        log.debug("Apartment to delete: {}", apartmentRepository.getOne(id));
        Apartment apartment = apartmentRepository.getOne(id);
        if(!apartmentRepository.existsById(id)){
            log.debug("Apartment with id: {} doesn't exist", id);
            throw new EntityDoesNotExistException(String.format(
                    "Apartment with id: {} doesn't exist", id));
        }
        log.debug("Apartment to delete: {}", apartment);
        apartmentRepository.delete(apartment);
        return true;

    }

    public List<ApartmentSummary> showByIds(List<Long> idList) {
        log.debug("Id list to look in db: {}", idList.toString());

        try {
            List <Apartment> apartmentEntities = apartmentRepository.findAllByIdIn(idList);
            return apartmentEntities.stream()
                    .map(apartmentConverter::from)
                    .collect(Collectors.toList());
        } catch (RuntimeException re) {
            log.error(re.getLocalizedMessage());
            return null;
        }

    }

    //todo apartmentsummary?
    public Apartment findApartmentById(Long id) {
        log.debug("Apartment id to show: {}", id);
        if (!apartmentRepository.existsById(id)) {
            log.debug("Tried to delete non-existing apartment!");
            throw new EntityDoesNotExistException(String.format("Apartment with id %s does not exist!", id));
        }
        return apartmentRepository.getOne(id);
    }

    public List<ApartmentSummary> findApartmentByBuildingId(Long id) {
        log.debug("Looking for apartments related to building id: {}", id);
        if (!buildingRepository.existsById(id)) {
            log.debug("Tried to find non-existing building!");
            throw new EntityDoesNotExistException(String.format("Building with id {} does not exist!", id));
        }

        try {
            List <Apartment> apartmentEntities = apartmentRepository.findAllByBuildingId(id);
            return apartmentEntities.stream()
                    .map(apartmentConverter::from)
                    .collect(Collectors.toList());
        } catch (RuntimeException re) {
            log.error(re.getLocalizedMessage());
            return null;
        }
    }

    //todo apartmentsummary?
    public List<Apartment> getUsersFavouriteApartments(User user) {
        return apartmentRepository.getUsersFavouriteApartments(user.getId());
    }
}