package com.fastagi;

import org.asteriskjava.fastagi.BaseAgiScript;

import com.fastagi.callinfo.callinfo;


import org.asteriskjava.fastagi.AgiException;

public class Test extends BaseAgiScript {
    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {
       
                 callinfo ci=new callinfo();
                 ci.getCallInfo();


                            answer();
        exec("MixMonitor", "/var/www/html/recording/sapan.wav,b");
        streamFile("ConVox/emri"); // play welcome.wav from Asterisk sounds
        setVariable("FASTAGI_OK", "1"); // optional, for your NoOp in dialplan
        

    }
}