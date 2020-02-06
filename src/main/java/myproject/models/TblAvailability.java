package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tblavailability")
public class TblAvailability {
    private java.sql.Time timeBegin;
    private java.sql.Time timeEnd;
    private boolean assigned;
    private int availabilityId;
    TblUsers users;

    //TODO change this to a many to many w/ tblDay and tblUsers
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_ID", referencedColumnName = "User_ID")
    public TblUsers getUsers() {
        return users;
    }

    public void setUsers(TblUsers user) {
        this.users = user;
    }


    @Basic
    @Column(name = "time_begin")
    public java.sql.Time getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(java.sql.Time timeBegin) {
        this.timeBegin = timeBegin;
    }

    @Basic
    @Column(name = "time_end")
    public java.sql.Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(java.sql.Time timeEnd) {
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

    @Id
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
