package ca.ualberta.medroad.model.raw_table_rows;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.ualberta.medroad.auxiliary.Encrypter;
import ca.ualberta.medroad.model.Patient;

/**
 * Created by Yuey on 2015-03-12.
 */
public class PatientRow
{
	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd",
			Locale.getDefault() );

	public String  id;
	public String  ahcn;
	public String  dob;
	public boolean live;
	public String  physician;
	public String  name;

	public PatientRow( String id, String ahcn, Date dob, boolean live, String physician, String name )
	{
		this.id = id;
		this.ahcn = ahcn;
		this.dob = sdf.format( dob );
		this.live = true;
		this.physician = physician;
		this.name = name;
	}

	public PatientRow( Patient patient )
	{
		this( patient.getId(),
			  patient.getAhcn(),
			  patient.getDob().getTime(),
			  true,
			  patient.getPhysician(),
			  patient.getName() );
	}

	public byte[] directToByte( PatientRow row )
	{
		Gson gson = new Gson();
		String jsonPayload = gson.toJson( row );
		return Encrypter.encryptToByteArray( jsonPayload );
	}
}
