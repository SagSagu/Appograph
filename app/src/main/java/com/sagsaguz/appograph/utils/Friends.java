package com.sagsaguz.appograph.utils;

public class Friends {

    private String name, dob, phone, alternate_phone, email, whatsapp, hobbies, others, image, coverImage;

    public Friends() {
    }

    public Friends(String name, String dob, String phone, String alternate_phone, String email, String whatsapp, String hobbies, String others, String image, String coverImage) {
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.alternate_phone = alternate_phone;
        this.email = email;
        this.whatsapp = whatsapp;
        this.hobbies = hobbies;
        this.others = others;
        this.image = image;
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlternate_phone() {
        return alternate_phone;
    }

    public void setAlternate_phone(String alternate_phone) {
        this.alternate_phone = alternate_phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
