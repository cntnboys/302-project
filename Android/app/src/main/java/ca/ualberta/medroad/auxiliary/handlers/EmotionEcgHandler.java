package ca.ualberta.medroad.auxiliary.handlers;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import ca.ammi.medlib.EmotionEcg;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-02-26.
 * <p/>
 * Handler for incoming EmotionEcg messages. The class also defines a callback interface for some
 * events. A class that wishes to consume incoming events simply needs to implement its interface
 * and pass itself in as a constructor argument.
 */
public class EmotionEcgHandler
		implements Handler.Callback
{
	protected EcgHandlerCallbacks callbackTarget;

	public EmotionEcgHandler( EcgHandlerCallbacks callbackTarget )
	{
		this.callbackTarget = callbackTarget;
	}

	@Override
	public boolean handleMessage( Message msg )
	{
		switch ( msg.what )
		{
		case EmotionEcg.GET_DATA:
			// Returns pulse (averaged over 5 packets) as msg.arg1
			break;

		case EmotionEcg.ECG_SAMPLES:
			/*
				Returns an EmotionEcg.EcgData object as the object with methods:
					dataFormat()  returns the data format as a float (0.7 )
					getSamples() returns an int[25] of micro volt readings,
						which can be plotted against the sampling frequency (def 100Hz)
					getRrInterval() returns the rr HTTP_INTERVAL in ms (int) 0 if there isn't one
					gotRrInterval() returns the true if there is an rr HTTP_INTERVAL, false otherwise
					getPeak() returns the absolute time or RR peak in ms (int)
					getPacketNumber() returns the packet number (int)
				Note that this object may be null
			 */
			callbackTarget.onEcgPacketReceive( (EmotionEcg.EcgData) msg.obj );
			break;

		case EmotionEcg.STOP_DATA:
			// have to stopHttpWorker reading from device and user didn't ask for it
			callbackTarget.onEcgBtDisconnected( null );
			break;

		case EmotionEcg.BATTERY_LOW:
			// battery low
			break;

		case EmotionEcg.CONNECTED_BT:
			// we connected to device
			callbackTarget.onEcgBtConnected( (BluetoothDevice) msg.obj );
			break;

		case EmotionEcg.CANNOT_CONNECT_BT:
			// we couldn't connect to device
			callbackTarget.onEcgBtDisconnected( (BluetoothDevice) msg.obj );
			break;

		case EmotionEcg.SENSOR_INACCURATE:
			// The sensors are incorrectly positioned
			break;

		default:
			Log.w( MainActivity.LOG_TAG, "EmotionEcgHandler defaulted: " + msg.what );
			return false;
		}

		return true;
	}

	public interface EcgHandlerCallbacks
	{
		public void onEcgBtConnected( BluetoothDevice device );

		public void onEcgBtDisconnected( BluetoothDevice device );

		public void onEcgPacketReceive( EmotionEcg.EcgData data );
	}
}
