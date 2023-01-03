package fr.esgi.cleancode.service;

import fr.esgi.cleancode.database.InMemoryDatabase;
import fr.esgi.cleancode.model.DrivingLicence;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DrivingLicenceFinderService {

    private final InMemoryDatabase database;

    public Optional<DrivingLicence> findById(UUID drivingLicenceId) {
        return database.findById(drivingLicenceId);
    }

    public void addDrivingLicence(String azerty741258963) {
    }

    public Optional<DrivingLicence> removePointOnDrivingLicence(UUID id, int i) {
        return null;
    }
}
