package com.example.hanshinlibrary.Model;

import android.app.Application;

public class Profile extends Application {

    public static String name;
    public static String student_ID;
    public static String password;
    public static String email;
    public static String major;
    public static String gender;

    public static String sit;

    public Profile(String name, String student_ID, String password, String email, String major, String gender) {
        this.name = name;
        this.student_ID = student_ID;
        this.password = password;
        this.email = email;
        this.major = major;
        this.gender = gender;
    }
}
