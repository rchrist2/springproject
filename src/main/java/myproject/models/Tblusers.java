package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tblusers")
public class Tblusers {
    private int userId;
    private String username;
    private String password;
    private Tblemployee employee;

    //one user account can belong to only one employee
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    public Tblemployee getEmployee() {
        return employee;
    }

    public void setEmployee(Tblemployee employee) {
        this.employee = employee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblusers tblusers = (Tblusers) o;
        return userId == tblusers.userId &&
                Objects.equals(username, tblusers.username) &&
                Objects.equals(password, tblusers.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password);
    }
}
