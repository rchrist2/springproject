package myproject.repositories;

import myproject.models.TblRoles;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "SELECT * FROM tblroles", nativeQuery = true)
    List<TblRoles> findAll();

    @Query(value = "SELECT * FROM tblroles WHERE role_name NOT IN ('Owner')", nativeQuery = true)
    List<TblRoles> findNotOwnerRoles();

    @Query(value = "SELECT * FROM tblRoles WHERE role_name = :role_name", nativeQuery = true)
    TblRoles findRole(@Param("role_name") String role_name);

    @Query(value = "SELECT role_name FROM tblRoles", nativeQuery = true)
    List<String> findAllRoleName();

    @Query(value = "SELECT role_desc FROM tblRoles WHERE role_id = :id", nativeQuery = true)
    String findRoleDescFromRoleId(@Param("id") int id);

    @Query(value = "SELECT role_name, role_desc FROM tblRoles", nativeQuery = true)
    List<String> findAllRoleNameAndDesc();

    @Query(value = "SELECT name FROM tblemployee e JOIN tblroles r ON e.roles_id = r.Role_ID WHERE r.Role_ID = :roleId", nativeQuery = true)
    List<String> findAllEmployeeWithRoleId(@Param("roleId") int roleId);

    @Modifying
    @Query(value = "UPDATE tblroles SET Role_Name = :roleName, Role_Desc = :roleDesc WHERE Role_ID = :roleId", nativeQuery = true)
    void updateRole(@Param("roleName") String roleName, @Param("roleDesc") String roleDesc, @Param("roleId") int roleId);
}
