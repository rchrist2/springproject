package myproject.models;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "tblavailability")
public class TblAvailability {
    private Time timeBegin;
    private Time timeEnd;
    private boolean assigned;
    private int availabilityId;
    TblUsers user;
    TblDay day;
    //private java.sql.Date date_created;

    //TODO change this to a many to many w/ tblDay and tblUsers
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_ID", referencedColumnName = "User_ID")
    public TblUsers getUser() {
        return user;
    }

    public void setUser(TblUsers user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Day_ID", referencedColumnName = "Day_id")
    public TblDay getDay() {
        return day;
    }

    public void setDay(TblDay day) {
        this.day = day;
    }


    @Basic
    @Column(name = "time_begin")
    public Time getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Time timeBegin) {
        this.timeBegin = timeBegin;
    }

    @Basic
    @Column(name = "time_end")
    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Basic
    @Column(name = "assigned")
    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

/*
    @Basic
    @Column(name = "date_created")
    public java.sql.Date getDate_created() {
        return date_created;
    }

    public void setDate_created(java.sql.Date date_created) {
        this.date_created = date_created;
    }
*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    public int getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(int availabilityId) {
        this.availabilityId = availabilityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblAvailability that = (TblAvailability) o;
        return assigned == that.assigned &&
                availabilityId == that.availabilityId &&
                Objects.equals(timeBegin, that.timeBegin) &&
                Objects.equals(timeEnd, that.timeEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeBegin, timeEnd, assigned, availabilityId);
    }
}
