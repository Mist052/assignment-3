/**
 * A simple model of moss.
 * Moss grows at a fixed rate and can be eaten by fishals and squirrels.
 *
 * @author Bishal
 * @version 1.0
 */
public class Moss extends Plant
{
    // The maximum size of the moss.
    private static final double MAX_SIZE = 10.0;
    // The growth rate of the moss.
    private static final double GROWTH_RATE = 2.0;

    /**
     * Create a new moss at the given location.
     * @param location The location of the moss.
     */
    public Moss(Location location)
    {
        super(location, GROWTH_RATE);
    }
    
    /**
     * Get the maximum size of the moss.
     * @return The maximum size of the moss.
     */
    @Override
    public double getMaxSize()
    {
        return MAX_SIZE;
    }
    
    /**
     * Check if the moss is fully grown.
     * @return true if the moss is fully grown.
     */
    @Override
    public boolean isFullyGrown()
    {
        return getSize() >= MAX_SIZE;
    }
}