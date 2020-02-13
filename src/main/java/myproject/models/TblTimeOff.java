package myproject.models;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "tbltimeoff")
public class TblTimeOff {
    private int timeOffId;
    private Date timeOffDate;
    private boolean approved;
    TblUsers user;

    public TblTimeOff() {
    }

    public TblTimeOff(Date timeOffDate, Boolean approved) {
        this.timeOffDate = timeOffDate;
        this.approved = approved;
    }



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_ID", referencedColumnName = "User_ID")
    public TblUsers getUser() {
        return user;
    }

    public void setUser(TblUsers user) {
        this.user = user;
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
    @Column(name = "time_off_date")
    public Date getTimeOffDate() {
        return timeOffDate;
    }

    public void setTimeOffDate(Date timeOffDate) {
        this.timeOffDate = timeOffDate;
    }

    @Basic
    @Column(name = "approved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblTimeOff that = (TblTimeOff) o;
        return timeOffId == that.timeOffId &&
                approved == that.approved &&
                Objects.equals(timeOffDate, that.timeOffDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOffId, timeOffDate, approved);
    }
}
