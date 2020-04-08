package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TimeOffRepository extends CrudRepository<Tbltimeoff, Integer> {

    @Query(value = "SELECT * FROM tbltimeoff t " +
            "JOIN tblschedule s ON s.schedule_id=t.schedule_id " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE begin_time_off_date=:date AND end_time_off_date=:date " +
            "AND approved=1 " +
            "AND s.employee_id = :empId", nativeQuery = true)
    Tbltimeoff findByDateAndEmployee(@Param("date") Date date, @Param("empId") int empId);

    @Query(value = "SELECT * FROM tblschedule s RIGHT JOIN tbltimeoff t " +
            "ON s.schedule_date >= t.begin_time_off_date " +
            "AND s.schedule_date <= t.end_time_off_date " +
            " AND t.employee_id = :empId " +
            "JOIN tblemployee e ON e.id=s.employee_id " +
            "JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE t.schedule_id IS NULL " +
            "AND (t.approved=1 OR t.approved=0) " +
            "AND Username = :username", nativeQuery = true)
    List<Tbltimeoff> findUnlinkedTimeOffForUserSchedule(@Param("username") String user, @Param("empId") int empId);

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "WHERE t.schedule_id = :id AND s.employee_id=:empId", nativeQuery = true)
    Tbltimeoff findScheduleTimeOff(@Param("id") Integer id, @Param("empId") Integer empId);

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id " +
            "JOIN tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE());", nativeQuery = true)
    List<Tbltimeoff> findTimeOffThisWeekForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())", nativeQuery = true)
    List<Tbltimeoff> findTimeOffThisWeekAllUser();

    @Query(value = "SELECT * FROM tbltimeoff t " +
            "JOIN tblemployee e ON e.id=t.employee_id JOIN tblusers u ON e.id=u.employee_id " +
            "WHERE Username = :username", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOffByUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tbltimeoff t LEFT JOIN tblschedule s ON t.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON e.id=s.employee_id JOIN tblusers u ON e.id=u.employee_id " +
            "WHERE Username = :username", nativeQuery = true)
    Tbltimeoff findTimeOffByUser(@Param("username") String user);

    //Returns a list of all time offs
    @Query(value = "SELECT * FROM tbltimeoff ", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOff();

    //Returns a list of all time offs
    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblemployee e ON " +
            "t.employee_id=e.id JOIN tblroles r ON r.role_id=e.roles_id" +
            " WHERE role_name NOT IN ('Owner','Manager')", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOffByRole();

    @Modifying
    @Query(value = "DELETE FROM tbltimeoff WHERE time_off_id = :id", nativeQuery = true)
    void deleteTimeOff(@Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM tbltimeoff WHERE employee_id = :empId", nativeQuery = true)
    void deleteEmployeeTimeOff(@Param("empId") int employeeId);

}