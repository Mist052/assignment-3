/**
 * Common elements of plants.
 * Plants grow at a fixed rate and cannot move from their position.
 *
 * @author Your Name
 * @version 1.0
 */
public abstract class Plant
{
    // The plant's position.
    private Location location;
    // The growth rate of the plant.
    private final double growthRate;
    // The current size of the plant.
    private double size;

    /**
     * Constructor for objects of class Plant.
     * @param location The plant's location.
     * @param growthRate The rate at which the plant grows.
     */
    public Plant(Location location, double growthRate)
    {
        this.location = location;
        this.growthRate = growthRate;
        this.size = 0.0;
    }

    /**
     * Grow the plant by its growth rate.
     */
    public void grow()
    {
        size += growthRate;
        if (size > getMaxSize()) {
            size = getMaxSize();
        }
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Return the current size of the plant.
     * @return The size of the plant.
     */
    public double getSize()
    {
        return size;
    }

    /**
     * Reduce the size of the plant when it is eaten.
     * @param amount The amount to reduce the size by.
     */
    public void reduceSize(double amount)
    {
        size = Math.max(0, size - amount);
    }

    /**
     * Get the maximum size of the plant.
     * @return The maximum size of the plant.
     */
    public abstract double getMaxSize();

    /**
     * Check if the plant is fully grown.
     * @return true if the plant is fully grown.
     */
    public abstract boolean isFullyGrown();
}