/**
 * @author RS
 * @version 1
 * @since 2014-10-2
 */
package rs.srtp.voice;

import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author RS
 * @version 1
 * @since 2014-10-2
 *
 */
public class LogRunnable implements Runnable {
	private int mRunCommand;

	/**
	 * 
	 * @param intent
	 */
	public LogRunnable(int runCommand) {
		this.mRunCommand = runCommand;
	}

	public void run() {
		// TODO Auto-generated method stub
			switch (mRunCommand) {
			case 1:
				Log.i("ryk", "开始");
				break;
			case 2:
				Log.i("ryk", "暂停");
				break;
			case 3:
				Log.i("ryk", "停止");
				break;
			}
	}
	

}
