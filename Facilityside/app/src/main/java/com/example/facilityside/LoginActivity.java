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

public class LoginActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_facilityid = "facilityid";
    private static final String TAG = "FacilitySetActivity";
    String mJsonString;
    String savefacilityid;

    private Button signinbtn, loginconf;
    private TextView insertid, insertpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        insertid = findViewById(R.id.insertid);
        insertpw = findViewById(R.id.insertpw);

        loginconf = findViewById(R.id.loginconfirm);
        loginconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (insertid.getText().toString().equals("") || insertid.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    String rid = insertid.getText().toString();
                    String rpw = insertpw.getText().toString();
                    GetData task = new GetData();
                    task.execute(rid, rpw);
                }

            }
        });

        signinbtn = findViewById(R.id.signin);
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), FacilitySetActivity.class);
                startActivity(intent);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result.equals("novoerlap")){
                savefacilityid = "nooverlap";
                Toast.makeText(getApplicationContext(), "등록된 계정이 아닙니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                Log.d(TAG, "mJsonString :  " + mJsonString);
                showResult();
                Intent intent;
                intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.putExtra("facilityid", savefacilityid);
                startActivity(intent);
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://3.35.25.168/getLogin.php";
            String postParameters = "repid=" + searchKeyword1 + "&reppw=" + searchKeyword2;
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

            savefacilityid = item.getString(TAG_facilityid);
            Log.d(TAG, "savefacilityid : " + savefacilityid);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}