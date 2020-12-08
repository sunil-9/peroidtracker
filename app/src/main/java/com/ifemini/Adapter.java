package com.ifemini;

import android.content.Context;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ifemini.activity.SafetyHomeActivity;
import com.ifemini.model.Person;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class Adapter extends RecyclerView.Adapter<MyviewHolder> {
    Context c;
    ArrayList<Person> person;
    View view;
    private EditText note_upd, title_upd;
    private Button btn_deleteupd, btn_upd;
    Realm realm;
    String name,phone,phone_new;
    int item;

    public Adapter(View view) {
        this.view = view;
    }

    public Adapter(Context c, ArrayList<Person> person) {
        this.c = c;
        this.person = person;
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(c).inflate(R.layout.item_data, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, final int position) {
            Person p = person.get(position);
            holder.name.setText(p.getName());
            holder.phone.setText(p.getPhone());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(c, position +" is clicked", Toast.LENGTH_SHORT).show();
                    update_data(position);
                }
            });
    }
    private void update_data(int postion) {
        Person p = person.get(postion);
        phone =p.getPhone();
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(c);
        LayoutInflater inflater = LayoutInflater.from(c);
        View myView = inflater.inflate(R.layout.contact_update_form, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();
        title_upd = myView.findViewById(R.id.title_update);
        note_upd = myView.findViewById(R.id.note_update);
        title_upd.setText(p.getName());
        note_upd.setText(p.getPhone());
        btn_upd = myView.findViewById(R.id.btn_update);
        btn_deleteupd = myView.findViewById(R.id.btn_delete_from_update);
        dialog.show();
        btn_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name = title_upd.getText().toString().trim();
                 phone_new = note_upd.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    note_upd.setError("Name is empty");
                    return;
                }
                if (TextUtils.isEmpty(phone_new)) {
                    note_upd.setError("Phone num is empty");
                    return;
                }
                if (phone_new.length()<10) {
                    note_upd.setError("phone is less than 10 digit");
                    return;
                }
//              TODO: update contacts here
                Realm myRealm = Realm.getDefaultInstance();
                myRealm.beginTransaction();
                Person person = myRealm.createObject(Person.class);
                person.setName(name);
                person.setPhone(phone_new);
                myRealm.insertOrUpdate(person);
                RealmResults<Person> totaldates = myRealm.where(Person.class).equalTo("phone", phone).findAll();
                totaldates.deleteAllFromRealm();
                myRealm.commitTransaction();
                Toast.makeText(c, "Data Updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btn_deleteupd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, phone +" is selected", Toast.LENGTH_SHORT).show();
                Realm myRealm = Realm.getDefaultInstance();
                myRealm.beginTransaction();
                RealmResults<Person> totaldates = myRealm.where(Person.class).equalTo("phone", phone).findAll();
                totaldates.deleteAllFromRealm();
                myRealm.commitTransaction();
                dialog.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        Log.e( "getItemCount: ","total record is : " + person.size());
        return person.size();
    }
}
