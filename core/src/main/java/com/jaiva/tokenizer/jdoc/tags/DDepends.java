package com.jaiva.tokenizer.jdoc.tags;

import java.util.ArrayList;
import java.util.Arrays;

public final class DDepends extends Tag {
    public DDepends(String input) {
        super(TagType.DEPENDS);
        // given an input such as "func1, func2, func3, func4"
        ArrayList<String> arr = new ArrayList<>();
        for (String symbol : input.split(",")) {
            arr.add(symbol.trim());
        }
        attributes.put("symbols", arr);
    }

    public DDepends(String... input) {
        ArrayList<String> arr = new ArrayList<>(
                Arrays.asList(input)
        );
        attributes.put("symbols", arr);
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        ArrayList<String> symbols = (ArrayList<String>) attributes.get("symbols");
        st.append("[");
        for (String sym : symbols) {
            if (symbols.getFirst().equals(sym)) {
                st.append("\"").append(sym).append("\"");
            } else {
                st.append(",").append("\"").append(sym).append("\"");
            }
        }
        return super.toString()
                + "\"symbols\":" + st.append("]") + "}";
    }
}
