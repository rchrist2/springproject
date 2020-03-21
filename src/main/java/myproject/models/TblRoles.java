package myproject.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblroles")
public class TblRoles {
    private int roleId;
    private String roleName;
    private String roleDesc;
    private Set<Tblemployee> employees;

    public TblRoles() {
    }

    public TblRoles(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Role_ID")
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "Role_Name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Basic
    @Column(name = "Role_Desc")
    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    //one role can belong to many employees
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "role",cascade = CascadeType.ALL)
    public Set<Tblemployee> getEmployee() {
        return employees;
    }

    public void setEmployee(Set<Tblemployee> employees) {
        this.employees = employees;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblRoles that = (TblRoles) o;
        return roleId == that.roleId &&
                Objects.equals(roleName, that.roleName) &&
                Objects.equals(roleDesc, that.roleDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDesc);
    }

    @Override
    public String toString(){
        return this.getRoleName();
    }
}
