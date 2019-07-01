package com.example.akin.bilekpartner;

public class weekData {

    int monday;
    int tuesday;
    int wednesday;
    int thursday;
    int friday;
    int saturday;
    int sunday;

    @Override
    public String toString() {
        return "weekData{" +
                "monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public weekData() {
        this.monday = 0;
        this.tuesday = 0;
        this.wednesday = 0;
        this.thursday = 0;
        this.friday = 0;
        this.saturday = 0;
        this.sunday = 0;
    }
}
