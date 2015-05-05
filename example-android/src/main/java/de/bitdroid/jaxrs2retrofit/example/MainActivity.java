package de.bitdroid.jaxrs2retrofit.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import de.bitdroid.jaxrs2retrofit.example.common.HelloWorld;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MyHelloWorldResource resource = new RestAdapter.Builder()
				.setEndpoint("<your server address here>")
				.build()
				.create(MyHelloWorldResource.class);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resource.getHelloWorld(new Callback<HelloWorld>() {
					@Override
					public void success(HelloWorld helloWorld, Response response) {
						Toast.makeText(MainActivity.this, helloWorld.getHello() + " " + helloWorld.getWorld(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void failure(RetrofitError error) {
						Toast.makeText(MainActivity.this, "failed to get hello world: " + error.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}

}
