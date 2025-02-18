import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * squirrels, wolves, fishals, bears and seals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.02;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.01;
    // The probability that a squirrel will be created in any given position.
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.08;
    // The probability that a fishal will be created in any given position.
    private static final double FISHAL_CREATION_PROBABILITY = 0.05;
    // The probability that a seal will be created in any given position.
    private static final double SEAL_CREATION_PROBABILITY = 0.07;
    // The probability that a moss will be created in any given position.
    private static final double MOSS_CREATION_PROBABILITY = 0.5;


    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // Time field to keep track of the time
    public static Time time;
    // Shows the current weather in the simulation.
    public static Weather weather;
    // Sleep cycle of the animals
    public static SleepCycle sleepCycle;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        time = new Time();
        weather = new Weather();
        sleepCycle = new SleepCycle();

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal.
     */
    public void simulateOneStep()
    {
        step++;
        // Every step in the simulation equals to one hour.
        time.incrementHour();
        // Update the weather every step.
        weather.update();
        
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());
        
        // Transfer plants (including Moss) to the next state
        for (Plant plant : field.getPlants()) {
            nextFieldState.placePlant(plant, plant.getLocation());
        }
        
        // Grow plants
        for (Plant plant : field.getPlants()) {
            plant.grow();
        }

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
        
        // Update the GUI with current time.
        view.updateTime(time.getTime());
        //Update the GUI with current weather.
        view.updateWeather(weather.getWeatherDescription());
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        time.reset();
        weather = new Weather();
        sleepCycle = new SleepCycle();
        populate();
        view.showStatus(step, field);
        view.updateTime(time.getTime());
        view.updateWeather(weather.getWeatherDescription());
    }
    
    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, location);
                    field.placeAnimal(wolf, location);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, location);
                    field.placeAnimal(bear, location);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, location);
                    field.placeAnimal(squirrel, location);
                }
                else if(rand.nextDouble() <= FISHAL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fishal fishal = new Fishal(true, location);
                    field.placeAnimal(fishal, location);
                }
                else if(rand.nextDouble() <= SEAL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seal seal = new Seal(true, location);
                    field.placeAnimal(seal, location);
                }
                else if(rand.nextDouble() <= MOSS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Moss moss = new Moss(location);
                    field.placePlant(moss, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
