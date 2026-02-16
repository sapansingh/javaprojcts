package com.fastagi.agi_script;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.BaseAgiScript;

import com.fastagi.models.InnerIvr;
import com.fastagi.repository.Ivr_repo;
import com.fastagi.repository.Schedular_repo;

import javassist.runtime.Inner;

public class Ivr extends BaseAgiScript {
    
    private final String RECORDING_FORMAT = "wav"; // or "mp3" based on your system
    private final String BASE_RECORDING_PATH = "/var/www/html/calls/";

    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {

    String[] args = request.getArguments();

    String ivr_id= args.length > 1 ? args[1] : null;
    String did = args.length > 0 ? args[0] : null;

    




    long nowDateEpoch = Instant.now().getEpochSecond();

    setVariable("IVR_START_EPOCH", String.valueOf(nowDateEpoch));

            ivr_process(ivr_id, did);
                            
    }

    private void ivr_process(String ivr_id, String did) throws  AgiException {
        // TODO Auto-generated method stub
        

List<InnerIvr> ivrData = new Ivr_repo().get_ivrdata(ivr_id);
                   
String File_Location = ivrData.get(0).getFile_Location();
String File_Name = ivrData.get(0).getFile_Name();
int File_Name_Int = Integer.parseInt(ivrData.get(0).getSeconds_to_wait()) ;                        
File_Location = File_Location+"/" + File_Name;
                            
int timeout = File_Name_Int * 1000; // Convert seconds to milliseconds
    int max_input_digits = 1;
    String total_digits="";                       
String dtmf_pressed="";
              String getdigit= getData(File_Location, timeout, max_input_digits);
              total_digits=getdigit;
                dtmf_pressed=dtmf_pressed+"~"+getdigit;
              setVariable("DTMF_PRESSED", getdigit);
            if(total_digits==""){

                nodtmfpressed();

            }else{
                   System.out.println("DTMF Pressed: " + getdigit);
                Xpress_ProcessOption(ivr_id,getdigit,did);
            }
           
    } 




    private void Xpress_ProcessOption(String ivr_id, String getdigit, String did) throws AgiException {
        // TODO Auto-generated method stub

        List<String> ivrOptions = new Ivr_repo().get_ivr_options(ivr_id,getdigit);
        String next_ivr_id=ivrOptions.get(0);
        String option_num=ivrOptions.get(1);
        String option_key=ivrOptions.get(2);
        String destination=ivrOptions.get(3);

        if(next_ivr_id!=null && !next_ivr_id.isEmpty()){

         processivr(next_ivr_id,option_num,option_key,destination,did);
          
        }else{
            // No matching option found, handle accordingly (e.g., repeat IVR, hang up, etc.)
            System.out.println("No matching IVR option found for IVR ID: " + ivr_id + " and DTMF: " + getdigit);
        }

        
    }

    private void nodtmfpressed() throws AgiException {
        // TODO Auto-generated method stub

        System.out.println("No DTMF pressed. Handling accordingly (e.g., repeat IVR, hang up, etc.)");


        
    }


  private void processivr(String ivr_id,String option_num,String option_key,String destination,String did) throws AgiException {
        // TODO Auto-generated method stub

     
       switch (option_key.toLowerCase()) {
            case "ivr":
                handleIVR(did, destination);
                break;

            case "callforward":
        
                break;

            case "extension":
             
                break;

            case "voicemail":
                
                break;

            case "process":
              
                break;

            case "queue":
              
                break;

            case "ip":
               
                break;

            case "play":
             
                break;

            case "complete":
           
                break;

            case "misscall":
     
                break;

            default:
            
                // Default to call forward for unknown types
               
                break;
        }


  
}


private void handleIVR(String did, String destination) throws AgiException {
        System.out.println("Routing to IVR: " + destination);

        String ivrData = new Ivr_repo().getivrid(destination);
        setVariable("ivr_id", ivrData);
        setVariable("orignal_did", did);
        setVariable("ROUTE_NAME", destination);
        System.out.println("Set variables for IVR routing: ivr_id=" + ivrData + ", original_did=" + did + ", ROUTE_NAME=" + destination);
        exec("Goto", "ringlogic_ivr,ivr,1");
    }
}




 