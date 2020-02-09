package myproject.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tblday")
public class TblDay {
    private int dayId;
    private String dayDesc;
    private Set<TblAvailability> availabilities;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day",cascade = CascadeType.ALL)
    public Set<TblAvailability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Set<TblAvailability> availabilities) {
        this.availabilities = availabilities;
    }


    @Id
    @Column(name = "Day_id")
    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    @Basic
    @Column(name = "Day_Desc")
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
