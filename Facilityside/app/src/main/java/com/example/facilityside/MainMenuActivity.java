package com.example.facilityside;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainMenuActivity extends AppCompatActivity {
    private Button getqrbtn, handwritebtn, redirbtn, logoutbtn;
    private ImageButton endbtn;

    String facilityid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        Intent secondIntent = getIntent();
        facilityid = secondIntent.getStringExtra("facilityid");

        getqrbtn = findViewById(R.id.getqr);
        getqrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), QRcreateActivity.class);
                intent.putExtra("facilityid", facilityid);
                startActivity(intent);
            }
        });

        handwritebtn = findViewById(R.id.handwrite);
        handwritebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), HandWriteInActivity.class);
                intent.putExtra("facilityid", facilityid);
                startActivity(intent);
            }
        });

        logoutbtn = findViewById(R.id.logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        redirbtn = findViewById(R.id.redir);
        redirbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), RevertFacActivity.class);
                intent.putExtra("facilityid", facilityid);
                startActivity(intent);
            }
        });

        endbtn = findViewById(R.id.endnow);
        endbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainMenuActivity.this);
                dlg.setTitle("").setMessage("어플을 종료하시겠습니까?");
                //버튼 클릭시 동작
                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(MainMenuActivity.this);
                        System.exit(0);
                    }
                });
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}