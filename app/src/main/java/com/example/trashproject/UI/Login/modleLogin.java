package com.example.trashproject.UI.Login;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import com.example.trashproject.repository.Repository;

public class modleLogin {

    Context context;
    Repository rp;

    public modleLogin(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
    }

    public int isExist(EditText etUser, EditText etPass)
    {
        if(etUser.getText().toString().contains("@"))
        {
            if (!rp.getMyDatabaseHelper().LoginUser(etUser.getText().toString(), etPass.getText().toString(), 2)) {
                return 2;
            } else
                return 0;

        }
        else {
            if (!rp.getMyDatabaseHelper().LoginUser(etUser.getText().toString(), etPass.getText().toString(), 1)) {
                return 1;
            } else
                return 0;
        }
    }


}
