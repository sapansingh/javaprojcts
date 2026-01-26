package com.fastagi.callinfodb;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.fastagi.config.Database;

public class checkagentstatus {
    

    public void getAgentStatus(){

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



}
