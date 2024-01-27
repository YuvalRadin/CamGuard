package com.example.trashproject.UI.Login;

import android.content.Context;
import android.widget.EditText;

import com.example.trashproject.Data.Repository.Repository;

public class moduleLogin {

    Context context;
    Repository rp;

    public moduleLogin(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
    }

    public int isExist(EditText etUser, EditText etPass)
    {
        if(etUser.getText().toString().contains("@"))
        {
            if (!rp.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 2)) {
                return 2;
            } else
                return 0;

        }
        else {
            if (!rp.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 1)) {
                return 1;
            } else
                return 0;
        }
    }


}
