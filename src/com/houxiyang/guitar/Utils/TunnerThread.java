package com.houxiyang.guitar.Utils;

import java.io.Serializable;
import java.util.Date;

import android.R.string;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

/**
 * 通过调用FFT方法来实时计算输入音频的频率
 * 
 * @author Young
 * 
 */
public class TunnerThread extends Thread implements Serializable{

	private double time=0;
	String lock="lock";
	static {
		System.loadLibrary("FFT");
	}

	public static native double processSampleData(byte[] sample, int sampleRate);

	private static final int[] OPT_SAMPLE_RATES = { 11025, 8000, 22050, 44100 };
	private static final int[] BUFFERSIZE_PER_SAMPLE_RATE = { 8 * 1024,
			4 * 1024, 16 * 1024, 32 * 1024 };

	private int SAMPLE_RATE = 12000;
	private int READ_BUFFERSIZE = 4 * 1024;
	private double currentFrequency;

	private Handler handler;
	private Runnable callback;
	private AudioRecord audioRecord;

	public TunnerThread(Handler handler, Runnable callback) {
		this.handler = handler;
		this.callback = callback;
		//initAudioRecord();
	}

	// 每个device的初始化参数可能不同
	private void initAudioRecord() {
		int counter = 0;
		/*READ_BUFFERSIZE=AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);*/
		for (int sampleRate : OPT_SAMPLE_RATES) {
			initAudioRecord(sampleRate);
			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				SAMPLE_RATE = sampleRate;
				//READ_BUFFERSIZE = BUFFERSIZE_PER_SAMPLE_RATE[counter];
				READ_BUFFERSIZE=AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				break;
			}
			counter++;
		}
		/*audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, READ_BUFFERSIZE);*/
	}

	@SuppressWarnings("deprecation")
	private void initAudioRecord(int sampleRate) {
		/*audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, sampleRate * 6);*/
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT));
	}

	@Override
	public void run() {
		initAudioRecord();
		audioRecord.startRecording();
		byte[] bufferRead = new byte[READ_BUFFERSIZE];
		long beginTIme=System.currentTimeMillis();
		int r=audioRecord.read(bufferRead, 0, READ_BUFFERSIZE) ;
		while (audioRecord!=null && r> 0) {
			
			double v = 0.0;  
            for (int i = 0; i <bufferRead.length; i++) {  
                v += bufferRead[i] * bufferRead[i];  
            }  
            double mean = v / (double) r;  
            double volume = 10.0 * Math.log10(mean/600.0); 
            Log.i("volume", ""+volume+"db");
            currentFrequency = processSampleData(bufferRead, SAMPLE_RATE);
            if(volume>6.0&&currentFrequency>100&&currentFrequency<600)
            {
            	
    			
    			time+=(double)(System.currentTimeMillis()-beginTIme)/1000.0;
    			Log.i("time", time+"秒");
    			handler.post(callback);
            }
            beginTIme=System.currentTimeMillis();
            r=audioRecord.read(bufferRead, 0, READ_BUFFERSIZE) ;
			/*try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			/*if (currentFrequency > 0) {
				handler.post(callback);
				try {
					synchronized (lock) {
						if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
							Log.i("rykaudio", "try");
							audioRecord.stop();
							Thread.sleep(20);
							audioRecord.startRecording();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			Log.i("rykaudio", "try");
		}
		Log.i("rykaudio", "run over");
	}

	public void close() {
		synchronized (lock) {
			if (audioRecord != null) {
				audioRecord.stop();
				audioRecord.release();
				audioRecord = null;
				Log.i("rykaudio", "close");
			}
		}
	}

	public double getCurrentFrequency() {
		return currentFrequency;
	}

	public double getTime() {
		return time;
	}
	

}
