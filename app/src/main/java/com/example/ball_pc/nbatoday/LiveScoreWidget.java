package com.example.ball_pc.nbatoday;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class LiveScoreWidget extends AppWidgetProvider {
    public static ArrayList<Game> GameList;
    public int currentGameNum = 0;
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.live_score_widget);


        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, "http://140.112.107.171:4000/today", null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray gameNums = response.getJSONArray("games");
                                Log.d("ARRAY", gameNums.toString());
                                Gson gson = new Gson();
                                String gameStatus = new String();
                                Game firstGame = gson.fromJson(gameNums.get(0).toString(), Game.class);

                                GameList = new ArrayList<>();
                                for (int i = 0; i < gameNums.length(); ++ i) {
                                    Game tempGame = gson.fromJson(gameNums.get(0).toString(), Game.class);
                                    GameList.add(tempGame);
                                }

                                int homeLogoID = context.getResources().getIdentifier(firstGame.home.toLowerCase(), "drawable", context.getPackageName());
                                int awayLogoID = context.getResources().getIdentifier(firstGame.away.toLowerCase(), "drawable", context.getPackageName());

                                views.setTextViewText(R.id.homeText, firstGame.home);
                                views.setTextViewText(R.id.awayText, firstGame.away);
                                views.setTextViewText(R.id.scoreText, firstGame.score);

                                views.setImageViewResource(R.id.awayLogo, awayLogoID);
                                views.setImageViewResource(R.id.homeLogo, homeLogoID);

                                if(firstGame.statusNum == 1) {
                                    gameStatus = context.getResources().getString(R.string.status_1);
                                } else if(firstGame.statusNum == 2) {
                                    gameStatus = context.getResources().getString(R.string.status_2);
                                } else if(firstGame.statusNum == 3) {
                                    gameStatus = context.getResources().getString(R.string.status_3);
                                }
                                views.setTextViewText(R.id.game_status, gameStatus);

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

