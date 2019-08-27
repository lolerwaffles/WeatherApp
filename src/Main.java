package weatherApp;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.json.JSONArray;
import org.json.JSONObject;


public class Main {
		
	public static String[] GetZip()
		throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader("weather.conf"));
		    String zipLine = reader.readLine();		    
			String zip =  zipLine.replace("zip=", "");
		    String keyLine = reader.readLine();		    
		    String key = keyLine.replace("key=", "");
			reader.close();
			String[] conf = new String[2];
			conf[0] = zip;
			conf[1] = key;

			return conf;
	}
	
	
	public static String GetWeather() {
		
	  String OutputLine = null;
	  try {
		String[] conf = GetZip();
		URL url = new URL("http://api.openweathermap.org/data/2.5/weather?zip=" + conf[0] +",us&appid=" + conf[1]);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		OutputLine = br.readLine();
		

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	return OutputLine;

	}
	public static String KtoF(double kTemp) {
		double fTemp = (kTemp - 273.15) * 9/5 + 32;		
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		return numberFormat.format(fTemp);
		
	}
	
	public static Component DisplayWeather()
			throws InterruptedException {		

    
		JSONObject jo = new JSONObject(GetWeather());
		JSONObject weather = new JSONObject(((JSONArray) jo.get("weather")).get(0).toString());
		JSONObject weatherMain = new JSONObject((jo.get("main")).toString());
		JSONObject weatherWind = new JSONObject((jo.get("wind")).toString());

		
		String locationName = (String) jo.get("name");
		
		String currentTemp = KtoF((double) weatherMain.get("temp"));
		String humidity = weatherMain.get("humidity").toString();
		String pressure = weatherMain.get("pressure").toString();
		
		String currentConditions = (String) weather.get("description");
		
		String windSpeed = (String) weatherWind.get("speed").toString();
		String windDirection = (String) weatherWind.get("deg").toString();


	    Component textArea = new JTextArea("Location:" + locationName + "\n" + 
	    		"Current Conditions:" + currentConditions + "\n" +
	    		"Current Temp:" + currentTemp + "\n" +
	    		"Humidity:" + humidity + "\n" +
	    		"Pressure:" + pressure + "\n" +
	    		"Wind Speed:" + windSpeed + "\n" +
	    		"Wind Direction:" + windDirection
	    		);
    	    
	    ((JTextComponent) textArea).setEditable(false);
	    ((JTextComponent) textArea).setFont(new Font("Monaco", Font.PLAIN, 20));
		return textArea;

	}
	
	public static void main(String[] args) {
		
        JFrame frame = new JFrame("weatherApp");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(300,300);
	    
		while (true) {
			try {
			    frame.getContentPane().add(DisplayWeather());
			    frame.setVisible(true);
			    TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}