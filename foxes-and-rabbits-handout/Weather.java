
/**
 * The Weather class creates and controls the
 * weather conditions in the simulation.
 * The different weather types can have
 * certain effects on the animals in the
 * simulation.
 *
 * @author Ahmed Imtiaz
 * @version (13.02.2025)
 */
public class Weather
{
    public enum WeatherType  // Stores the different weather types
    {
        CLEAR, // Normal weather with no effects
        BLIZZARD // A storm that can effect the movements of the animals.
    }
    // The current weather conditions.
    private WeatherType currentWeather;
    // The number of steps the condition lasts for.
    private int weatherDuration;
    // The probability of a blizzard occuring.
    private static final double BLIZZARD_PROBABILITY = 0.05;
    // The minimum number of steps the blizzard occurs for.
    private static final int MIN_BLIZZARD_DURATION = 5;
    // The maximum number of steps the blizzard occurs for.
    private static final int MAX_BLIZZARD_DURATION = 10;
    // A chance to skip movement in a blizzard which overall makes animals slower.
    private static final double SKIP_MOVE_PROBABILITY = 0.3;

    /**
     * Constructor for the Weather class.
     * Sets the default weather to clear.
     */
    public Weather()
    {
        currentWeather = WeatherType.CLEAR;
        weatherDuration = 0;
    }

    /**
     * This method updates the weather conditions for
     * every step in the simulation.
     *
     * It decrements the steps of the weather conditions
     * and changes weather when the steps reach zero.
     * It uses probability to decide if the weather
     * condition occurs or not and for how many steps.
     */
    public void update()
    {
        // Decrement the duration of blizzard if active
        // and change the weather when steps reaach zero.
        if (currentWeather == WeatherType.BLIZZARD) {
            weatherDuration--;
            if (weatherDuration <= 0 ) {
                currentWeather = WeatherType.CLEAR;
            }
        } else {
            // If blizzard not active, use probability to decide if it occurs
            // and for how long.
            if (Randomizer.getRandom().nextDouble() < BLIZZARD_PROBABILITY) {
                currentWeather = WeatherType.BLIZZARD;
                weatherDuration = MIN_BLIZZARD_DURATION +
                Randomizer.getRandom().nextInt(MAX_BLIZZARD_DURATION - MIN_BLIZZARD_DURATION +1);
            }
        }
    }

    /**
     * This method decides if an animal skips its movement
     * on the current step.
     * This overall slows the animal down.
     * This is the effect of the blizzard and only
     * occurs when blizzard is active.
     * 
     * @return  True if the animal should skip its movement
     *          false otherwise
     */
    public boolean skipMoveChance()
    {
        if (currentWeather == WeatherType.BLIZZARD) {
            return Randomizer.getRandom().nextDouble() < SKIP_MOVE_PROBABILITY;
        }
        return false;
    }

    /**
     * This method returns the current weather condition in
     * the simulation as a string.
     * 
     * @return "Blizzard" if weather is a blizzard, otherwise "Clear sky".
     */
    public String getWeatherDescription() {
        if (currentWeather == WeatherType.BLIZZARD) {
            return "Blizzard";
        } else {
            return "Clear sky";
        }
    }

    /**
     * @return  The current WeatherType  
     */
    public WeatherType getCurrentWeather() {
        return currentWeather;  // BLIZZARD OR CLEAR
    }
}
