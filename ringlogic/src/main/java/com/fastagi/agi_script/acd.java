package com.fastagi.agi_script;

import java.util.List;

import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.g;

import com.fastagi.callinfodb.Routedb;
import com.fastagi.callinfodb.cdr_log;


public class acd extends BaseAgiScript  {
       @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {
       


                            
    String[] args = request.getArguments();
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument[" + i + "] = " + args[i]);
        }

        cdr_log cdr_log=new cdr_log();

           // Access specific arguments
        String extentsion   = args.length > 0 ? args[0] : request.getExtension();
        String callerId   = args.length > 1 ? args[1] : request.getCallerIdNumber();
        String uniqueId   = args.length > 2 ? args[2] : request.getUniqueId();
        String channelName= args.length > 3 ? args[3] : channel.getName();
        String dnid       = args.length > 4 ? args[4] : "";
        String callerDNID = args.length > 5 ? args[5] : "";
        String callerANI  = args.length > 6 ? args[6] : "";
        channel.answer();
  
        cdr_log.insertCdrLog(extentsion, callerId, uniqueId, channelName, "INCOMING", dnid, extentsion, uniqueId, "INBOUND");

        Routedb rd=new Routedb();
        List<String> routes = rd.getRouteStatusList(args[0]);
        String Application_type=routes.get(0);
        String Application_value=routes.get(1);


        switch (Application_type) {
case "ivr":
    setVariable("ORIGINAL_DID", request.getExtension());
    setVariable("IVR_ID", Application_value);

exec("Goto", "ringlogic_ivr,ivr,1");
return;


               
            case "callforward":
                   setVariable("ORIGINAL_DID", request.getExtension());
    setVariable("phone_no", request.getCallerIdNumber());

exec("Goto", "ringlogic_ivr,ivr,1");
                
                break;
            case "complete":
                                System.out.println("complete logic to be implemented");

                
                break;
            case "extention":
                                System.out.println("extention logic to be implemented");

                
                break;
            case "ip":
                                                System.out.println("ip logic to be implemented");

                
                break;
            case "misscall":
                System.out.println("misscall logic to be implemented");
                break;
            case "play":
                System.out.println("play logic to be implemented");
                break;
            case "process":
                System.out.println("PROCESS logic to be implemented");
                break;
            case "queue":
                System.out.println("queue logic to be implemented");
                break;
            case "voicemail":
                System.out.println("voicemail logic to be implemented");
                break;
            default:
                break;
        }


}
}
