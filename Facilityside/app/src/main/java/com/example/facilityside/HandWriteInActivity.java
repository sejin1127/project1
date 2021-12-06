package com.example.facilityside;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandWriteInActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static String TAG = "phptest";
    private static final String TAG_JSON="webnautes";

    private EditText edat, enumb;
    private Button insentrance;

    String facilityid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handwritein);

        Intent secondIntent = getIntent();
        facilityid = secondIntent.getStringExtra("facilityid");

        insentrance = findViewById(R.id.insertentrance);
        edat = (EditText)findViewById(R.id.edate);
        enumb = (EditText)findViewById(R.id.enumber);

        insentrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emptext3 = edat.getText().toString();
                String emptext4 = enumb.getText().toString();
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insertEntrance3.php", emptext4, facilityid, emptext3);
                Toast.makeText(getApplicationContext(), "출입명부 등록 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(HandWriteInActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //  mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String num = (String)params[1];
            String facid = (String)params[2];
            String da = (String)params[3];


            String serverURL = (String)params[0];
            String postParameters = "number=" + num + "&facilityid=" + facid + "&datetime=" + da;


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

                Log.d(TAG, "HandWriteInActivity: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}