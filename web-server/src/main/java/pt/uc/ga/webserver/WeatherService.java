package pt.uc.ga.webserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}";

    private final RestTemplate restTemplate;
    private final String apiKey;

    public WeatherService(@Value("${weather.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    public WeatherResponse getWeather(String city) {
        WeatherResponse response = restTemplate.getForObject(WEATHER_API_URL, WeatherResponse.class, city, apiKey);

        // Convert temperatures from Kelvin to Celsius and round to integer
        int tempInCelsius = Math.round((float) (response.getMain().getTemp() - 273.15));
        int minTempInCelsius = Math.round((float) (response.getMain().getTemp_min() - 273.15));
        int maxTempInCelsius = Math.round((float) (response.getMain().getTemp_max() - 273.15));

        response.getMain().setTemp(tempInCelsius);
        response.getMain().setTemp_min(minTempInCelsius);
        response.getMain().setTemp_max(maxTempInCelsius);


        return response;
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

        // Convert temperatures from Kelvin to Celsius and round to integer
        int tempInCelsius = Math.round((float) (response.getMain().getTemp() - 273.15));
        int minTempInCelsius = Math.round((float) (response.getMain().getTemp_min() - 273.15));
        int maxTempInCelsius = Math.round((float) (response.getMain().getTemp_max() - 273.15));

        response.getMain().setTemp(tempInCelsius);
        response.getMain().setTemp_min(minTempInCelsius);
        response.getMain().setTemp_max(maxTempInCelsius);


        return response;
    }

    public String getWeatherIcon(String weatherMain) {
        switch (weatherMain.toLowerCase()) {
            case "clear":
                return "☀️";
            case "clouds":
                return "☁️";
            case "rain":
                return "🌧️";
            case "snow":
                return "❄️";
            default:
                return "";
        }
    }
}