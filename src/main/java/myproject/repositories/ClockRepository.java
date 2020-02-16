package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockRepository extends CrudRepository<Tblclock, Integer> {
    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username", nativeQuery = true)
    Tblclock findClockForUser(@Param("username") String user);

    Tblclock findClockBySchedule(@Param("schedule") Tblschedule schedule);
}
