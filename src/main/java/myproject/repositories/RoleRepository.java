package myproject.repositories;

import myproject.models.TblRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<TblRoles, Integer> {

    //Return the description of role id
    @Query(value = "SELECT Role_Desc FROM tblRoles WHERE Role_ID = :id", nativeQuery = true)
    String findRoleDesc(@Param("id") int roleId);

    @Query(value = "SELECT * FROM tblRoles WHERE Role_ID = :id", nativeQuery = true)
    TblRoles findRole(@Param("id") int id);

    @Query(value = "SELECT role_desc FROM tblRoles", nativeQuery = true)
    List<String> findAllRoleDesc();



}
