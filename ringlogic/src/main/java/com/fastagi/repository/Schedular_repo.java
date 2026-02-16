package com.fastagi.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.fastagi.config.Database;

public class Schedular_repo {
    private DataSource db = Database.getDataSource();

    public List<String> getSchedularInfo(String did) {
        List<String> schedularInfo = new ArrayList<>();
        System.out.println("Fetching schedular info for DID: " + did);

        String sql = "SELECT Application_Type, Application_Value, Route_Name " +
                     "FROM xpress_routes WHERE did_num = ? AND ACTIVE = 'Y' LIMIT 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, did);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                schedularInfo.add(rs.getString("Application_Type"));
                schedularInfo.add(rs.getString("Application_Value"));
                schedularInfo.add(rs.getString("Route_Name"));
                System.out.println("Found route: " + schedularInfo);
            } else {
                System.out.println("No active route found for DID: " + did);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getSchedularInfo: " + e.getMessage());
            e.printStackTrace();
        }
        return schedularInfo;
    }

    public List<String> getfileidplay(String fileid) {
        List<String> fileinfo = new ArrayList<>();
        String sql = "SELECT File_Name, File_Location FROM xpress_files WHERE file_id = ? LIMIT 1";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fileid);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                fileinfo.add(rs.getString("File_Name"));
                fileinfo.add(rs.getString("File_Location"));
                System.out.println("Found file info: " + fileinfo);
            } else {
                System.out.println("File not found for ID: " + fileid);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getfileidplay: " + e.getMessage());
            e.printStackTrace();
        }
        return fileinfo;
    }

    public boolean updateCdrWithRecording(int cdrId, String recordingFilePath) {
        String sql = "UPDATE cdr_log_buffer SET recording_file = ? WHERE cdr_id = ? LIMIT 1";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recordingFilePath);
            stmt.setInt(2, cdrId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("Updated CDR recording path. File: " + recordingFilePath + 
                             ", Affected rows: " + affectedRows);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error in updateCdrWithRecording: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public String getivrid(String ivrname){
        String ivrid = null;
        String sql = "SELECT ivr_id FROM xpress_ivr WHERE ivr_name = ? LIMIT 1";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ivrname);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ivrid = rs.getString("ivr_id");
                System.out.println("Found IVR ID: " + ivrid + " for IVR Name: " + ivrname);
            } else {
                System.out.println("IVR not found for Name: " + ivrname);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getivrid: " + e.getMessage());
            e.printStackTrace();
    }

return ivrid;
}
}