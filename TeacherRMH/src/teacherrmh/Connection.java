package teacherrmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
*
* @author Sam
*/
public class Connection {

    //socket: the Socket that this connection is connected to
    Socket socket;
    //for reading from the socket
    BufferedReader input;
    //The username of the user on the other end of this connection
    String username;
    //The two threads that this connection uses
    Thread thread, thread2;
    //Is this connection still operating
    private volatile boolean running = true;

    public Connection(Socket s, int n) {
        //this socket is the one that represents the connection accepted in MainClass
        socket = s;
        //try to initialize the BufferedReader for this socket
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
        }
        //Thread for accepting commands fromt the client
        thread = new Thread() {
            public void run() {
                //while this connection is still operating
                while (running) {
                    String read = null;
                    //Read input from the socket
                    try {
                        read = input.readLine();
                    } catch (IOException ex) {
                    }
                    //Process the input read
                    processInput(read);
                }
            }
        };
        //Start the thread
        thread.start();
        //Thread for sending the command for putting a client's hand down
        //Not currently functional
        //No comments on this for now
        thread2 = new Thread() {
            public void run() {
                while (running) {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        //out.println(" ");
                    } catch (Exception ex) {
                        System.out.println("DISCONNECTED");
                    }
                    //System.out.println("1st check: " + MainClass.textField.getText().indexOf(username));
                    if (MainClass.removeFirstInLine) {

                        if (MainClass.textField.getText().indexOf(username) == 0) {
                            PrintWriter out = null;
                            try {
                                out = new PrintWriter(socket.getOutputStream(), true);
                            } catch (IOException ex) {
                            }
                            out.println("Teacher is putting your hand DOWN");
                            MainClass.removeFirstInLine = false;
                        }
                    }
                }
            }
        };
        thread2.start();
    }
    public String getName (){
        return username;
    }
    
    public void processInput(String in) {
		if (in != null) {System.out.println(in);}
		
		String screenID = in.substring(0, in.length()-1);
		boolean state = in.substring(in.length()-1).equals("T");
        //Get the text that is currently in the text field
        String txt = MainClass.textField.getText();

        //If the user closes their application
        if (in.equals("QUIT")) {
            try {
                //Stop the thread from running
                running = false;
                //Close the socket
                socket.close();
            } catch (IOException ex) {
            }
        }
        
        //If recieving the username
		else if (in.startsWith("USERNAME:")) {
            //Set the variable username to the name recieved
            username = in.substring(in.indexOf(":") + 1);
        }
        //If the command for raising hand is recieved
		else if (state==true) {
            //Add the user's name with the ASSIST tag at the end
            txt = txt + (screenID + " - " + username + "\n\r");
            //Update the text
            MainClass.textField.setText(txt);
        }
		
        //If the command for putting hand down is recieved
		else if (state==false) {
            //Find and remove the user's name if it begins with 'ASSIST - '
            try {
				int ma = txt.indexOf(screenID + " - " + username);
                txt = txt.substring(0, ma) + txt.substring(ma + username.length() + 5 + screenID.length());	// 6 comes from "# - " ... "\n\r"
            }
            catch (StringIndexOutOfBoundsException e) {}
            //Update the text
            MainClass.textField.setText(txt);
        }
    }
}

