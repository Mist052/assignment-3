
/**
 * The time class controls and keeps track of the
 * time in the simulation.
 * It stores the time in a 24-hour clock format.
 *
 * @author Ahmed Imtiaz
 * @version 17.02.2025
 */
public class Time
{
    // Instance variables to store the hours and minutes
    private int hour;
    private int minute;
    // Instance variables to store the constructor values
    private final int chosenHour;
    private final int chosenMinute;

    /**
     * Constructor for Time class.
     * Controls the time at which the simulation starts.
     */
    public Time()
    {
        // Sets the start time to 6:00.
        this.hour = 6;
        this.minute = 0;
        // Stores these chosen values.
        this.chosenHour = hour;
        this.chosenMinute = minute;
    }

    /**
     * Contructor for Time class with parameters.
     * Allows user to set the time at which
     * the simulation starts.
     * 
     * @param  hour  The starting hour of the simulation.
     *               In 24-hour format.
     * @param  minute  The starting minute of the simulation.
     */
    public Time(int hour, int minute)
    {
        // Sets a limit so that only numbers between 0 and 23
        // can be chosen as starting hours.
        if (hour < 0 || hour > 23) {
            System.out.println("Hour must be between 0 and 23");
        }
        // Sets a limit so that only numbers between 0 and 59
        // can be chosen as starting minutes.
        if (minute < 0 || minute > 59) {
            System.out.println("Minute must be between 0 and 59");
        }
        this.hour = hour;
        this.minute = minute;
        this.chosenHour = hour;
        this.chosenMinute = minute;
    }

    /**
     * A method to increment the minutes of the time
     * in the simulation.
     */
    public void incrementMinute()
    {
        minute++;
        // Resets minute to zero when it reaches 60
        // and increments the hour by one.
        if (minute >= 60) {
            minute = 0;
            hour++;
            if (hour>= 24) {        // Resets hour to zero when it reaches 24.
                hour = 0;
            }
        }
    }

    /**
     * A method to increment the hours of the time
     * in the simulation.
     */
    public void incrementHour()
    {
        hour++;
        // Resets hour to zero when it reaches 24
        // to create a functioning 24-hour clock.
        if (hour >=24) {
            hour = 0;
        }
    }

    /**
     * A method to reset the clock to the default starting
     * time set in the constructor.
     */
    public void reset()
    {
        hour = chosenHour;
        minute = chosenMinute;
    }

    /**
     * This method is used to find out the current time
     * in the simulation.
     * It will return the time as a string in the
     * 24-hour clock format.
     */
    public String getTime()
    {
        // A condition to check if the hour is less than 10.
        // If true, the string begins with "0" followed by the hour
        // otherwise, if false, string does not start with "0".
        String hourString = (hour < 10 ? "0" : "") + hour;
        String minuteString = (minute < 10 ? "0" : "") + minute;
        return hourString + ":" + minuteString;
    }

    /**
     * This method returns the current hour in the
     * simulation.
     * 
     * @return  The current hour as a value between 0 and 23.
     */
    public int getHour()
    {
        return hour;
    }
}
