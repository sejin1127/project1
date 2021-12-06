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

public class FacilitySetActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "3.35.25.168";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_businessnumber = "businessnumber";
    private static final String TAG = "FacilitySetActivity";
    String mJsonString;
    String savebusinessnumber;
    int check = 0;

    private Button facilitysetbtn, businessnumbercheck;
    private TextView businessname, businessnumber, representname, facilitynumber, sector, adress, detailedadress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facilityset);

        businessname = (EditText)findViewById(R.id.businessname);
        businessnumber = (EditText)findViewById(R.id.businessnumber);
        representname = (EditText)findViewById(R.id.representname);
        facilitynumber = (EditText)findViewById(R.id.facilitynumber);
        sector = (EditText)findViewById(R.id.sector);
        adress = (EditText)findViewById(R.id.adress);
        detailedadress = (EditText)findViewById(R.id.detailedadress);

        businessnumbercheck = findViewById(R.id.businessnumbercheck);
        businessnumbercheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emptext1 = businessnumber.getText().toString();
                GetData task = new GetData();
                task.execute(emptext1);
            }
        });

        facilitysetbtn = findViewById(R.id.facilityset);
        facilitysetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check == 1) {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), UserSetActivity.class);
                    intent.putExtra("businessname", businessname.getText().toString());
                    intent.putExtra("businessnumber", businessnumber.getText().toString());
                    intent.putExtra("representname", representname.getText().toString());
                    intent.putExtra("facilitynumber", facilitynumber.getText().toString());
                    intent.putExtra("sector", sector.getText().toString());
                    intent.putExtra("adress", adress.getText().toString());
                    intent.putExtra("detailedadress", detailedadress.getText().toString());
                    startActivity(intent);
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
            progressDialog = ProgressDialog.show(FacilitySetActivity.this,
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
}