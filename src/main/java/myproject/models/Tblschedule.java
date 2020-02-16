package myproject.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblschedule")
public class Tblschedule {
    private int scheduleId;
    private java.sql.Time scheduleTimeBegin;
    private java.sql.Time scheduleTimeEnd;
    private Timestamp scheduleDate;
    private Tbltimeoff timeOff;
    private Set<Tblclock> clocks;
    Tblemployee employee;
    TblDay day;

    //a schedule can only have one time off request
    @OneToOne(mappedBy = "schedule")
    public Tbltimeoff getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(Tbltimeoff timeOff) {
        this.timeOff = timeOff;
    }

    //one schedule can have many clock-ins/outs
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "schedule",cascade = CascadeType.ALL)
    public Set<Tblclock> getClocks() {
        return clocks;
    }

    public void setClocks(Set<Tblclock> clocks) {
        this.clocks = clocks;
    }

    //many schedules can belong to one employee
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    public Tblemployee getEmployee() {
        return employee;
    }

    public void setEmployee(Tblemployee employee) {
        this.employee = employee;
    }

    //many schedules can have one day
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "day_id", referencedColumnName = "day_id")
    public TblDay getDay() {
        return day;
    }

    public void setDay(TblDay day) {
        this.day = day;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Basic
    @Column(name = "schedule_time_begin")
    public java.sql.Time getScheduleTimeBegin() {
        return scheduleTimeBegin;
    }

    public void setScheduleTimeBegin(java.sql.Time scheduleTimeBegin) {
        this.scheduleTimeBegin = scheduleTimeBegin;
    }

    @Basic
    @Column(name = "schedule_time_end")
    public java.sql.Time getScheduleTimeEnd() {
        return scheduleTimeEnd;
    }

    public void setScheduleTimeEnd(java.sql.Time scheduleTimeEnd) {
        this.scheduleTimeEnd = scheduleTimeEnd;
    }

    @Basic
    @Column(name = "schedule_date")
    public Timestamp getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Timestamp scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblschedule that = (Tblschedule) o;
        return scheduleId == that.scheduleId &&
                Objects.equals(scheduleTimeBegin, that.scheduleTimeBegin) &&
                Objects.equals(scheduleTimeEnd, that.scheduleTimeEnd) &&
                Objects.equals(scheduleDate, that.scheduleDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId, scheduleTimeBegin, scheduleTimeEnd, scheduleDate);
    }

    @Override
    public String toString(){
        return this.scheduleDate.toString();
    }
}
