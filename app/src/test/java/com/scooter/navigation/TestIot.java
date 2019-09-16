package com.scooter.navigation;

import com.redescooter.iot.client.Client;
import com.scooter.navigation.push.DefaultMessageListener;

public class TestIot {
    public static void main(String[] args) {
        Client.getInstance().initialization("A0000001", "13.209.4.155", 7333, new DefaultMessageListener());
    }
}
