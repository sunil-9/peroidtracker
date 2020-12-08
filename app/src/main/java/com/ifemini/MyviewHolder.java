package com.ifemini;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyviewHolder extends RecyclerView.ViewHolder  {
    View myView;
    TextView  name,phone;

    public MyviewHolder(@NonNull View itemView) {
        super(itemView);
        myView = itemView;
        name= myView.findViewById(R.id.txt_name);
         phone = myView.findViewById(R.id.txt_phone);

    }

}
