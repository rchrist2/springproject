package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tblroles", schema = "dbo", catalog = "4375db")
public class TblrolesNew {
    private int roleId;
    private String roleName;
    private String roleDesc;

    @Id
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblrolesNew that = (TblrolesNew) o;
        return roleId == that.roleId &&
                Objects.equals(roleName, that.roleName) &&
                Objects.equals(roleDesc, that.roleDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDesc);
    }
}
