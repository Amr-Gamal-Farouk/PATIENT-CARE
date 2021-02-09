package com.example.amrgamal.weartracker.models;


/**
 * Created by amrga on 02/02/2018.
 */

public class User_Model {

    String Name,Adress,Time;

    public User_Model() {

    }

    public User_Model(String name, String adress, String time) {
        Name = name;
        Adress = adress;
        Time = time;
    }




    public String getName() {
        return Name;
    }

    public String getAdress() {
        return Adress;
    }

    public String getTime() {
        return Time;
    }

}
