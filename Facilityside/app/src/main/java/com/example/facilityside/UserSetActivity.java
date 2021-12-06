package com.example.facilityside;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UserSetActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static String TAG = "phptest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_repid = "repid";
    String mJsonString;
    String saverepid;

    private Button signupbtn;
    private TextView repid, reppw;

    String facid;
    String businessname, businessnumber, representname, facilitynumber, sector, adress, detailedadress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userset);

        facid = getUUID();

        Intent secondIntent = getIntent();
        businessname = secondIntent.getStringExtra("businessname");
        businessnumber = secondIntent.getStringExtra("businessnumber");
        representname = secondIntent.getStringExtra("representname");
        facilitynumber = secondIntent.getStringExtra("facilitynumber");
        sector = secondIntent.getStringExtra("sector");
        adress = secondIntent.getStringExtra("adress");
        detailedadress = secondIntent.getStringExtra("detailedadress");

        repid = findViewById(R.id.repid);
        reppw = findViewById(R.id.reppw);

        signupbtn = findViewById(R.id.signup);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptext2 = repid.getText().toString();
                GetData task = new GetData();
                task.execute(emptext2);
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(UserSetActivity.this,
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

            String fid = (String)params[1];
            String busname = (String)params[2];
            String busnum = (String)params[3];
            String repname = (String)params[4];
            String facnum = (String)params[5];
            String sec = (String)params[6];
            String adr = (String)params[7];
            String dtadr = (String)params[8];
            String rid = (String)params[9];
            String rpw = (String)params[10];

            String serverURL = (String)params[0];
            String postParameters = "facilityid=" + fid + "&businessname=" + busname + "&businessnumber=" + busnum + "&representname=" + repname +
                    "&facilitynumber=" + facnum + "&sector=" + sec + "&adress=" + adr + "&detailedadress=" + dtadr
                    + "&repid=" + rid + "&reppw=" + rpw;


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

    public String getUUID() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            progressDialog = ProgressDialog.show(UserSetActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result.equals("novoerlap")){
                saverepid = "nooverlap";
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insertSignIn2.php", facid, businessname, businessnumber, representname, facilitynumber,
                        sector, adress, detailedadress, repid.getText().toString(), reppw.getText().toString());

                Intent intent;
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
            else {
                mJsonString = result;
                Log.d(TAG, "mJsonString :  " + mJsonString);
                showResult();
                Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://3.35.25.168/findOverlap3.php";
            String postParameters = "repid=" + searchKeyword1;
            Log.d(TAG, "postParameters :  " + postParameters);


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            saverepid = item.getString(TAG_repid);
            Log.d(TAG, "saverepid : " + repid);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}