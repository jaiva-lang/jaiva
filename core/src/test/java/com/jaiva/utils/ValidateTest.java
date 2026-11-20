package com.jaiva.utils;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.yetnt.utils.tuple.SamePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidateTest {

    @Test
    void isOperator() {
        ArrayList<Character> input = new ArrayList<>(Arrays.asList('!', '=', 'c', '>', '?', ' ', '#'));
        ArrayList<Boolean> expected = new ArrayList<>(Arrays.asList(true, true, false, true, true, false, true));

        for (int i = 0; i < input.size(); i++) {
            Assertions.assertEquals(expected.get(i), Validate.isOperator(input.get(i)));
        }
    }

    @Test
    void testcontainsOperator() {
        Assertions.assertEquals(-1, Validate.containsOperator("hello. i have no operator.".toCharArray()));
        Assertions.assertEquals(2, Validate.containsOperator("I lowkey do + have an operaotr".toCharArray()));
        Assertions.assertEquals(5, Validate.containsOperator("Jaiva != Java && Jaiva != Javascript.".toCharArray()));
    }

    @Test
    void testIsUnaryMinus() {
        Assertions.assertFalse(Validate.isUnaryMinus(1, 0, "2-2"));
        Assertions.assertFalse(Validate.isUnaryMinus(2, 0, "8 - -2"));
        Assertions.assertTrue(Validate.isUnaryMinus(4, 2, "8 - -2"));
        Assertions.assertTrue(Validate.isUnaryMinus(0, 0, "-2"));
    }

    @Test
    void testIsLogicalNOT() {
        Assertions.assertFalse(Validate.isLogicalNot(0, 2, "'d"));
        Assertions.assertTrue(Validate.isLogicalNot(4, 6, "true' - false"));
        Assertions.assertTrue(Validate.isLogicalNot(6, 7, "teflon'"));
    }

    @Test
    void testIsOpInPair() {
        ArrayList<SamePair<Integer>> list = new ArrayList<>(Arrays.asList(
                new SamePair<>(0, 2),
                new SamePair<>(3, 6)));
        Assertions.assertEquals(0, Validate.isOpInPair(1, list));
        Assertions.assertEquals(1, Validate.isOpInPair(4, list));
        Assertions.assertEquals(-1, Validate.isOpInPair(8, list));
    }

    @Test
    void testIsValidSymbolName() {
        Assertions.assertEquals(new Validate.IsValidSymbolName(), Validate.isValidSymbolName("yo"));
        Assertions.assertEquals(new Validate.IsValidSymbolName("!", false), Validate.isValidSymbolName("yo!"));
    }
}
