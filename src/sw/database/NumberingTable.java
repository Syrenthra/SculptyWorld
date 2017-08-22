package sw.database;

/**
 * Used as a central reference point for the NumberingTable in the database.  This
 * table provides the next unique index value for tables that use a pseudo-primary key.
 * We use this so we know what number was just assigned.
 * @author cdgira
 *
 */
public class NumberingTable
{

    public static final String NAME = "NUMBERING_TABLE";
    
    public static final String TABLE_NAME = "TABLE_NAME";
    
    public static final String NEXT_NUMBER = "NEXT_NUMBER";
}
