package myproject.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
//@Table(name = "tblDay")
public class TblDay {
    private int dayId;
    private String dayDesc;

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
