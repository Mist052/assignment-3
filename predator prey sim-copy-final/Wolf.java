import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat squirrels, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single squirrel. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int SQUIRREL_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The likelihood of getting infected by hypothermia.
    private static final double INFECTION_PROBABILITY = 0.1;
    
    // Individual characteristics (instance fields).

    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating squirrels.
    private int foodLevel;
    // The wolf's gender (true for male, false for female).
    private final boolean isMale;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(SQUIRREL_FOOD_VALUE);
        isMale = rand.nextBoolean();
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * squirrels. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        updateHypothermia();
        
        // If the fishal had died no further actions occur.
        if (!isAlive()) {
            return;
        }
        // If the fishal is infected it remains in current location.
        if(isInfected()) {
            incrementAge();
            incrementHunger();
            if(isAlive()) {
                nextFieldState.placeAnimal(this, getLocation());
            }
            return;
        }
        // If it's sleep time, the fishal does not move.
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
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }



    @Override
    public String toString() {
        return "Wolf{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                ", isMale=" + isMale +
                '}';
    }

    /**
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for squirrels adjacent to the current location.
     * Only the first live squirrel is eaten.
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
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Squirrel squirrel) {
                if(squirrel.isAlive()) {
                    squirrel.setDead();
                    foodLevel = SQUIRREL_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // Check if there is a male and female pair in neighboring cells
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (canBreed() && hasMate(currentField)) {
            int births = breed();
            if(births > 0) {
                for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                    Location loc = freeLocations.remove(0);
                    Wolf young = new Wolf(false, loc);
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
            if (animal instanceof Wolf wolf) {
                if (wolf.isMale != this.isMale) {
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
     * A wolf can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Get the gender of the wolf.
     * @return true if the seal is male and false if female.
     */
    public boolean isMale()
    {
        return isMale;
    }
}
