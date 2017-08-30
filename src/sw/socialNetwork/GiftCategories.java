package sw.socialNetwork;
/**
 * 
 * @author Kalan Kriner
 * 
 * This class is the holder of the Gift Categories that are used by NPCs and Items
 * the categories are simply integers starting at 0, with an upper limit set by 
 * the CategoryNum variable
 */
public class GiftCategories 
{
	private int CategoryNum=3;
	
	/**
	 * These categories can be changed at any time, these are placeholder for now.
	 * Categories are:
	 * 0-House Items
	 * 1-Adventuring Gear
	 * 2-Nature Items
	 */
	public int getCategoryNum()
	{
		return CategoryNum;
	}
}
