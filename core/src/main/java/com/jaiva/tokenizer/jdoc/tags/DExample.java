package com.jaiva.tokenizer.jdoc.tags;

import java.util.ArrayList;

public final class DExample extends Tag {
    public DExample(String input) {
        super(TagType.EXAMPLE);
        // given an input such as "codeblock"
        ArrayList<String> codeblock = new ArrayList<>();
        codeblock.add(input);
        attributes.put("codeblock", codeblock);
    }

    public DExample(ArrayList<String> st) {
        super(TagType.EXAMPLE);
        attributes.put("codeblock", st);
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        ArrayList<String> lines = (ArrayList<String>) attributes.get("codeblock");
        st.append("[");
        for (String lin : lines) {
            String lineOriginal = lin;
            lin = lin.replace("\\", "\\\\").replace("\n", "\\n");
            lin = lin.replace("\"", "\\\"").replace("\r", "\\r");
            if (lines.getFirst().equals(lineOriginal)) {
                st.append("\"").append(lin).append("\"");
            } else {
                st.append(",").append("\"").append(lin).append("\"");
            }
        }
        return super.toString()
                + "\"codeblock\":" + st.append("]") + "}";
    }

    @Override
    public boolean addToDescription(String description) {
//            if (attributes.containsKey("codeblock")) {
        ArrayList<String> codeblock = (ArrayList<String>) attributes.get("codeblock");
        codeblock.add(description);
        return false;
//            }
//            return true;
    }
}
