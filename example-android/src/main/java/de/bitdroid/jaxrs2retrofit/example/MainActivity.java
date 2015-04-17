package de.bitdroid.jaxrs2retrofit.example;

import android.app.Activity;
import android.os.Bundle;

import retrofit.RestAdapter;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MyResource myResource = new RestAdapter.Builder()
				.setEndpoint("http://example.com")
				.build()
				.create(MyResource.class);
	}

}
