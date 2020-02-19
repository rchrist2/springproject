package myproject.repositories;

import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Tblschedule, Integer> {
    /*@Query(value = "SELECT * FROM tblAvailability a JOIN tblUsers u ON a.User_ID=u.User_ID WHERE Username = :username", nativeQuery = true)
    TblAvailability findTblAvailabilitiesByUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblAvailability a JOIN tblUsers u ON a.User_ID=u.User_ID JOIN tblDay d ON a.Day_ID=d.Day_id " +
            "WHERE Username = :username AND Day_Desc = :dayDesc", nativeQuery = true)
    TblAvailability findTblAvailabilitiesByUserAndDay(@Param("username") String user, @Param("dayDesc") String dayDesc);
    */

    @Query(value = "SELECT * FROM tblschedule", nativeQuery = true)
    List<Tblschedule> findAll();

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username", nativeQuery = true)
    Tblschedule findScheduleForUser(@Param("username") String user);

    @Modifying
    @Query(value = "UPDATE tblschedule SET schedule_time_begin = :begin, schedule_time_end = :end WHERE employee_id = :empId", nativeQuery = true)
    void updateSchedule(@Param("begin") Time begin, @Param("end") Time end, @Param("empId") int empId);
}

