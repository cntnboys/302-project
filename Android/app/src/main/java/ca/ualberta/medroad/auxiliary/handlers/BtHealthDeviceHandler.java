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
	private final String                  deviceTag;
	private       BtHealthDeviceCallbacks callbackTarget;

	public BtHealthDeviceHandler( String deviceTag, BtHealthDeviceCallbacks callbackTarget )
	{
		this.deviceTag = deviceTag;
		this.callbackTarget = callbackTarget;
	}

	@Override
	public boolean handleMessage( Message msg )
	{
		switch ( msg.what )
		{
		case BtHealthDevice.CONNECTED_BT:
			callbackTarget.onHealthDeviceBtConnected( deviceTag, (BluetoothDevice) msg.obj );
			break;

		case BtHealthDevice.CANNOT_CONNECT_BT:
			callbackTarget.onHealthDeviceBtDisconnected( deviceTag, (BluetoothDevice) msg.obj );
			break;

		case BtHealthDevice.GET_DATA:
			callbackTarget.onHealthDeviceDataRecieve( deviceTag, (ByteBuffer) msg.obj );
			break;

		case BtHealthDevice.STOP_DATA:
			callbackTarget.onHealthDeviceBtDisconnected( deviceTag, null );
			break;

		default:
			Log.w( MainActivity.LOG_TAG, "BtHealthDeviceHandler defaulted: " + msg.what );
			return false;
		}

		return true;
	}

	public interface BtHealthDeviceCallbacks
	{
		public void onHealthDeviceBtConnected( String tag, BluetoothDevice device );

		public void onHealthDeviceBtDisconnected( String tag, BluetoothDevice device );

		public void onHealthDeviceDataRecieve( String tag, ByteBuffer data );
	}
}
