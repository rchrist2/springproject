package myproject.repositories;

import myproject.models.Tbltimeoff;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRepository extends CrudRepository<Tbltimeoff, Integer> {

    //Returns a list of all time offs by user
    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblschedule s ON t.schedule_id=s.schedule_id" +
            " JOIN tblemployee e ON e.id=s.employee_id JOIN tblusers u ON u.employee_id=e.id " +
            "WHERE Username = :username", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOffByUser(@Param("username") String user);

    //Returns a list of all time offs
    @Query(value = "SELECT * FROM tbltimeoff ", nativeQuery = true)
    List<Tbltimeoff> findAllTimeOff();

}