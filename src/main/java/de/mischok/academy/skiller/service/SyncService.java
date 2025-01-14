package de.mischok.academy.skiller.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SyncService {

    @Autowired
    private PlanningSystem planningSystem;

    public void makeChanges(final List<HrSystemVacation> vacations) {
        // query the saved absences from planning system
        List<PlanningSystem.Absence> absences = planningSystem.getAllPlanningSystemAbsences();

        // query the persons from planning system
        List<PlanningSystem.Person> persons = planningSystem.getAllPlanningSystemPersons();

        // TODO: update planning system with vacation data from HR-system
    }

    @Builder
    @Data
    public static class HrSystemVacation {
        LocalDate startDate;
        LocalDate endDate;
        String employeeID;
        String status;
    }
}
