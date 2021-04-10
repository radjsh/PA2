import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {

	public static void main(String[] args) {
		boolean isMultiple = false;

    	String filename = "100.txt";
    	if (args.length > 0) {
			if (args[0].compareTo("multiple") == 0) {
				isMultiple = true;
			}
			filename = args[0];
		}

    	String serverAddress = "localhost";
    	if (args.length > 1) {
			if (args[1] == "multiple") {
				isMultiple = true;
			}
			filename = args[1];
		}

    	int port = 4321;
    	if (args.length > 2) port = Integer.parseInt(args[2]);

		int numBytes = 0;

		Socket clientSocket = null;

        DataOutputStream toServer = null;
        DataInputStream fromServer = null;

    	FileInputStream fileInputStream = null;
        BufferedInputStream bufferedFileInputStream = null;

		long timeStarted = System.nanoTime();

		try {

			System.out.println("Establishing connection to server...");

			// Connect to server and get the input and output streams
			clientSocket = new Socket(serverAddress, port);
			toServer = new DataOutputStream(clientSocket.getOutputStream());
			fromServer = new DataInputStream(clientSocket.getInputStream());

			System.out.println("isMultiple: " + String.valueOf(isMultiple));

			if (isMultiple == true) {
				Scanner input = new Scanner(System.in);

				while (true) {
					System.out.println("Enter filename: ");
					filename = input.nextLine();
	
					if (filename.compareTo("exit") == 0) {
						System.out.println("Exiting");
						break;
					}

					// Send the filename
					toServer.writeInt(0); // "0" links to FILNENAME
					toServer.writeInt(filename.getBytes().length);
					System.out.println("numBytes: " + String.valueOf(filename.getBytes().length));
					toServer.write(filename.getBytes());
					System.out.println("filename: " + String.valueOf(filename.getBytes().toString()));
					toServer.flush();

					// Open the file
					fileInputStream = new FileInputStream(filename);
					bufferedFileInputStream = new BufferedInputStream(fileInputStream);

					byte [] fromFileBuffer = new byte[117];

					System.out.println("Sending file...");

					// Send the file
					for (boolean fileEnded = false; !fileEnded;) {
						//numBytes = number of bytes to be read from the buffer
						numBytes = bufferedFileInputStream.read(fromFileBuffer);
						fileEnded = numBytes < 117;

						toServer.writeInt(1); // "1" links to FILE CONTENT
						toServer.writeInt(numBytes);
						toServer.write(fromFileBuffer);
						toServer.flush();
					}	

				}

				
				

			}



	        bufferedFileInputStream.close();
	        fileInputStream.close();

			System.out.println("Closing connection...");
		

		} catch (Exception e) {e.printStackTrace();}

		long timeTaken = System.nanoTime() - timeStarted;
		System.out.println("Program took: " + timeTaken/1000000.0 + "ms to run");
	}
}
