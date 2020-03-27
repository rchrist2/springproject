package myproject.models;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "tblusers")
public class Tblusers {
    private int userId;
    private String username;
    private String password;
    private Tblemployee employee;
    private byte[] saltPassword;
    private String hashedPassword;

    public Tblusers() {
    }

    public Tblusers(String username, String password, Tblemployee employee){
        this.username = username;
        this.password = password;
        this.employee = employee;
    }

    public Tblusers(String username, String password, byte[] saltPassword, String hashedPassword, Tblemployee employee){
        this.username = username;
        this.password = password;
        this.saltPassword = saltPassword;
        this.hashedPassword = hashedPassword;
        this.employee = employee;
    }

    //one user account can belong to only one employee
    @OneToOne(fetch = FetchType.EAGER)
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
    @Column(name = "salt_password")
    public byte[] getSaltPassword() {
        return saltPassword;
    }

    public void setSaltPassword(byte[] saltPassword) {
        this.saltPassword = saltPassword;
    }

    @Basic
    @Column(name = "hashed_password")
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblusers that = (Tblusers) o;
        return userId == that.userId &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Arrays.equals(saltPassword, that.saltPassword) &&
                Objects.equals(hashedPassword, that.hashedPassword);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userId, username, password, hashedPassword);
        result = 31 * result + Arrays.hashCode(saltPassword);
        return result;
    }
}
