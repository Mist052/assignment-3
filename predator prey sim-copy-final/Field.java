import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Animal> field = new HashMap<>();
    // The animals.
    private final List<Animal> animals = new ArrayList<>();
    // Plants mapped by location.
    private final Map<Location, Plant> plants = new HashMap<>();
    // The plants.
    private final List<Plant> plantList = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param the animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeAnimal(Animal anAnimal, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            animals.remove(other);
        }
        field.put(location, anAnimal);
        animals.add(anAnimal);
    }
    
    /**
     * Place a plant at the given location.
     * If there is already a plant at the location it will
     * be lost.
     * @param the plant to be placed.
     * @param location Where to place the plant.
     */
    public void placePlant(Plant aPlant, Location location)
    {
        assert location != null;
        plants.put(location, aPlant);
        plantList.add(aPlant);
    }
    
    /**
     * Return the object at the given location, if any.
     * @param location Where in the field.
     * @return The object at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        Animal animal = field.get(location);
        if(animal != null) {
            return animal;
        }
        return plants.get(location);
    }
    
    /**
     * Get a list of all plants in the field.
     * @return A list of all plants.
     */
    public List<Plant> getPlants()
    {
        return plantList;
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Animal anAnimal = field.get(next);
            if(anAnimal == null) {
                free.add(next);
            }
            else if(!anAnimal.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of each type of animal in the field.
     */
    public void fieldStats()
    {
        int numWolves = 0, numSquirrels = 0, numBears = 0, numFishals = 0, numSeals = 0;
        for(Animal anAnimal : field.values()) {
            if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    numWolves++;
                }
            }
            else if(anAnimal instanceof Bear bear) {
                if(bear.isAlive()) {
                    numBears++;
                }
            }
            else if(anAnimal instanceof Squirrel squirrel) {
                if(squirrel.isAlive()) {
                    numSquirrels++;
                }
            }
            else if(anAnimal instanceof Fishal fishal) {
                if(fishal.isAlive()) {
                    numFishals++;
                }
            }
            else if(anAnimal instanceof Seal seal) {
                if(seal.isAlive()) {
                    numSeals++;
                }
            }
        }
        System.out.println("Squirrels: " + numSquirrels +
                           " Wolves: " + numWolves +
                           " Bears: " + numBears +
                           " Fishals: " + numFishals +
                           " Seals: " + numSeals);

    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one squirrel, one wolf, one bear, one fishal and one seal in the field.
     * @return true if there is at least one squirrel, one wolf, one bear, one fishal and one seal in the field.
     */
    public boolean isViable()
    {
        boolean squirrelFound = false;
        boolean wolfFound = false;
        boolean bearFound = false;
        boolean fishalFound = false;
        boolean sealFound = false;
        Iterator<Animal> it = animals.iterator();
        while(it.hasNext() && ! (squirrelFound && wolfFound && bearFound && fishalFound && sealFound)) {
            Animal anAnimal = it.next();
            if(anAnimal instanceof Squirrel squirrel) {
                if(squirrel.isAlive()) {
                    squirrelFound = true;
                }
            }
            else if(anAnimal instanceof Bear bear) {
                if(bear.isAlive()) {
                    bearFound = true;
                }
            }
            else if(anAnimal instanceof Fishal fishal) {
                if(fishal.isAlive()) {
                    fishalFound = true;
                }
            }
            else if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    wolfFound = true;
                }
            }
            else if(anAnimal instanceof Seal seal) {
                if(seal.isAlive()) {
                    sealFound = true;
                }
            }
        }
        return squirrelFound && wolfFound && bearFound && fishalFound && sealFound;
    }
    
    /**
     * Get the list of animals.
     */
    public List<Animal> getAnimals()
    {
        return animals;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
