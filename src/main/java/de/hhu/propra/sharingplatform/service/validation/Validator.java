package de.hhu.propra.sharingplatform.service.validation;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    /**
     * Valid mails are like foo.bar@mail.de
     *
     * @param mail
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
     * @param string
     * @return true for strings free of special chars
     */
    public static boolean freeOfSpecialChars(String string) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9| |,|.|-]*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * Allowed chars are: a-z, A-Z, 0-9
     *
     * @param string
     * @return true for alphanumeric strings
     */
    public static boolean isAlphanumeric(String string) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * Guidlines:
     * String not null,
     * String not empty,
     * String length < 256
     *
     * @param string
     * @return true for guidline matching strings
     */
    public static boolean matchesDBGuidlines(String string) {
        return string != null && string.length() != 0 && string.length() < 256;
    }

    /**
     * Allewed chars are printable chars
     *
     * @param string
     * @return true for printable strings
     */
    public static boolean isPrintable(String string) {
        Pattern pattern = Pattern.compile("^[\\p{Graph}\\x20]+$");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
}
