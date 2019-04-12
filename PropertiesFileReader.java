package paymentMonitor;

import java.util.Properties;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;

public class PropertiesFileReader {
    private static Properties props;
    
    static {
        initialize();
    }
    private PropertiesFileReader() {}    
    private static void initialize(){
        try {           
            props = new Properties();
           URL configFileURL = PropertiesFileReader.class.getResource("notification.config");
           System.out.println(configFileURL);
            String filePath = URLDecoder.decode(configFileURL.getPath(), "UTF-8");
            FileInputStream configFile = new FileInputStream(filePath);
            props.load(configFile);
            configFile.close();
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Could not read the configuration from the properties file.");
        }        
    }
    public static Properties getProperties(){
        if(props==null) initialize();
        return props;
    }
}
