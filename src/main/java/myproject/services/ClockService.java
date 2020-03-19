package myproject.services;

import myproject.repositories.ClockRepository;
import myproject.repositories.TimeOffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class ClockService {
    @Autowired
    private ClockRepository clockRepository;

    @Transactional
    public void deleteClock(int clockId){
        clockRepository.deleteClock(clockId);
    }
}
