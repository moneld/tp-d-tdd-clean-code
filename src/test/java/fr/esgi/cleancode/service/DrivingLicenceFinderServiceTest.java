package fr.esgi.cleancode.service;

import fr.esgi.cleancode.database.InMemoryDatabase;
import fr.esgi.cleancode.exception.InvalidDriverSocialSecurityNumberException;
import fr.esgi.cleancode.exception.ResourceNotFoundException;
import fr.esgi.cleancode.model.DrivingLicence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DrivingLicenceFinderServiceTest {

    @InjectMocks
    private DrivingLicenceFinderService service;

    @Mock
    private InMemoryDatabase database;

    private final DrivingLicenceIdGenerationService drivingLicenceIdGenerationService = new DrivingLicenceIdGenerationService();
    private final UUID DRIVING_ID = drivingLicenceIdGenerationService.generateNewDrivingLicenceId();
    private final UUID DRIVING_NOT_FOUND_ID = drivingLicenceIdGenerationService.generateNewDrivingLicenceId();

    private String socialSecurityNumber = "741025896354215";

    @Test
    void should_find() {
        DrivingLicence drivingLicence = DrivingLicence.builder()
                                                        .id(DRIVING_ID)
                                                        .driverSocialSecurityNumber(socialSecurityNumber)
                                                        .build();

        when(database.save(DRIVING_ID,drivingLicence));

        assertThat(
                database.findById(DRIVING_ID)
                        .get()
                        .getDriverSocialSecurityNumber()
                )
                .isSameAs(socialSecurityNumber);
    }

    @Test
    void should_not_find() {
        when(database.findById(DRIVING_ID))
                .thenReturn(Optional.empty());
        Optional<DrivingLicence> drivingLicenceNotExist = service.findById(DRIVING_ID);
        assertThat(drivingLicenceNotExist).isEmpty();
    }

    @Test
    void should_wrong_driving_licence(){
        InvalidDriverSocialSecurityNumberException thrown = assertThrows(
                InvalidDriverSocialSecurityNumberException.class,
                ()->{
                    service.addDrivingLicence("AZERTY741258963"); // Numéro invalide
                    service.addDrivingLicence("741258963"); // Numéro trop court
                    service.addDrivingLicence("   "); // Numéro vide
                }
        );
        assertTrue(thrown.getMessage().contains("Invalid social security number"));
    }

    @Test
    void should_add_driving_licence(){
        DrivingLicence drivingLicence = DrivingLicence.builder()
                .id(DRIVING_ID)
                .driverSocialSecurityNumber(socialSecurityNumber)
                .build();

        DrivingLicence newDrivingLicence =  database.save(DRIVING_ID,drivingLicence);

        assertThat(newDrivingLicence.getDriverSocialSecurityNumber()).isSameAs(socialSecurityNumber);

    }

    @Test
    void should_not_found_driving_licence_by_id(){
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    service.findById(DRIVING_NOT_FOUND_ID);
                }
        );
        assertTrue(thrown.getMessage().contains("Driving licence not found"));
    }
    @Test
    void should_remove_point_on_driving_licence(){
        DrivingLicence drivingLicence = DrivingLicence.builder()
                .id(DRIVING_ID)
                .driverSocialSecurityNumber(socialSecurityNumber)
                .build();

        DrivingLicence newDrivingLicence =  database.save(DRIVING_ID,drivingLicence);

        when(database.findById(DRIVING_ID)).thenReturn(service.removePointOnDrivingLicence(newDrivingLicence.getId(), 2));
    }


}