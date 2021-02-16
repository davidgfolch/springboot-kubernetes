package com.kubikdata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class JsonStringifier {

    private static final ObjectMapper om = new ObjectMapper();

    @SneakyThrows
    public static String serialize(Object object) {
        return new String(om.writeValueAsBytes(object));
    }

}
