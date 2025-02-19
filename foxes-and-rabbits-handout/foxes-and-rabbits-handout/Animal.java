
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling, Ahmed Imtiaz
 * @version 7.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    
    private Hypothermia hypothermia;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
    }

    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * @return true if the animal is currently infected.
     */
    public boolean isInfected() {
        return hypothermia != null;
    }

    /**
     * Infect the animal with the disease.
     */
    public void infect() {
        if (hypothermia == null) {
            hypothermia = new Hypothermia();
        }
    }

    /**
     * Remove the disease from the animal (i.e. recovery).
     */
    public void cureDisease() {
        hypothermia = null;
    }

    /**
     * If the animal is infected, update the disease state.
     */
    public void updateHypothermia() {
        if (hypothermia != null) {
            hypothermia.update(this);
        }
    }

}
