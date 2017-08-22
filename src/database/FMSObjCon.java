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
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FMSObjCon
    {

    public FMSObjCon()
        {
        }

    public static ByteArrayInputStream addObjectToStatement(int index, Object obj, PreparedStatement ps) throws IOException, SQLException
        {
        ByteArrayInputStream bytesIn = null;

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

        bytesIn = new ByteArrayInputStream(buf);
        ps.setBinaryStream(index, bytesIn, buf.length);

        return bytesIn;
        }
    }
