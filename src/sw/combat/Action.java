package sw.combat;

public class Action
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
}
