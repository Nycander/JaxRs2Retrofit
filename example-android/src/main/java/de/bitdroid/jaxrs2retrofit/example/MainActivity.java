package de.bitdroid.jaxrs2retrofit.example;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MyResource myResource = new RestAdapter.Builder()
				.setEndpoint("http://example.com")
				.build()
				.create(MyResource.class);

		myResource.getHelloWorld(new Callback<String>() {
			@Override
			public void success(String s, Response response) {
				Log.d("", "hello world success");
			}

			@Override
			public void failure(RetrofitError error) {
				Log.d("", "hello world error");
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);

	}
}
