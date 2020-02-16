package myproject.repositories;

import myproject.models.Tblclock;
import myproject.models.Tblusers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockRepository extends CrudRepository<Tblclock, Integer> {
}
