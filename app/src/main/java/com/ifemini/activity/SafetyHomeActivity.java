package com.ifemini.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ifemini.Adapter;
import com.ifemini.Constants;
import com.ifemini.R;
import com.ifemini.model.MyHelper;
import com.ifemini.model.Person;

import io.realm.Realm;
import io.realm.RealmChangeListener;


public class SafetyHomeActivity extends AppCompatActivity {
    private DatabaseReference mdatabase;
    private FirebaseAuth mauth;
    private RecyclerView recyclerView;
    private MyHelper myHelper;
    private EditText note_upd, title_upd;
    private Button btn_deleteupd, btn_upd;
    private String title;
    private String note;
    private String post_key;
    private Toolbar toolbar;
    private Boolean mode = false;
    RelativeLayout background_layout;
    Realm realm;
    RealmChangeListener realmChangeListener;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_home);

        realm = Realm.getDefaultInstance();
        myHelper = new MyHelper(realm);
        myHelper.selectFromDb();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = findViewById(R.id.recycleView);

        Adapter adapter = new Adapter(SafetyHomeActivity.this, myHelper.jsutRefress());
        recyclerView.setLayoutManager(new LinearLayoutManager(SafetyHomeActivity.this));
        recyclerView.setAdapter(adapter);


        FloatingActionButton create = findViewById(R.id.float_create);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(SafetyHomeActivity.this);
                LayoutInflater inflater = LayoutInflater.from(SafetyHomeActivity.this);
                View myView = inflater.inflate(R.layout.contact_custom_input, null);
                myDialog.setView(myView);
                final AlertDialog dialog = myDialog.create();
                final EditText title = myView.findViewById(R.id.title);
                final EditText note = myView.findViewById(R.id.note);
                Button btn_save = myView.findViewById(R.id.btn_save);
                dialog.show();
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mtitle = title.getText().toString().trim();
                        String mnote = note.getText().toString().trim();
                        if (TextUtils.isEmpty(mtitle)) {
                            title.setError("Name is empty");
                            return;
                        }
                        if (TextUtils.isEmpty(mnote)) {
                            note.setError("Phone is empty");
                            return;
                        }
                        if (mnote.length()<10) {
                            note.setError("phone is less than 10 digit");
                            return;
                        }
                        Toast.makeText(SafetyHomeActivity.this, "save button clicked with:" + mtitle + "and number:" + mnote, Toast.LENGTH_SHORT).show();
//                        TODO: to save data
                        saveData(mtitle, mnote);
                        dialog.dismiss();
                    }
                });
            }
        });
        refress();


    }

    public void refress() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                Adapter adapter = new Adapter(SafetyHomeActivity.this, myHelper.jsutRefress());
                recyclerView.setAdapter(adapter);

            }
        };
        realm.addChangeListener(realmChangeListener);

    }


    private void saveData(final String name, final String phone) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgrealm) {
                Person user = bgrealm.createObject(Person.class);
                user.setName(name);
                user.setPhone(phone);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                Toast.makeText(SafetyHomeActivity.this, "recorded success", Toast.LENGTH_SHORT).show();

            }
        }, new Realm.Transaction.OnError() {


            @Override
            public void onError(Throwable error) {
                Toast.makeText(SafetyHomeActivity.this, "recorded not success " + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}
