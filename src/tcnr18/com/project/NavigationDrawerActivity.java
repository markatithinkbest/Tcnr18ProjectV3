/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tcnr18.com.project;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class NavigationDrawerActivity extends Activity implements
		PlanetAdapter.OnItemClickListener {

	static String LOG_TAG = "MARK987";

	// --- GCM ---
	String PROJECT_NUMBER = "538682377549";// Project ID: taipei-ok Project
											// Number: 538682377549
	static String regid = null;
	GoogleCloudMessaging gcm;

	private DrawerLayout mDrawerLayout;
	private RecyclerView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_drawer);

		//
		notifyAppWebpage();
		//
		getRegId();

		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(
				R.array.certification_category);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		// mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		// GravityCompat.START);
		// improve performance by indicating the list if fixed size.
		mDrawerList.setHasFixedSize(true);
		mDrawerList.setLayoutManager(new LinearLayoutManager(this));

		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */

		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;
					// Toast.makeText(getApplicationContext(),
					// Log.i(LOG_TAG, msg);

					String result = readGcmInsertResult();
					// Log.i(LOG_TAG, "...readGcmInsertResult() " + result);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	public String readGcmInsertResult() {
		if (regid == null) {
			Log.d(LOG_TAG, "regid is null");
			return "";
		}
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String str = "http://ithinkbest.com/tcnr18project/gcm_insert.php?reg_id="
				+ regid;
		HttpGet httpGet = new HttpGet(str);
		// Log.d(LOG_TAG, "new HttpGet(str) => " + str);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(LOG_TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(LOG_TAG, "Exception " + e.toString());
		}
		return builder.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_drawer, menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		// case R.id.action_websearch:
		// // create intent to perform web search for this planet
		// Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		// intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
		// // catch event that there's no activity to handle intent
		// if (intent.resolveActivity(getPackageManager()) != null) {
		// startActivity(intent);
		// } else {
		// Toast.makeText(this, R.string.app_not_available,
		// Toast.LENGTH_LONG).show();
		// }
		// return true;
		case R.id.action_update:
			Log.d(LOG_TAG, "...DEBUG action_update");
			// use this to start and trigger a service
			Intent i = new Intent(getApplicationContext(), UpdateService.class);
			// potentially add data to the intent
			int[] cats = { 0, 1 };
			i.putExtra("CATS", cats);
			getApplicationContext().startService(i);

			return true;

		case R.id.action_del_all:
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			getContentResolver().delete(TaipeiOkProvider.CONTENT_URI, null,
					null);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void notifyGooglePlay() {
		int idGooglePlay = 12345;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.to_google_play));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ToGooglePlayActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ToGooglePlayActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(idGooglePlay, mBuilder.build());

	}

	private void notifyAppWebpage() {
		int idGooglePlay = 12346;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.to_app_webpage));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ToAppWebpageActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ToAppWebpageActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(idGooglePlay, mBuilder.build());

	}

	/* The click listener for RecyclerView in the navigation drawer */
	@Override
	public void onClick(View view, int position) {
		selectItem(position);
	}

	private void selectItem(int position) {
		Fragment fragment = TaipeiFragment.newInstance(position);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();

		// update selected item title, then close the drawer
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame", shows a planet
	 */
	public static class TaipeiFragment extends Fragment implements
			AdapterView.OnItemSelectedListener {
		String[] Taipei_District = null;// =
										// res.getStringArray(R.array.planets_array);
		String[] Certification_Category = null;
		ListView listView;
		Spinner spinner;
		SimpleCursorAdapter spinnerAdapter;
		int selectedCategory = 0;

		public static final String ARG_PLANET_NUMBER = "planet_number";

		public TaipeiFragment() {
			// Empty constructor required for fragment subclasses
		}

		public static Fragment newInstance(int position) {
			Log.d(LOG_TAG, " ### Fragment newInstance, position=" + position);
			Fragment fragment = new TaipeiFragment();
			Bundle args = new Bundle();
			args.putInt(TaipeiFragment.ARG_PLANET_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		private Cursor getSummary(int cat) {
			Uri uri = TaipeiOkProvider.CONTENT_URI_RAW_QUERY;
			String[] projection = new String[] { TaipeiOkProvider.COLUMN_ID,
					TaipeiOkProvider.COLUMN_NAME,
					TaipeiOkProvider.COLUMN_DISPLAY_ADDR };
			//
			String selection = TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY
					+ "=\"" + Certification_Category[cat] + "\"";

			String[] selectionArgs = null;
			String sortOrder = null;

			return getActivity().managedQuery(uri, projection, selection,
					selectionArgs, sortOrder);

			// return null;
		}

		private void updateSpinner(int cat) {

			spinnerAdapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_list_item_2, getSummary(cat),
					new String[] { TaipeiOkProvider.COLUMN_DISTRICT, "CNT" },
					new int[] { android.R.id.text1, android.R.id.text2 });
			spinnerAdapter.notifyDataSetChanged();
			spinner.setAdapter(spinnerAdapter);

		}

		private Cursor getList(int cat) {
			Uri uri = TaipeiOkProvider.CONTENT_URI;
			String[] projection = new String[] { TaipeiOkProvider.COLUMN_ID,
					TaipeiOkProvider.COLUMN_NAME,
					TaipeiOkProvider.COLUMN_DISPLAY_ADDR };
			//
			String selection = TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY
					+ "=\"" + TaipeiOkProvider.CATXX[cat] + "\"";

			String[] selectionArgs = null;
			String sortOrder = TaipeiOkProvider.COLUMN_DISPLAY_ADDR;

			return getActivity().managedQuery(uri, projection, selection,
					selectionArgs, sortOrder);
		}

		private Cursor getList(int cat, String district) {
			Uri uri = TaipeiOkProvider.CONTENT_URI;
			String[] projection = new String[] { TaipeiOkProvider.COLUMN_ID,
					TaipeiOkProvider.COLUMN_NAME,
					TaipeiOkProvider.COLUMN_DISPLAY_ADDR };
			//
			String selection = TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY
					+ "=\"" + TaipeiOkProvider.CATXX[cat] + "\"" + " AND "
					+ TaipeiOkProvider.COLUMN_DISTRICT + " LIKE '%" + district
					+ "%'";
			// name like '% LIM %'

			String[] selectionArgs = null;
			String sortOrder = TaipeiOkProvider.COLUMN_DISPLAY_ADDR;

			return getActivity().managedQuery(uri, projection, selection,
					selectionArgs, sortOrder);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Taipei_District = getResources().getStringArray(
					R.array.taipei_district);
			Certification_Category = getResources().getStringArray(
					R.array.certification_category);

			View rootView = inflater.inflate(R.layout.fragment_listview_v2,
					container, false);
			spinner = (Spinner) rootView.findViewById(R.id.spinner);
			listView = (ListView) rootView.findViewById(R.id.listView2);

			selectedCategory = getArguments().getInt(ARG_PLANET_NUMBER);
			spinner.setOnItemSelectedListener(this);

			// new UpdateService().processJson(selectedCategory);
			processJson(selectedCategory);
			spinnerAdapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_list_item_2,
					getSummary(selectedCategory), new String[] {
							TaipeiOkProvider.COLUMN_DISTRICT, "CNT" },
					new int[] { android.R.id.text1, android.R.id.text2 });

			spinner.setAdapter(spinnerAdapter);

			Cursor mGrpMemberCursor = getList(selectedCategory);
			getActivity().startManagingCursor(mGrpMemberCursor);
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(), android.R.layout.simple_list_item_2,
					mGrpMemberCursor, new String[] {
							TaipeiOkProvider.COLUMN_NAME,
							TaipeiOkProvider.COLUMN_DISPLAY_ADDR }, new int[] {
							android.R.id.text1, android.R.id.text2 });

			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView textView1 = (TextView) view
							.findViewById(android.R.id.text1);
					TextView textView2 = (TextView) view
							.findViewById(android.R.id.text2);
					Toast.makeText(getActivity(),
							textView1.getText() + " " + textView2.getText(),
							Toast.LENGTH_SHORT).show();
				}
			});

			// set title on fragment
			String[] certification_category = getResources().getStringArray(
					R.array.certification_category);
			getActivity().setTitle(certification_category[selectedCategory]);
			return rootView;
		}

		public void processJson(int cat) {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			Vector<ContentValues> cVVector = null;

			try {
				String strJson = readRawJson(cat);

				if (strJson == null || strJson.length() == 0) {
					// Log.d(LOG_TAG, "NO JSON");
					return;
				}

				// Log.d(LOG_TAG, "(first 50)input=" + strJson.substring(0,
				// 50));

				JSONArray jsonArray = new JSONArray(strJson);
				cVVector = new Vector<ContentValues>(jsonArray.length());

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String name = jsonObject.getString(
							TaipeiOkProvider.COLUMN_NAME).trim();
					String certification_category = jsonObject.getString(
							TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY)
							.trim();
					String tel = jsonObject.getString(
							TaipeiOkProvider.COLUMN_TEL).trim();
					// not to show null
					if (tel == null || tel.equals("null")) {
						tel = "";
					}
					String display_addr = jsonObject.getString(
							TaipeiOkProvider.COLUMN_DISPLAY_ADDR).trim();

					String poi_addr = jsonObject.getString(
							TaipeiOkProvider.COLUMN_POI_ADDR).trim();

					//

					ContentValues weatherValues = new ContentValues();
					weatherValues.put(TaipeiOkProvider.COLUMN_NAME, name);
					weatherValues.put(
							TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY,
							certification_category);
					weatherValues.put(TaipeiOkProvider.COLUMN_TEL, tel);

					if (tel.equals("")) {
						weatherValues.put(TaipeiOkProvider.COLUMN_DISPLAY_ADDR,
								display_addr);

					} else {
						weatherValues.put(TaipeiOkProvider.COLUMN_DISPLAY_ADDR,
								display_addr + "  tel: " + tel);

					}

					weatherValues.put(TaipeiOkProvider.COLUMN_POI_ADDR,
							poi_addr);

					//
					String strDist = getDistrict(display_addr);
					weatherValues
							.put(TaipeiOkProvider.COLUMN_DISTRICT, strDist);
					cVVector.add(weatherValues);
				}
			} catch (JSONException e) {
				Log.d(LOG_TAG, "JSONException " + e.toString());
			} catch (Exception e) {
				Log.d(LOG_TAG, "Exception " + e.toString());
			}

			// add to database in bulk
			if (cVVector.size() > 0) {
				String str = null;

				String selection = TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY
						+ "=\"" + TaipeiOkProvider.CATXX[cat] + "\"";

				int delCnt = getActivity().getContentResolver().delete(
						TaipeiOkProvider.CONTENT_URI, selection, null);
				Log.d(LOG_TAG, "del cnt= " + delCnt);

				ContentValues[] cvArray = new ContentValues[cVVector.size()];
				cVVector.toArray(cvArray);
				int bulkCnt = getActivity().getContentResolver().bulkInsert(
						TaipeiOkProvider.CONTENT_URI, cvArray);
				// Log.d(LOG_TAG, "bulk cnt= " + bulkCnt);
			}

		}

		String getDistrict(String address) {
			String strDist = null;
			int knownDist = Taipei_District.length - 1;
			for (int i = 0; i < Taipei_District.length - 1; i++) {
				strDist = Taipei_District[i].substring(4);
				if (address.indexOf(strDist) >= 0) {
					knownDist = i;
					break;
				}
			}
			return Taipei_District[knownDist];
		}

		public String readRawJson(int cat) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			String str = TaipeiOkProvider.JSNXX[cat];

			HttpGet httpGet = new HttpGet(str);
			// Log.d(LOG_TAG, "new HttpGet(str) => " + str);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					// Log.e(LOG_TAG, "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// Log.d(LOG_TAG, "Exception " + e.toString());
			}
			return builder.toString();
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			try {
				Resources res = getResources();
//				String[] taipei_district = res
//						.getStringArray(R.array.taipei_district);

				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				String strDist = textView.getText().toString();
//				Log.d(LOG_TAG, " textView:" + strDist);

				Cursor mGrpMemberCursor = getList(selectedCategory, strDist);
				getActivity().startManagingCursor(mGrpMemberCursor);
				SimpleCursorAdapter adapter = new SimpleCursorAdapter(
						getActivity(), android.R.layout.simple_list_item_2,
						mGrpMemberCursor, new String[] {
								TaipeiOkProvider.COLUMN_NAME,
								TaipeiOkProvider.COLUMN_DISPLAY_ADDR },
						new int[] { android.R.id.text1, android.R.id.text2 });

				listView.setAdapter(adapter);
			} catch (Exception e) {
//				Log.d(LOG_TAG, "onItemSelected, textView is null");

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

}
