package com.peaceteam.jeremydorne.shoppingwithfriends;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class for the Profile of a user
 * @author Robert Guthrie
 * @version 1.0
 */
public class ProfileActivity extends Activity {

    private String currentUserEmail;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        currentUserEmail = getIntent().getStringExtra("currentUserEmail");
        email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        Double rating = getIntent().getDoubleExtra("rating", 0.0);
        Integer numSalesReported = getIntent().getIntExtra("numSalesReported", 0);
        TextView emailTextField = (TextView) findViewById(R.id.email_text_field);
        emailTextField.setText(email);
        TextView nameTextField = (TextView) findViewById(R.id.name_text_field);
        nameTextField.setText(name);
        TextView ratingTextField = (TextView) findViewById(R.id.rating_text_field);
        ratingTextField.setText(rating.toString());
        TextView numSalesTextField = (TextView) findViewById(R.id.num_sales_reported_text_field);
        numSalesTextField.setText(numSalesReported.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    /**
     * Removes a friend upon clicking the remove friend button
     * @param v view
     */
    public void removeFriend(View v) {
        RemoveFriendTask task = new RemoveFriendTask(currentUserEmail, email);
        task.execute((Void) null);
    }

    /**
     * Called after removing a friend to return to the previous screen.
     */
    void finishRemoveFriend() {
        finish();
    }

    /**
     * Performs an HTTP Request to remove a friend
     * @author Robert Guthrie
     * @version 1.0
     */
    public class RemoveFriendTask extends AsyncTask<Void, Void, Boolean> {
        private final String currentUserEmail;
        private final String friendEmail;

        /**
         * Creates a RemoveFriendTask
         * @param user email of the user who is removing a friend
         * @param friend email of the friend who is being removed
         */
        RemoveFriendTask(String user, String friend) {
            currentUserEmail = user;
            friendEmail = friend;
        }

        /**
         * Performs an HTTP request to remove a friend
         * @param params void
         * @return whether the removal was successful
         */
        @Override
        public Boolean doInBackground(Void... params) {
            String jsonReq = "{ \"email\": \"" + currentUserEmail + "\", \"friendEmail\": \""
                    + friendEmail + "\"}";
            try {
                URL url = new URL("http://10.0.2.2:3000/removefriend");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                conn.setRequestProperty("Content-Type", "application/json");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(jsonReq);
                wr.flush();
                wr.close();

                //TODO Read a response and make sure it was written
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject responseObject = new JSONObject(response.toString());
                return responseObject.getBoolean("didRemoveFriend");

            } catch (Exception e) {
                Log.d("info", "Error occurred");
            }
            return false;
        }

        /**
         * If the HTTP request was succcessful, remove the friend
         * @param success if the request was successful
         */
        @Override
        public void onPostExecute(final Boolean success) {
            if (success) {
                finishRemoveFriend();
            }
        }
    }
}
