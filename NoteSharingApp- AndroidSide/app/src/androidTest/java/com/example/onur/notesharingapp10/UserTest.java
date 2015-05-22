package com.example.onur.notesharingapp10;

import junit.framework.TestCase;

import org.junit.Test;

public class UserTest extends TestCase {
    @Test
    public void testUser() {
        SignUpActivity sa = new SignUpActivity();

        boolean check = sa.emailCheck("onur@std.iyte.edu.tr");

        assertEquals(true, check);
    }

    @Test
    public void testInvalidUser() {
        SignUpActivity sa = new SignUpActivity();

        boolean check = sa.emailCheck("onur@gmail.com");

        assertEquals(false, check);
    }

}