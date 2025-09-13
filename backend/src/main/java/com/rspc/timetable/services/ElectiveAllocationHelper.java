package com.rspc.timetable.services;

import com.rspc.timetable.entities.ElectiveAllocation;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.Year;
import com.rspc.timetable.entities.Teacher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElectiveAllocationHelper {

    private final ElectiveAllocationService allocationService;

    public ElectiveAllocationHelper(ElectiveAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    /**
     * Automatically split students into elective divisions and save allocations.
     *
     * @param subject     the elective subject
     * @param year        year object
     * @param semester    semester object
     * @param teacher     assigned teacher
     * @param totalStudents total number of students enrolled for this elective
     * @param maxCapacity max students per group
     * @return list of saved ElectiveAllocation
     */
    public List<ElectiveAllocation> createElectiveGroups(
            Subject subject,
            Year year,
            Semester semester,
            Teacher teacher,
            int totalStudents,
            int maxCapacity
    ) {
        List<ElectiveAllocation> allocations = new ArrayList<>();

        int groupCount = (int) Math.ceil((double) totalStudents / maxCapacity);

        for (int i = 1; i <= groupCount; i++) {
            int capacity = (i < groupCount) ? maxCapacity : (totalStudents - maxCapacity * (groupCount - 1));
            String electiveDivision = "E" + i;

            ElectiveAllocation allocation = new ElectiveAllocation(
                    subject,
                    electiveDivision,
                    capacity,
                    year,
                    semester,
                    teacher
            );

            allocations.add(allocation);
        }

        // Save all groups
        return allocationService.saveAll(allocations);
    }
}
