package tcnr18.com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ToGcmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   savedInstanceState.get

        setContentView(R.layout.activity_to_gcm);
        TextView tv=(TextView)findViewById(R.id.showMsg);
        String msg=getIntent().getStringExtra("message");

      //  String msg=getIntent().getBundleExtra("message").toString();


      //  savedInstanceState.g("message");
        tv.setText(msg);
//        String str="https://play.google.com/store/search?q=ithinkbest.com";
//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
//        startActivity(myIntent);
//        finish();
    }

    public void onClickLaunch(View view){
    	   Intent intent = new Intent(this, MainActivity.class);
//    	   Intent intent = new Intent(this, NavigationDrawerActivity.class);

        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
