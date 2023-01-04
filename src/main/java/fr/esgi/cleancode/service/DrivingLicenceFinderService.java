package fr.esgi.cleancode.service;

import fr.esgi.cleancode.database.InMemoryDatabase;
import fr.esgi.cleancode.exception.InvalidDriverSocialSecurityNumberException;
import fr.esgi.cleancode.exception.ResourceNotFoundException;
import fr.esgi.cleancode.model.DrivingLicence;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DrivingLicenceFinderService {

    private final InMemoryDatabase database;
    private final DrivingLicenceIdGenerationService drivingLicenceIdGenerationService;

    public Optional<DrivingLicence> findById(UUID drivingLicenceId) {
        return database.findById(drivingLicenceId);
    }

    public void addDrivingLicence(String socialSecurityNumber) {

        if (socialSecurityNumber.matches("^\\d{15}$")) {

            UUID drivingId = drivingLicenceIdGenerationService.generateNewDrivingLicenceId();
            DrivingLicence drivingLicence =DrivingLicence.builder()
                    .id(drivingId)
                    .driverSocialSecurityNumber(socialSecurityNumber)
                    .build();

            database.save(drivingId, drivingLicence);

        } else{
            throw new InvalidDriverSocialSecurityNumberException("Invalid social security number");
        }

    }

    public Optional<DrivingLicence> removePointOnDrivingLicence(UUID drivingId, int point) {
        Optional<DrivingLicence> optionalDrivingLicence = this.findById(drivingId);

        if (optionalDrivingLicence.isEmpty()){
            throw new ResourceNotFoundException("Driving licence not found");
        }
        int newPoint = 0;

        if (optionalDrivingLicence.get().getAvailablePoints() > 0 || optionalDrivingLicence.get().getAvailablePoints() - point > 0){
            newPoint = optionalDrivingLicence.get().getAvailablePoints() - point;
        }
        DrivingLicence drivingLicence = DrivingLicence.builder()
                .id(optionalDrivingLicence.get().getId())
                .driverSocialSecurityNumber(optionalDrivingLicence.get().getDriverSocialSecurityNumber())
                .availablePoints(newPoint)
                .build();

        return Optional.ofNullable(database.save(drivingId,drivingLicence));
    }
}
