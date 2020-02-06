package myproject.repositories;

import myproject.models.TblDay;
import myproject.models.TblUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayRepository extends CrudRepository<TblDay, Integer> {
}
