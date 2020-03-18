package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRepository extends CrudRepository<Tbltimeoff, Integer> {

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "WHERE t.schedule_id = :id", nativeQuery = true)
    Tbltimeoff findScheduleTimeOff(@Param("id") Integer id);

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id " +
            "JOIN tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE());", nativeQuery = true)
    List<Tbltimeoff> findTimeOffThisWeekForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())", nativeQuery = true)
    List<Tbltimeoff> findTimeOffThisWeekAllUser();

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id" +
            " JOIN tblemployee e ON e.id=s.employee_id JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE Username = :username", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOffByUser(@Param("username") String user);

    //Returns a list of all time offs
    @Query(value = "SELECT * FROM tbltimeoff ", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOff();

}