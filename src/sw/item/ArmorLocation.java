package sw.item;

public enum ArmorLocation
{
    LEGS ("LEGS"), 
    BODY ("BODY"), 
    HEAD ("HEAD"), 
    HANDS ("HANDS"), 
    FEET ("FEET");
    
    public final String type;
    
    ArmorLocation(String t)
    {
        type = t;
    }
}
