package com.fastagi.callinfodb;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.fastagi.config.Database;

public class callinfo {
    

    public void getCallInfo(){

                   DataSource db=Database.getDataSource();
                        try(java.sql.Connection conn=db.getConnection();java.sql.PreparedStatement stmt=conn.prepareStatement("SELECT * FROM `agent_status` LIMIT 1;")){

                                java.sql.ResultSet rs=stmt.executeQuery();
                                if(rs.next()){
                                    System.out.println("Agent Status: "+rs.getString("status"));
                                }   
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
    
    }

public void insertCdrLog(String extension, String callerId, String uniqueId,
                         String channel, String callType, String didNum,
                         String routeName, String callReferenceNo, String callMode) {

    // Get the DataSource
    DataSource db = Database.getDataSource();

    // SQL INSERT query
    String insertQuery = "INSERT INTO cdr_log_buffer " +
            "(server_ip, phone_number, extension, entry_date, uniqueid, channel, " +
            "call_referenceno, call_type, call_mode, traverse_path, did_num, route_name) " +
            "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?)";

    try (java.sql.Connection conn = db.getConnection();
         java.sql.PreparedStatement stmt = conn.prepareStatement(insertQuery, java.sql.Statement.RETURN_GENERATED_KEYS)) {

        // Dummy method to get server IP
        String currentServerIp = "127.0.0.1"; 
        String traversePath = "Route_Name:" + routeName + "~DNID:" + didNum + "~ANI:" + callerId;

        // Set the values for placeholders
        stmt.setString(1, currentServerIp);
        stmt.setString(2, callerId);
        stmt.setString(3, extension);
        stmt.setString(4, uniqueId);
        stmt.setString(5, channel);
        stmt.setString(6, callReferenceNo);
        stmt.setString(7, callType);
        stmt.setString(8, callMode);
        stmt.setString(9, traversePath);
        stmt.setString(10, extension);
        stmt.setString(11, "routeName");

        // Execute the insert
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long cdrLogId = rs.getLong(1);
                    System.out.println("Inserted CDR Log ID: " + cdrLogId);
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
