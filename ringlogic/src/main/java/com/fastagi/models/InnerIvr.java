package com.fastagi.models;

public class InnerIvr {

    String ivr_id;
    String ivr_name;
    String voice_file;
    String seconds_to_wait;
    String repeats;
    String direct_call;
    String File_Name;
    String File_Location;
  
public InnerIvr(String ivr_id, String ivr_name, String voice_file, String seconds_to_wait, String repeats, String direct_call, String File_Name, String File_Location) {
        this.ivr_id = ivr_id;
        this.ivr_name = ivr_name;
        this.voice_file = voice_file;
        this.seconds_to_wait = seconds_to_wait;
        this.repeats = repeats;
        this.direct_call = direct_call;
        this.File_Name = File_Name;
        this.File_Location = File_Location;
    }   
    

    public String getIvr_id() {
        return this.ivr_id;
    }

    public void setIvr_id(String ivr_id) {
        this.ivr_id = ivr_id;
    }

    public String getIvr_name() {
        return this.ivr_name;
    }

    public void setIvr_name(String ivr_name) {
        this.ivr_name = ivr_name;
    }

    public String getVoice_file() {
        return this.voice_file;
    }

    public void setVoice_file(String voice_file) {
        this.voice_file = voice_file;
    }

    public String getSeconds_to_wait() {
        return this.seconds_to_wait;
    }

    public void setSeconds_to_wait(String seconds_to_wait) {
        this.seconds_to_wait = seconds_to_wait;
    }

    public String getRepeats() {
        return this.repeats;
    }

    public void setRepeats(String repeats) {
        this.repeats = repeats;
    }

    public String getDirect_call() {
        return this.direct_call;
    }

    public void setDirect_call(String direct_call) {
        this.direct_call = direct_call;
    }

    public String getFile_Name() {
        return this.File_Name;
    }

    public void setFile_Name(String File_Name) {
        this.File_Name = File_Name;
    }

    public String getFile_Location() {
        return this.File_Location;
    }

    public void setFile_Location(String File_Location) {
        this.File_Location = File_Location;
    }


}
