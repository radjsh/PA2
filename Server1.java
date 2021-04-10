import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server1 {

	public static void main(String[] args) {

    	int port = 4321;
    	if (args.length > 0) port = Integer.parseInt(args[0]);

		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		DataOutputStream toClient = null;
		DataInputStream fromClient = null;

		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferedFileOutputStream = null;

		try {
			welcomeSocket = new ServerSocket(port);
			connectionSocket = welcomeSocket.accept();
			fromClient = new DataInputStream(connectionSocket.getInputStream());
			toClient = new DataOutputStream(connectionSocket.getOutputStream());

			while (!connectionSocket.isClosed()) {


				int packetType = fromClient.readInt();

				// If the packet is for transferring the filename
				if (packetType == 0) {

					System.out.println("Receiving file...");

					int numBytes = fromClient.readInt(); 
					System.out.println("I AM HERE 01");
					byte [] filename = new byte[numBytes];
					System.out.println("I AM HERE 02");
					// Must use read fully!
					// See: https://stackoverflow.com/questions/25897627/datainputstream-read-vs-datainputstream-readfully
					try { fromClient.readFully(filename, 0, numBytes);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("IOEXCEPTION CAUGHT");
					}
					System.out.println("I AM HERE 03");

					if (new String(filename, 0, numBytes).compareTo("exit") == 0) {
						System.out.println("EXITING");
						break;
					}

					System.out.println("FILEPATH: recv_"+new String(filename, 0, numBytes));

					fileOutputStream = new FileOutputStream("recv_"+new String(filename, 0, numBytes));
					bufferedFileOutputStream = new BufferedOutputStream(fileOutputStream);

				// If the packet is for transferring a chunk of the file
				} else if (packetType == 1) {
					System.out.println("Entered packetType == 1");

					int numBytes = fromClient.readInt();
					byte [] block = new byte[numBytes];
					fromClient.readFully(block, 0, numBytes);

					if (numBytes > 0)
						bufferedFileOutputStream.write(block, 0, numBytes);

					if (numBytes < 117) {
						if (bufferedFileOutputStream != null) bufferedFileOutputStream.close();
						if (bufferedFileOutputStream != null) fileOutputStream.close();
						System.out.println("Files Closed");
					} 
				}
			}

			System.out.println("Closing connection...");
			fromClient.close();
			toClient.close();
			connectionSocket.close();

		} catch (Exception e) {e.printStackTrace();}

	}

}
