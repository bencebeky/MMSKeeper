/*
    MMSKeeper Android app: switches data on/off while retaining MMS traffic.
    Copyright (C) 2014 Bence Béky

    This file is part of MMSKeeper.

    MMSKeeper is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MMSKeeper is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MMSKeeper.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.harvard.android.mmskeeper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MMSKeeperWidget extends AppWidgetProvider {
	static public final String CLICK = "edu.harvard.android.mmskeeper.CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            // Create intent and bind it to click event.
            Intent intent = new Intent(CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        MMSKeeper.updateWidgetIcons(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onEnabled(Context context) {
	}
	
	@Override
	public void onDisabled(Context context) {
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if(action.equals(CLICK))
			MMSKeeper.toggleData(context);
		else
			super.onReceive(context, intent);
	}
}
