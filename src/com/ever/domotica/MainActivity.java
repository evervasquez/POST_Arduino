package com.ever.domotica;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ever.domotica.utils.ConnectionUtils;

public class MainActivity extends Activity {
	private static final int RESULT_SETTING = 1;
	private static final String TAG = "MainActivity";
	private String led;
	public String server = null;
	public Context context;
	private TextView iptext;
	private Button btn_on, btn_off;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		iptext = (TextView) findViewById(R.id.txtip);
		btn_on = (Button) findViewById(R.id.btn_on);
		btn_off = (Button) findViewById(R.id.btn_off);

		btn_on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.btn_on:
					led = "1";
					break;
				case R.id.btn_off:
					led = "0";
					break;
				default:
					break;
				}

				new datosAsyncTask(server, led).execute();
			}
		});

		btn_off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.btn_off:
					led = "0";
					break;
				default:
					break;
				}
				new datosAsyncTask(server, led).execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
			startActivityForResult(intent, RESULT_SETTING);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	// metodo para recuperar la url de preferences
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_SETTING:
			showSettings();
			break;

		default:
			break;
		}

		this.server = getUrl();
		iptext.setText(this.server);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showSettings() {
		SharedPreferences mSharePreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String url = mSharePreferences.getString("prefUrl", "NULL");

		mSharePreferences = getSharedPreferences("DomoticaPreferences",
				MODE_PRIVATE);

		// guardamos las preferencias en un xml
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putString("url", url);
		editor.commit();
	}

	private String getUrl() {
		SharedPreferences pref = getSharedPreferences("DomoticaPreferences",
				MODE_PRIVATE);
		return pref.getString("url", "hubo un error al guardar");
	}

	private class datosAsyncTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog pd;
		private String paramPOST;
		private ConnectionUtils conn;
		private String response;
		private String led;
		private String server = null;

		public datosAsyncTask(String server, String led) {
			this.server = server;
			this.led = led;
		}

		@Override
		protected void onPreExecute() {
			// super.onPreExecute();
			pd = ProgressDialog.show(context, "", "Verificando datos...", true);
			try {
				String param = URLEncoder.encode("led", "UTF-8") + "="
						+ URLEncoder.encode(led, "UTF-8");

				paramPOST = param;
				Log.v(TAG, paramPOST);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... arg0) {
			conn = new ConnectionUtils(getApplicationContext(), this.server,
					paramPOST);
			response = conn.getResponse();
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();

			Log.v(TAG, response);
		}
	}

}
