package com.example.ball_pc.nbatoday;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    public static int currentGameNum = 0;
    public static Game displayGame;


    private final static String ACTION_UPDATE_BUTTON_CLICK = "com.example.ball_pc.nbatoday.APPWIDGET_UPDATE_ACTION";
    private final static String ACTION_NEXT_BUTTON_CLICK = "com.example.ball_pc.nbatoday.APPWIDGET_NEXT_GAME";
    private final static String ACTION_LAST_BUTTON_CLICK = "com.example.ball_pc.nbatoday.APPWIDGET_LAST_GAME";

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.live_score_widget);

        views.setOnClickPendingIntent(R.id.homeLogo, getPendingSelfIntent(context, ACTION_UPDATE_BUTTON_CLICK));
        views.setOnClickPendingIntent(R.id.nextButton, getPendingSelfIntent(context, ACTION_NEXT_BUTTON_CLICK));
//        views.setOnClickPendingIntent(R.id.lastButton, getPendingSelfIntent(context, ACTION_LAST_BUTTON_CLICK));
        String scoreboardUrl = "http://nba.ballchen.cc/today";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, scoreboardUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray gameNums = response.getJSONArray("games");
                            Gson gson = new Gson();
                            String gameStatus = new String();

                            GameList = new ArrayList<>();
                            for (int i = 0; i < gameNums.length(); ++ i) {
                                Game tempGame = gson.fromJson(gameNums.get(i).toString(), Game.class);
                                GameList.add(tempGame);
                            }

                            if(currentGameNum >= GameList.size() || currentGameNum < 0) {
                                currentGameNum = 0;
                            }

                            displayGame = GameList.get(currentGameNum);

                            int homeLogoID = context.getResources().getIdentifier(displayGame.home.toLowerCase(), "drawable", context.getPackageName());
                            int awayLogoID = context.getResources().getIdentifier(displayGame.away.toLowerCase(), "drawable", context.getPackageName());

                            views.setTextViewText(R.id.homeText, displayGame.home);
                            views.setTextViewText(R.id.awayText, displayGame.away);
                            views.setTextViewText(R.id.scoreText, displayGame.score);
                            views.setTextViewText(R.id.matchProgress, displayGame.clock);

                            views.setImageViewResource(R.id.awayLogo, awayLogoID);
                            views.setImageViewResource(R.id.homeLogo, homeLogoID);

                            if(displayGame.statusNum == 1) {
                                gameStatus = context.getResources().getString(R.string.status_1);
                            } else if(displayGame.statusNum == 2) {
                                switch (displayGame.quarter) {
                                    case "1":
                                        gameStatus = "第一節";
                                        break;
                                    case "2":
                                        gameStatus = "第二節";
                                        break;
                                    case "3":
                                        gameStatus = "第三節";
                                        break;
                                    case "4":
                                        gameStatus = "第四節";
                                        break;
                                }
                            } else if(displayGame.statusNum == 3) {
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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION_UPDATE_BUTTON_CLICK)) {

        }

        if(intent.getAction().equals(ACTION_NEXT_BUTTON_CLICK)) {
            if(currentGameNum + 1 < GameList.size()) {
                currentGameNum ++;
            }
            else if(currentGameNum + 1 >= GameList.size()) {
                currentGameNum = 0;

            }
        }

        if(intent.getAction().equals(ACTION_LAST_BUTTON_CLICK)) {
            if(0 <= currentGameNum - 1) {
                currentGameNum --;
            }
        }

        callUpdateAppWidget(context);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, LiveScoreWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void callUpdateAppWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context, LiveScoreWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

