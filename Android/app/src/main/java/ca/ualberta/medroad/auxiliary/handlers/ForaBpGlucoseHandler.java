package ca.ualberta.medroad.auxiliary.handlers;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import ca.ammi.medlib.ForaBpGlucose;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-02.
 * <p/>
 * Handler for incoming ForaBpGlucose messages. Also defines a callback interface for data
 * consumers.
 *
 * @see EmotionEcgHandler
 */
public class ForaBpGlucoseHandler
		implements Handler.Callback
{
	BpGlucoseHandlerCallbacks callbackTarget;

	public ForaBpGlucoseHandler( BpGlucoseHandlerCallbacks callbackTarget )
	{
		this.callbackTarget = callbackTarget;
	}

	@Override
	public boolean handleMessage( Message msg )
	{
		switch ( msg.what )
		{
		case ForaBpGlucose.CONNECTED_BT:
			// if the connection succeeds and no successflag is given
			// returns the bluetooth device as the message object
			callbackTarget.onBpGlucoseBtConnected( (BluetoothDevice) msg.obj );
			break;

		case ForaBpGlucose.CANNOT_CONNECT_BT:
			// if the connection fails and no nosuccessflag is given
			// returns the bluetooth device as the message object
			callbackTarget.onBpGlucoseBtDisconnected( (BluetoothDevice) msg.obj );
			break;

		case ForaBpGlucose.FORA_GET_ALL_DATA:
			// for EACH data item found
			break;

		case ForaBpGlucose.FORA_GET_LATEST_DATA:
			// for the data item found
			callbackTarget.onBpGlucosePacketReceive( (ForaBpGlucose.ForaData) msg.obj );
			break;

		case ForaBpGlucose.CHECKSUM_ERROR:
			// if there is a checksum error on a read
			break;

		case ForaBpGlucose.FORA_GET_SERIAL:
			// the object returned is a byte array with 8 bytes
			break;

		case ForaBpGlucose.FORA_CLEAR_DATA:
			// no object. Acknowledgement
			break;

		case ForaBpGlucose.FORA_TURN_OFF:
			// no object. Acknowledgement
			callbackTarget.onBpGlucoseBtDisconnected( null );
			break;

		case ForaBpGlucose.FORA_CHECK_FOR_DATA:
			// the arg1 value is the number of data (response from checkForData)
			break;

		case ForaBpGlucose.FORA_SET_TIME:
			// the object is a date/time string. Acknowledges change
			break;

		case ForaBpGlucose.FORA_GET_TIME:
			// the object is a date/time string
			break;

		case ForaBpGlucose.FORA_GET_PROJECT:
			// the object is a byte array with 2 bytes
			break;

		default:
			Log.w( MainActivity.LOG_TAG, "EmotionEcgHandler defaulted: " + msg.what );
			return false;
		}
		return true;
	}

	public interface BpGlucoseHandlerCallbacks
	{
		public void onBpGlucoseBtConnected( BluetoothDevice device );

		public void onBpGlucoseBtDisconnected( BluetoothDevice device );

		public void onBpGlucosePacketReceive( ForaBpGlucose.ForaData data );
	}
}
