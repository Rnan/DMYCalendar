/**************************************************************
  Copyright 2015 Anan Sriram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *****************************************************************/

package com.rnan.android.calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import java.util.Locale;

public class HomeApplication extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_application);
        Locale.setDefault(Locale.US);

        //*** create the TabHost that will contain the Tabs ***
        TabHost tabHost = getTabHost();
        Resources res = getResources();

        Intent intentCal = new Intent().setClass(this, CalendarView.class);
        TabHost.TabSpec tabSpecCal = tabHost
                .newTabSpec("CalendarTab")
                .setIndicator("ปฏิทิน", res.getDrawable(R.drawable.icon_calendar_config))
                .setContent(intentCal);
        Intent intentAbout = new Intent().setClass(this, About.class);
        TabHost.TabSpec tabSpecAbout = tabHost
                .newTabSpec("AboutTab")
                .setIndicator("เกี่ยวกับ", res.getDrawable(R.drawable.icon_about_config))
                .setContent(intentAbout);

        tabHost.addTab(tabSpecCal);
        tabHost.addTab(tabSpecAbout);
        //*****
        tabHost.setCurrentTab(0);

    }
}
