package com.cti.web.dto;

import java.util.HashMap;

public class JobsJsonResponse extends HashMap<String, Integer> {

    public void addAmount(String user, Integer amount) {
        if (containsKey(user)) {
            compute(user, (k, v) -> v += amount);
        } else {
            put(user, amount);
        }
    }

}
