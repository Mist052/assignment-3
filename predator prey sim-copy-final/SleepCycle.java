
/**
 * The SleepCycle class allows the Animals to have a sleep schedule.
 * From here you are able to control the sleep cycle of all the
 * animals in the simulation.
 *
 * @author Ahmed Imtiaz
 * @version 16.02.2025
 */
public class SleepCycle
{
    // The hour at which the animals fall asleep.
    private final int sleepHour;

    // The hour at which the animals wake up.
    private final int wakeUpHour;

    /**
     * Constructor for SleepCycle.
     * Initialises a specific sleep schedule for
     * all the animals.
     */
    public SleepCycle()
    {
        // Sleep at 22:00.
        this.sleepHour = 22;

        // Wake up at 5:00
        this.wakeUpHour = 5;
    }

    /**
     * Constructor with a parameter.
     * Allows the user to customise the sleep schedule.
     * 
     * @param  sleepHour  The hour at which the animals fall asleep.
     *                    24-hour format.
     * @param  wakeUpHour  The hour at which the animals wake up.
     *                     24-hour format.
     */
    public SleepCycle(int sleepHour, int wakeUpHour)
    {
        this.sleepHour = sleepHour;
        this.wakeUpHour = wakeUpHour;
    }

    /**
     * This method finds out if the current time is
     * in the sleep cycle.
     *
     * @param  time  An object representing the current time.
     * @return  True if the current time is within the sleep cycle,
     *          false otherwise.
     */
    public boolean isSleepTime(Time time)
    {
        // Find out the current time in the simulation.
        int currentHour = time.getHour();

        // Check if the current time is in between the time to sleep
        // and the time to wake up.
        return (currentHour >= sleepHour) || (currentHour < wakeUpHour);

    }

    /**
     * This method is used to find out the current sleep
     * cycle in the simulation.
     * It will return the sleep cycle as a string in the
     * 24-hour clock format.
     * 
     * @return  A string of the sleep cycle in the 24-hour format.
     */
    public String getSleepCycle()
    {
        // A condition to check if the hour is less than 10.
        // If true, the string begins with "0" followed by the hour
        // otherwise, if false, string does not start with "0".
        String sleep = (sleepHour < 10 ? "0" : "") + sleepHour + ":00";
        String wake = (wakeUpHour < 10 ? "0" : "") + wakeUpHour + ":00";
        return sleep + " - " + wake; 
    }
}
