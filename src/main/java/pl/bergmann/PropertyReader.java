package pl.bergmann;


import java.io.FileReader;
import java.util.Properties;

public  class PropertyReader
{
    public static String getValueInPropertyFileByKey(String key)
    {
        String value = "";

        try(FileReader reader = new FileReader("config.properties"))
        {
            Properties properties  = new Properties();
            properties.load(reader);
            value = properties.getProperty(key);
        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
        return value;
    }
}

