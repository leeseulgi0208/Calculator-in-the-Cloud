import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CalcClientEx {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            ServerConfig serverConfig = ConfigReader.readConfig("server_info.dat");
            Socket socket = new Socket(serverConfig.getIp(), serverConfig.getPort());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                while (true) {
                    System.out.print("Enter expression (format: OPERATION OP1 OP2) or 'bye' to exit: ");
                    String expression = scanner.nextLine();

                    if (expression.equalsIgnoreCase("bye")) {
                        out.write("bye\n");
                        out.flush();
                        break;
                    }

                    out.write(expression + "\n");
                    out.flush();

                    String result = in.readLine();
                    System.out.println("Server response: " + result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
