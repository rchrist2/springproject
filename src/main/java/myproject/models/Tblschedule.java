package myproject.models;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Set;
import java.sql.Time;

@Entity
@Table(name = "tblschedule")
public class Tblschedule {
    private int scheduleId;
    private Time scheduleTimeBegin;
    private Time scheduleTimeEnd;
    private Date scheduleDate;
    private Tbltimeoff timeOffs;
    private Set<Tblclock> clocks;

    Tblemployee employee;
    TblDay day;

    public Tblschedule() {
    }

    public Tblschedule(Time scheduleTimeBegin, Time scheduleTimeEnd, Date scheduleDate, Tblemployee employee, TblDay day) {
        this.scheduleTimeBegin = scheduleTimeBegin;
        this.scheduleTimeEnd = scheduleTimeEnd;
        this.scheduleDate = scheduleDate;
        this.employee = employee;
        this.day = day;
    }

    public Tblschedule(Time scheduleTimeBegin, Time scheduleTimeEnd, TblDay day) {
        this.scheduleTimeBegin = scheduleTimeBegin;
        this.scheduleTimeEnd = scheduleTimeEnd;
        this.day = day;
    }

    //a schedule can have only one time off request
    //uses cascade REMOVE to allow deletions of related time offs
    @OneToOne(fetch = FetchType.LAZY,mappedBy = "schedule",cascade = CascadeType.ALL)
    public Tbltimeoff getTimeOffs() {
        return timeOffs;
    }

    public void setTimeOffs(Tbltimeoff timeOffs) {
        this.timeOffs = timeOffs;
    }

    //one schedule can have many clock-ins/outs
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "schedule",cascade = CascadeType.ALL)
    public Set<Tblclock> getClocks() {
        return clocks;
    }

    public void setClocks(Set<Tblclock> clocks) {
        this.clocks = clocks;
    }

    //many schedules can belong to one employee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    public Tblemployee getEmployee() {
        return employee;
    }

    public void setEmployee(Tblemployee employee) {
        this.employee = employee;
    }

    //many schedules can have one day
    @ManyToOne(fetch = FetchType.LAZY)
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
    public Time getScheduleTimeBegin() {
        return scheduleTimeBegin;
    }

    public void setScheduleTimeBegin(Time scheduleTimeBegin) {
        this.scheduleTimeBegin = scheduleTimeBegin;
    }

    @Basic
    @Column(name = "schedule_time_end")
    public Time getScheduleTimeEnd() {
        return scheduleTimeEnd;
    }

    public void setScheduleTimeEnd(Time scheduleTimeEnd) {
        this.scheduleTimeEnd = scheduleTimeEnd;
    }

    @Basic
    @Column(name = "schedule_date")
    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
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
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        return day.getDayDesc() + ", " + dateFormat.format(scheduleDate) + " "
                + timeFormat.format(scheduleTimeBegin) + " to " + timeFormat.format(scheduleTimeEnd);
    }
}
