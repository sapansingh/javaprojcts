package com.fastagi.callinfodb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.fastagi.config.Database;

public class Routedb {
    

 public List<String> getRouteStatusList(String didnum) {

    List<String> statusList = new ArrayList<>();
    DataSource db = Database.getDataSource();

    String sql = "SELECT * FROM xpress_routes WHERE did_num = ? AND ACTIVE = 'Y'";

    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, didnum);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            statusList.add(rs.getString("Application_Type"));
            statusList.add(rs.getString("Application_Value"));
            
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return statusList;
}

public List<String> getivrvoice(String ivrname) {

    List<String> statusList = new ArrayList<>();
    DataSource db = Database.getDataSource();

    String sql = "SELECT * FROM `xpress_ivr` JOIN `xpress_files` ON xpress_ivr.voice_file=xpress_files.File_id  WHERE ivr_name='secondivr'";

    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, ivrname);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            statusList.add(rs.getString("Application_Type"));
            statusList.add(rs.getString("Application_Value"));
            
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return statusList;
}


}
