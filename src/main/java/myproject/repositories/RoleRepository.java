package myproject.repositories;

import myproject.models.TblRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<TblRoles, Integer> {

    //Return the description of role id
    @Query(value = "SELECT Role_Desc FROM tblRoles WHERE Role_ID = :id", nativeQuery = true)
    String findRoleDesc(@Param("id") int roleId);

}
