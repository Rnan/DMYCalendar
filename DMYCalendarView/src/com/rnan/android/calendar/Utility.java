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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Utility {
	public static ArrayList<String> nameOfEvent = new ArrayList<String>();
	public static ArrayList<String> startDates = new ArrayList<String>();
	public static ArrayList<String> endDates = new ArrayList<String>();
	public static ArrayList<String> descriptions = new ArrayList<String>();

    public static ArrayList<DateInfo> readCalendarEvent(int y,int m,int d){
        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();

        TYCalendar ty = new TYCalendar();
        ty.initDaysInMonth(y,m,d);
        ArrayList allDateInMonth= ty.ProcessDaysInMonth();
        System.out.println("=====test=====");
        for (int iDay=0;iDay < allDateInMonth.size();iDay++) {
            DateInfo day = (DateInfo) allDateInMonth.get(iDay);
            Calendar start = day.GetDate();
            if(day.IsWanPra()) {
                nameOfEvent.add(day.GetMoonDate());
                startDates.add(getDate(start.getTimeInMillis()));
                descriptions.add(day.GregorianDate());
            }
        }
        return allDateInMonth;
    }

	public static ArrayList<String> readCalendarEvent(Context context) {
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse("content://com.android.calendar/events"),
						new String[] { "calendar_id", "title", "description",
								"dtstart", "dtend", "eventLocation" }, null,
						null, null);
		cursor.moveToFirst();
		// fetching calendars name
		String CNames[] = new String[cursor.getCount()];

		// fetching calendars id
		nameOfEvent.clear();
		startDates.clear();
		endDates.clear();
		descriptions.clear();
        /*
		for (int i = 0; i < CNames.length; i++) {

			nameOfEvent.add(cursor.getString(1));
			startDates.add(getDate(Long.parseLong(cursor.getString(3))));
			endDates.add(getDate(Long.parseLong(cursor.getString(4))));
			descriptions.add(cursor.getString(2));
			CNames[i] = cursor.getString(1);
			cursor.moveToNext();

		}*/

        //** dummy ***/
        System.out.println("=====test=====");
        Calendar calendar = Calendar.getInstance();
        Calendar start= new GregorianCalendar(2015,2,27);
        //Calendar end= new GregorianCalendar(2015,2,28);
        nameOfEvent.add("วันนี้วันพระ");
        startDates.add(getDate(start.getTimeInMillis()));
        //endDates.add(getDate(end.getTimeInMillis()));
        descriptions.add("ดิสคลิปชั่น");

		return nameOfEvent;
	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}
