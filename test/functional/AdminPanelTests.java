package functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

//import org.seleniumhq.selenium.Webdriver;
//import org.seleniumhq.selenium.By;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;



import java.io.File;
///Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome

public class AdminPanelTests {

    WebDriver d;
    Process femrAppProcess;

    /*
    * CLI Message Formatting
    **/
    private void gb(String consolemsg){ System.out.println("[\033[42m" + "  " + "\033[0m] " + consolemsg); }
    private void rb(String consolemsg){ System.out.println("[\033[41m" + "  " + "\033[0m] " + consolemsg); }
    private void nb(String consolemsg){ System.out.println("[\033[46m" + "  " + "\033[0m] " + consolemsg); }
    private void gb(String blockmsg, String consolemsg){System.out.println("[\033[42m" + blockmsg + "\033[0m] " + consolemsg); }
    private void rb(String blockmsg, String consolemsg){System.out.println("[\033[41m" + blockmsg + "\033[0m] " + consolemsg); }
    private void nb(String blockmsg, String consolemsg){System.out.println("[\033[46m" + blockmsg + "\033[0m] " + consolemsg); }
    private void banner(String bannermsg){
        int leftLength;
        int rightLength;
        if(bannermsg.length() % 2 == 0){
            leftLength = (80 - bannermsg.length())/2;
            rightLength = (80 - bannermsg.length())/2;
        } else {
            leftLength = (80 - bannermsg.length())/2;
            rightLength = (80 - bannermsg.length())/2 + 1;
        }
        System.out.println("\033[44m"
                + new String(new char[leftLength]).replace("\0", " ")
                + "\033[37m\033[1m"
                + bannermsg
                + new String(new char[rightLength]).replace("\0", " ")
                + "\033[0m"
        );
    }


    @Before
    public void startFEMR(){
        banner("AdminPanelTest");
        nb("setup", "Starting AdminPanelTest...");
        //Produce production script for fEMR for testing, then unzip.
        try {
            nb("setup", "Doing `sbt clean compile dist`.");
            ProcessBuilder pb = new ProcessBuilder("sbt","clean","compile","dist");
            pb.directory(new File("."));
            pb.redirectError();
            pb.redirectOutput();
            Process p = pb.start();
            p.waitFor();
            gb("setup", "Created fEMR production executable.");

//            //get name of zip
            String zipfilename = new File("./target/universal").listFiles(
                    new FilenameFilter() {
                        public boolean accept(File dir, String name) {return name.toLowerCase().endsWith(".zip");}
                    })[0].toString();
            pb = new ProcessBuilder("unzip", "./target/universal/" + zipfilename, "prodTest" );
            pb.directory(new File("."));
            pb.redirectError();
            pb.redirectOutput();
            p = pb.start();
            p.waitFor();
            gb("setup", "Unzipped fEMR script.");
        } catch (Exception e){
            rb("setup", "Failed to build production executable.");
            e.printStackTrace();
        }
        //Run created production script

        //init Webdriver and send it to fEMR
        d = new ChromeDriver();
        gb("setup", "Got ChromeDriver");
        d.get("https://www.google.com/");
    }
    @Test
    public void dontCrash(){}

    @After
    public void tearDown(){
        nb("teardown", "Beginning AdminPanelTests Teardown...");
        d.quit();
        gb("teardown", "Quit WebDriver");
        nb("teardown", "Replacing dist femr/target with clean compile femr/target");
        try {
            FileUtils.deleteDirectory(new File("/Users/Olesh/Documents/SchoolArchives/CSC4111/femr/target"));
            gb("teardown", "Deleted dist target");
            nb("teardown", "Creating clean compile femr/target");
            ProcessBuilder pb = new ProcessBuilder("sbt", "clean", "compile");
            pb.directory(new File("/Users/Olesh/Documents/SchoolArchives/CSC4111/femr"));
            pb.redirectError();
            pb.redirectOutput();
            Process p = pb.start();
            p.waitFor();
            gb("teardown", "Created clean compile femr/target");
            gb("teardown", "Replaced clean compile femr/target");
        } catch (Exception e){
            e.printStackTrace();
            rb("teardown", "Failed to Replace femr/target");
        }

    }

}
