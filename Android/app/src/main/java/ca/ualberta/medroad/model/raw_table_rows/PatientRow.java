package ca.ualberta.medroad.model.raw_table_rows;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.ualberta.medroad.auxiliary.Encrypter;
import ca.ualberta.medroad.model.Patient;

/**
 * Created by Yuey on 2015-03-12.
 * <p/>
 * Class representing a raw database latestRow for patients.
 */
public class PatientRow
{
	public static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd",
																	 Locale.getDefault() );

	public String patient_id;
	public String ahcn;
	public String dob;
	public String liveStatus;
	public String doctor;
	public String name;

	public PatientRow( int id, String ahcn, Date dob, boolean live, String physician, String name )
	{
		this.patient_id = String.valueOf( id );
		this.ahcn = ahcn;
		this.dob = sdf.format( dob );
		//this.liveStatus = String.valueOf( live );
		this.doctor = physician;
		this.name = name;

		if ( live )
		{
			liveStatus = "y";
		}
		else
		{
			liveStatus = "n";
		}
	}

	public PatientRow( Patient patient, boolean live )
	{
		this( patient.getId(),
			  patient.getAhcn(),
			  patient.getDob().getTime(),
			  live,
			  patient.getDoctor(),
			  patient.getName() );
	}

	public static String getJSON( PatientRow row )
	{
		Gson gson = new Gson();
		return Encrypter.encryptToString( gson.toJson( row ) );
	}

	public static byte[] directToByte( PatientRow row )
	{
		Gson gson = new Gson();
		String jsonPayload = gson.toJson( row );
		return Encrypter.encryptToByteArray( jsonPayload );
	}
}
