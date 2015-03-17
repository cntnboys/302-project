package ca.ualberta.medroad.model.raw_table_rows;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.ualberta.medroad.auxiliary.Encrypter;

/**
 * Created by Yuey on 2015-03-12.
 *
 * Class representing a raw database latestRow for data.
 */
public class DataRow
{
	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss",
			Locale.getDefault() );

	/*  */
	public String patient_id;
	public String mv;
	public String pulse;
	public String oxygen;
	public String diastolicbp;
	public String systolicbp;
	public String map2;
	public String timestamp;
	public String session_id;

	public DataRow( int id, int mv, int pulse, int o2, int dbp, int sbp, int bpmap, Date timestamp, int sessionID )
	{
		this.patient_id = String.valueOf( id );
		this.mv = String.valueOf( mv );
		this.pulse = String.valueOf( pulse );
		this.oxygen = String.valueOf( o2 );
		this.diastolicbp = String.valueOf( dbp );
		this.systolicbp = String.valueOf( sbp );
		this.map2 = String.valueOf( bpmap );
		this.timestamp = sdf.format( timestamp );
		this.session_id = String.valueOf( sessionID );
	}

	public DataRow()
	{
		this.patient_id = "1";
		this.mv = "0";
		this.pulse = "0";
		this.oxygen = "0";
		this.diastolicbp = "0";
		this.systolicbp = "0";
		this.map2 = "0";
		this.session_id = "1";
	}

	public static byte[] directToByte( DataRow row )
	{
		Gson gson = new Gson();
		return Encrypter.encryptToByteArray( gson.toJson( row ) );
	}

	public static String getJSON( DataRow row )
	{
		Gson gson = new Gson();
		return Encrypter.encryptToString( gson.toJson( row ) );
	}
}
