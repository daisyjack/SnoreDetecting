/**
 * @author RS
 * @version 1
 * @since 2014-9-20
 */
package rs.srtp.activity;

//import ca.uol.aig.fftpack.RealDoubleFFT;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.houxiyang.guitar.Utils.KeepScreenon;
import com.houxiyang.guitar.Utils.TunnerThread;

import rs.srtp.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import rs.srtp.voice.Voice;

/**
 * 
 * @author RS
 * @version 1
 * @since 2014-9-20
 */
public class MainActivity extends Activity {
	//private Button mDoc_Button;
	//private Button mPause_Button;
	private Button mStart_Button;
	private TextView mState;
	private Button mStop_Button;
	private TunnerThread mTunner=null;
	private Handler mHandler;
	private Runnable mRunnable;
	private TextView mTime;
	byte[] bufferRead = new byte[4096];

	public native double processSampleData(byte[] sample, int sampleRate);
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		KeepScreenon.keepScreenOn(this, true);
		setContentView(R.layout.main);
		double result = TunnerThread.processSampleData(bufferRead, 8000);
		Log.i("rykjni", ""+result);
		//this.mDoc_Button = (Button) findViewById(R.id.doc_button);
		this.mStart_Button = (Button) findViewById(R.id.start_button);
		//this.mPause_Button = (Button) findViewById(R.id.pause_button);
		this.mStop_Button = (Button) findViewById(R.id.stop_button);
		this.mState = (TextView) findViewById(R.id.running_state);
		this.mState.setText(R.string.running_stopped);
		mTime=(TextView)findViewById(R.id.time);
		mHandler=new Handler();
		mRunnable=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateText(mTunner.getCurrentFrequency());
				mTime.setText((long)mTunner.getTime()+"秒");
			}
		};
		//mTunner=new TunnerThread(mHandler, mRunnable);
		this.mStart_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				MainActivity.this.mState.setText(R.string.running_started);
				if(mTunner==null || !mTunner.isAlive())
				{
					mTunner=new TunnerThread(mHandler, mRunnable);
					mTunner.start();
				}
				
				/*Intent localIntent = new Intent(MainActivity.this,
						Voice.class);
				localIntent.putExtra("runcommand", 1);
				localIntent.putExtra("tunner", mTunner);
				MainActivity.this.startService(localIntent);*/
			}
		});
		/*this.mPause_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				MainActivity.this.mState.setText(R.string.running_paused);
				Intent localIntent = new Intent(MainActivity.this, Voice.class);
				localIntent.putExtra("runcommand", 2);
				MainActivity.this.startService(localIntent);
			}
		});*/
		this.mStop_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				MainActivity.this.mState.setText(R.string.running_stopped);
				mTunner.close();
				/*Intent localIntent = new Intent(MainActivity.this, Voice.class);
				localIntent.putExtra("runcommand", 3);
				MainActivity.this.startService(localIntent);*/
			}
		});
	}
	
	private void updateText(double currentFrequency) {
/*		while (currentFrequency < 82.41) {
			currentFrequency = currentFrequency * 2;
		}
		while (currentFrequency > 164.81) {
			currentFrequency = currentFrequency * 0.5;
		}
		BigDecimal a = new BigDecimal(currentFrequency);
		BigDecimal result = a.setScale(2, RoundingMode.DOWN);
		mState.setText(String.valueOf(result));*/
		mState.setText((long)currentFrequency+"Hz");
	}
}
