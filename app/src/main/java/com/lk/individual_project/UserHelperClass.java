package com.lk.individual_project;

public class UserHelperClass {
    String Fname,Lname,Gender,Email,Password,Confirmpwd;;

    public UserHelperClass() {
    }


    public UserHelperClass(String fname, String lname, String gender, String email) {
        Fname = fname;
        Lname = lname;
        Gender = gender;
        Email = email;
//        Password = password;
//        Confirmpwd = confirmpwd;



    }
    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmpwd() {
        return Confirmpwd;
    }

    public void setConfirmpwd(String confirmpwd) {
        Confirmpwd = confirmpwd;
    }




}
