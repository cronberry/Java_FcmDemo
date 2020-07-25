package com.cronberry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CronberryPref myPref;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("cronberry", "intent");
        Log.d("cronberry", "intent.extras.dfasd");

        if (null != getIntent().getExtras()) {
            if (getIntent().getExtras().containsKey("actionURL")) {
                String url = getIntent().getExtras().getString("actionURL");
                Log.d("cronberry", "intent.extras.toString()");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(Intent.createChooser(intent, "Browse with"));
                finish();
                return;
            }
        }
        myPref = new CronberryPref(this);
        if (myPref.getUserEmail() != "") {
            openSecondActivity();
        }
        button = findViewById(R.id.button);
        final EditText emailId = findViewById(R.id.editText2);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (emailId.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    if (!emailId.getText().toString().trim().contains("@")) {
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT)
                                .show();
                        return;

                    }
                }

                progressBar.setVisibility(View.VISIBLE);
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {


                                if (!task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "No token found", Toast.LENGTH_SHORT)
                                            .show();
                                    Log.d("cronberry", "no token found");
                                    return;
                                }

                                String refreshedToken = task.getResult().getToken();
                                Log.d("cronberry", "NEw Token: ");
                                Log.d("cronberry", "Token: $refreshedToken");
                                WebAPI retrofitObj = Utility.getRetrofitObj(MainActivity.this);
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put("projectKey", "VW50aXRsZSBQcm9qZWN0MTU5MDc1OTQ2NDgzNA==");
                                hashMap.put("audienceId", Settings.Secure.getString(MainActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                                hashMap.put("android_fcm_token", refreshedToken);
                                List<Map<String, Object>> paramList = new ArrayList<>();
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put("paramKey", "demo_email");
                                dataMap.put("paramValue", emailId.getText().toString().trim());
                                paramList.add(dataMap);
                                hashMap.put("paramList", paramList);
                                retrofitObj.registerAudience((HashMap<String, Object>) hashMap)
                                        .enqueue(new Callback<LinkedHashMap<String, Object>>() {
                                            @Override
                                            public void onResponse(Call<LinkedHashMap<String, Object>> call, Response<LinkedHashMap<String, Object>> response) {
                                                progressBar.setVisibility(View.GONE);
                                                try {
                                                    LinkedHashMap<String, Object> body = response.body();
                                                    if (body.get("status").toString().equals("false")) {
                                                        Toast.makeText(MainActivity.this, body.get("error_msgs").toString(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        myPref.setUserEmail(emailId.getText().toString().trim());
                                                        openSecondActivity();
                                                    }
                                                } catch (Exception ex) {
                                                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure
                                                    (Call<LinkedHashMap<String, Object>> call, Throwable t) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                Log.d("cronberry", " token found");
                                Log.d("cronberry", refreshedToken);
                            }
                        });
            }
        });
    }

    private void openSecondActivity() {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}