package database;

/* 
 Writes an object to a file so that it can then be read and stored in
 MS's SQL Server DB.

 Author: Dudley Girard
 Started: 2-12-2001
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;

public class FMSObjCon
{

    public FMSObjCon()
    {
    }

    public static void addObjectToStatement(int index, Object obj, PreparedStatement ps) throws IOException, SQLException
    {
        // Because MS SQL doesn't support Java Objects we have to write the
        // object to a
        // binary stream, then read it from the binary stream.

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
        objectOut.writeObject(obj);
        objectOut.flush();
        objectOut.close();
        bytesOut.flush();
        bytesOut.close();

        byte[] buf = bytesOut.toByteArray();

        ps.setBytes(index, buf);
    }

    public static Object convertBytesToObject(byte[] buf)
    {
        Object data = null;
        try
        {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
            ObjectInputStream objectIn = new ObjectInputStream(bytesIn);
            data = objectIn.readObject();
            objectIn.close();
            bytesIn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }
}
