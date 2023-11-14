import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CalcServerEx {

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                while (true) {
                    String inputMessage = in.readLine();
                    if (inputMessage == null || inputMessage.equalsIgnoreCase("bye")) {
                        System.out.println("Client disconnected.");
                        break;
                    }

                    System.out.println("Received expression: " + inputMessage);

                    String res = calc(inputMessage);
                    out.write(res + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static ServerConfig serverConfig;

    static {
        serverConfig = ConfigReader.readConfig("server_info.dat");
    }

    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() != 3)
            return "Incorrect: Invalid expression format";
        String res = "";
        String opcode = st.nextToken();
        int op1 = Integer.parseInt(st.nextToken());
        int op2 = Integer.parseInt(st.nextToken());
        switch (opcode) {
            case "ADD":
                res = Integer.toString(op1 + op2);
                break;
            case "SUB":
                res = Integer.toString(op1 - op2);
                break;
            case "MUL":
                res = Integer.toString(op1 * op2);
                break;
            case "DIV":
                if (op2 == 0)
                    res = "Incorrect: Division by zero";
                else
                    res = Integer.toString(op1 / op2);
                break;
            default:
                res = "Incorrect: Invalid operation";
        }
        return res;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected.");
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
