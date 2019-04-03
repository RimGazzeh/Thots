package io.geekgirl.thots.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import io.geekgirl.thots.R;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.DebugLog;

/**
 * Created by Rim Gazzah on 23/10/18
 */
public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(getApplicationContext(), intent);
    }

    class WidgetRemoteViewFactory implements RemoteViewsFactory {
        Context mContext;
        Intent mIntent;
        ArrayList<String> mMessages = new ArrayList<>();
        int mAppWidgetId;

        public WidgetRemoteViewFactory(Context context, Intent intent) {
            mContext = context;
            mIntent = intent;
            getData();
        }

        private void getData() {
            mAppWidgetId = mIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            mMessages = mIntent.getStringArrayListExtra(Constants.MSG_LIST_WIDGET_EXTRA);
            if (mMessages != null) {
                DebugLog.d(mMessages.size() + " " + mMessages.toString());
            } else {
                DebugLog.d("messages list null");
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            getData();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return mMessages == null ? 0 : mMessages.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_msg);
            remoteView.setTextViewText(R.id.ingredient_widget_title, mMessages.get(position));
            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
