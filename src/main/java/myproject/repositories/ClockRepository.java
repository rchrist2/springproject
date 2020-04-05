package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblschedule;
import myproject.models.Tblusers;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClockRepository extends CrudRepository<Tblclock, Integer> {
    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id JOIN tblroles r ON r.role_id=e.roles_id " +
            "WHERE role_name NOT IN ('Owner')", nativeQuery = true)
    List<Tblclock> findAllByRole();

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON c.employee_id=e.id " +
            "WHERE c.schedule_id = :id" +
            " AND c.employee_id=:empId", nativeQuery = true)
    List<Tblclock> findScheduleClock(@Param("id") Integer id, @Param("empId") Integer empId);

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id " +
            "JOIN tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE());", nativeQuery = true)
    List<Tblclock> findClockThisWeekForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())", nativeQuery = true)
    List<Tblclock> findClockThisWeekAllUser();

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id JOIN tblroles r ON r.role_id=e.roles_id " +
            "WHERE DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE()) AND (role_name NOT IN ('Owner','Manager'))", nativeQuery = true)
    List<Tblclock> findClockThisWeekAllUserByRole();

    @Query(value = "SELECT clock_id, punch_in, " +
            "punch_out, c.schedule_id, date_created, c.day_id, c.employee_id FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE date_created=(SELECT MAX(date_created) FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE c.schedule_id = :schedule " +
            "AND datediff(day, date_created, s.schedule_date) = 0) " +
            "GROUP BY clock_id, punch_in, punch_out, c.schedule_id, date_created, c.day_id, c.employee_id;", nativeQuery = true)
    Tblclock findRecentClockForSchedule(@Param("schedule") Integer schedule);

    @Query(value = "SELECT clock_id, punch_in, " +
            "punch_out, c.schedule_id, date_created, c.day_id, c.employee_id FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE date_created=(SELECT MAX(date_created) FROM tblclock c " +
            "JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id " +
            "WHERE username = :username) " +
            "GROUP BY clock_id, punch_in, punch_out, c.schedule_id, date_created, c.day_id, c.employee_id;", nativeQuery = true)
    Tblclock findRecentClockForUser(@Param("username") String username);

    @Query(value = "SELECT * FROM tblclock c JOIN tblschedule s ON c.schedule_id=s.schedule_id " +
            "JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username", nativeQuery = true)
    List<Tblclock> findClockForUser(@Param("username") String user);

    List<Tblclock> findAll();

    @Modifying
    @Query(value = "DELETE FROM tblclock WHERE clock_id = :id", nativeQuery = true)
    void deleteClock(@Param("id") int id);
}
