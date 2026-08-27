ALUNOS:

Gabriel Ferreira de Souza__202410076
Murillo de Sousa Sales__

# Laboratório de Sockets TCP em Java

## Identificação

**Disciplina:** Segurança da Informação
**Curso:** ADS

**Alunos:** [Gabriel Ferreira de Souza__202410076]
            [Murillo de Sousa Sales]


## Objetivo

Demonstrar o funcionamento de uma comunicação cliente/servidor via
**Socket TCP** em Java, incluindo estabelecimento de conexão, troca de
mensagens e encerramento controlado — conceitos centrais para entender
tanto aplicações de rede legítimas quanto o funcionamento de ferramentas
de acesso remoto, como bind shells e reverse shells.

## Decisão de projeto

O servidor **não executa comandos arbitrários do sistema operacional**.
Em vez de repassar a string recebida do cliente para o shell (o que
seria feito, por exemplo, com `Runtime.exec()` ou `ProcessBuilder`), o
servidor reconhece apenas um vocabulário fechado de comandos, definido
em tempo de compilação na classe `ProcessadorComandos`. Essa decisão é
detalhada na seção **Análise Teórica de Segurança**, abaixo.

## Estrutura do repositório

```
/
├── src/
│   ├── Servidor.java             # Ponta que aguarda conexão (rede)
│   ├── Cliente.java               # Ponta que inicia conexão (rede)
│   └── ProcessadorComandos.java   # Lógica de comandos (isolada da rede)
├── .gitignore
├── README.md
└── LICENSE
```

A separação entre `Servidor` (rede) e `ProcessadorComandos` (lógica de
negócio) segue o princípio de responsabilidade única: a classe de rede
não decide o que cada comando faz, apenas repassa a entrada para quem
sabe interpretá-la dentro de limites seguros.

## Requisitos

- JDK 17 ou superior (o projeto usa *text blocks*, mas caso use uma
  versão mais antiga, é só adaptar as strings)
- Terminal ou Prompt de Comando

Verifique sua instalação:

```bash
java -version
javac -version
```

## Compilação

A partir da raiz do projeto:

```bash
javac -d bin src/Servidor.java src/Cliente.java src/ProcessadorComandos.java
```

Isso gera os `.class` dentro da pasta `bin/` (ignorada pelo Git).

## Execução

**Terminal 1 — inicia o servidor:**

```bash
java -cp bin Servidor
```

**Terminal 2 — conecta o cliente:**

```bash
java -cp bin Cliente
```

O projeto usa `127.0.0.1` (loopback) na porta `5000`, mantendo toda a
comunicação dentro da própria máquina.

## Comandos disponíveis

| Comando    | Função                          |
|------------|----------------------------------|
| `help`     | Lista os comandos disponíveis   |
| `data`     | Mostra a data atual             |
| `hora`     | Mostra a hora atual             |
| `datetime` | Mostra data e hora              |
| `status`   | Mostra o status do servidor     |
| `exit`     | Encerra a conexão               |
| `quit`     | Encerra a conexão               |

## Demonstração de uso

```
=== CLIENTE - LABORATORIO DE SOCKETS TCP ===
Conectado ao servidor 127.0.0.1:5000
Conexao estabelecida com o servidor.
Digite 'help' para consultar os comandos.

Digite um comando: status
Resposta do servidor: Servidor ativo e operando normalmente.

Digite um comando: datetime
Resposta do servidor: 26/08/2026 14:32:07

Digite um comando: exit
Resposta do servidor: Conexao encerrada pelo cliente.

Cliente encerrado.
```

No terminal do servidor, o log correspondente:

```
=== SERVIDOR - LABORATORIO DE SOCKETS TCP ===
Porta: 5000
Aguardando conexao em 127.0.0.1:5000...
Cliente conectado: 127.0.0.1
Comando recebido: status
Comando recebido: datetime
Comando recebido: exit
Cliente solicitou encerramento.
Servidor encerrado.
```

## Conceito explorado

`ServerSocket` é usado pelo servidor para aguardar conexões TCP
(equivalente às chamadas de baixo nível `socket()`, `bind()`, `listen()`
e `accept()`). O `Socket` do lado cliente estabelece a conexão
(`socket()` + `connect()`). Uma vez conectados, ambos os lados trocam
dados por meio de streams de entrada e saída (`send()`/`recv()`).

Esse mesmo modelo de comunicação é a base tanto de aplicações de rede
comuns quanto de **bind shells** (o alvo abre uma porta e espera
conexão) e **reverse shells** (o alvo inicia a conexão de saída para o
atacante). A diferença nunca está no socket em si — que é neutro quanto
ao conteúdo transmitido — mas no que o programa faz com os dados
recebidos. Um bind/reverse shell malicioso normalmente interpreta a
string recebida como comando de shell e a executa via `Runtime.exec()`
ou `ProcessBuilder`, retornando a saída padrão do processo pela mesma
conexão.

## Análise Teórica de Segurança (Blue Team)

Este laboratório evita deliberadamente a execução arbitrária de
comandos, mas o conceito que ele ilustra — um processo aceitando
conexões TCP e processando entradas remotas — é exatamente o que uma
equipe de defesa (Blue Team) precisa saber identificar em um ambiente
real. Um bind shell costuma ser detectado por meio de monitoramento de
portas TCP em escuta que não correspondem a nenhum serviço legítimo
conhecido (`netstat`, `ss`, ferramentas de EDR), enquanto um reverse
shell é mais bem identificado pelo monitoramento de conexões de saída
incomuns, especialmente quando o processo de origem é um interpretador
de comandos (`cmd.exe`, `powershell.exe`, `/bin/sh`, `/bin/bash`) sendo
gerado como processo filho de um serviço que normalmente não deveria
abrir shells (um servidor web, por exemplo). Regras de firewall com
política de saída restritiva (*egress filtering*), segmentação de rede,
auditoria de processos (Sysmon no Windows, `auditd` no Linux), sistemas
de IDS/IPS e o princípio do menor privilégio reduzem tanto a chance de
um backdoor ser implantado quanto o impacto caso ele já esteja em
execução. Manter sistemas atualizados e monitorar continuamente
conexões de rede inesperadas são práticas complementares essenciais
para essa detecção.

## Limitações

O servidor não executa comandos arbitrários do sistema operacional.
Somente os comandos previamente definidos em `ProcessadorComandos` são
processados; qualquer entrada fora desse vocabulário retorna uma
mensagem de "comando não permitido". Essa é uma limitação intencional,
não uma lacuna de implementação — o objetivo do projeto é demonstrar o
funcionamento de sockets TCP e o raciocínio de defesa associado, sem
produzir uma ferramenta funcional de acesso remoto.

## Conclusão

O projeto demonstra os fundamentos da comunicação cliente/servidor com
Sockets TCP em Java — estabelecimento de conexão, troca de dados e
encerramento controlado — além de discutir, em nível teórico, como esse
mesmo modelo de comunicação é explorado por bind e reverse shells, e
como uma equipe de defesa pode detectá-los e mitigá-los.
