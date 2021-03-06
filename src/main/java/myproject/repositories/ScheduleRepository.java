package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblemployee;
import myproject.models.Tblschedule;
import myproject.models.Tbltimeoff;
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

    @Query(value = "SELECT * FROM tblschedule s LEFT JOIN tbltimeoff t " +
            "ON s.schedule_date >= t.begin_time_off_date " +
            "AND s.schedule_date <= t.end_time_off_date " +
            " AND t.employee_id = :empId " +
            "JOIN tblemployee e ON e.id=s.employee_id " +
            "JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE t.schedule_id IS NULL " +
            "AND (t.approved=1 OR t.approved=0) " +
            "AND Username = :username", nativeQuery = true)
    List<Tblschedule> findScheduleForUserWithUnlinkedTimeOff(@Param("username") String user, @Param("empId") int empId);


    @Query(value = "SELECT * FROM tblschedule WHERE schedule_id = :id", nativeQuery = true)
    List<Tblschedule> findScheduleNotDayOff();



    @Query(value = "SELECT * FROM tblschedule WHERE schedule_id = :id", nativeQuery = true)
    Tblschedule findByScheduleId(@Param("id") Integer id);

    @Query(value = "SELECT * FROM tblschedule", nativeQuery = true)
    List<Tblschedule> findAll();

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            "AND DATEPART(week, s.schedule_date) = DATEPART(week, GETDATE())", nativeQuery = true)
    List<Tblschedule> findScheduleThisWeekForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username " +
            " AND s.schedule_date = CAST(GETDATE() AS DATE)", nativeQuery = true)
    Tblschedule findScheduleThisWeekForUserSameDay(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username" +
            " AND s.schedule_date >= CAST(GETDATE() AS DATE)" +
            " ORDER BY s.schedule_date", nativeQuery = true)
    List<Tblschedule> findScheduleForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username" +
            " ORDER BY s.schedule_date", nativeQuery = true)
    List<Tblschedule> findAllScheduleForUser(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s JOIN tblemployee e ON s.employee_id=e.id JOIN " +
            "tblusers u ON e.id=u.employee_id WHERE Username = :username" +
            " AND s.schedule_date <= CAST(GETDATE() AS DATE)" +
            " ORDER BY s.schedule_date", nativeQuery = true)
    List<Tblschedule> findAllScheduleForUserLessThanEqualToToday(@Param("username") String user);

    @Query(value = "SELECT * FROM tblschedule s LEFT JOIN tbltimeoff t ON s.schedule_id=t.schedule_id " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE s.employee_id = :employeeId AND t.schedule_id IS NULL " +
            "OR s.employee_id = :employeeId AND NOT (t.approved=1)", nativeQuery = true)
    List<Tblschedule> findScheduleForEmployee(@Param("employeeId") int employeeId);

    @Query(value = "SELECT * FROM tblschedule s LEFT JOIN tbltimeoff t ON s.schedule_id=t.schedule_id " +
            "JOIN tblemployee e ON s.employee_id = e.id " +
            "WHERE s.employee_id = :employeeId AND t.schedule_id IS NULL " +
            "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek " +
            "OR s.employee_id = :employeeId AND NOT (t.approved=1) " +
            "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek", nativeQuery = true)
    List<Tblschedule> findScheduleForEmployeeSchedList(@Param("employeeId") int employeeId, @Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT d.Day_Desc " +
                   "FROM tblday d " +
                   "JOIN tblSchedule s " +
                   "ON d.Day_id = s.day_id WHERE s.employee_id = :id " +
                   "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek " +
                   "EXCEPT " +
                   "SELECT d.Day_Desc " +
                   "FROM tblday d " +
                   "JOIN tblSchedule s " +
                   "ON d.Day_id = s.day_id " +
                   "JOIN tbltimeoff t " +
                   "ON s.schedule_id = t.schedule_id " +
                   "WHERE s.employee_id = :id AND t.approved=1 " +
                   "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek", nativeQuery = true)
    List<String> findEmployeeDays(@Param("id") int id, @Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT s.schedule_time_begin " +
                   "FROM tblschedule s WHERE s.employee_id = :empId AND s.day_id = :dayId  " +
                   "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek " +
                   "EXCEPT " +
                   "SELECT sm.schedule_time_begin " +
                   "FROM tblschedule sm " +
                   "JOIN tblemployee e " +
                   "ON sm.employee_id = e.id " +
                   "JOIN tbltimeoff t " +
                   "ON sm.schedule_id = t.schedule_id " +
                   "WHERE sm.employee_id = :empId AND sm.day_id = :dayId AND t.approved=1 " +
                   "AND sm.schedule_date >= :startOfTheWeek AND sm.schedule_date <= :endOfTheWeek", nativeQuery = true)
    Time findEmployeeStartHours(@Param("empId") int empId, @Param("dayId") int dayId, @Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT s.schedule_time_end " +
            "FROM tblschedule s WHERE s.employee_id = :empId AND s.day_id = :dayId " +
            "AND s.schedule_date >= :startOfTheWeek AND s.schedule_date <= :endOfTheWeek " +
            "EXCEPT " +
            "SELECT sm.schedule_time_end " +
            "FROM tblschedule  sm " +
            "JOIN tblemployee e " +
            "ON sm.employee_id = e.id " +
            "JOIN tbltimeoff t " +
            "ON sm.schedule_id = t.schedule_id " +
            "WHERE sm.employee_id = :empId AND sm.day_id = :dayId AND t.approved=1 " +
            "AND sm.schedule_date >= :startOfTheWeek AND sm.schedule_date <= :endOfTheWeek", nativeQuery = true)
    Time findEmployeeEndHours(@Param("empId") int empId, @Param("dayId") int dayId, @Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);


    //Commented this out because we aren't using them
/*    @Query(value = "SELECT DISTINCT * FROM tblschedule WHERE schedule_date >= CAST(:startOfNextWeek AS DATE) AND schedule_date <= CAST(:endOfNextWeek AS DATE) AND employee_id = :employeeId",
            nativeQuery = true)
    Tblschedule checkEmployeeScheduleForNextWeek(@Param("employeeId") int employeeId, @Param("startOfNextWeek") String startOfNextWeek,
                                                 @Param("endOfNextWeek") String endOfNextWeek);

    @Query(value = "SELECT * FROM tblSchedule WHERE schedule_date >= :startOfNextWeek AND schedule_date <= :endOfNextWeek", nativeQuery = true)
    List<Tblschedule> findAllEmployeeScheduleForWeek(@Param("startOfNextWeek") String startOfNextWeek,
                                                     @Param("endOfNextWeek") String endOfNextWeek);

    @Modifying
    @Query(value = "INSERT INTO tblschedule VALUES (null, null, null, :employeeId, 1)", nativeQuery = true)
    void insertEmployeeScheduleForNewWeek(@Param("employeeId") int employeeId);*/

    @Modifying
    @Query(value = "UPDATE tblschedule SET schedule_time_begin = :begin, schedule_time_end = :end, day_id = :dayId WHERE employee_id = :empId", nativeQuery = true)
    void updateSchedule(@Param("begin") Time begin, @Param("end") Time end, @Param("empId") int empId, @Param("dayId") int dayId);

    @Modifying
    @Query(value = "DELETE FROM tblschedule WHERE employee_id = :empId", nativeQuery = true)
    void deleteEmployeeSchedule(@Param("empId") int employeeId);

    @Modifying
    @Query(value = "DELETE FROM tblschedule WHERE schedule_id = :id", nativeQuery = true)
    void deleteSchedule(@Param("id") int id);

    @Modifying
    @Query(value = "INSERT INTO tblschedule VALUES (:startTime, :endTime, :scheduleDate, :empId, :dayId)", nativeQuery = true)
    void insertEmployeeSchedule(@Param("startTime") Time startTime, @Param("endTime") Time endTime, @Param("scheduleDate") Date scheduleDate, @Param("empId") int empId, @Param("dayId") int day);

}

