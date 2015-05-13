/**
 * @author RS
 * @version 2
 * @since 2014-9-20
 */
package rs.srtp.voice;

import com.houxiyang.guitar.Utils.TunnerThread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 
 * @author RS
 * @version 2
 * @since 2014-9-20
 *
 */
public class Voice extends Service {
	
	TunnerThread mTunner=null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.hasExtra("tunner"))
		mTunner=(TunnerThread)intent.getSerializableExtra("tunner");
		
		switch(intent.getIntExtra("runncommand", 0))
		{
		case 1:
			mTunner.start();
			break;
		case 3:
			mTunner.close();
			stopSelf();
			break;
		}
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void startTunning() {
		mTunner.start();
	}
	
	private void stopTunning() {
		mTunner.close();
	}

}
