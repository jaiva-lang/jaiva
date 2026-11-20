package com.jaiva.utils.generic;

import com.jaiva.utils.Find;
import io.github.yetnt.utils.tuple.Pair;
import io.github.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;

/**
 * A record to hold the results of the {@link Find#bracePairs(String)} method.
 *
 * @param closedPairs An {@link ArrayList} of {@link SamePair} representing the start and end indices of closed brace pairs.
 * @param unclosedBraces An {@link ArrayList} of {@link Pair} where each pair contains the index and character of an unclosed brace.
 */
public record BracePairs(
        ArrayList<SamePair<Integer>> closedPairs,
        ArrayList<Pair<Integer, Character>> unclosedBraces,
        ArrayList<Pair<Integer, Character>> danglingClose
) {}
