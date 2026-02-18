package com.fastagi.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.sql.Statement;
import org.checkerframework.checker.units.qual.radians;

import com.fastagi.config.Database;

public class Cdr_log_repo {
    private DataSource db = Database.getDataSource();


    public String getroutenameString(String did) {

        System.out.println("Fetching route name for DID: " + did);
        String routequery="SELECT Route_Name FROM xpress_routes WHERE did_num = ? AND ACTIVE = 'Y' LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(routequery)) {

            stmt.setString(1, did);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String routeName = rs.getString("Route_Name");
                System.out.println("Found route: " + routeName);
                return routeName;
            } else {
                System.out.println("No active route found for DID: " + did);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Database error in getroutenameString: " + e.getMessage());
            e.printStackTrace();
            return "NA";
        }       

        
    }
       
    public String setcdrlog(String ip, String callerid, String exten,
                       String uniqueId, String channel, String callReferenceNo, 
                       String callType, String callMode, String traverse_path, 
                       String dnid, String Route_Name) {
    
    String query = "INSERT INTO cdr_log_buffer " +
                  "SET server_ip=?, phone_number=?, extension=?, " +
                  "entry_date=NOW(), uniqueid=?, channel=?, " +
                  "call_referenceno=?, call_type=?, call_mode=?, " +
                  "traverse_path=?, did_num=?, route_name=?";
    
    String insertedId = "0";
    
    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
         
        stmt.setString(1, ip);
        stmt.setString(2, callerid);
        stmt.setString(3, exten);
        stmt.setString(4, uniqueId);
        stmt.setString(5, channel);
        stmt.setString(6, callReferenceNo);
        stmt.setString(7, callType);
        stmt.setString(8, callMode);
        stmt.setString(9, traverse_path);
        stmt.setString(10, dnid);
        stmt.setString(11, Route_Name);
        
        int rowsAffected = stmt.executeUpdate();
        
        if (rowsAffected > 0) {
            // Get the generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    insertedId = generatedKeys.getString(1);
                    System.out.println("CDR log inserted successfully. ID: " + insertedId);
                }
            }
        }
        
    } catch (SQLException e) {
        System.err.println("Database error in setcdrlog: " + e.getMessage());
        e.printStackTrace();
    }
    
    return insertedId;
}



}