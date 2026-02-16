package com.fastagi.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.fastagi.config.Database;
import com.fastagi.models.InnerIvr;

public class Ivr_repo {
     private DataSource db = Database.getDataSource();

     public List<InnerIvr> get_ivrdata(String ivr_id) {

        String sql = "SELECT I.*, F.File_Name, F.File_Location FROM xpress_ivr AS I LEFT JOIN xpress_files AS F ON I.voice_file=F.File_id WHERE ivr_id=?";
        
        try(Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ivr_id);
            ResultSet rs = stmt.executeQuery();
            List<InnerIvr> ivrData = new ArrayList<>();
            while (rs.next()) {
                ivrData.add(new InnerIvr(
                    rs.getString("ivr_id"),
                    rs.getString("ivr_name"),
                    rs.getString("voice_file"),
                    rs.getString("seconds_to_wait"),
                    rs.getString("repeats"),
                    rs.getString("direct_call"),
                    rs.getString("File_Name"),
                    rs.getString("File_Location")
                ));
            }

            System.out.println("IVR data retrieved for ivr_id: " + ivr_id);
            return ivrData;
        } catch (SQLException e) {
            System.err.println("Database error in get_ivrdata: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
     }

public List<String> get_ivr_options(String ivr_id,String option_num) {

    String sql = "SELECT * FROM xpress_ivroption WHERE ivr_id=? AND option_num=? ORDER BY option_num ASC";
    
    try(Connection conn = db.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, ivr_id);
        stmt.setString(2, option_num);
        ResultSet rs = stmt.executeQuery();

        List<String> ivrOptions = new ArrayList<>();
        while (rs.next()) {
            ivrOptions.add(rs.getString("ivr_id"));
            ivrOptions.add(rs.getString("option_num"));
            ivrOptions.add(rs.getString("option_key"));
            ivrOptions.add(rs.getString("destination"));


        
        }
        return ivrOptions;
    } catch (SQLException e) {
        System.err.println("Database error in get_ivr_options: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
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
