package com.app.homiefy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateValidator {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static class ValidationResult {
        public boolean isValid;
        public String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
    }

    public static ValidationResult validateDates(String startDateStr, String endDateStr) {
        // Check if dates are empty
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            return new ValidationResult(false, "Please enter both start and end dates");
        }

        // Check if date format is valid
        if (!isValidDateFormat(startDateStr)) {
            return new ValidationResult(false, "Invalid start date format. Please use dd/MM/yyyy");
        }
        if (!isValidDateFormat(endDateStr)) {
            return new ValidationResult(false, "Invalid end date format. Please use dd/MM/yyyy");
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            Date currentDate = new Date();

            // Check if dates are valid
            if (startDate == null || endDate == null) {
                return new ValidationResult(false, "Invalid date format. Please use dd/MM/yyyy");
            }

            // Check if start date is before current date
            if (startDate.before(currentDate)) {
                return new ValidationResult(false, "Start date cannot be in the past");
            }

            // Check if end date is before start date
            if (endDate.before(startDate)) {
                return new ValidationResult(false, "End date must be after start date");
            }

            // Check if rental period is too short (less than 1 month)
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();
            calStart.setTime(startDate);
            calEnd.setTime(endDate);

            long diffInMillis = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

            if (diffInDays < 30) {
                return new ValidationResult(false, "Minimum rental period is 1 month");
            }

            // Check if rental period is too long (more than 24 months)
            if (diffInDays > 730) { // 730 days = 24 months
                return new ValidationResult(false, "Maximum rental period is 24 months");
            }

            return new ValidationResult(true, "");
        } catch (ParseException e) {
            return new ValidationResult(false, "Invalid date format. Please use dd/MM/yyyy");
        }
    }

    private static boolean isValidDateFormat(String dateStr) {
        try {
            // Split date string into day, month, and year
            String[] parts = dateStr.split("/");
            if (parts.length != 3) return false;

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Check month range
            if (month < 1 || month > 12) return false;

            // Use Calendar to validate day in month
            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.YEAR, year);

            calendar.getTime();

            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
