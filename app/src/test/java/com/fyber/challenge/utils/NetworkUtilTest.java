/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.fyber.challenge.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nauman Zubair on 16/10/2016.
 */
public class NetworkUtilTest {


    private static final String EMPTY = "";

    @Test
    public void testHasValidIpAddress() {
        String ipAddress = NetworkUtil.getIPAddress(true);
        assertNotEquals(ipAddress, EMPTY);
    }

}