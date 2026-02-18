package com.fastagi.agi_script;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.checkerframework.checker.units.qual.cd;

import com.fastagi.repository.Acd_log_repo;

public class Acd_log extends BaseAgiScript{

     int wait_id = 0;
      String method = "FIFO"; 
  @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {

 String[] args = request.getArguments();
                            LocalDateTime now = LocalDateTime.now();
 DateTimeFormatter callerIdFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String CallerID_date = "V" + now.format(callerIdFormatter);
    

    String method = "FIFO"; 
    int minimum_delay=2;
    String act_context="ringlogic-acd";
    String ivr_context= "ringlogic-ivr";
    String defaultprocess="process";
    String voicemail_context="ringlogic-voicemail";
    int wait_flag=0;

    String ipaddress=getVariable("IP_ADDRESS");
   

    if(args.length > 0 && !args[0].isEmpty()) {

     String Process_DID = args.length > 0 ? args[0] : "";
        System.err.println("Process DID: " + Process_DID);

        String CallerID = args.length > 1 ? args[1] : "";
        String Recording_FileName = args.length > 2 ? args[2] : "";
        String Channel = args.length > 3 ? args[3] : "";
        String Context = args.length > 4 ? args[4] : "";
        String UniqueID = args.length > 5 ? args[5] : "";

        // Split UniqueID like Perl: ($UniqueID,$precision) = split(/[.]/,$UniqueID);
        String precision = "";
        if (!UniqueID.isEmpty()) {
            String[] parts = UniqueID.split("\\.");
            UniqueID = parts[0];
            if (parts.length > 1) {
                precision = parts[1];
            }
        }

        String Transfer_from = args.length > 6 ? args[6] : "";
        String call_hit_referenceno = args.length > 7 ? args[7] : "";

        // if(length($ARGV[9])<1) {$ARGV[9] = "NA";}
        String traverse_path = (args.length > 9 && args[9] != null && args[9].length() > 0)
                ? args[9]
                : "NA";

        String did_num = args.length > 13 ? args[13] : "";
        String transferee_agent = args.length > 14 ? args[14] : "";
        String transferee_extension = args.length > 15 ? args[15] : "";
        String is_transfered_call = args.length > 16 ? args[16] : "";
        String transferee_mode = args.length > 17 ? args[17] : "";
        String dialing_trunk_id = args.length > 18 ? args[18] : "";

        // $is_transfered_call =~ s/ //g;
        is_transfered_call = is_transfered_call.replace(" ", "");

        String transfered_call_type = "";

        // if(length($is_transfered_call)<1)
        if (is_transfered_call.length() < 1) {
            is_transfered_call = "N";
            transfered_call_type = "NONE";
        }
        // elsif($is_transfered_call eq "Y")
        else if (is_transfered_call.equals("Y")) {
            transfered_call_type = "RECEIVED";
        }

        // if($call_hit_referenceno)
        if (!call_hit_referenceno.isEmpty()
                && call_hit_referenceno.length() > 10) {

            // $CallerID = substr($call_hit_referenceno,0,length($call_hit_referenceno)-10);
            CallerID = call_hit_referenceno.substring(
                    0,
                    call_hit_referenceno.length() - 10
            );
        }

     int recording_started = 0;
String Waiting_Context = "ringlogic-waiting";
int TimeOut = 5000;
int Max_Input_Digits = 1;
int RetryLoop = 0;

// default call mode is inbound
String dialer_call_mode = "inbound";

// If CallerID is empty (Perl: if($CallerID =~ /^$/))
if (CallerID == null || CallerID.isEmpty()) {

    CallerID = CallerID_date;

    // If using Asterisk AGI in Java (example using setVariable method)
    // agi.setVariable("CALLERID(num)", "\"" + CallerID + "\"");

    System.out.println("CALLERID(num) set to: \"" + CallerID + "\"");
}

if(Transfer_from != null && Transfer_from.equals("QUEUE")) {
    System.out.println("Call is transferred from: " + Transfer_from);
    // Set the call mode to "transfer" for transferred calls

    Map<String, String> queueData = new Acd_log_repo().get_queue_data(Process_DID);

   if(queueData != null) {
        String queueId = queueData.get("queue_id");
        String queueName = queueData.get("queue_name");
        String queueDid = queueData.get("queue_did");
        String process = queueData.get("process");
        String queueLength = queueData.get("queue_length");
        String Process_Queue_Flow="DTMF";
        System.out.println("Queue Data - ID: " + queueId + ", Name: " + queueName + ", DID: " + queueDid + ", Process: " + process + ", Queue Length: " + queueLength);
   
        Map<String, String> dncData = new Acd_log_repo().getdnc(process);
         String dncCheck = dncData.get("dnc_check");
            String allowQ = dncData.get("allow_q");
            String agentNotAvailableFile = dncData.get("agent_not_available_file");
           
        if(dncData != null) {
           System.out.println("DNC Data - DNC Check: " + dncCheck + ", Allow Queue: " + allowQ + ", Agent Not Available File: " + agentNotAvailableFile);
 

        checkdnc(dncCheck);

        }else{
                System.out.println("No DNC data found for process: " + process);
        }


        allowqueue(allowQ,agentNotAvailableFile);

        setVariable("CDRPROCESS", process);
        setVariable("CDRQUEUE", queueName);

         wait_id = new Acd_log_repo().insert_waiting_calls(CallerID, process, queueName, Channel, UniqueID, dialer_call_mode, call_hit_referenceno, dialing_trunk_id,ipaddress);
   
        Map<String,String> getqueuecount= new Acd_log_repo().getqueuecount(process);
        String priority_count=getqueuecount.get("queue_count");

        LocalDateTime now_epoch = LocalDateTime.now();

        setVariable("QUEUE_START_EPOCH", now_epoch.toString());
        getinqueue(queueName,process);
   
    } else {
        System.out.println("No data found for DID: " + did_num);
    }
}

}
                        }


                        private void checkdnc(String dncCheck){




                        }

                        private void allowqueue(String allowQ, String agentNotAvailableFile) {




                            System.out.println("Allow Queue: " + allowQ + ", Agent Not Available File: " + agentNotAvailableFile);
                        }

                        private void getinqueue(String queue_name,String process) throws AgiException {

                            Map<String,String> getinqueue=new Acd_log_repo().getinqueue(queue_name,process);
                            String queue_id=getinqueue.get("queue_id");
                            String queue_did=getinqueue.get("queue_did");
                            String greeting_file_id=getinqueue.get("greeting_file_id");
                            String call_queue_file_id=getinqueue.get("call_queue_file_id");
                            String queue_drop_time=getinqueue.get("queue_drop_time");
                            String queue_drop_action=getinqueue.get("queue_drop_action");
                            String queue_drop_value=getinqueue.get("queue_drop_value");
                            String queue_length=getinqueue.get("queue_length");
                            String queue_over_flow=getinqueue.get("queue_over_flow");
                            String file_id=greeting_file_id;
                            

                            Map<String,String> filename=new Acd_log_repo().getfilename(file_id);
                            String Queue_Greeting_File=filename.get("file_name");
                            String File_Location=filename.get("file_location");

                            file_id=call_queue_file_id;
                            Map<String,String> filename1=new Acd_log_repo().getfilename(file_id);
                            String Call_Queue_File=filename1.get("file_name");
                            String CQ_File_Location=filename1.get("file_location");

                          String Queue_Drop_Value = "";
String QD_File_Location = "";

if(queue_drop_action.equals("play") || queue_drop_action.equals("voicemail")){
    file_id = queue_drop_value;
    Map<String,String> filename2 = new Acd_log_repo().getfilename(file_id);
    Queue_Drop_Value = filename2.get("file_name");
    QD_File_Location = filename2.get("file_location");
}
                            //Play Queue's Greeting File
                            setVariable("CDRQUEUE", queue_name);
                            streamFile(File_Location+"/"+Queue_Greeting_File);
                            // Checking in waiting calls 
                            long waiting_call_start_epoch = Instant.now().getEpochSecond();
                            setVariable("WAITING_CALL_START_EPOCH", String.valueOf(waiting_call_start_epoch));
                            waitingcalls(queue_drop_time,queue_drop_action,Call_Queue_File,
    CQ_File_Location,
    Queue_Drop_Value,
    QD_File_Location,
    process,               
    queue_name,            
    queue_id,              
    queue_length,process,queue_name);


                        }



private void waitingcalls(  String queueDropTime,
        String queueDropAction,
        String callQueueFile,
        String cqFileLocation,
        String queueDropValue,
        String qdFileLocation,
        String processQueueFlow,
        String queueName,
        String queueId,
        String queueLength,String process,String queue_name) throws AgiException {



            long waiting_call_end_epoch = Instant.now().getEpochSecond();
            Map<String, String> waitingCallData = new Acd_log_repo().getwaiitngcount(process,queue_name);
            String waitingCallCount = waitingCallData.get("waiting_calls");

            System.out.println(waitingCallCount);
            int waitingcallcountno=(Integer.parseInt(waitingCallCount));
            if(waitingcallcountno > 0) {
                System.out.println("Waiting calls count: " + waitingCallCount);
            } else {

                System.out.println("No waiting calls for Queue_name: " + queue_name + ", process: " + process); 
            }

            System.out.println(queueLength+"queue length");

            if((waitingcallcountno>=Integer.parseInt(queueLength)) && (Integer.parseInt(queueLength)>0)){

                System.out.println("Queue length limit reached for Queue_name: " + queue_name + ", process: " + process + ". Current waiting calls: " + waitingCallCount);
            getinqueue(queue_name, process);
            

            }else{
                ACD(queueId,queueName);

            }

}


private void ACD(String queue_id,String queue_name) throws AgiException{

    int lead_id=0;
    String music_class=queue_id;
    exec("StartMusicOnHold", music_class);
    wait_id=wait_id>0?wait_id:0;
    int updateWaitingCalls=new Acd_log_repo().update_waiting_cals(queue_name,lead_id,wait_id);

    Map<String,String> getagent= new Acd_log_repo().getcheckplayque(queue_name);

      String play_queue_no="NO";
     String play_hold_durn="NO";
     String queue_no_file="";
     String hold_durn_file="";
     int playback_freq=0;
    

    if (!getagent.isEmpty()) {
          play_queue_no=getagent.get("play_queue_no");
      play_hold_durn=getagent.get("play_hold_durn");
      queue_no_file=getagent.get("queue_no_file");
      hold_durn_file=getagent.get("hold_durn_file");
      playback_freq=Integer.parseInt(getagent.get("playback_freq")) ;
      String file_name=getagent.get("file_name");
        if(play_queue_no.equals("YES")) {
            
            play_queue_no="YES";
       
        }

        if(play_hold_durn.equals("Y")) {
            
            play_hold_durn="YES";
       
        }
        

    }



    if (method.equals("FIFO")) {
        exec("Queue", queue_name + ",tT");
        
    }

    
   


}




                        
                    }