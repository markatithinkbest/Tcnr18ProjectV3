package tcnr18.com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ToGcmActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_gcm);
		TextView tv = (TextView) findViewById(R.id.showMsg);
		String msg = getIntent().getStringExtra("message");
		tv.setText(msg);
	}

	public void onClickLaunch(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
