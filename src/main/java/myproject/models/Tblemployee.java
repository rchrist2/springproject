package myproject.models;

import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    //private Set<Tbltimeoff> timeOffs;
    //private Set<Tblclock> clocks;
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

    /*@OneToMany(fetch = FetchType.EAGER,mappedBy = "employee",cascade = CascadeType.ALL)
    public Set<Tblclock> getClocks() {
        return clocks;
    }

    public void setClocks(Set<Tblclock> clocks) {
        this.clocks = clocks;
    }*/

    //one employee can only have one user account
    @OneToOne(fetch = FetchType.EAGER,mappedBy = "employee",cascade = CascadeType.ALL)
    public Tblusers getUser() {
        return user;
    }

    public void setUser(Tblusers user) {
        this.user = user;
    }

    //one employee can have many time off requests
    /*@OneToMany(fetch = FetchType.EAGER,mappedBy = "employee",cascade = CascadeType.ALL)
    public Set<Tbltimeoff> getTimeOffs() {
        return timeOffs;
    }

    public void setTimeOffs(Set<Tbltimeoff> timeOffs) {
        this.timeOffs = timeOffs;
    }*/

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
    public String employeeSchedule(Date startOfTheWeek, Date endOfTheWeek){
        List<String> listOfDays = new ArrayList<>();

        for (Tblschedule schedule : this.schedules) {
            //Grabs the schedule within the week
            if(schedule.getScheduleDate().compareTo(startOfTheWeek) >= 0 && schedule.getScheduleDate().compareTo(endOfTheWeek) <= 0)
                //Checks if time off is null OR if employee doesn't have a day off AND time off is not approved
                if(schedule.getTimeOffs() == null || !(schedule.getTimeOffs().isApproved())){
                    listOfDays.add(schedule.getDay().getDayDesc());
            }
        }

        return String.join("\n", listOfDays);
    }

    public String employeeDates(Date startOfTheWeek, Date endOfTheWeek){
        List<String> listOfDates = new ArrayList<>();

        for (Tblschedule tblschedule : this.schedules){
            if(tblschedule.getScheduleDate().compareTo(startOfTheWeek) >= 0 && tblschedule.getScheduleDate().compareTo(endOfTheWeek) <= 0)
                if(tblschedule.getTimeOffs() == null || !(tblschedule.getTimeOffs().isApproved())){
                    listOfDates.add(tblschedule.getScheduleDate().toString());
                }
        }

        return String.join("\n", listOfDates);
    }

    public String employeeStartHours(Date startOfTheWeek, Date endOfTheWeek){
        List<String> listOfHours = new ArrayList<>();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        for (Tblschedule hours : this.schedules) {
            if(hours.getScheduleDate().compareTo(startOfTheWeek) >= 0 && hours.getScheduleDate().compareTo(endOfTheWeek) <= 0)
                if(hours.getTimeOffs() == null || !(hours.getTimeOffs().isApproved())){
                    listOfHours.add(timeFormat.format(hours.getScheduleTimeBegin()));
                }
        }

        return String.join("\n", listOfHours);
    }

    public String employeeEndHours(Date startOfTheWeek, Date endOfTheWeek){
        List<String> listOfHours = new ArrayList<>();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        for(Tblschedule hours : this.schedules) {
            if(hours.getScheduleDate().compareTo(startOfTheWeek) >= 0 && hours.getScheduleDate().compareTo(endOfTheWeek) <= 0)
                if(hours.getTimeOffs() == null || !(hours.getTimeOffs().isApproved())){
                    listOfHours.add(timeFormat.format(hours.getScheduleTimeEnd()));
                }
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
