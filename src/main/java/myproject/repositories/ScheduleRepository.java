package myproject.repositories;

import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Tblschedule, Integer> {
    /*@Query(value = "SELECT * FROM tblAvailability a JOIN tblUsers u ON a.User_ID=u.User_ID WHERE Username = :username", nativeQuery = true)
    TblAvailability findTblAvailabilitiesByUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblAvailability a JOIN tblUsers u ON a.User_ID=u.User_ID JOIN tblDay d ON a.Day_ID=d.Day_id " +
            "WHERE Username = :username AND Day_Desc = :dayDesc", nativeQuery = true)
    TblAvailability findTblAvailabilitiesByUserAndDay(@Param("username") String user, @Param("dayDesc") String dayDesc);
    */

    @Query(value = "SELECT * FROM tblschedule WHERE schedule_id = :id", nativeQuery = true)
    Tblschedule findByScheduleId(@Param("id") Integer id);

    @Query(value = "SELECT * FROM tblschedule", nativeQuery = true)
    List<Tblschedule> findAll();

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())", nativeQuery = true)
    List<Tblschedule> findScheduleThisWeekForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username", nativeQuery = true)
    List<Tblschedule> findScheduleForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE employee_id = :employeeId", nativeQuery = true)
    List<Tblschedule> findScheduleForEmployee(@Param("employeeId") int employeeId);

    @Query(value = "SELECT d.Day_Desc FROM tblday d JOIN tblSchedule s ON d.Day_id = s.day_id WHERE employee_id = :id", nativeQuery = true)
    List<String> findEmployeeDays(@Param("id") int id);

    @Query(value = "SELECT s.schedule_time_begin FROM tblschedule s " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE s.employee_id = :empId AND s.day_id = :dayId", nativeQuery = true)
    String findEmployeeStartHours(@Param("empId") int empId, @Param("dayId") int dayId);

    @Query(value = "SELECT s.schedule_time_end FROM tblschedule s " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE s.employee_id = :empId AND s.day_id = :dayId", nativeQuery = true)
    String findEmployeeEndHours(@Param("empId") int empId, @Param("dayId") int dayId);

    @Query(value = "SELECT DISTINCT * FROM tblschedule WHERE schedule_date >= :startOfNextWeek AND schedule_date <= :endOfNextWeek AND employee_id = :employeeId",
            nativeQuery = true)
    Tblschedule checkEmployeeScheduleForNextWeek(@Param("employeeId") int employeeId, @Param("startOfNextWeek") String startOfNextWeek,
                                                 @Param("endOfNextWeek") String endOfNextWeek);

    @Query(value = "SELECT * FROM tblSchedule WHERE schedule_date >= :startOfNextWeek AND schedule_date <= :endOfNextWeek", nativeQuery = true)
    List<Tblschedule> findAllEmployeeScheduleForWeek(@Param("startOfNextWeek") String startOfNextWeek,
                                                     @Param("endOfNextWeek") String endOfNextWeek);

    @Modifying
    @Query(value = "INSERT INTO tblschedule VALUES (null, null, null, :employeeId, 1)", nativeQuery = true)
    void insertEmployeeScheduleForNewWeek(@Param("employeeId") int employeeId);

    @Modifying
    @Query(value = "UPDATE tblschedule SET schedule_time_begin = :begin, schedule_time_end = :end, schedule_date = :scheduleDate, " +
            "employee_id = :empId, day_id = :dayId WHERE employee_id = :empId", nativeQuery = true)
    void updateSchedule(@Param("begin") Time begin, @Param("end") Time end,  @Param("scheduleDate") Date scheduleDate, @Param("empId") int empId, @Param("dayId") int day);

    @Modifying
    @Query(value = "DELETE FROM tblschedule WHERE employee_id = :empId", nativeQuery = true)
    void deleteEmployeeSchedule(@Param("empId") int employeeId);

    @Modifying
    @Query(value = "INSERT INTO tblschedule VALUES (:startTime, :endTime, :scheduleDate, :dayOff, :empId, :dayId)", nativeQuery = true)
    void insertEmployeeSchedule(@Param("startTime") Time startTime, @Param("endTime") Time endTime, @Param("scheduleDate") Date scheduleDate, @Param("dayOff") boolean dayOff, @Param("empId") int empId, @Param("dayId") int day);

    @Query(value = "SELECT * FROM tblschedule s " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE employee_id = :employeeId", nativeQuery = true)
    Tblschedule findSingleScheduleForEmployee(@Param("employeeId") int employeeId);

}

