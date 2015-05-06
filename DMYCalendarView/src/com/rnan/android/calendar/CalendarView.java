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
import java.util.*;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class CalendarView extends Activity {

	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;// for grabbing some event values for showing the dot
							// marker.
	public ArrayList<String> items; // container to store calendar items which
									// needs showing the event marker
	//ArrayList<String> event;
	ArrayList<DateInfo> dateInfoList;
	//LinearLayout rLayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		Locale.setDefault(Locale.US);
		//rLayout = (LinearLayout) findViewById(R.id.text);
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();

		adapter = new CalendarAdapter(this, month);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(GetTitle(month));

		RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
				// removing the previous view if added
				/*
				if (((LinearLayout) rLayout).getChildCount() > 0) {
					((LinearLayout) rLayout).removeAllViews();
				}
				*/

				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				String selectedGridDate = CalendarAdapter.dayString
						.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				String[] values = null;
				if(Utility.startDates.size() > 0) {

					for (int i = 0; i < Utility.startDates.size(); i++) {
						if (Utility.startDates.get(i).equals(selectedGridDate)) {
							values = new String[1];
							values[0] = "วันพระ : " + Utility.nameOfEvent.get(i) + " " + Utility.descriptions.get(i);
							//Log.d("Val", values[0]);
						}
					}
					if(values!=null) {
						LoadListView(values);
					}
					else {
						ListView listViexw = (ListView) findViewById(R.id.listView);
						listViexw.setAdapter(null);
					}

				}


			}

		});

		//LoadListView();
	}

	private String GetTitle(GregorianCalendar m){
		String text="";
		int bcYear= m.get(GregorianCalendar.YEAR)+543;
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM", new Locale("th"));
		String monthName = sdf.format(m.getTime());
		text = monthName + " " + bcYear;
		return text;
	}

	private void LoadListView(String[] values){
		if(values!=null && values.length >0 ) {
			ListViewAdapter adapter = new ListViewAdapter(this, values);
			ListView listView = (ListView) findViewById(R.id.listView);
			listView.setAdapter(adapter);

		}
	}

	private String[] LoadWanPra(){
		String[] values = null;

		if(Utility.startDates.size() > 0) {
			values = new String[Utility.startDates.size()];
			for (int i = 0; i < Utility.startDates.size(); i++) {
				values[i] = "วันพระ : " + Utility.nameOfEvent.get(i) + " " + Utility.descriptions.get(i);
			}
		}

		return values;
     }

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	protected void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

	}

	public void refreshCalendar() {
		TextView title = (TextView) findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items
		title.setText(GetTitle(month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();
			// removing the previous view if added
			/*
            if (((LinearLayout) rLayout).getChildCount() > 0) {
                ((LinearLayout) rLayout).removeAllViews();
            }*/
			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            int y= month.get(Calendar.YEAR);
            int m= month.get(Calendar.MONTH);
            int d= month.get(Calendar.DATE);
			dateInfoList = Utility.readCalendarEvent(y,m,d);
			//Log.d("Val", event.toString());
            //Log.d("=====Date ARRAY====", Utility.startDates.toString());
			for (int i = 0; i < Utility.startDates.size(); i++) {
				//itemvalue = df.format(itemmonth.getTime());
				//itemmonth.add(GregorianCalendar.DATE, 1);
				items.add(Utility.startDates.get(i).toString());
			}
			adapter.setItems(items);
			adapter.setDateInfoList(dateInfoList);
			adapter.notifyDataSetChanged();

			LoadListView(LoadWanPra());
		}
	};
}
