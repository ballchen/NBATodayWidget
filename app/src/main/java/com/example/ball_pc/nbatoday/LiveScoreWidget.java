package com.example.ball_pc.nbatoday;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class LiveScoreWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.live_score_widget);



        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, "http://140.112.107.171:3000/today", null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RES", response.toString());
                            try {
                                JSONArray gameNums = response.getJSONArray("games");
                                Log.d("ARRAY", gameNums.toString());
                                Gson gson = new Gson();
                                Game firstGame = gson.fromJson(gameNums.get(0).toString(), Game.class);
                                views.setTextViewText(R.id.homeText, firstGame.home);
                                views.setTextViewText(R.id.awayText, firstGame.away);
                                views.setTextViewText(R.id.scoreText, firstGame.score);
                                appWidgetManager.updateAppWidget(appWidgetId, views);


                            } catch (Exception e) {

                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch(Exception e) {

        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

