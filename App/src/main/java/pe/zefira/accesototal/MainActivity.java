package pe.zefira.accesototal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    SessionManager session;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    EditText txtUsername, txtPassword;
    Button button;
    HashMap<String, String> parsed_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtUsername = (EditText)findViewById(R.id.email);
        txtPassword = (EditText)findViewById(R.id.password);
        session = new SessionManager(getApplicationContext());

        parsed_user = new HashMap<String, String>();


        button = (Button)findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                new AuthenticateTask().execute(username, password);
            }

        });

    }

    private class AuthenticateTask extends AsyncTask<String, String, String> {
        String url_all_products = "http://api-zefira.herokuapp.com/people/auth";

        /**
             * Before starting background thread Show Progress Dialog
             * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", args[0]));
            params.add(new BasicNameValuePair("password", args[1]));

            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST" , params);

            Log.d("User", json.toString());

            try {
                int success = json.getInt("success");
                if (success == 1){

                    JSONObject user = json.getJSONObject("user");
                    parsed_user.put("name", user.getString("name"));
                    Log.d("parsed", parsed_user.toString());
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    //FIX
                }

            } catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String result) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            SharedPreferences settings = getSharedPreferences("User", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("name", parsed_user.get("name"));
            editor.commit();


        }

    }




}
