package ca.ualberta.medroad.auxiliary.handlers;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;

import ca.ammi.medlib.BtHealthDevice;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-04.
 * <p/>
 * A handler for incoming BtHealthDevice messages. Also defines a callback interface for data
 * consumers.
 *
 * @see EmotionEcgHandler
 */
public class BtHealthDeviceHandler
		implements Handler.Callback
{
	private final int                     index;
	private       BtHealthDeviceCallbacks callbackTarget;

	public BtHealthDeviceHandler( BtHealthDeviceCallbacks callbackTarget, int index )
	{
		this.callbackTarget = callbackTarget;
		this.index = index;
	}

	@Override
	public boolean handleMessage( Message msg )
	{
		switch ( msg.what )
		{
		case BtHealthDevice.CONNECTED_BT:
			callbackTarget.onHealthDeviceBtConnected( (BluetoothDevice) msg.obj, index );
			break;

		case BtHealthDevice.CANNOT_CONNECT_BT:
			callbackTarget.onHealthDeviceBtDisconnected( (BluetoothDevice) msg.obj, index );
			break;

		case BtHealthDevice.GET_DATA:
			callbackTarget.onHealthDeviceDataReceive( (ByteBuffer) msg.obj, msg.arg1, index );
			break;

		case BtHealthDevice.STOP_DATA:
			callbackTarget.onHealthDeviceBtDisconnected( null, index );
			break;

		default:
			Log.w( MainActivity.LOG_TAG, "BtHealthDeviceHandler defaulted: " + msg.what );
			return false;
		}

		return true;
	}

	public interface BtHealthDeviceCallbacks
	{
		void onHealthDeviceBtConnected( BluetoothDevice device, int index );

		void onHealthDeviceBtDisconnected( BluetoothDevice device, int index );

		void onHealthDeviceDataReceive( ByteBuffer data, int length, int index );
	}
}
