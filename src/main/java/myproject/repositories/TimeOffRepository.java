package myproject.repositories;

import myproject.models.TblEmployee;
import myproject.models.TblRoles;
import myproject.models.TblTimeOff;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRepository extends CrudRepository<TblTimeOff, Integer> {

    //Returns a list of all the employees
    @Query(value = "SELECT * FROM tbltimeoff t JOIN tblusers u ON t.User_ID=u.User_ID " +
            "WHERE Username = :username", nativeQuery = true)
    List<TblTimeOff> findAllTimeOffByUser(@Param("username") String user);

}