package sw.lifeform;


public interface PCObserver
{
    /**
     * Informs what the PC just did.
     * @param spawn
     */
    public void pcUpdate(PCEvent event);
    

}
