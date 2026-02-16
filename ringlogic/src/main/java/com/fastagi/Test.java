package com.fastagi;

import org.asteriskjava.fastagi.BaseAgiScript;



import org.asteriskjava.fastagi.AgiException;

public class Test extends BaseAgiScript {
    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {
       
       


                            answer();
                            System.out.println("Answered the call");
        streamFile("ConVox/namaskar"); // play welcome.wav from Asterisk sounds
        System.out.println("Played welcome message");
        setVariable("FASTAGI_OK", "1"); // optional, for your NoOp in dialplan
        

    }
}