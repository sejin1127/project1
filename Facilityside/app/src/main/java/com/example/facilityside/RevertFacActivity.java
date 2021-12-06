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

public class RevertFacActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_businessnumber = "businessnumber";
    private static final String TAG = "RevertFacActivity";
    String mJsonString;
    String savebusinessnumber;

    private Button facilitysetbtn, businessnumbercheck;
    private TextView businessname, businessnumber, representname, facilitynumber, sector, adress, detailedadress;

    String facilityid;

    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revertfac);

        Intent secondIntent = getIntent();
        facilityid = secondIntent.getStringExtra("facilityid");

        businessname = (EditText)findViewById(R.id.businessname2);
        businessnumber = (EditText)findViewById(R.id.businessnumber2);
        representname = (EditText)findViewById(R.id.representname2);
        facilitynumber = (EditText)findViewById(R.id.facilitynumber2);
        sector = (EditText)findViewById(R.id.sector2);
        adress = (EditText)findViewById(R.id.adress2);
        detailedadress = (EditText)findViewById(R.id.detailedadress2);

        businessnumbercheck = findViewById(R.id.businessnumbercheck2);
        businessnumbercheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emptext1 = businessnumber.getText().toString();
                GetData task = new GetData();
                task.execute(emptext1);
            }
        });

        facilitysetbtn = findViewById(R.id.facilityset2);
        facilitysetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check == 1) {
                    String busname = businessname.getText().toString();
                    String busnum = businessnumber.getText().toString();
                    String repname = representname.getText().toString();
                    String facnum = facilitynumber.getText().toString();
                    String sec = sector.getText().toString();
                    String adr = adress.getText().toString();
                    String dtladr = detailedadress.getText().toString();

                    ModifyData task2 = new ModifyData();
                    task2.execute("http://" + IP_ADDRESS + "/modifySign2.php", facilityid, busname, busnum, repname, facnum, sec, adr, dtladr);

                    Toast.makeText(getApplicationContext(), "시설 정보가 수정되었습니다", Toast.LENGTH_SHORT).show();
                }

                else
                    Toast.makeText(getApplicationContext(), "중복 확인을 완료해주세요", Toast.LENGTH_SHORT).show();
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
            progressDialog = ProgressDialog.show(RevertFacActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result.equals("novoerlap")){
                savebusinessnumber = "nooverlap";
                check = 1;
                Toast.makeText(getApplicationContext(), "중복 확인 완료", Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                Log.d(TAG, "mJsonString :  " + mJsonString);
                showResult();
                Toast.makeText(getApplicationContext(), "이미 가입된 사업자등록번호 입니다.", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://3.35.25.168/findOverlap2.php";
            String postParameters = "businessnumber=" + searchKeyword1;
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

            savebusinessnumber = item.getString(TAG_businessnumber);
            Log.d(TAG, "savebusinessnumber : " + businessnumber);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }

    class ModifyData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RevertFacActivity.this,
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

            String serverURL = (String)params[0];
            String postParameters = "facilityid=" + fid + "&businessname=" + busname + "&businessnumber=" + busnum + "&representname=" + repname +
                    "&facilitynumber=" + facnum + "&sector=" + sec + "&adress=" + adr + "&detailedadress=" + dtadr;


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

                Log.d(TAG, "modifySign: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}