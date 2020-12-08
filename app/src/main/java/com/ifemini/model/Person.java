package com.ifemini.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Person extends RealmObject
{

    String name;

    String phone;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
