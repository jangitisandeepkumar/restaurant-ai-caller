package com.restaurant.caller.service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.caller.Restaurant;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME =
            "Restaurant AI Caller";

    private static final String CREDENTIALS_FILE =
            "restaurant-ai-caller-79548ac0b340.json";

    private static final String SPREADSHEET_ID =
            "1rpxPFbozRh4Ez0kyoe-NP4Z6AmLAqiM3dzPUIveco9g";

    // ------------------------------------------------
    // CONNECT TO GOOGLE SHEETS
    // ------------------------------------------------

    public Sheets getSheetsService() throws Exception {

        GoogleCredentials credentials =
                GoogleCredentials
                        .fromStream(
                                new FileInputStream(CREDENTIALS_FILE)
                        )
                        .createScoped(
                                Collections.singleton(
                                        "https://www.googleapis.com/auth/spreadsheets"
                                )
                        );

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // ------------------------------------------------
    // READ ALL RESTAURANTS
    // ------------------------------------------------

    public List<List<Object>> getRestaurants()
            throws Exception {

        Sheets sheets = getSheetsService();

        String range = "Sheet1!A:F";

        ValueRange response =
                sheets.spreadsheets()
                        .values()
                        .get(SPREADSHEET_ID, range)
                        .execute();

        List<List<Object>> values =
                response.getValues();

        if (values == null || values.isEmpty()) {

            return Collections.emptyList();
        }

        return values;
    }

    // ------------------------------------------------
    // CONVERT SHEET ROWS TO RESTAURANT OBJECTS
    // ------------------------------------------------

    public List<Restaurant> getRestaurantObjects()
            throws Exception {

        List<List<Object>> rows =
                getRestaurants();

        List<Restaurant> restaurants =
                new ArrayList<>();

        // Start from 1 because row 0 contains headers
        for (int i = 1; i < rows.size(); i++) {

            List<Object> row =
                    rows.get(i);

            Restaurant restaurant =
                    new Restaurant();

            if (row.size() > 0) {

                restaurant.setRestaurant(
                        row.get(0).toString()
                );
            }

            if (row.size() > 1) {

                restaurant.setPhoneNumber(
                        row.get(1).toString()
                );
            }

            if (row.size() > 2) {

                restaurant.setPromotion(
                        row.get(2).toString()
                );
            }

            if (row.size() > 3) {

                restaurant.setPayment(
                        row.get(3).toString()
                );
            }

            if (row.size() > 4) {

                restaurant.setIfYesDay(
                        row.get(4).toString()
                );
            }

            if (row.size() > 5) {

                restaurant.setStatus(
                        row.get(5).toString()
                );
            }

            restaurants.add(restaurant);
        }

        return restaurants;
    }

    // ------------------------------------------------
    // UPDATE ONE RESTAURANT RESULT
    // ------------------------------------------------

    public void updateRestaurantResult(
            int rowNumber,
            String promotion,
            String payment,
            String ifYesDay,
            String status)
            throws Exception {

        Sheets sheets = getSheetsService();

        // Google Sheets rows start from 1
        String range =
                "Sheet1!C" + rowNumber + ":F" + rowNumber;

        List<Object> values =
                List.of(
                        promotion,
                        payment,
                        ifYesDay,
                        status
                );

        List<List<Object>> bodyValues =
                List.of(values);

        ValueRange body =
                new ValueRange()
                        .setValues(bodyValues);

        sheets.spreadsheets()
                .values()
                .update(
                        SPREADSHEET_ID,
                        range,
                        body
                )
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}