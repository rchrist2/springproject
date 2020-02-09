package myproject.repositories;

import myproject.models.TblDay;
import myproject.models.TblEmployee;
import myproject.models.TblUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayRepository extends CrudRepository<TblDay, Integer> {
    @Query(value = "SELECT * FROM tblDay WHERE Day_Desc = :dayPar", nativeQuery = true)
    TblDay findDay(@Param("dayPar") String day);
}
