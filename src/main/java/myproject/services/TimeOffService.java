package myproject.services;

import myproject.repositories.TimeOffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class TimeOffService {
    @Autowired
    private TimeOffRepository timeOffRepository;

    @Transactional
    public void deleteTimeOff(int timeOffId){
        timeOffRepository.deleteTimeOff(timeOffId);
    }

    @Transactional
    public void deleteTimeOffByEmp(int employeeId){
        timeOffRepository.deleteEmployeeTimeOff(employeeId);
    }
}
