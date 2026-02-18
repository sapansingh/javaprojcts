package com.fastagi.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.fastagi.config.Database;

public class Acd_log_repo {

      private DataSource db = Database.getDataSource();



      public Map<String, String> get_queue_data(String queue_did) {
        String sql = "SELECT * FROM queues WHERE queue_did=?";


        try(Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, queue_did);
            ResultSet rs = stmt.executeQuery();
            Map<String, String> queueData = new HashMap<>();
            if (rs.next()) {
                queueData.put("queue_id", rs.getString("queue_id"));
                queueData.put("queue_name", rs.getString("queue_name"));
                queueData.put("queue_did", rs.getString("queue_did"));
                queueData.put("process", rs.getString("queue_assigned_process"));
                 queueData.put("queue_length", rs.getString("queue_length"));
                System.out.println("Queue data retrieved for queue_did: " + queue_did);
            } else {
                System.out.println("No queue found for queue_did: " + queue_did);
            }
            return queueData;
        } catch (SQLException e) {
            System.err.println("Database error in get_queue_data: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
   
    
}




public Map<String,String> getdnc(String process){

    String sql = "SELECT dnc_check, allow_q, agent_not_available_file FROM process_details WHERE process=?";
    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, process);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> dncData = new HashMap<>();
        if (rs.next()) {
            dncData.put("dnc_check", rs.getString("dnc_check"));
            dncData.put("allow_q", rs.getString("allow_q"));
            dncData.put("agent_not_available_file", rs.getString("agent_not_available_file"));
            System.out.println("DNC data retrieved for process: " + process);
        } else {
            System.out.println("No DNC data found for process: " + process);
        }
        return dncData;
    } catch (SQLException e) {
        System.err.println("Database error in getdnc: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}



public int insert_waiting_calls(String callerid,String process,String queue_name,String channel,String UniqueID,String dialer_call_mode,String call_hit_referencen,String dialing_trunk_id,String ipaddres){
        
    String sql="INSERT INTO waiting_calls(phone_number,process,queue_name,lead_id,channel_name,entry_time,call_uniqueid,call_mode,call_hit_referenceno,trunk_id,server_ip,filename) VALUES (?,?,?,?,?,NOW(),?,?,?,?,?,?)";



    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, callerid);
        stmt.setString(2, process);
        stmt.setString(3, queue_name);
        stmt.setInt(4, 0 );
        stmt.setString(5, channel);
        stmt.setString(6, UniqueID);
        stmt.setString(7, dialer_call_mode);
        stmt.setString(8, call_hit_referencen);
        stmt.setInt(9, 0);
        stmt.setString(10, ipaddres);
        stmt.setString(11, "waiting_call");

        int rowsAffected = stmt.executeUpdate();
        System.out.println("Rows affected in insert_waiting_calls: " + rowsAffected);
        return rowsAffected;
    } catch (SQLException e) {
        System.err.println("Database error in insert_waiting_calls: " + e.getMessage());
        e.printStackTrace();
    }
    return 0;
}


public Map<String,String> getqueuecount(String process){

 String sql="SELECT COUNT(*) AS QUEUE_COUNT FROM queues WHERE queue_assigned_process=?";   

    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, process);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> queueCount = new HashMap<>();
        if (rs.next()) {
            queueCount.put("queue_count", rs.getString("QUEUE_COUNT"));
            System.out.println("Queue count retrieved for process: " + process + ", Count: " + rs.getString("QUEUE_COUNT"));
        } else {
            queueCount.put("queue_count", "0");
            System.out.println("No queue count found for process: " + process);
        }
        return queueCount;
    } catch (SQLException e) {
        System.err.println("Database error in getqueuecount: " + e.getMessage());
        e.printStackTrace();
    }

    return null;

}


public Map<String,String> getinqueue(String Queue_name,String process){


    String sql="SELECT queue_id,queue_did,greeting_file_id,call_queue_file_id,queue_drop_time,queue_drop_action,queue_drop_value,queue_length,queue_over_flow FROM queues WHERE queue_name=? AND  queue_assigned_process=?";

    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, Queue_name);
        stmt.setString(2, process);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> queueData = new HashMap<>();
        if (rs.next()) {
            queueData.put("queue_id", rs.getString("queue_id"));
            queueData.put("queue_did", rs.getString("queue_did"));
            queueData.put("greeting_file_id", rs.getString("greeting_file_id"));
            queueData.put("call_queue_file_id", rs.getString("call_queue_file_id"));
            queueData.put("queue_drop_time", rs.getString("queue_drop_time"));
            queueData.put("queue_drop_action", rs.getString("queue_drop_action"));
            queueData.put("queue_drop_value", rs.getString("queue_drop_value"));
            queueData.put("queue_length", rs.getString("queue_length"));
            queueData.put("queue_over_flow", rs.getString("queue_over_flow"));
            System.out.println("Queue data retrieved for Queue_name: " + Queue_name + ", process: " + process);
        } else {
            System.out.println("No queue found for Queue_name: " + Queue_name + ", process: " + process);
        }
        return queueData;
    } catch (SQLException e) {
        System.err.println("Database error in getinqueue: " + e.getMessage());
        e.printStackTrace();
    }






return null;
}






public Map<String,String> getfilename(String file_id){

    String sql="SELECT File_Name As file_name,File_Location as file_location FROM xpress_files WHERE File_id=?";

    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, file_id);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> fileData = new HashMap<>();
        if (rs.next()) {
            fileData.put("file_name", rs.getString("file_name"));
            fileData.put("file_location", rs.getString("file_location"));
            System.out.println("File data retrieved for file_id: " + file_id);
        } else {
            System.out.println("No file found for file_id: " + file_id);
        }
        return fileData;
    } catch (SQLException e) {
        System.err.println("Database error in queueProcess: " + e.getMessage());
        e.printStackTrace();
    }




    return null;

}


public Map<String,String> getwaiitngcount(String process,String queue_name){

    String sql="SELECT COUNT(*) AS waiting_calls FROM waiting_calls WHERE process=? AND queue_name=?";
    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, process);
        stmt.setString(2, queue_name);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> waitingCount = new HashMap<>();
        if (rs.next()) {
            waitingCount.put("waiting_calls", rs.getString("waiting_calls"));
            System.out.println("Waiting calls count retrieved for process: " + process + ", queue_name: " + queue_name + ", Count: " + rs.getString("waiting_calls"));
        } else {
            waitingCount.put("waiting_calls", "0");
            System.out.println("No waiting calls found for process: " + process + ", queue_name: " + queue_name);
        }
        return waitingCount;
    } catch (SQLException e) {
        System.err.println("Database error in getwaiitngcount: " + e.getMessage());
        e.printStackTrace();
    }



    return null;
}


public int update_waiting_cals(String queue_name,int lead_id,int wait_id){

    String sql="UPDATE waiting_calls SET queue_name=? , lead_id=? WHERE wait_id=?";
    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, queue_name);
        stmt.setInt(2, lead_id);
        stmt.setInt(3, wait_id);

        int rowsAffected = stmt.executeUpdate();
        System.out.println("Rows affected in update_waiting_cals: " + rowsAffected);
        return rowsAffected;
    } catch (SQLException e) {
        System.err.println("Database error in update_waiting_cals: " + e.getMessage());
        e.printStackTrace();
    }


return 0;
}

public Map<String,String> getcheckplayque(String queue_name){
    String sql="select play_queue_no,play_hold_durn,queue_no_file,hold_durn_file,playback_freq from queues where queue_name=? and (play_queue_no='Y' or play_hold_durn='Y')";
    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, queue_name);
        ResultSet rs = stmt.executeQuery();
        Map<String, String> playQueueData = new HashMap<>();
        if (rs.next()) {
            playQueueData.put("play_queue_no", rs.getString("play_queue_no"));
            playQueueData.put("play_hold_durn", rs.getString("play_hold_durn"));
            playQueueData.put("queue_no_file", rs.getString("queue_no_file"));
            playQueueData.put("hold_durn_file", rs.getString("hold_durn_file"));
            playQueueData.put("playback_freq", rs.getString("playback_freq"));
            System.out.println("Play queue data retrieved for queue_name: " + queue_name);
        } else {
            System.out.println("No play queue data found for queue_name: " + queue_name);
        }
        return playQueueData;
    } catch (SQLException e) {
        System.err.println("Database error in getcheckplayque: " + e.getMessage());
        e.printStackTrace();
    }



    return null;
}


}