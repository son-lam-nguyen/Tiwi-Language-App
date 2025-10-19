package com.example.tiwilanguageapp;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    public String id;
    public String text;
    public String english;

    // set by Teacher after recording
    public String teacherRecordingUrl;

    public String category;

    // list of student takes
    public List<StudentRec> studentRecordings = new ArrayList<>();
}
