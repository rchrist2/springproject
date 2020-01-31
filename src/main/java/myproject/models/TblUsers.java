package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tblUsers")
public class TblUsers {
    private int userId;
    private String username;
    private String password;
    //private TblRoles roleId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @ManyToOne
//    @JoinColumn(name = "Role_id", referencedColumnName = "Role_ID")
//    public TblRoles getRoleId() {
//        return roleId;
//    }
//
//    public void setRoleId(TblRoles roleId) {
//        this.roleId = roleId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblUsers tblUsers = (TblUsers) o;
        return userId == tblUsers.userId &&
                Objects.equals(username, tblUsers.username) &&
                Objects.equals(password, tblUsers.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password);
    }
}
