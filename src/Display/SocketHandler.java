package Display;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class SocketHandler {
    protected final Socket socket;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    public SocketHandler(Socket socket){
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            System.out.println("Unable to get input/output streams.");
            close();
        }
    }
    public void close() {
        try {
            List<Closeable> objects = Arrays.asList(input,output,socket);
            for (Closeable object : objects) {
                if (object != null) object.close();
            }
        }
        catch (IOException e) {
            System.out.println("Error closing all objects...");
        }
    }
}
