package myproject.repositories;

import myproject.models.Tblemployee;
import myproject.models.Tbltimeoff;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.security.PermitAll;
import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Tblemployee, Integer> {

    @Query(value = "SELECT COUNT(*) FROM tblemployee e JOIN tblroles r\n" +
            "ON r.role_id=e.roles_id JOIN tblusers u ON u.employee_id=e.id\n" +
            "WHERE role_name='Owner'", nativeQuery = true)
    Integer numberOfOwner();

    @Query(value = "SELECT COUNT(*), e.id, e.name, e.email, e.address, e.phone, e.roles_id FROM tblemployee e JOIN tblroles r\n" +
            "ON r.role_id=e.roles_id JOIN tblusers u ON u.employee_id=e.id\n" +
            "WHERE role_name='Owner'" +
            "GROUP BY e.id, e.name, e.email, e.address, e.phone, e.roles_id", nativeQuery = true)
    Tblemployee numberOfOwnerGetEmp();

    //Returns a list of all the employees
    @Query(value = "SELECT * FROM tblemployee", nativeQuery = true)
    List<Tblemployee> findAllEmployee();

    @Query(value = "SELECT * FROM tblemployee e JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE Username = :username", nativeQuery = true)
    Tblemployee findAllEmployeeByUser(@Param("username") String username);

    @Query(value = "SELECT * FROM tblemployee e JOIN tblroles r ON r.role_id=e.roles_id " +
            "WHERE role_name NOT IN ('Owner','Manager')", nativeQuery = true)
    List<Tblemployee> findAllEmployeeByRoleEmployee();

    @Query(value = "SELECT * FROM tblemployee e JOIN tblroles r ON r.role_id=e.roles_id " +
            "WHERE role_name NOT IN ('Owner')", nativeQuery = true)
    List<Tblemployee> findAllEmployeeByRole();

    @Query(value = "SELECT * FROM tblemployee e " +
            "LEFT JOIN tblschedule s " +
            "ON e.id = s.employee_id " +
            "WHERE s.schedule_id IS NULL", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithoutSchedule();

    @Query(value = "SELECT * FROM tblemployee WHERE email = :email", nativeQuery = true)
    Tblemployee findEmployeeFromEmployeeEmail(@Param("email") String email);

    @Query(value = "SELECT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e " +
            "EXCEPT " +
            "SELECT em.id, em.name, em.email, em.address, em.phone, em.roles_id " +
            "FROM tblemployee em " +
            "JOIN tblschedule s " +
            "ON em.id = s.employee_id " +
            "WHERE schedule_date BETWEEN CAST(:startOfTheWeek AS DATE) AND CAST(:endOfTheWeek AS DATE)", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithoutScheduleByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e JOIN tblusers u ON u.employee_id=e.id WHERE Username = :username " +
            "EXCEPT " +
            "SELECT em.id, em.name, em.email, em.address, em.phone, em.roles_id " +
            "FROM tblemployee em " +
            "JOIN tblschedule s " +
            "ON em.id = s.employee_id " +
            "JOIN tblusers u ON em.id=u.employee_id " +
            "WHERE schedule_date BETWEEN CAST(:startOfTheWeek AS DATE) AND CAST(:endOfTheWeek AS DATE)", nativeQuery = true)
    List<Tblemployee> findCurrentEmployeeWithoutScheduleByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek, @Param("username") String username);

    @Query(value = "SELECT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e JOIN tblroles r ON r.role_id=e.roles_id WHERE role_name NOT IN ('Owner') " +
            "EXCEPT " +
            "SELECT em.id, em.name, em.email, em.address, em.phone, em.roles_id " +
            "FROM tblemployee em " +
            "JOIN tblschedule s " +
            "ON em.id = s.employee_id " +
            "JOIN tblusers u ON em.id=u.employee_id " +
            "WHERE schedule_date BETWEEN CAST(:startOfTheWeek AS DATE) AND CAST(:endOfTheWeek AS DATE)", nativeQuery = true)
    List<Tblemployee> findByRoleWithoutScheduleByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT * FROM tblemployee emp " +
            "WHERE emp.id IN (SELECT e.id FROM tblemployee e " +
            "JOIN tblschedule s ON e.id = s.employee_id " +
            "WHERE s.employee_id IS NOT NULL " +
            "AND schedule_date >= :startOfTheWeek " +
            "AND schedule_date <= :endOfTheWeek)", nativeQuery = true)
    List<Tblemployee> findAllEmployeesWithSchedule(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT * FROM tblemployee WHERE email = :email", nativeQuery = true)
    Tblemployee findEmployeeByEmail(@Param("email") String email);

    @Query(value = "SELECT email FROM tblemployee", nativeQuery = true)
    List<String> findAllEmails();

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

    @Query(value = "SELECT DISTINCT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e " +
            "JOIN tblschedule s ON e.id = s.employee_id JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE schedule_date >= :startOfTheWeek AND schedule_date <= :endOfTheWeek AND Username = :username",nativeQuery = true)
    List<Tblemployee> findCurrentEmployeeByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek, @Param("username") String username);

    @Query(value = "SELECT DISTINCT e.id, e.name, e.email, e.address, e.phone, e.roles_id " +
            "FROM tblemployee e JOIN tblroles r ON r.role_id=e.roles_id " +
            "JOIN tblschedule s ON e.id = s.employee_id JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE schedule_date >= :startOfTheWeek AND schedule_date <= :endOfTheWeek AND (role_name NOT IN ('Owner'))",nativeQuery = true)
    List<Tblemployee> findByRoleByWeek(@Param("startOfTheWeek") String startOfTheWeek, @Param("endOfTheWeek") String endOfTheWeek);

    @Query(value = "SELECT * FROM tblemployee WHERE id = :id ", nativeQuery = true)
    Tblemployee findEmployeeById(@Param("id") int id);

    @Modifying
    @Query(value = "UPDATE tblemployee SET name = :name, email = :email, address = :address, phone = :phone, roles_id = :roleId WHERE id = :id", nativeQuery = true)
    void updateEmployee(@Param("name") String name, @Param("email") String email, @Param("address") String address, @Param("phone") String phone, @Param("id") int id, @Param("roleId") int roleId);

    @Modifying
    @Query(value = "UPDATE tblemployee SET role_id = :role WHERE email = :email", nativeQuery = true)
    void updateEmployeeRole(@Param("role") int roleId, @Param("email") String email);

    @Modifying
    @Query(value = "DELETE FROM tblemployee WHERE id = :id", nativeQuery = true)
    void deleteEmployee(@Param("id") int employeeId);
}
