package ca.ualberta.medroad.model.raw_table_rows;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.ualberta.medroad.auxiliary.Encrypter;

/**
 * Created by Yuey on 2015-03-12.
 */
public class DataRow
{
	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss",
			Locale.getDefault() );

	public String id;
	public int    mv;
	public int    pulse;
	public int    o2;
	public int    dbp;
	public int    sbp;
	public int    bpmap;
	public String timestamp;
	public String sessionID;

	public DataRow( String id, int mv, int pulse, int o2, int dbp, int sbp, int bpmap, Date timestamp, String sessionID )
	{
		this.id = id;
		this.mv = mv;
		this.pulse = pulse;
		this.o2 = o2;
		this.dbp = dbp;
		this.sbp = sbp;
		this.bpmap = bpmap;
		this.timestamp = sdf.format( timestamp );
		this.sessionID = sessionID;
	}

	public static byte[] directToByte( DataRow row )
	{
		Gson gson = new Gson();
		String jsonPayload = gson.toJson( row );
		return Encrypter.encryptToByteArray( jsonPayload );
	}
}
