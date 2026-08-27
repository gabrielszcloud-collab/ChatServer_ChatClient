import java.io.*;
import java.net.*;

/**
 * Servidor
 * --------
 * Ponta que aguarda uma conexão TCP e delega o processamento de
 * comandos para ProcessadorComandos.
 *
 * Ciclo de vida do socket do lado servidor:
 *   1. new ServerSocket(PORTA)      -> socket() + bind() + listen()
 *   2. serverSocket.accept()        -> accept() (bloqueante até um
 *                                       cliente se conectar)
 *   3. getInputStream()/getOutputStream() -> recv()/send()
 *   4. Encerramento explícito ao receber "exit"/"quit"
 *
 * Toda a lógica de "o que cada comando faz" fica em ProcessadorComandos,
 * mantendo esta classe focada apenas na parte de rede.
 */
public class Servidor {

    private static final int PORTA = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR - LABORATORIO DE SOCKETS TCP ===");
        System.out.println("Porta: " + PORTA);

        // try-with-resources: o ServerSocket é fechado automaticamente
        // ao sair do bloco, mesmo em caso de exceção.
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {

            System.out.println("Aguardando conexao em 127.0.0.1:" + PORTA + "...");

            // accept() bloqueia até um cliente conectar. O retorno é um
            // NOVO socket, dedicado exclusivamente a essa conexão.
            try (
                    Socket socket = serverSocket.accept();
                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter saida = new PrintWriter(
                            socket.getOutputStream(), true)
            ) {
                System.out.println("Cliente conectado: "
                        + socket.getInetAddress().getHostAddress());

                ProcessadorComandos processador = new ProcessadorComandos();

                saida.println("Conexao estabelecida com o servidor.");
                saida.println("Digite 'help' para consultar os comandos.");

                String linha;
                while ((linha = entrada.readLine()) != null) {
                    String comando = linha.trim().toLowerCase();
                    System.out.println("Comando recebido: " + comando);

                    if (processador.isComandoDeSaida(comando)) {
                        saida.println("Conexao encerrada pelo cliente.");
                        System.out.println("Cliente solicitou encerramento.");
                        break;
                    }

                    saida.println(processador.processar(comando));
                }
            }

            System.out.println("Servidor encerrado.");

        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
