package com.legendarysoftwares.homerental;

public class ReadWriteUserDetailsModel {
    String name,photo,email,dob,gender,mobile,about;

    public ReadWriteUserDetailsModel() {}



    public ReadWriteUserDetailsModel(String name, String photo, String email, String dob, String gender, String mobile, String about) {
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
        this.name=name;
        this.photo=photo;
        this.email=email;
        this.about=about;
    }


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
