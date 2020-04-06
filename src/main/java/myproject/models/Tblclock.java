package myproject.models;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblclock")
public class Tblclock {
    private int clockId;
    private java.sql.Time punchIn;
    private java.sql.Time punchOut;
    private Timestamp dateCreated;
    //private String dayDesc;
    Tblschedule schedule;
    TblDay day;
    //Tblemployee employee;

    /*@Basic
    @Column(name = "day_desc")
    public String getDayDesc() {
        return dayDesc;
    }

    public void setDayDesc(String dayDesc) {
        this.dayDesc = dayDesc;
    }*/

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    public Tblemployee getEmployee() {
        return employee;
    }

    public void setEmployee(Tblemployee employee) {
        this.employee = employee;
    }*/

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "day_id", referencedColumnName = "day_id")
    public TblDay getDay() {
        return day;
    }

    public void setDay(TblDay day) {
        this.day = day;
    }

    //many clock-ins/outs can be used for one schedule
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    public Tblschedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Tblschedule schedule) {
        this.schedule = schedule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clock_id")
    public int getClockId() {
        return clockId;
    }

    public void setClockId(int clockId) {
        this.clockId = clockId;
    }

    @Basic
    @Column(name = "punch_in")
    public java.sql.Time getPunchIn() {
        return punchIn;
    }

    public void setPunchIn(java.sql.Time punchIn) {
        this.punchIn = punchIn;
    }

    @Basic
    @Column(name = "punch_out")
    public java.sql.Time getPunchOut() {
        return punchOut;
    }

    public void setPunchOut(java.sql.Time punchOut) {
        this.punchOut = punchOut;
    }

    @Basic
    @Column(name = "date_created")
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblclock tblclock = (Tblclock) o;
        return clockId == tblclock.clockId &&
                Objects.equals(punchIn, tblclock.punchIn) &&
                Objects.equals(punchOut, tblclock.punchOut) &&
                Objects.equals(dateCreated, tblclock.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clockId, punchIn, punchOut);
    }

    @Override
    public String toString(){
        return this.getDay().getDayDesc();
    }
}
