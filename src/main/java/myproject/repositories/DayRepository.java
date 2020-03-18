package myproject.repositories;

import myproject.models.TblDay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayRepository extends CrudRepository<TblDay, Integer> {

    @Query(value = "SELECT * FROM tblday WHERE day_id = :dayPar", nativeQuery = true)
    TblDay findDayByID(@Param("dayPar") int dayId);

    @Query(value = "SELECT * FROM tblday WHERE Day_Desc = :dayPar", nativeQuery = true)
    TblDay findDay(@Param("dayPar") String day);

    @Query(value = "SELECT day_desc FROM tblday", nativeQuery = true)
    List<String> findAllDays();
}
