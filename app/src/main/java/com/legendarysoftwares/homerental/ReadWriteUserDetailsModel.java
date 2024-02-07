package com.legendarysoftwares.homerental;

public class ReadWriteUserDetailsModel {
    String dob,gender,mobile;

    public ReadWriteUserDetailsModel() {}

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ReadWriteUserDetailsModel(String dob, String gender, String mobile) {
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
    }
}
