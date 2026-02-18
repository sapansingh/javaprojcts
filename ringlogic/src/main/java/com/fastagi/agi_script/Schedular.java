package com.fastagi.agi_script;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.BaseAgiScript;

import com.fastagi.repository.Ivr_repo;
import com.fastagi.repository.Schedular_repo;

public class Schedular extends BaseAgiScript {
    
    private final String RECORDING_FORMAT = "wav"; // or "mp3" based on your system
    private final String BASE_RECORDING_PATH = "/var/www/html/calls/";

    @Override
    public void service(org.asteriskjava.fastagi.AgiRequest request,
                        org.asteriskjava.fastagi.AgiChannel channel) throws AgiException {

        String[] args = request.getArguments();
        String did = args.length > 0 ? args[0] : request.getExtension();
        String callerId = args.length > 1 ? args[1] : request.getCallerIdNumber();
        String uniqueId = args.length > 2 ? args[2] : request.getUniqueId();
        String channelName = args.length > 3 ? args[3] : channel.getName();
        
        // Get variables passed from dialplan
        String cdrLogId = getVariable("CDRLOGID");
        String callReference = getVariable("CALLREFERENCE");
        String routeName = getVariable("ROUTE_NAME");
        String didNum = getVariable("DIDNUM");

        setVariable("original-did", did);
        System.out.println("=== Starting Schedular AGI ===");
        System.out.println("DID: " + did);
        System.out.println("CallerID: " + callerId);
        System.out.println("UniqueID: " + uniqueId);
        System.out.println("Channel: " + channelName);
        System.out.println("CDRLOGID: " + cdrLogId);
        System.out.println("CALLREFERENCE: " + callReference);
        System.out.println("ROUTE_NAME: " + routeName);
        System.out.println("DIDNUM: " + didNum);

        Schedular_repo repo = new Schedular_repo();
        List<String> schedularInfo = repo.getSchedularInfo(did);
        System.out.println("Schedular Info: " + schedularInfo);

        if (schedularInfo == null || schedularInfo.size() < 2) {
            System.out.println("No route found for DID: " + did);
            hangup();
            return;
        }

        String type = schedularInfo.get(0);
        String applicationValue = schedularInfo.get(1);
        String routeNameFromDB = schedularInfo.size() > 2 ? schedularInfo.get(2) : "";

        // Use route name from database if not already set
        if ((routeName == null || routeName.isEmpty()) && !routeNameFromDB.isEmpty()) {
            routeName = routeNameFromDB;
            setVariable("ROUTE_NAME", routeName);
        }

        switch (type.toLowerCase()) {
            case "ivr":
                handleIVR(did, applicationValue, routeName);
                break;

            case "callforward":
                handleCallForward(did, callerId, applicationValue, cdrLogId, routeName, channelName);
                break;

            case "extension":
                handleExtension(did, callerId, applicationValue, cdrLogId, routeName, channelName);
                break;

            case "voicemail":
                handleVoicemail(did, applicationValue, routeName);
                break;

            case "process":
                handleProcess(did, applicationValue, routeName);
                break;

            case "queue":
                handleQueue(did, applicationValue, routeName);
                break;

            case "ip":
                handleIP(did, applicationValue, routeName);
                break;

            case "play":
                handlePlayType(applicationValue);
                break;

            case "complete":
                handleComplete();
                break;

            case "misscall":
                handleMisscall(did);
                break;

            default:
                System.out.println("Unknown application type: " + type);
                // Default to call forward for unknown types
                handleExtension(did, callerId, applicationValue, cdrLogId, routeName, channelName);
                break;
        }
    }

    private void handleIVR(String did, String ivrId, String routeName) throws AgiException {
        System.out.println("Routing to IVR: " + ivrId);
        Schedular_repo repo = new Schedular_repo();
        String ivrData = repo.getivrid(ivrId);
        setVariable("ivr_id", ivrData);
        setVariable("orignal_did", did);
        setVariable("ROUTE_NAME", routeName);
        exec("Goto", "ringlogic-ivr,ivr,1");
    }

    private void handleExtension(String did, String callerId, String action, 
                                String cdrLogId, String routeName, String channelName) throws AgiException {
        System.out.println("Handling extension/call forward for DID: " + did);
        System.out.println("Call forward numbers: " + action);
        System.out.println("CDR Log ID from dialplan: " + cdrLogId);
        
        // Generate recording file path
        String recordingFilePath = generateRecordingFilePath(callerId, channelName, cdrLogId);
        
        // Set AGI variables for recording
        setVariable("Recording_file", recordingFilePath);
        setVariable("Recording_format", RECORDING_FORMAT);
        setVariable("CDR(userfield)", recordingFilePath + "." + RECORDING_FORMAT);
        
        // Update CDR with recording path (if we have cdrLogId)
        Schedular_repo repo = new Schedular_repo();
        if (cdrLogId != null && !cdrLogId.isEmpty()) {
            try {
                int cdrId = Integer.parseInt(cdrLogId);
                boolean updated = repo.updateCdrWithRecording(cdrId, recordingFilePath + "." + RECORDING_FORMAT);
                System.out.println("CDR recording path updated: " + updated);
            } catch (NumberFormatException e) {
                System.err.println("Invalid CDRLOGID format: " + cdrLogId);
            }
        }
        
        // Process call forward numbers
        String multipleCallForward = buildMultipleCallForward(action);
        setVariable("MULTIPLE_CALL_FORWARD", multipleCallForward);
        
        // Set additional variables
        setVariable("phone_no", action);
        setVariable("phone_no_type", "internal");
        setVariable("ROUTE_NAME", routeName);
        setVariable("orignal_did", did);
        
        // Log for debugging
        System.out.println("Multiple Call Forward string: " + multipleCallForward);
        System.out.println("Recording file: " + recordingFilePath);
        System.out.println("Route to: convox_callforward,callforward,1");
        
        // Route to call forward context
        exec("Goto", "convox-callforward,callforward,1");
    }

    private void handleCallForward(String did, String callerId, String action, 
                                  String cdrLogId, String routeName, String channelName) throws AgiException {
        System.out.println("Handling call forward for DID: " + did);
        
        // Generate recording file path
        String recordingFilePath = generateRecordingFilePath(callerId, channelName, cdrLogId);
        
        // Set AGI variables
        setVariable("Recording_file", recordingFilePath);
        setVariable("Recording_format", RECORDING_FORMAT);
        
        String multipleCallForward = buildMultipleCallForward(action);
        setVariable("MULTIPLE_CALL_FORWARD", multipleCallForward);
        
        setVariable("phone_no", action);
        setVariable("phone_no_type", "internal");
        setVariable("ROUTE_NAME", routeName);
        setVariable("orignal_did", did);
        
        System.out.println("Routing to call forward context");
        exec("Goto", "convox-callforward,callforward,1");
    }

    private void handleVoicemail(String did, String voicemailId, String routeName) throws AgiException {
        System.out.println("Routing to voicemail: " + voicemailId);
        setVariable("voicemail_id", voicemailId);
        setVariable("orignal_did", did);
        setVariable("ROUTE_NAME", routeName);
        exec("Goto", "ringlogic-voicemail,voicemail,1");
    }

    private void handleProcess(String did, String process, String routeName) throws AgiException {
        System.out.println("Routing to process: " + process);
        setVariable("process", process);
        setVariable("orignal_did", did);
        setVariable("ROUTE_NAME", routeName);
        exec("Goto", "ringlogic-process,process,1");
    }

    private void handleQueue(String did, String queueId, String routeName) throws AgiException {
      
        long nowDateEpoch = Instant.now().getEpochSecond();
        setVariable("IVR_END_EPOCH", String.valueOf(nowDateEpoch));
        
        List<String> QueueOptions = new Ivr_repo().getquename(queueId);
        
        String queue_did=QueueOptions.get(0);
        String queue_name=QueueOptions.get(1);

        if (!queue_name.isEmpty()) {
            exec("goto", "ringlogic-direct-queue",queue_did,",1");

            
        }else {

                System.out.println("No queue found for destination: " + queueId);
                // Handle the case where no queue is found (e.g., route to a default IVR, hang up, etc.)
                exec("Goto", "ringlogic-ivr,ivr,1");
        }
        
        System.out.println("Routing to Queue: " + queueId);

    }

    private void handleIP(String did, String ipAddress, String routeName) throws AgiException {
        System.out.println("Routing to IP: " + ipAddress);
        setVariable("ip_address", ipAddress);
        setVariable("orignal_did", did);
        setVariable("ROUTE_NAME", routeName);
        exec("Goto", "ringlogic-ip,ip,1");
    }

    private void handlePlayType(String fileId) throws AgiException {
        Schedular_repo repo = new Schedular_repo();
        List<String> fileInfo = repo.getfileidplay(fileId);

        if (fileInfo != null && fileInfo.size() >= 2) {
            String fileName = fileInfo.get(0);
            String filePath = fileInfo.get(1);
            String fullPath = filePath + "/" + fileName;

            System.out.println("Playing file: " + fullPath);
            
            answer();
            setVariable("COMPLETEDBY", "system");
            streamFile(fullPath);
            hangup();
        } else {
            System.out.println("File not found for ID: " + fileId);
            hangup();
        }
    }

    private void handleComplete() throws AgiException {
        System.out.println("Playing welcome message");
        answer();
        setVariable("COMPLETEDBY", "system");
        streamFile("welcome");
        hangup();
    }

    private void handleMisscall(String did) throws AgiException {
        System.out.println("Routing to misscall handler for DID: " + did);
        exec("Goto", "ringlogic-misscall," + did + ",1");
    }

    private String generateRecordingFilePath(String callerId, String channelName, String cdrLogId) {
        // Generate current date folder structure (yyyy-MM-dd format)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFolder = dateFormat.format(new Date());
        
        // Generate timestamp for unique filename
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        String timestamp = timeFormat.format(new Date());
        
        // Generate random suffix for uniqueness
        Random random = new Random();
        int randomSuffix = random.nextInt(1000);
        
        // Create the recording path
        String basePath = BASE_RECORDING_PATH + dateFolder + "/callforward/";
        
        // Create unique filename
        String filename;
        if (cdrLogId != null && !cdrLogId.isEmpty()) {
            filename = callerId + "_" + cdrLogId + "_" + timestamp + "_" + randomSuffix;
        } else {
            // Use channel name if no cdrLogId
            String channelSuffix = channelName != null ? 
                channelName.replace("/", "_").replace(":", "_") : "unknown";
            filename = callerId + "_" + channelSuffix + "_" + timestamp + "_" + randomSuffix;
        }
        
        // Clean filename (remove any invalid characters)
        filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        String fullPath = basePath + filename;
        System.out.println("Generated recording path: " + fullPath);
        
        return fullPath;
    }

    private String buildMultipleCallForward(String action) {
        if (action == null || action.isEmpty()) {
            System.err.println("No call forward numbers specified");
            return "";
        }
        
        // Split action by comma (for multiple call forward numbers)
        String[] numbers = action.split(",");
        StringBuilder multipleCallForward = new StringBuilder();
        
        for (String number : numbers) {
            // Remove any whitespace and clean the number
            String trimmedNumber = number.trim();
            if (!trimmedNumber.isEmpty()) {
                // Add SIP prefix
                multipleCallForward.append("SIP/").append(trimmedNumber).append("&");
            }
        }
        
        // Remove the trailing '&'
        if (multipleCallForward.length() > 0) {
            multipleCallForward.setLength(multipleCallForward.length() - 1);
        }
        
        String result = multipleCallForward.toString();
        System.out.println("Built call forward string: " + result);
        return result;
    }
}