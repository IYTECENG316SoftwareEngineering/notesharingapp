package com.example.onur.notesharingapp10;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.EditText;


public class SignUpActivityTest extends ActivityInstrumentationTestCase2<SignUpActivity>
{
    SignUpActivity activity;

    public SignUpActivityTest() {
        super(SignUpActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////


    @SmallTest
    public void testNameRegisterNotNull()
    {
        EditText Name = (EditText)activity.findViewById(R.id.name);
        assertNotNull(userName);
    }


    @SmallTest
    public void testUserNameRegisterNotNull()
    {
        EditText userName = (EditText)activity.findViewById(R.id.user);
        assertNotNull(userName);
    }


    @SmallTest
    public void testEmailRegisterNotNull()
    {
        EditText email = (EditText)activity.findViewById(R.id.email);
        assertNotNull(email);
    }


    @SmallTest
    public void testPasswordRegisterNotNull()
    {
        EditText password = (EditText)activity.findViewById(R.id.pass);
        assertNotNull(password);
		EditText passwordConfirm = (EditText)activity.findViewById(R.id.pass2);
		password.asserEquals(passwordConfirm);
    }

    

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

}