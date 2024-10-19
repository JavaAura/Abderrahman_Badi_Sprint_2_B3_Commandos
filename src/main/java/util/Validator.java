package util;

import java.util.ArrayList;
import java.util.List;

import model.Client;
import model.User;
import model.enums.Role;

public class Validator {

    private static void validateString(String fieldName, String value, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(fieldName + " should not be empty.");
        } else if (value.length() < 3) {
            errors.add(fieldName + " must contain at least 3 characters.");
        }
    }

    private static void validateEmail(String email, List<String> errors) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (email == null || !email.matches(emailRegex)) {
            errors.add("Invalid email format.");
        }
    }

    public static List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        validateString("First Name", user.getFirstName(), errors);
        validateString("Last Name", user.getLastName(), errors);
        validateEmail(user.getEmail(), errors);
        validateString("Password", user.getPassword(), errors);

        if (user.getRole() == Role.CLIENT) {
            if (user instanceof Client) {
                Client client = (Client) user;
                validateString("Delivery Address", client.getAddressDelivery(), errors);
                validateString("Payment Method", client.getPaymentMethod(), errors);
            } else {
                errors.add("User is marked as CLIENT but is not of type Client.");
            }
        }
        return errors;
    }
}
