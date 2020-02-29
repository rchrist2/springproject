package myproject.models;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private TblRoles role;

    private Set<Tblschedule> schedules;
    private Tblusers user;

    public Tblemployee() {
    }

    public Tblemployee(String name, String email, String address, String phone, TblRoles role) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.role = role;
        this.schedules = null;
    }

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

    //Returns the list of days the employee works for the tableview
    public String employeeSchedule(){
        List<String> listOfDays = new ArrayList<>();

        for (Tblschedule schedule : this.schedules) {
            System.out.println(schedule);
            listOfDays.add(schedule.getDay().getDayDesc());
        }

        return String.join("\n", listOfDays);
    }

    public String employeeHours(){
        List<String> listOfHours = new ArrayList<>();

        String strDateFormat = "HH:mm a";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(strDateFormat);

        for (Tblschedule hours : this.schedules) {
            listOfHours.add(hours.getScheduleTimeBegin().toLocalTime().format(timeFormatter) + " - " + hours.getScheduleTimeEnd().toLocalTime().format(timeFormatter));
        }

        return String.join("\n", listOfHours);
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

    @Override
    public String toString() {
        return name;
    }
}
