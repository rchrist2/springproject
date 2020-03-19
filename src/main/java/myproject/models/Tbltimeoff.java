package myproject.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
@Table(name = "tbltimeoff")
public class Tbltimeoff {
    private int timeOffId;
    private java.sql.Time beginTimeOffDate;
    private java.sql.Time endTimeOffDate;
    private boolean approved;
    private boolean dayOff;
    private TblDay day;
    //private String dayDesc;
    private String reasonDesc;
    private Tblschedule schedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "day_id", referencedColumnName = "day_id")
    public TblDay getDay() {
        return day;
    }

    public void setDay(TblDay day) {
        this.day = day;
    }

    /*@Basic
    @Column(name = "day_desc")
    public String getDayDesc() {
        return dayDesc;
    }

    public void setDayDesc(String dayDesc) {
        this.dayDesc = dayDesc;
    }*/

    //only one time off request can belong to only one schedule
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    public Tblschedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Tblschedule schedule) {
        this.schedule = schedule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_off_id")
    public int getTimeOffId() {
        return timeOffId;
    }

    public void setTimeOffId(int timeOffId) {
        this.timeOffId = timeOffId;
    }

    @Basic
    @Column(name = "begin_time_off_date")
    public java.sql.Time getBeginTimeOffDate() {
        return beginTimeOffDate;
    }

    public void setBeginTimeOffDate(java.sql.Time beginTimeOffDate) {
        this.beginTimeOffDate = beginTimeOffDate;
    }

    @Basic
    @Column(name = "end_time_off_date")
    public java.sql.Time getEndTimeOffDate() {
        return endTimeOffDate;
    }

    public void setEndTimeOffDate(java.sql.Time endTimeOffDate) {
        this.endTimeOffDate = endTimeOffDate;
    }

    @Basic
    @Column(name = "approved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Basic
    @Column(name = "day_off")
    public boolean isDayOff() {
        return dayOff;
    }

    public void setDayOff(boolean dayOff) {
        this.dayOff = dayOff;
    }

    @Basic
    @Column(name = "reason_desc")
    public String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tbltimeoff that = (Tbltimeoff) o;
        return timeOffId == that.timeOffId &&
                approved == that.approved &&
                Objects.equals(beginTimeOffDate, that.beginTimeOffDate) &&
                Objects.equals(endTimeOffDate, that.endTimeOffDate) &&
                Objects.equals(reasonDesc, that.reasonDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOffId, beginTimeOffDate, endTimeOffDate, approved, reasonDesc);
    }

    @Override
    public String toString(){
        return this.getSchedule().toString();
    }
}
