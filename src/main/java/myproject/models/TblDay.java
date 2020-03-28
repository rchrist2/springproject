package myproject.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblday")
public class TblDay {
    private int dayId;
    private String dayDesc;
    private Set<Tblschedule> schedules;
    private Set<Tblclock> clocks;
    private Set<Tbltimeoff> timeOffs;

    //one day can be used in many schedules
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day",cascade = CascadeType.ALL)
    public Set<Tblschedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Tblschedule> schedules) {
        this.schedules = schedules;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day",cascade = CascadeType.ALL)
    public Set<Tblclock> getClocks() {
        return clocks;
    }

    public void setClocks(Set<Tblclock> clocks) {
        this.clocks = clocks;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day",cascade = CascadeType.ALL)
    public Set<Tbltimeoff> getTimeOffs() {
        return timeOffs;
    }

    public void setTimeOffs(Set<Tbltimeoff> timeOffs) {
        this.timeOffs = timeOffs;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    @Basic
    @Column(name = "day_desc")
    public String getDayDesc() {
        return dayDesc;
    }

    public void setDayDesc(String dayDesc) {
        this.dayDesc = dayDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblDay tblDay = (TblDay) o;
        return dayId == tblDay.dayId &&
                Objects.equals(dayDesc, tblDay.dayDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId, dayDesc);
    }
}
