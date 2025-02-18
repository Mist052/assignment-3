import java.util.List;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling and Ahmed Imtiaz.
 * @version 7.1
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).
    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The likelihood of getting infected by hypothermia.
    private static final double INFECTION_PROBABILITY = 0.002;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The rabbit's age.
    private int age;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

    /**
     * This is what the rabbit does most of the time - it runs 
     * around and sleeps. Sometimes it will breed, get infected,
     * die of old age, or die due to hypothermia.
     * 
     * @param  currentField  The field occupied.
     * @param  nextFieldState  The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        // Update the infection after every action.
        updateHypothermia();
        // If the rabbit had died no further actions occur.
        if(!isAlive()) {
            return;
        }
        // Checks if the current time is sleeping time.
        // Rabbit does not move if true.
        if(Simulator.sleepCycle.isSleepTime(Simulator.time)) {
            incrementAge();
            nextFieldState.placeAnimal(this, getLocation()); // Place the rabbit in the same location
            return;                                          //  next state hence no movement
        }
        // If rabbit is not infected there is a chance it gets infected.
        if (!isInfected() && Randomizer.getRandom().nextDouble() < INFECTION_PROBABILITY) {
            infect();
        }
        // If the rabbit is infected it remains in current location
        //but can still breed and age.
        if(isInfected()) {
            incrementAge();
            if(isAlive()) {
                List<Location> freeLocations = currentField.getFreeAdjacentLocations(getLocation());
                if(!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }

                nextFieldState.placeAnimal(this, getLocation());
            }
            return;
        }

        // Checks the weather to see if a blizzard is occuring and if movements slows down.
        if (Simulator.weather.getCurrentWeather() == Weather.WeatherType.BLIZZARD
        && Simulator.weather.skipMoveChance()) {
            incrementAge();
            if (isAlive()) {
                List<Location> freeLocations = currentField.getFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }
                nextFieldState.placeAnimal(this, getLocation());
            }
            return;
        }
        incrementAge();
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
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
        return "Rabbit{" +
        "age=" + age +
        ", alive=" + isAlive() +
        ", location=" + getLocation() +
        (isInfected() ? ", infected" : "") +
        '}';
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Rabbit young = new Rabbit(false, loc);
                if (this.isInfected()) {
                    young.infect();
                }
                nextFieldState.placeAnimal(young, loc);
            }
        }
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
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
