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

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Telephony.Carriers;
import android.widget.Toast;
import android.net.Uri;
import android.appwidget.AppWidgetManager;
import android.view.ContextThemeWrapper;
import android.widget.RemoteViews;

/* MMSKeeper:       main activity for settings and toggle,
 * MMSKeeperWidget: widget for toggle,
 * MMSKeeperCommon: backend for APN configuration.
 *
 * Widget icons are updated from MMSKeeperCommon: it needs to be done with every toggle.
 * Main activity is only refreshed when
 *    - user toggles from main activity,
 *    - or main activity is brought to foreground.
 */

public class MMSKeeper extends ContextThemeWrapper {
        public static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
        public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

        // Update icons of every widget instance. Called when widget is updated, and when data is toggled.
        public static void updateWidgetIcons(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
                final int N = appWidgetIds.length;
                Integer defaultId = getDefaultId(context);
                if (defaultId != -1) {
                        // Perform this loop procedure for each App Widget that belongs to this provider
                        for (int i=0; i<N; i++) {
                                int appWidgetId = appWidgetIds[i];
                                // Update icon.
                                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                                if (getDataOn(context, defaultId))
                                        views.setImageViewResource(R.id.widgetImage, R.drawable.ic_data_on);
                                else
                                        views.setImageViewResource(R.id.widgetImage, R.drawable.ic_data_off);
                                // Tell the AppWidgetManager to perform an update on the current app widget.
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                }
        }

        // Toggle types when Toggle button is tapped.
        public static void toggleData(Context context) {
                Integer defaultId = getDefaultId(context);
                if (defaultId != -1) {
                        // Query status.
                        Boolean isDataOn = getDataOn(context, defaultId);
                        // Set inverse.
                        setData(context, defaultId, !isDataOn);
                        /* Update widget icons. Settings Activity icons and text
                         * are updated by Settings class when toggle is initiated from
                         * Settings activity, and later by Settings.onResume if it is
                         * initiated by widgets.
                         */
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        updateWidgetIcons(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(),MMSKeeperWidget.class.getName())));
                }
        }

        // Compare Type to our strings to see if data is on.
        public static Boolean getDataOn(Context context, Integer defaultId) {
                String type = getType(context, defaultId);
                if (type == null)
                        // We don't have permission.
                        return true;
                else {
                        SharedPreferences settings = context.getSharedPreferences("global", MODE_PRIVATE);
                        String dataOnType = settings.getString("dataOnType", context.getString(R.string.dataOnType));
                        return (type.compareTo(dataOnType) == 0);
                }
        }

        // Read preferred table to find _id of default APN.
        public static Integer getDefaultId(Context context) {
                Cursor c;
                String[] projections = new String[] {Carriers._ID};
                Integer defaultId = -1;
                try {
                        // Query database.
                        c = context.getContentResolver().query(PREFERRED_APN_URI, projections, null, null, Carriers.DEFAULT_SORT_ORDER);
                        if (c != null) {
                                if (c.moveToFirst()) {
                                        defaultId = c.getInt(c.getColumnIndex(Carriers._ID));
                                }
                                c.close();
                        }
                } catch (SecurityException e) {
                        // No permission.
                        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                Toast toast = Toast.makeText(context, context.getString(R.string.permissionError), Toast.LENGTH_SHORT);
                                toast.show();
                        }
                }
                return defaultId;
        }

        // Read current type from database.
        public static String getType(Context context, Integer defaultId) {
                if (defaultId == -1)
                        return null;
                Cursor c;
                String[] projections = new String[] {Carriers._ID, Carriers.TYPE};
                String where = "_id = ?";
                String wargs[] = new String[] {Integer.toString(defaultId)};
                String type = null;
                try {
                        // Query database.
                        c = context.getContentResolver().query(APN_TABLE_URI, projections, where, wargs, Carriers.DEFAULT_SORT_ORDER);
                        if (c != null) {
                                if (c.moveToFirst()) {
                                        type = c.getString(c.getColumnIndex(Carriers.TYPE));
                                }
                                c.close();
                        }
                } catch (SecurityException e) {
                        // No permission: do nothing. Toaster is displayed by getDefaultId anyway.
                }
                return type;
        }

        // Turn data on or off.
        public static void setData(Context context, Integer defaultId, Boolean isDataOn) {
                if (defaultId == -1)
                        return;
                // Load data strings from preferences.
                SharedPreferences settings = context.getSharedPreferences("global", MODE_PRIVATE);
                String newType;
                if (isDataOn)
                        newType = settings.getString("dataOnType", context.getString(R.string.dataOnType));
                else
                        newType = settings.getString("dataOffType", context.getString(R.string.dataOffType));
                // Assemble query.
                String where = "_id = ?";
                String wargs[] = new String[] {Integer.toString(defaultId)};
                ContentValues updateValues = new ContentValues();
                updateValues.put(Carriers.TYPE, newType);
                try {
                        // Update database.
                        context.getContentResolver().update(APN_TABLE_URI, updateValues, where, wargs);
                } catch (SecurityException e) {
                        // No permission: do nothing. Toaster is displayed by getDefaultId anyway.
                }
        }
}
