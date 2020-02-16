package myproject.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblclock")
public class Tblclock {
    private int clockId;
    private Timestamp punchIn;
    private Timestamp punchOut;
    Tblschedule schedule;

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
    public Timestamp getPunchIn() {
        return punchIn;
    }

    public void setPunchIn(Timestamp punchIn) {
        this.punchIn = punchIn;
    }

    @Basic
    @Column(name = "punch_out")
    public Timestamp getPunchOut() {
        return punchOut;
    }

    public void setPunchOut(Timestamp punchOut) {
        this.punchOut = punchOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tblclock tblclock = (Tblclock) o;
        return clockId == tblclock.clockId &&
                Objects.equals(punchIn, tblclock.punchIn) &&
                Objects.equals(punchOut, tblclock.punchOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clockId, punchIn, punchOut);
    }
}
