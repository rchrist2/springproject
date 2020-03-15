package myproject.repositories;

import myproject.models.Tblemployee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.security.PermitAll;
import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Tblemployee, Integer> {

    //Returns a list of all the employees
    @Query(value = "SELECT * FROM tblemployee", nativeQuery = true)
    List<Tblemployee> findAllEmployee();

    @Query(value = "SELECT * FROM tblemployee e " +
            "LEFT JOIN tblschedule s " +
            "ON e.id = s.employee_id " +
            "WHERE s.schedule_id IS NULL", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithoutSchedule();

    @Query(value = "SELECT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e " +
            "EXCEPT " +
            "SELECT em.id, em.name, em.email, em.address, em.phone, em.roles_id " +
            "FROM tblemployee em " +
            "JOIN tblschedule s " +
            "ON em.id = s.employee_id " +
            "WHERE schedule_date BETWEEN CAST(:startOfTheWeek AS DATE) AND CAST(:endOfTheWeek AS DATE)", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithoutScheduleByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);



    @Query(value = "SELECT * FROM tblemployee e JOIN tblschedule s ON e.id = s.employee_id", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithSchedule();

    @Query(value = "SELECT * FROM tblemployee WHERE name = :name", nativeQuery = true)
    Tblemployee findEmployeeByName(@Param("name") int empName);

    @Query(value = "SELECT name FROM tblemployee", nativeQuery = true)
    List<Tblemployee> findAllEmployeeByName();

    //Returns the user for an employee
    @Query(value = "SELECT * FROM tblemployee e JOIN tblusers u ON e.id=u.employee_id WHERE username = :username", nativeQuery = true)
    List<Tblemployee> findUserForEmployee(@Param("username") String user);

    @Query(value = "SELECT * FROM tblemployee e " +
                   "JOIN tblschedule s " +
                   "ON e.id = s.employee_id " +
                   "WHERE schedule_date >= :startOfTheWeek " +
                   "AND schedule_date <= :endOfTheWeek AND employee_id = :employeeId", nativeQuery = true)
    List<Tblemployee> findEmployeeByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek, @Param("employeeId") int employeeId);

    @Query(value = "SELECT DISTINCT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e " +
            "JOIN tblschedule s ON e.id = s.employee_id " +
            "WHERE schedule_date >= :startOfTheWeek AND schedule_date <= :endOfTheWeek",nativeQuery = true)
    List<Tblemployee> findAllEmployeeByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT * FROM tblemployee WHERE id = :id ", nativeQuery = true)
    Tblemployee findEmployeeById(@Param("id") int id);

    @Modifying
    @Query(value = "UPDATE tblemployee SET name = :name, email = :email, address = :address, phone = :phone WHERE id = :id", nativeQuery = true)
    void updateEmployee(@Param("name") String name, @Param("email") String email, @Param("address") String address, @Param("phone") String phone, @Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM tblemployee WHERE id = :id", nativeQuery = true)
    void deleteEmployee(@Param("id") int employeeId);
}
