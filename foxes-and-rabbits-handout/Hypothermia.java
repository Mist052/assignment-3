
import java.util.Random;
/**
 * The Hypothermia class represents a disease that an animal
 * may get infected by due to the cold conditions.
 * It causes the animal's movement to stop and
 * has a maximum duration of steps.
 * Each step there is a chance for the animal to recover,
 * but it dies if maximum number of steps are reached.
 * The host can also transmit the infection to offspring
 * when breeding or to predator when it is eaten.
 * 
 * @author Ahmed Imtiaz
 * @version 16.02.2025
 */
public class Hypothermia {
    private int stepsInfected;
    private static final int MAX_STEPS = 20; // The max number of steps until the animal dies.
    private static final double RECOVERY_PROBABILITY = 0.1; // 10% chance per step to recover.

    /**
     * The contructor for the Hypothermia class. 
     */
    public Hypothermia() {
        stepsInfected = 0;
    }

    /**
     * This method progresses the hypothermia one step.
     * If it has lasted too long, the host dies.
     * Otherwise, there is a chance for recovery.
     * 
     * @param  host  The animal that is infected by hypothermia.
     */
    public void update(Animal host) {
        // Increment the duration of hypothermia.
        stepsInfected++;
        if (stepsInfected >= MAX_STEPS) {
            // The host dies when maximum steps are reached.
            host.setDead();
        } else {
            // Chance to recover.
            Random rand = Randomizer.getRandom();
            if (rand.nextDouble() < RECOVERY_PROBABILITY) {
                host.cureDisease();
            }
        }
    }
}
