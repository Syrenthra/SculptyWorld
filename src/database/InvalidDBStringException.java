package database;

public class InvalidDBStringException extends Exception
    {
    
    public InvalidDBStringException(String invalidString)
        {
        super("InvalidDBString - "+invalidString);
        }

    }
