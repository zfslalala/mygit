package com.example.hasee.lanyademo.bean;

/**
 * Created by hasee on 2018/9/22.
 */

public class BlueDevice {
    public String name;
    public String address;
    public int state;

    public BlueDevice() {
        name = "";
        address = "";
        state = 0;
    }

    public BlueDevice(String name, String address, int state) {
        this.name = name;
        this.address = address;
        this.state = state;
    }
}