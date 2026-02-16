package com.fastagi.agi_script;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.asteriskjava.pbx.CallerID;

import com.fastagi.repository.Cdr_log_repo;


public class Cdr_log extends BaseAgiScript {
    

    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {

         String[] args = request.getArguments();
        String cdrlogid="0";
        
        String action = args.length > 0 ? args[0] : "";              // "CDR_LOG"


        if(action.equals("CDR_LOG")) {
                
            
            String exten = args.length > 1 ? args[1] : "";               // ${EXTEN}
        String callerIdNum = args.length > 2 ? args[2] : "";         // ${CALLERID(num)}
        String uniqueId = args.length > 3 ? args[3] : "";            // ${UNIQUEID}
        String Channel = args.length > 4 ? args[4] : "";         // ${CHANNEL}
        String dnid = args.length > 5 ? args[5] : "";                // ${DNID}
        String callerIdDnid = args.length > 6 ? args[6] : "";        // ${CALLERID(dnid)}
        String callerIdAni = args.length > 7 ? args[7] : "";
         String callReferenceNo = args.length > 8 ? args[8] : "";  
        
String callType="incoming" ;
String RouteName="NA";
String clid="";
String[] uniqueIdParts = uniqueId.split("\\.");
String traverse_path="NA";
String call_mode="inbound";

setVariable("", call_mode);
            if (callerIdNum == null || callerIdNum.trim().isEmpty()) {
    callerIdNum = generateCallerIdDate(); // Your logic
    setVariable("CALLERID(num)", callerIdNum);
}
                 if (callerIdNum.length() > 10) {
            callerIdNum = callerIdNum.substring(callerIdNum.length() - 10);
        }
        setVariable("CALLERID(num)", callerIdNum);       

        if (uniqueIdParts.length > 0) {
            uniqueId = uniqueIdParts[0];
        }
              // Handle empty call type
    if (dnid != null && !dnid.isEmpty()) {
    callType = "outgoing";  // Or maybe "outgoing" instead of dnid?
}

        
if(callerIdDnid.length() < 1) {
    callerIdDnid = "NA";
}
if(callerIdAni.length()<1)
{
    callerIdAni = "NA";
}

if (args.length > 2 && args[2] != null) {
    String arg2 = args[2];
    if (arg2.length() >= 10) {
        // Get last 10 characters (negative index in Perl)
        clid = arg2.substring(arg2.length() - 10);
    } else {
        // If string is shorter than 10 characters, get the whole string
        clid = arg2;
    }
}


clid = clid.trim();

if(clid.length() > 0) {
String revclid="";
revclid = clid;
StringBuilder sb = new StringBuilder(revclid);
sb.reverse();
revclid = sb.toString();
}



Cdr_log_repo cdrLogRepo = new Cdr_log_repo();
 RouteName = cdrLogRepo.getroutenameString(exten);


 if(RouteName.length() < 1) {
    RouteName = "NA";
 }

 traverse_path="Route_Name:"+RouteName+"-DNID"+callerIdDnid+"-ANI"+callerIdAni;


if(args.length > 8 && args[8].length() > 0 && callType.equals("outgoing")) {


    callerIdNum = callReferenceNo.substring(0, callReferenceNo.length() - 10);
setVariable("CALLREFERENCE", callReferenceNo);
}else {

  long nowDateEpoch = Instant.now().getEpochSecond();

// Combine with caller ID
 callReferenceNo = callerIdNum + nowDateEpoch;

// Set the AGI variable
setVariable("CALLREFERENCE", callReferenceNo);
}

setVariable("TRAVERSE_PATH", traverse_path);
setVariable("ROUTE_NAME", RouteName);
setVariable("DIDNUM", callerIdDnid);


if(args.length>9 && args[9].length() > 0) {

    call_mode = args[9];


}


 cdrlogid=cdrLogRepo.setcdrlog("192.168.200.52",callerIdNum,exten,uniqueId,Channel,callReferenceNo,callType,call_mode,traverse_path,callerIdDnid,RouteName);

setVariable("CDRLOGID", String.valueOf(cdrlogid));


        }else if (action.equals("CDR_UPDATE_LOG")) {

// Extract all parameters with null safety
    String callerid = args.length > 1 ? args[1] : "";
    String call_referenceno = args.length > 2 ? args[2] : "";
    String call_end_node = args.length > 3 ? args[3] : "";
    String status = args.length > 4 ? args[4] : "";
    String hangup_cause_code = args.length > 5 ? args[5] : "";
    String billsecStr = args.length > 6 ? args[6] : "0";
    String uniqueid = args.length > 7 ? args[7] : "";
    String Channel = args.length > 8 ? args[8] : "";
    String completed_by = args.length > 9 ? args[9] : "";
    String call_type = args.length > 10 ? args[10] : "";
    String action_type = args.length > 11 ? args[11] : "";
    String dialerStatusArg = args.length > 12 ? args[12] : "";
    String process = args.length > 13 ? args[13] : "";
    String call_mode = args.length > 14 ? args[14] : "";
    
    // Node duration timestamps
    String ivr_start_epoch = args.length > 15 ? args[15] : "0";
    String ivr_end_epoch = args.length > 16 ? args[16] : "0";
    String queue_start_epoch = args.length > 17 ? args[17] : "0";
    String queue_end_epoch = args.length > 18 ? args[18] : "0";
    String ringing_start_epoch = args.length > 19 ? args[19] : "0";
    String ringing_end_epoch = args.length > 20 ? args[20] : "0";
    String did_num = args.length > 21 ? args[21] : "";
    String queue_id = args.length > 22 ? args[22] : "";
    String vm_start_epoch = args.length > 23 ? args[23] : "0";
    String vm_end_epoch = args.length > 24 ? args[24] : "0";
    String callforward_start_epoch = args.length > 25 ? args[25] : "0";
    String callforward_end_epoch = args.length > 26 ? args[26] : "0";
    String recordingstudio_start_epoch = args.length > 27 ? args[27] : "0";
    String recordingstudio_end_epoch = args.length > 28 ? args[28] : "0";



    
            
        }
       


   
}
        
public String generateCallerIdDate() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    return "V" + now.format(formatter);
}
        
 }