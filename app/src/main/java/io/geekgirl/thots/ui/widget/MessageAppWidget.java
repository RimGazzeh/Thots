package io.geekgirl.thots.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import io.geekgirl.thots.R;
import io.geekgirl.thots.models.Message;
import io.geekgirl.thots.utils.AppExecutors;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.DebugLog;
import io.geekgirl.thots.utils.Prefs;

/**
 * Implementation of App Widget functionality.
 */
public class MessageAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.message_app_widget);
        Intent widgetIntent = new Intent(context, WidgetRemoteViewsService.class);
        String uid = Prefs.getPref(Prefs.USER_UID, context);
        if (!TextUtils.isEmpty(uid)) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                FirebaseDatabase.getInstance().getReference().child(Constants.USER_PATH).child(uid)
                        .child(Constants.MESSAGE_PATH).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            ArrayList<String> messages = new ArrayList<>();
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                Message message = snap.getValue(Message.class);
                                messages.add(message.getMessage());
                            }
                            DebugLog.d(messages.size() + "");
                            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                            widgetIntent.putStringArrayListExtra(Constants.MSG_LIST_WIDGET_EXTRA, messages);
                            views.setRemoteAdapter(R.id.widget_list_msg, widgetIntent);
                            views.setEmptyView(R.id.widget_list_msg, R.id.widget_emptyView);
                            appWidgetManager.updateAppWidget(appWidgetId, views);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            });
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

