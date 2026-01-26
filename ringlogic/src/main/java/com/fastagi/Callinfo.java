package com.fastagi;

import org.asteriskjava.fastagi.BaseAgiScript;

import com.fastagi.callinfodb.callinfo;

import org.asteriskjava.fastagi.AgiException;

public class Callinfo extends BaseAgiScript {
    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {
       
                 callinfo ci=new callinfo();
                 ci.getCallInfo();

                        answer();
                            System.out.println("Answered the call");
        streamFile("ConVox/namaskar"); // play welcome.wav from Asterisk sounds
        System.out.println("Played welcome message");
        System.out.println("Inside Callinfo Class");
        setVariable("FASTAGI_OK", "1"); // optional, for your NoOp in dialplan
        

    }
}