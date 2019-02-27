package de.hhu.propra.sharingplatform.service.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorTest {

    @Test
    public void mailTestValidMail1() {
        assertTrue(Validator.isValidMail("foo@bar.com"));
    }

    @Test
    public void mailTestValidMail2() {
        assertTrue(Validator.isValidMail("foo.bar@bar.com"));
    }

    @Test
    public void mailTestValidMail3() {
        assertTrue(Validator.isValidMail("foo1999@bar.com"));
    }

    @Test
    public void mailTestValidMail4() {
        assertTrue(Validator.isValidMail("foo-bar@bar.com"));
    }

    @Test
    public void mailTestInvalidMail1() {
        assertFalse(Validator.isValidMail("f$_@bar.com"));
    }

    @Test
    public void mailTestInvalidMail2() {
        assertFalse(Validator.isValidMail("bar.com"));
    }

    @Test
    public void mailTestInvalidMail3() {
        assertFalse(Validator.isValidMail("foo@bar"));
    }

    @Test
    public void mailTestInvalidMail4() {
        assertFalse(Validator.isValidMail("@bar.com"));
    }

    ///////////////////////////

    @Test
    public void noSpecialChars1() {
        assertFalse(Validator.freeOfSpecialChars("aaa!bbb"));
    }

    @Test
    public void noSpecialChars2() {
        assertTrue(Validator.freeOfSpecialChars("aaa-bbb"));
    }

    @Test
    public void noSpecialChars3() {
        assertFalse(Validator.freeOfSpecialChars("aa%&$"));
    }

    @Test
    public void noSpecialChars4() {
        assertTrue(Validator.freeOfSpecialChars("aaa123bbb"));
    }

    @Test
    public void noSpecialChars5() {
        assertTrue(Validator.freeOfSpecialChars("b.a-c"));
    }

    @Test
    public void noSpecialChars6() {
        assertTrue(Validator.freeOfSpecialChars("aa bb"));
    }

    /////////////////////////////

    @Test
    public void alphaNumericMinus() {
        assertFalse(Validator.isAlphanumeric("a-b"));
    }

    @Test
    public void alphaNumericSpace() {
        assertFalse(Validator.isAlphanumeric("a b"));
    }

    @Test
    public void alphaNumericLetters() {
        assertTrue(Validator.isAlphanumeric("a1b"));
    }

    @Test
    public void alphaNumericNumbers() {
        assertTrue(Validator.isAlphanumeric("123"));
    }

    //////////////////////////////

    @Test
    public void matchesGuidelinesEmpty() {
        assertFalse(Validator.matchesDbGuidelines(""));
    }

    @Test
    public void matchesGuidelinesNull() {
        assertFalse(Validator.matchesDbGuidelines(null));
    }

    @Test
    public void matchesGuidelinesNormal() {
        assertTrue(Validator.matchesDbGuidelines("!_llslfg$"));
    }

    @Test
    public void matchesGuidelinesTooLong() {
        StringBuilder veryLongString = new StringBuilder("A");
        for (int i = 0; i < 256; i++) {
            veryLongString.append("B");
        }
        assertFalse(Validator.matchesDbGuidelines(veryLongString.toString()));
    }

    //////////////////////////////

    @Test
    public void isPrintableTrue() {
        assertTrue(Validator.isPrintable("abx!. "));
    }

    @Test
    public void isPrintableFalse1() {
        assertFalse(Validator.isPrintable("\n"));
    }

    @Test
    public void isPrintableFalse2() {
        assertFalse(Validator.isPrintable("äüö"));
    }
}