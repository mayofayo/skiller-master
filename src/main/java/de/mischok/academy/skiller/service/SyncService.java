package de.mischok.academy.skiller.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyncService {

    @Autowired
    private PlanningSystem planningSystem;

    public void makeChanges(final List<HrSystemVacation> vacations) {
        // query the saved absences from planning system
        List<PlanningSystem.Absence> absences = planningSystem.getAllPlanningSystemAbsences();

        // query the persons from planning system
        List<PlanningSystem.Person> persons = planningSystem.getAllPlanningSystemPersons();

        // ignore vacations of non-registered employees from HR-list
        List<HrSystemVacation> registeredEmployeesVacations = vacations.stream()
                .filter(v -> persons.stream()
                        .anyMatch(p -> p.getId().equals(v.getEmployeeID())))
                .collect(Collectors.toList());

        // create a list of absences not looked ab witch will be deleted afterward
        List<String> absencesNotLookedAt = absences.stream().map(PlanningSystem.Absence::getId).collect(Collectors.toList());


        for (PlanningSystem.Absence absence : absences) {
            for (HrSystemVacation vacation : registeredEmployeesVacations) {
                if (!vacation.getEmployeeID().equals(absence.getPersonId())) {
                    continue;
                }

                // if no changes needed don't change anything
                if (vacation.getStartDate().equals(absence.getStartDate()) && vacation.getEndDate().equals(absence.getEndDate()) && vacation.getStatus().equals(absence.getStatus())) {
                    registeredEmployeesVacations.remove(vacation);
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                }

                // update on status change
                else if (vacation.getStartDate().equals(absence.getStartDate()) && vacation.getEndDate().equals(absence.getEndDate()) && !vacation.getStatus().equals(absence.getStatus()) && !vacation.getStatus().equals("Abgelehnt")) {
                    planningSystem.updateBooking(vacation, absence.getId());
                    registeredEmployeesVacations.remove(vacation);
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                }

                // remove if denied
                else if (vacation.getStartDate().equals(absence.getStartDate()) && vacation.getEndDate().equals(absence.getEndDate()) && !vacation.getStatus().equals(absence.getStatus()) && vacation.getStatus().equals("Abgelehnt")) {
                    planningSystem.deleteBooking(absence.getId());
                    registeredEmployeesVacations.remove(vacation);
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                }

                // remove and create new if dates don't match
                else if (!vacation.getStartDate().equals(absence.getStartDate()) && vacation.getEndDate().equals(absence.getEndDate()) && vacation.getStatus().equals(absence.getStatus())) {
                    planningSystem.deleteBooking(absence.getId());
                    planningSystem.createBooking(vacation);
                    registeredEmployeesVacations.remove(vacation);
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                } else if (vacation.getStartDate().equals(absence.getStartDate()) && !vacation.getEndDate().equals(absence.getEndDate()) && vacation.getStatus().equals(absence.getStatus())) {
                    planningSystem.deleteBooking(absence.getId());
                    planningSystem.createBooking(vacation);
                    registeredEmployeesVacations.remove(vacation);
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                }

                // if nothing matches, remove absence
                else {

                    planningSystem.deleteBooking(absence.getId());
                    absencesNotLookedAt.remove(absence.getId());
                    break;
                }
            }
        }

        // delete all bookings that are not mentioned in vacations
        for (String absenceId : absencesNotLookedAt) {
            planningSystem.deleteBooking(absenceId);
        }

        // create all remaining vacations
        for (HrSystemVacation vacation : registeredEmployeesVacations) {
            if (!vacation.getStatus().equals("Abgelehnt")) {
                planningSystem.createBooking(vacation);
            }
        }
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

