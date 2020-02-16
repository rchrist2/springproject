package myproject.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblemployee")
public class Tblemployee {
    private int id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private Set<Tblschedule> schedules;
    private TblRoles role;
    private Tblusers user;

    //one employee can only have one user account
    @OneToOne(mappedBy = "employee")
    public Tblusers getUser() {
        return user;
    }

    public void setUser(Tblusers user) {
        this.user = user;
    }

    //many employees can belong to a role
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_id", referencedColumnName = "role_id")
    public TblRoles getRole() {
        return role;
    }

    public void setRole(TblRoles role) {
        this.role = role;
    }

    //one employee can have many schedules
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "employee",cascade = CascadeType.ALL)
    public Set<Tblschedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Tblschedule> schedules) {
        this.schedules = schedules;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblemployee that = (Tblemployee) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, address, phone);
    }
}
