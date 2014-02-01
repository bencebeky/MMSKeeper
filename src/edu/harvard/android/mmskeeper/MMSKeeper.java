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

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/* MMSKeeper:       main activity for settings and toggle,
 * MMSKeeperWidget: widget for toggle,
 * MMSKeeperCommon: backend for APN configuration.
 *
 * Widget icons are updated from MMSKeeperCommon: it needs to be done with every toggle.
 * Main activity is only refreshed when
 *    - user toggles from main activity,
 *    - or main activity is brought to foreground.
 */

public class MMSKeeper extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
                // Save type when first run.
                saveInitialType();
                // Fill up EditTexts from preferences.
                discardTypes(null);
                // Update initType TextView.
                // Note that currentType TextView and toggleImage ImageButton are updated by onResume(),
                // even when the app first starts up.
                Context context = getApplicationContext();
                updateInit(context);
        }

        @Override
        public void onResume() {
                // Always call the superclass method first.
                super.onResume();
                // Update currentType TextView and toggleImage ImageButton.
                Context context = getApplicationContext();
                Integer defaultId = MMSKeeperCommon.getDefaultId(context);
                updateCurrent(context, defaultId);
                updateImage(context, defaultId);
        }

        // Toggle image tapped.
        public void toggleTap(View view) {
                Context context = getApplicationContext();
                Integer defaultId = MMSKeeperCommon.getDefaultId(context);
                MMSKeeperCommon.toggleData(context);
                /* Do not forget to update toggleImage and currentType.
                 * MMSKeeper does not do this because
                 * 1. most of the time there is no Activity, only widget;
                 * 2. it is cumbersome to query Activity from a different class.
                 */
                updateImage(context, defaultId);
                updateCurrent(context, defaultId);
        }

        // Save dataOnType and dataOffType preferences when Save is tapped.
        public void saveTypes(View view) {
                SharedPreferences settings = getSharedPreferences("global", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                EditText editDataOn = (EditText) findViewById(R.id.editDataOn);
                EditText editDataOff = (EditText) findViewById(R.id.editDataOff);
                editor.putString("dataOnType", editDataOn.getText().toString());
                editor.putString("dataOffType", editDataOff.getText().toString());
                editor.apply();
        }

        // Fill EditTexts from preferences when Discard is tapped.
        public void discardTypes(View view) {
                // Load preferences to fill in EditTexts.
                SharedPreferences settings = getSharedPreferences("global", MODE_PRIVATE);
                String dataOnType = settings.getString("dataOnType", getString(R.string.dataOnType));
                String dataOffType = settings.getString("dataOffType", getString(R.string.dataOffType));
                // Fill EditTexts.
                EditText editDataOn = (EditText) findViewById(R.id.editDataOn);
                EditText editDataOff = (EditText) findViewById(R.id.editDataOff);
                editDataOn.setText(dataOnType);
                editDataOff.setText(dataOffType);
        }

        // Fill EditTexts from initType and "mms" when Reset is tapped.
        public void resetTypes(View view) {
                SharedPreferences settings = getSharedPreferences("global", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                String initType = settings.getString("initType", getString(R.string.dataOnType));
                editor.putString("dataOnType", initType);
                editor.putString("dataOffType", "mms");
                editor.apply();
                // And copy preferences to EditTexts.
                discardTypes(null);
        }

        // Update toggleImage ImageButton.
        private void updateImage(Context context, Integer defaultId) {
                if (defaultId == -1)
                        return;
                ImageButton toggleImage = (ImageButton) findViewById(R.id.toggleImage);
                Integer image;
                if (MMSKeeperCommon.getDataOn(context, defaultId))
                        image = R.drawable.ic_data_on;
                else
                        image = R.drawable.ic_data_off;
                toggleImage.setImageResource(image);
        }

        // Update currentType TextView.
        private void updateCurrent(Context context, Integer defaultId) {
                TextView currentType = (TextView) findViewById(R.id.currentType);
                String type = null;
                if (defaultId != -1)
                        // Query current TYPE.
                        type = MMSKeeperCommon.getType(context, defaultId);
                if (type != null)
                        currentType.setText(getString(R.string.currentType) + "\"" + type + "\".");
                else
                        currentType.setText(getString(R.string.currentType) + "null.");
        }

        // Update initType TextView.
        private void updateInit(Context context) {
                TextView initType = (TextView) findViewById(R.id.initType);
                SharedPreferences settings = getSharedPreferences("global", MODE_PRIVATE);
                String type = settings.getString("initType", getString(R.string.dataOnType));
                initType.setText(getString(R.string.initType) + " \"" + type + "\".");
        }

        // Save current type if this is the first time.
        private void saveInitialType() {
                SharedPreferences settings = getSharedPreferences("global", MODE_PRIVATE);
                String initType = settings.getString("initType", null);
                // If preference does not exist: this is the first time.
                if (initType == null) {
                        Context context = getApplicationContext();
                        Integer defaultId = MMSKeeperCommon.getDefaultId(context);
                        if (defaultId != -1) {
                                String type = MMSKeeperCommon.getType(context, defaultId);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("initType", type);
                                editor.apply();
                        }
                }
        }
}
