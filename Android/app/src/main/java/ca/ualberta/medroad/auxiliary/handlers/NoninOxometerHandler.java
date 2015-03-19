package ca.ualberta.medroad.auxiliary.handlers;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import ca.ammi.medlib.NoninOximeter;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-02.
 * <p/>
 * Handler for incoming NoninOxometer messages. Also defines a callback interface for data
 * consumers.
 *
 * @see EmotionEcgHandler
 */
public class NoninOxometerHandler
		implements Handler.Callback
{
	protected OxometerHandlerCallbacks callbackTarget;

	public NoninOxometerHandler( OxometerHandlerCallbacks callbackTarget )
	{
		this.callbackTarget = callbackTarget;
	}

	@Override
	public boolean handleMessage( Message msg )
	{
		switch ( msg.what )
		{
		case NoninOximeter.CONNECTED_BT: //
			// if the connection succeeds and no successflag is given
			// returns the bluetooth device as the message object
			callbackTarget.onOxometerBtConnected( (BluetoothDevice) msg.obj );
			break;

		case NoninOximeter.CANNOT_CONNECT_BT:
			// if the connection fails and no nosuccessflag is given
			// returns the bluetooth device as the message object
			callbackTarget.onOxometerBtDisconnected( (BluetoothDevice) msg.obj );
			break;

		case NoninOximeter.NONIN_GET_DATA:
			/*
				for EACH data packet received
				The object returned is a NoninOximeter.NoninData object
				with methods:
					dataFormat()  8 or 13
					getPulse() returns the pulse (int)
					getSpO2() returns the SpO2 reading (int)
					getTimeStamp() returns a string with the time stamp yyyy-mm-dd hh:mm:ss
						(only available for format 13)
				Note that this object may be null
			*/
			callbackTarget.onOxometerPacketReceive( (NoninOximeter.NoninData) msg.obj );
			break;

		case NoninOximeter.NONIN_STOP_DATA:
			// if something other than a call to stopData() makes it stop reading
			callbackTarget.onOxometerBtDisconnected( null );
			break;

		case NoninOximeter.NO_RESPONSE:
			// if there is no response when expected
			// the first argument is the operation this was for (FORA_GET_SERIAL etc)
			break;

		case NoninOximeter.NONIN_REDO_MEASUREMENT:
			// measurement is not current - from memory. Redo
			break;

		case NoninOximeter.NONIN_BATTERY_LOW:
			// the battery is low and should be replaced
			break;

		case NoninOximeter.NONIN_SENSOR_INACCURATE:
			// the sensor needs to be repositioned
			break;

		case NoninOximeter.NONIN_GET_TIME:
			// the object is a date/time string
			break;

		case NoninOximeter.NONIN_SET_TIME:
			// the object is a date/time string. Acknowledges the change
			break;

		case NoninOximeter.NONIN_SET_FORMAT_8:
			// as acknowledgement of change
			// Don`t rely on it doing this! There are issues sometimes. Assume the change worked when you request it
			break;

		case NoninOximeter.NONIN_SET_FORMAT_13:
			// as acknowledgement
			// Don`t rely on it doing this! There are issues sometimes. Assume the change worked when you request it
			break;

		default:
			Log.w( MainActivity.LOG_TAG, "NoninOxometerHandler defaulted: " + msg.what );
			return false;
		}

		return true;
	}

	public interface OxometerHandlerCallbacks
	{
		public void onOxometerBtConnected( BluetoothDevice device );

		public void onOxometerBtDisconnected( BluetoothDevice device );

		public void onOxometerPacketReceive( NoninOximeter.NoninData data );
	}
}
