package main;


import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class to execute a command as in terminal. Taken from:
 * http://stackoverflow.com/questions/26830617/java-running-bash-commands
 *
 * @author mickzer
 */
public class ExecuteShellCommand {

public String executeCommand(String command) {

    StringBuffer output = new StringBuffer();

    Process p;
    try {
        p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = "";           
        while ((line = reader.readLine())!= null) {
            output.append(line + "\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return output.toString();

}

}