package de.hhu.propra.sharingplatform.service.validation;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO annotations?
public class Validator {

    /**
     * Valid mails are like foo.bar@mail.de
     *
     * @param mail e-mail address
     * @return true if mail is valid
     */
    public static boolean isValidMail(String mail) {
        Pattern pattern = Pattern.compile("[\\w|.|-]+@\\w[\\w|-]*\\.[a-z]{2,3}");
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    /**
     * Allowed chars are: a-z, A-Z, 0-9, '-', ' ', '.', ','
     *
     * @param string string to check
     * @return true for strings free of special chars
     */
    public static boolean freeOfSpecialChars(String string) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9| |'|ß|,|.|-]*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * Allowed chars are: a-z, A-Z, 0-9.
     *
     * @param string string to check
     * @return true for alphanumeric strings
     */
    public static boolean isAlphanumeric(String string) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * Guidelines:
     * String not null.
     * String not empty.
     * String length < 256.
     *
     * @param string string to check
     * @return true for guidline matching strings
     */
    public static boolean matchesDbGuidelines(String string) {
        return string != null && string.length() != 0 && string.length() < 255;
    }

    /**
     * Allewed chars are printable chars.
     *
     * @param string string to check
     * @return true for printable strings
     */
    public static boolean isPrintable(String string) {
        Pattern pattern = Pattern.compile("^[\\p{Graph}\\x20]+$");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public static void validateName(String name, String message) {
        if (!Validator.matchesDbGuidelines(name)
            || !Validator.isPrintable(name)
            || !Validator.freeOfSpecialChars(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public static void validateAccountName(String accountName, String message) {
        if (!Validator.matchesDbGuidelines(accountName)
            || !Validator.isPrintable(accountName)
            || !Validator.freeOfSpecialChars(accountName)
            || accountName.equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public static void validateAdress(String accountName, String message) {
        if (!Validator.matchesDbGuidelines(accountName)
            || !Validator.isPrintable(accountName)
            || !Validator.freeOfSpecialChars(accountName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
