package myproject.repositories;

import myproject.models.TblDay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DayRepository extends CrudRepository<TblDay, Integer> {
    @Query(value = "SELECT * FROM tblDay WHERE Day_Desc = :dayPar", nativeQuery = true)
    TblDay findDay(@Param("dayPar") String day);
}
