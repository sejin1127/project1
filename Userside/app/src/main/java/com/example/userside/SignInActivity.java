package com.example.userside;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static String TAG = "phptest";

    Context context;
    Resources res;
    View dialog;
    String[] si, gu;

    private EditText nametext1;
    private EditText numtext1;
    private EditText residencetext1;
    private Button signsave1;
    private Button selectres1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        context = this;
        res = getResources();
        si = res.getStringArray(R.array.si);

        nametext1 = (EditText)findViewById(R.id.signName1);
        numtext1 = (EditText)findViewById(R.id.signNumber1);
        residencetext1 = (EditText)findViewById(R.id.signResidence1);
        signsave1 = findViewById(R.id.signSave1);
        signsave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(SignInActivity.this);

                String name = nametext1.getText().toString();
                String num = numtext1.getText().toString();
                String residence = residencetext1.getText().toString();
                String email = gsa.getEmail();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insertSignIn.php", name, num, residence, email);

                Intent intent = new Intent(getApplicationContext(), QRcaptureActivity.class);
                startActivity(intent);
            }
        });


        selectres1 = findViewById(R.id.selectResidence1);
        selectres1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = View.inflate(SignInActivity.this, R.layout.residencedialog1, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(SignInActivity.this);

                dlg.setView(dialog);
                dlg.show();
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignInActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String)params[1];
            String num = (String)params[2];
            String residence = (String)params[3];
            String email = (String)params[4];

            String serverURL = (String)params[0];
            String postParameters = "name=" + name + "&number=" + num + "&residence=" + residence + "&email=" + email;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "insertSignIn: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}