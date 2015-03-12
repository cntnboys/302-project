package ca.ualberta.medroad.model.raw_table_rows;

import com.google.gson.Gson;

import java.util.Date;

import ca.ualberta.medroad.auxiliary.Encrypter;
import ca.ualberta.medroad.model.Patient;

/**
 * Created by Yuey on 2015-03-12.
 */
public class PatientRow
{
	public String  id;
	public String  ahcn;
	public Date    dob;
	public boolean live;
	public String  physician;
	public String  name;

	public PatientRow( String id, String ahcn, Date dob, String physician, String name )
	{
		this.id = id;
		this.ahcn = ahcn;
		this.dob = dob;
		this.live = true;
		this.physician = physician;
		this.name = name;
	}

	public PatientRow( Patient patient )
	{
		this( patient.getId(),
			  patient.getAhcn(),
			  patient.getDob().getTime(),
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
