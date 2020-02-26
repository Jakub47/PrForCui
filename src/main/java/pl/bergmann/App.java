package pl.bergmann;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //Get Url from property file
        String url = PropertyReader.getValueInPropertyFileByKey("nbp.urlfirstPart");
        Scanner input = new Scanner(System.in);
        CustomTuple<String,String> userData = getDataFromUser(input);

        //Replace parameters with actual date
        url = url.replace("{startingData}",userData.getStartingDate())
                .replace("{endingDate}",userData.getEndingDate());
        String response = getResponseFromNbp(url);
        ExchangeData data = getDataFromResponseAndCalculateDiffrence(response);
        showResult(data);
    }

    /**
     * Method which takes start and end data from user in command-line
     * @param input Scanner used for gettin input
     * @return CustomTuple Pair containing start and end date
     */
    private static CustomTuple<String,String> getDataFromUser(Scanner input)
    {
        String startingDate = getDateFromUser("od",input);
        String endingDate;

        System.out.println("Czy sprawdzić kurs do dzisiaj y/n");

        Scanner in = new Scanner(System.in); //beacuse of bug it skips getting line from user need to create new scanner
        String decision = in.nextLine();

        //If user don't want to change date it means he wants to have today date else ask for date
        if(decision.equals("y"))
            endingDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        else
            endingDate = getDateFromUser("do",input);

        return new CustomTuple<String,String>(startingDate,endingDate);
    }

    /**
     * Get String from user containing year month and day
     * @param whenWord help word to show to user
     * @param input   Scanner used for gettin input
     * @return String in format year-month-day
     */
    private static String getDateFromUser(String whenWord,Scanner input)
    {
        System.out.println("Podaj dzień " + whenWord + " kiedy należy sprawdzić kurs");
        Integer dayNumber = input.nextInt();
        String day = checkDateFormat(dayNumber);

        System.out.println("Podaj miesiąc " + whenWord + " kiedy należy sprawdzić kurs");
        Integer monthNumber = input.nextInt();
        String month = checkDateFormat(monthNumber);

        System.out.println("Podaj rok " + whenWord + " kiedy należy sprawdzić kurs");
        Integer yearNumber = input.nextInt();
        String year = checkYearFormat(yearNumber);

        return year + "-" + month + "-" + day;
    }

    /**
     * Check wheter day or month below 10 cotain 0 at begining
     * @param number Number representing day or month
     * @return String with 0 at begining if number is below 10
     */
    private static String checkDateFormat(Integer number)
    {
        if(number < 10 && number>0)
            return "0" + String.valueOf(number);
        else
            return String.valueOf(number);
    }

    /**
     * Checks if provided year is in good format
     * @param number Year to check
     * @return String with correct year
     */
    private static String checkYearFormat(Integer number)
    {
        Integer currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));

        if(number > 1900 && number <= currentYear)
            return String.valueOf(number);
        else
            return String.valueOf(currentYear - 1);
    }

    /**
     * Get data from nbp containg all exchange rates
     * @param url Url from which data will be taken
     * @return String in json format with exchange rates
     */
    public static String getResponseFromNbp(String url)
    {
        String response = "";
        try {
            //Get data from url in and store that into String
            URLConnection connection = new URL(url).openConnection();
            try (Scanner scanner = new Scanner(connection.getInputStream());) {
                response = scanner.useDelimiter("\\A").next();
            }
        }
        catch (IOException e)
        {
            System.out.println("Przekroczony został limit proszę zmienić daty tak aby różnica była mniejsza niż 365 dni");
        }
        return response;
    }

    /**
     * From Nbp in json format forms data to class for easier formatting with counting diffrence
     * @param response Answer from Nbp in json format
     * @return ExchangeData which contains all data from Nbp
     */
    public static ExchangeData getDataFromResponseAndCalculateDiffrence(String response)
    {
        //Using Gson extract data from String in json format and convert that to ExchangeData class
        ExchangeData data = new Gson().fromJson(response, ExchangeData.class);
        data.rates.forEach(a -> a.diffrence = a.ask - a.bid);
        return data;
    }

    /**
     * Show results to user
     * @param data ExchangeData containing data to show
     */
    public static void showResult(ExchangeData data)
    {
        data.rates.forEach( a ->
                System.out.println("Dla dnia " + a.effectiveDate + ": kurs zakupu wynosi " +
                        a.bid + " | kurs sprzedaży wynosi " + a.ask + " | różnica wynosi " + a.diffrence
                )
        );
    }
}
