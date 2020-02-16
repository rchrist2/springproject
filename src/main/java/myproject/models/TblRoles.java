package myproject.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblroles")
public class TblRoles {
    private int roleId;
    private String roleDesc;
    private Set<Tblemployee> employees;

    //one role can belong to many employees
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "role",cascade = CascadeType.ALL)
    public Set<Tblemployee> getEmployee() {
        return employees;
    }

    public void setEmployee(Set<Tblemployee> employees) {
        this.employees = employees;
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
    @Column(name = "Role_Desc")
    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblRoles tblRoles = (TblRoles) o;
        return roleId == tblRoles.roleId &&
                Objects.equals(roleDesc, tblRoles.roleDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleDesc);
    }
}
