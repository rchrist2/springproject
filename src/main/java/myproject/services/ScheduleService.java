package myproject.services;

import myproject.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Time;

@Component
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Transactional
    public void updateSchedule(Time begin, Time end, int employeeId){
        scheduleRepository.updateSchedule(begin, end, employeeId);
    }

}
