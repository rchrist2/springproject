package myproject.services;

import myproject.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Time;

@Component
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Transactional
    public void updateSchedule(Time begin, Time end, int employeeId, int dayId){
        scheduleRepository.updateSchedule(begin, end, employeeId, dayId);
    }

    @Transactional
    public void deleteSchedule(int employeeId){
        scheduleRepository.deleteEmployeeSchedule(employeeId);
    }

    @Transactional
    public void deleteScheduleByID(int id){
        scheduleRepository.deleteSchedule(id);
    }

    @Transactional
    public void insertSchedule(Time startTime, Time startEnd, Date todayDate, int empId, int dayId){
        scheduleRepository.insertEmployeeSchedule(startTime, startEnd, todayDate, empId, dayId);
    }
}
