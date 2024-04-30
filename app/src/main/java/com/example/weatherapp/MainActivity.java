package com.example.weatherapp;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    ImageView timeIcon;
    TextView state, dataTextView, desc, tempC, city, humidity, pressure, feelsLike, max, min;
    LinearLayout mainTemp, swipeUp, Hero, mainStatsScreen, homeStatsScreen, containerLayout, love;
    private GestureDetector gestureDetector;
    boolean mainPageOpened = true;


    private static final String API_KEY = "HBNH7QAWHUEPF7PE93TEJT3Y8";
    private static final String CITY = "Kolkata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timeIcon = findViewById(R.id.timeIcon);
        state = findViewById(R.id.state);
        dataTextView = findViewById(R.id.data);
        desc = findViewById(R.id.desc);
        mainTemp = findViewById(R.id.mainTemp);
        swipeUp = findViewById(R.id.swipeUp);
        Hero = findViewById(R.id.Hero);
        mainStatsScreen = findViewById(R.id.mainStatsScreen);
        love = findViewById(R.id.love);
        tempC = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        feelsLike = findViewById(R.id.tempFeelsLike);
        max = findViewById(R.id.tempMax);
        min = findViewById(R.id.tempMin);
        city = findViewById(R.id.city);
        homeStatsScreen = findViewById(R.id.homeStatsScreen);
        containerLayout = findViewById(R.id.containerLayout);

        gestureDetector = new GestureDetector(this, this);

        fetchWeatherForecast();
        fetchCurrentWeather();
    }


    private void fetchWeatherForecast() {
        new Thread(() -> {
            try {
                URL url = new URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                        "Kolkata,%20WB,%20India" + "?unitGroup=uk&key=" + API_KEY + "&contentType=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Extract the forecast data
                    JSONArray days = jsonResponse.getJSONArray("days");

                    // Ensure that we have enough forecast data for the upcoming days
                    int numDays = Math.min(days.length(), 7); // Limit to the next 7 days
                    for (int i = 0; i < numDays; i++) {
                        JSONObject day = days.getJSONObject(i);
                        String date = day.getString("datetime");
                        double minTemperature = day.getDouble("temp");

                        // Get the day of the week for the forecasted date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date forecastDate = dateFormat.parse(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(forecastDate);
                        String dayOfWeek = new SimpleDateFormat("EEE").format(calendar.getTime());

                        // Update UI with forecasted weather data
                        int layoutId = getResources().getIdentifier("day" + (i + 1), "id", getPackageName());
                        TextView dayTextView = findViewById(layoutId);
                        if (dayTextView != null) {
                            runOnUiThread(() -> {
                                dayTextView.setText(dayOfWeek);
                            });
                        }

                        layoutId = getResources().getIdentifier("min" + (i + 1), "id", getPackageName());
                        TextView minTempTextView = findViewById(layoutId);
                        if (minTempTextView != null) {
                            runOnUiThread(() -> {
                                minTempTextView.setText(String.valueOf(minTemperature) + " °C");
                            });
                        }
                    }
                } else {
                    Log.e("WeatherApp", "Failed to fetch weather forecast data. HTTP error code: " + conn.getResponseCode());
                }

                conn.disconnect();

            } catch (IOException | JSONException | ParseException e) {
                e.printStackTrace();
                Log.e("WeatherApp", "Error: " + e.getMessage());
            }
        }).start();
    }



    private void fetchCurrentWeather() {
        new Thread(() -> {
            try {
                URL url = new URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                        "Kolkata,%20WB,%20India" + "?unitGroup=uk&key=" + API_KEY + "&contentType=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // Log the JSON response for debugging
                    Log.d("WeatherApp", "JSON Response: " + response.toString());

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Extract the current day's weather data
                    JSONArray days = jsonResponse.getJSONArray("days");
                    if (days.length() > 0) {
                        JSONObject today = days.getJSONObject(0);
                        double temperature = today.getDouble("temp");
                        double feelsLike = today.getDouble("feelslike");
                        double maxTemperature = today.getDouble("tempmax");
                        double minTemperature = today.getDouble("tempmin");
                        String conditions = today.getString("conditions");
                        int humidity = (int) today.getDouble("humidity");
                        double pressure = today.getDouble("pressure");

                        // Update UI with current weather data
                        runOnUiThread(() -> {
                            setStats(conditions, humidity, pressure, temperature, "Kolkata, WB, India", minTemperature, maxTemperature, feelsLike);
                        });
                    } else {
                        Log.e("WeatherApp", "No weather data available.");
                    }
                } else {
                    Log.e("WeatherApp", "Failed to fetch current weather data. HTTP error code: " + conn.getResponseCode());
                }

                conn.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("WeatherApp", "Error: " + e.getMessage());
            }
        }).start();
    }


    private void setStats(String description, int hum, double pre, double temp, String cityName, double minT, double maxT, double feelL) {
        int iconResId;

        // Determine weather icon based on description
//        description = "Partially cloudy";
        description = description.toLowerCase();
        switch (description) {
            case "few clouds":
            case "scattered clouds":
            case "broken clouds":
            case "partially cloudy":
                iconResId = R.drawable.cloudy;
                break;
            case "shower rain":
            case "rain":
                iconResId = R.drawable.rain;
                break;
            case "thunderstorm":
                iconResId = R.drawable.storm;
                break;
            case "snow":
                iconResId = R.drawable.sleet;
                break;
            case "mist":
            case "smoke":
            case "haze":
            case "dust":
            case "fog":
            case "sand":
            case "ash":
            case "squall":
            case "tornado":
                iconResId = R.drawable.fog;
                break;
            default:
                // Determine icon based on day/night time if weather condition not recognized
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                if (currentHour >= 6 && currentHour < 18) {
                    // It's day
                    iconResId = R.drawable.sun;
                } else {
                    // It's night
                    iconResId = R.drawable.moon;
                }
                break;
        }

        // Set weather icon
        timeIcon.setVisibility(View.VISIBLE);
        timeIcon.setImageResource(iconResId);
        timeIcon.setAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_up)); // Use appropriate animation

        // Update UI with weather details
        state.setText(firstCaps(description));
        dataTextView.setText(String.format("%.0f", temp));
        tempC.setText(String.format("%.0f°C", temp));
        city.setText(cityName);
        humidity.setText(String.format("H: %d%%", hum));
        pressure.setText(String.format("P: %.0f hPa", pre));
        max.setText(String.format("Max: %.0f°C", maxT));
        min.setText(String.format("Min: %.0f°C", minT));
        feelsLike.setText(String.format("Feels like %.0f°C", feelL));

        // Delay for displaying UI elements with animations
        int delayMillis = 1000;
        new Handler().postDelayed(() -> {
            mainTemp.setVisibility(View.VISIBLE);
            mainTemp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }, delayMillis);

        String finalDescription = description;
        new Handler().postDelayed(() -> {
            desc.setText("- It's " + firstCaps(finalDescription) + " -");
            desc.setVisibility(View.VISIBLE);
            desc.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }, delayMillis * 2);

        new Handler().postDelayed(() -> {
            swipeUp.setVisibility(View.VISIBLE);
            swipeUp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            swipeUp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_up)); // Use appropriate animation
        }, delayMillis * 2);
    }

    // Utility method to capitalize the first letter of each word
    public String firstCaps(String s) {
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Dispatch touch event to GestureDetector
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        if (mainPageOpened) {
            if (e1 != null && e2 != null && e1.getY() > e2.getY()) {
                // Swipe from bottom to top detected
                mainPageOpened = false;
                animateStatsScreenIn();
            }
        } else {
            if (e1 != null && e2 != null && e1.getY() < e2.getY()) {
                // Swipe from top to bottom detected
                mainPageOpened = true;
                animateStatsScreenOut();
            }
        }
        return false;
    }


    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    private void animateStatsScreenIn() {
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
//        homeStatsScreen.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            homeStatsScreen.setRenderEffect(RenderEffect.createBlurEffect(200, 200, Shader.TileMode.MIRROR));
            mainStatsScreen.setRenderEffect(null);
        }
        mainStatsScreen.setVisibility(View.VISIBLE);
        love.setVisibility(View.VISIBLE);
        mainStatsScreen.startAnimation(slideInAnimation);
        love.startAnimation(slideInAnimation);
    }

    private void animateStatsScreenOut() {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation start callback
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainStatsScreen.setVisibility(View.INVISIBLE);
                love.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    mainStatsScreen.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
                    homeStatsScreen.setRenderEffect(null);
                }
                homeStatsScreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat callback
            }
        });
        mainStatsScreen.startAnimation(slideOutAnimation);
        love.startAnimation(slideOutAnimation);
    }

}