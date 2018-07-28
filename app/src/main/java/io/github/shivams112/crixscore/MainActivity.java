package io.github.shivams112.crixscore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button getScore;
    private ListView matchDetails;
    final String api = "sAkHLF373yPDSYmaqSFO99xj2af1 ";
    String URL = "http://cricapi.com/api/matches/";
    String URLSCORE = "http://cricapi.com/api/cricketScore/sAkHLF373yPDSYmaqSFO99xj2af1?";
    private ArrayList<String> arrayList;
    private boolean tap = true;
    int idUnique[];
    String scores = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getScore = findViewById(R.id.button);
        matchDetails = findViewById(R.id.list_match);
        arrayList = new ArrayList<>();
        idUnique = new int[50];

        getScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tap) {
                    tap = false;
                    getScore.setVisibility(View.GONE);
                    letsDoNetworking();
                } else
                    Toast.makeText(MainActivity.this, "List is Updated", Toast.LENGTH_SHORT).show();

            }
        });

        matchDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("unique_id", idUnique[position]);
                Log.d("Cric", "Position :" + URLSCORE + params);
                client.get(URLSCORE, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                        try {
                            scores = responseBody.getString("score");
                            ((TextView) view).setText(scores);
                            Log.d("Cric", "Scores:" + scores);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.i("Crick", throwable.toString());
                    }
                });


            }
        });
    }

    private void letsDoNetworking() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL + api, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                // Log.d("Cric :",responseBody.toString());
                try {
                    int x = 0;
                    for (int i = 0; i < responseBody.getJSONArray("matches").length(); i++) {

                        try {
                            if (responseBody.getJSONArray("matches").getJSONObject(i).getBoolean("matchStarted")) {
                                // Log.d("Cric","Live Matches:"+responseBody.getJSONArray("matches").getJSONObject(i).getString("team-1")+" VS "+responseBody.getJSONArray("matches").getJSONObject(i).getString("team-2"));
                                idUnique[x] = responseBody.getJSONArray("matches").getJSONObject(i).getInt("unique_id");
                                String matches = responseBody.getJSONArray("matches").getJSONObject(i).getString("team-1").toString() + " VS " + responseBody.getJSONArray("matches").getJSONObject(i).getString("team-2").toString();
                                // matchDetails.setText(matches + "\n");
                                arrayList.add(matches);
                                x++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                matchDetails.setAdapter(arrayAdapter);
                for (int j = 0; j < arrayList.size(); j++) {
                    Log.d("Cric", arrayList.get(j) + "ID ;" + idUnique[j]);
                }
                //arrayList.clear();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Cric :", "Failed" + throwable.toString());
            }
        });

    }

    private String getMeScores(int index) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("unique_id", idUnique[index]);
        Log.d("Cric", "Position :" + URLSCORE + params);
        client.get(URLSCORE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                try {
                    scores = responseBody.getString("score");
                    Log.d("Cric", "Scores:" + scores);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("Crick", throwable.toString());
            }
        });
        return scores;
    }
}
