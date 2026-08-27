import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente
 * -------
 * Ponta que inicia a conexão TCP e envia comandos ao Servidor.
 *
 * Ciclo de vida do socket do lado cliente:
 *   1. new Socket(HOST, PORTA)                    -> socket() + connect()
 *   2. getOutputStream()/getInputStream()          -> send()/recv()
 *   3. Encerramento explícito ao digitar "exit"/"quit"
 */
public class Cliente {

    private static final String HOST = "127.0.0.1";
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE - LABORATORIO DE SOCKETS TCP ===");

        try (
                Socket socket = new Socket(HOST, PORTA);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao servidor " + HOST + ":" + PORTA);

            // Lê as duas mensagens de boas-vindas enviadas pelo servidor
            System.out.println(entrada.readLine());
            System.out.println(entrada.readLine());

            while (true) {
                System.out.print("\nDigite um comando: ");
                String comando = scanner.nextLine();

                saida.println(comando);
                String resposta = entrada.readLine();

                if (resposta != null) {
                    System.out.println("Resposta do servidor: " + resposta);
                }

                if (comando.equalsIgnoreCase("exit")
                        || comando.equalsIgnoreCase("quit")) {
                    break;
                }
            }

            System.out.println("\nCliente encerrado.");

        } catch (ConnectException e) {
            System.err.println("Nao foi possivel conectar ao servidor.");
            System.err.println("Verifique se o Servidor.java esta em execucao.");
        } catch (IOException e) {
            System.err.println("Erro na comunicacao: " + e.getMessage());
        }
    }
}
