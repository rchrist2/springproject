package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClockRepository extends CrudRepository<Tblclock, Integer> {
    @Query(value = "SELECT clock_id, punch_in, " +
            "punch_out, c.schedule_id, date_created FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE c.schedule_id = :schedule AND date_created=(SELECT MAX(date_created) FROM tblclock) " +
            "GROUP BY clock_id, punch_in, punch_out, c.schedule_id, date_created;", nativeQuery = true)
    Tblclock findRecentClockForSchedule(@Param("schedule") Integer schedule);

    @Query(value = "SELECT clock_id, punch_in, " +
            "punch_out, c.schedule_id, date_created FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE Username = :username AND date_created=(SELECT MAX(date_created) FROM tblclock) " +
            "GROUP BY clock_id, punch_in, punch_out, c.schedule_id, date_created;", nativeQuery = true)
    Tblclock findRecentClockForUser(@Param("username") String username);

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username", nativeQuery = true)
    List<Tblclock> findClockForUser(@Param("username") String user);

    List<Tblclock> findAll();
}
