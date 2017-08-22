package net;

import java.io.ByteArrayOutputStream;

public class SimpleByteArrayOutputStream extends ByteArrayOutputStream
    {
    /**
     * So I don't have to create a clone when I want to get the array stored
     * by buf.  Only good because I use ByteArrayOutputStream in this case to
     * convert an Object into an array of bytes and then I'm done with the class.
     */
    @Override
    public byte[] toByteArray()
        {
        return buf;
        }
    }
