import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Squirrel.
 * Squirrel age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Squirrel extends Animal
{
    // Characteristics shared by all squirrels (class variables).
    // The age at which a squirrel can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a squirrel can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a squirrel breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single moss. In effect, this is the
    // number of steps a squirrel can go before it has to eat again.
    private static final double MOSS_FOOD_VALUE = 20.0;
    // Initial food level
    private static final double FOOD_LEVEL = 100.0;
    // The likelihood of getting infected by hypothermia.
    private static final double INFECTION_PROBABILITY = 0.1;
    
    // Individual characteristics (instance fields).
    
    // The squirrel's age.
    private int age;
    // The squirrel's food level, which is increased by eating moss.
    private double foodLevel;
    // The squirrel's gender (true for male, false for female).
    private final boolean isMale;
    
    /**
     * Create a new squirrel. A squirrel may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the squirrel will have a random age.
     * @param location The location within the field.
     */
    public Squirrel(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = FOOD_LEVEL;
        isMale = rand.nextBoolean();
    }
    
    /**
     * This is what the squirrel does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        decrementFoodLevel();
        updateHypothermia();
        
        // If the squirrel had died no further actions occur.
        if (!isAlive()) {
            return;
        }
        // If the squirrel is infected it remains in current location.
        if(isInfected()) {
            incrementAge();
            decrementFoodLevel();
            if(isAlive()) {
                nextFieldState.placeAnimal(this, getLocation());
            }
            return;
        }
        // If squirrel is not infected there is a chance it gets infected.
        if (!isInfected() && Randomizer.getRandom().nextDouble() < INFECTION_PROBABILITY) {
            infect();
        }
        // If it's sleep time, the squirrel does not move.
        if(Simulator.sleepCycle.isSleepTime(Simulator.time)) {
            nextFieldState.placeAnimal(this, getLocation());
            return;
        }
        // Checks the weather to see if a blizzard is occuring and if so then movements slows down.
        if (Simulator.weather.getCurrentWeather() == Weather.WeatherType.BLIZZARD
        && Simulator.weather.skipMoveChance()) {
            incrementAge();
            if (isAlive()) {
                List<Location> freeLocations = currentField.getFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    giveBirth(currentField, nextFieldState, freeLocations);
                }
                nextFieldState.placeAnimal(this, getLocation());
            }
            return;
        }
        
        if(isAlive()) {
            Location nextLocation = findFood(currentField);
            if (nextLocation == null) {
                // No food found - try to move to a free location.
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    nextLocation = freeLocations.remove(0);
                }
            }
            // See if it was possible to move.
            if (nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            } else {
                // Overcrowding.
                setDead();
            }
            giveBirth(currentField, nextFieldState, nextFieldState.getFreeAdjacentLocations(getLocation()));
        }
    }

    @Override
    public String toString() {
        return "Squirrel{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                ", isMale=" + isMale +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the squirrel's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Decrease the food level.
     * This could result in the fishal's death.
     */
    private void decrementFoodLevel()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for moss adjacent to the current location.
     * Only the first live moss is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Object object = field.getObjectAt(loc);
            if(object instanceof Moss) {
                Moss moss = (Moss) object;
                if(moss.getSize() > 0) {
                    moss.reduceSize(MOSS_FOOD_VALUE);
                    foodLevel += MOSS_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether or not this squirrel is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // Check if there is a male and female pair in neighboring cells
        // New squirrels are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (canBreed() && hasMate(currentField)) {
            int births = breed();
            if(births > 0) {
                for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                    Location loc = freeLocations.remove(0);
                    Squirrel young = new Squirrel(false, loc);
                    if (this.isInfected()) {
                        young.infect();
                    }
                    nextFieldState.placeAnimal(young, loc);
                }
            }
        }
    }
    
    /**
     * Check if there is a mate (opposite gender) in neighboring cells.
     * @param field The field to check for mates.
     * @return true if a mate is found, false otherwise.
     */
    private boolean hasMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        for (Location loc : adjacent) {
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Squirrel squirrel) {
                if (squirrel.isMale != this.isMale) {
                    return true;
                }
            }
        }
        return false;
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A squirrel can breed if it has reached the breeding age.
     * @return true if the squirrel can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Get the gender of the squirrel.
     * @return true if the fishal is male, false if female.
     */
    public boolean isMale()
    {
        return isMale;
    }
}
