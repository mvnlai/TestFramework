package com.testframework.fileio;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class WriteFile {

    //work in progress
    private String path;
    private boolean append_to_file = false;
    
    public WriteFile(String file_path, boolean append_value){
        
        path = file_path;
        append_to_file = append_value;
        
    }
    
    public void WriteToFile(String Value) throws IOException {
        FileWriter write = new FileWriter(path, append_to_file);
        PrintWriter print_line = new PrintWriter(write);
         
        print_line.printf("%s" + "%n", Value); //"%s" means string of any length, "%n" means newline
        print_line.close();
    }
    
    
}
