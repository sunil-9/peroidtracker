package com.ifemini.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyHelper {
    Realm realm;
    RealmResults<Person> person;

    public MyHelper(Realm realm) {
        this.realm = realm;
    }
    public void selectFromDb(){
        person =realm.where(Person.class).findAll();

    }
    public ArrayList<Person> jsutRefress(){
        ArrayList<Person> listItems = new ArrayList<>();
        for (Person p : person){
            listItems.add(p);
        }
        return listItems;
    }
}
