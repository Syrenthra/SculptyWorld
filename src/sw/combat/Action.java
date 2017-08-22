package sw.combat;

public abstract class Action
{
	ActionType a_type;
	public Action(ActionType type)
	{
		a_type = type;
	}
	
	public ActionType getActionType()
	{
		return a_type;
	}
	
	public abstract void apply();
}
