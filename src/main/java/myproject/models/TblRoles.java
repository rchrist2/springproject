package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
//@Table(name = "tblRoles")
public class TblRoles {
    private int roleId;
    private String roleDesc;

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
