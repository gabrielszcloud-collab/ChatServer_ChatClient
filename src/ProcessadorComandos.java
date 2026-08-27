import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ProcessadorComandos
 * --------------------
 * Centraliza a lógica de "quais comandos existem e o que cada um retorna".
 *
 * Decisão de projeto importante:
 * Em vez de repassar a string recebida do cliente diretamente para o
 * shell do sistema operacional (ex.: Runtime.exec(comando) ou
 * ProcessBuilder), usamos um mapa fechado de comandos conhecidos em
 * tempo de compilação.
 *
 * É exatamente essa escolha que separa este laboratório didático de
 * um backdoor real: aqui a superfície de ataque é zero, porque o
 * servidor nunca interpreta a entrada do cliente como comando de SO —
 * ele só reconhece um vocabulário fixo e pré-definido.
 */
public class ProcessadorComandos {

    private final Map<String, Supplier<String>> comandos = Map.of(
            "data", () -> LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            "hora", () -> LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            "datetime", () -> LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            "status", () -> "Servidor ativo e operando normalmente.",
            "help", () -> "Comandos disponiveis: help, data, hora, datetime, status, exit"
    );

    /** Verifica se o comando recebido deve encerrar a conexão. */
    public boolean isComandoDeSaida(String comando) {
        return comando.equals("exit") || comando.equals("quit");
    }

    /**
     * Resolve o comando recebido dentro do vocabulário fechado.
     * Qualquer entrada fora da lista cai no valor padrão (getOrDefault),
     * nunca é executada como comando do sistema operacional.
     */
    public String processar(String comando) {
        return comandos
                .getOrDefault(comando, () ->
                        "Comando nao permitido. Digite 'help' para consultar os comandos.")
                .get();
    }
}
