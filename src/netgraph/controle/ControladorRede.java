package netgraph.controle;

import java.util.List;
import java.util.Objects;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

/**
 * Ponte entre a interface grafica e a logica da rede.
 * Nao cria grafo nem guarda dispositivos: so delega aos gerenciadores recebidos
 * e transforma o resultado em mensagens para a tela.
 */
public class ControladorRede {

    private final Grafo grafo; // recebido para uso futuro (BFS, DFS, analises); nao e recriado aqui
    private final GerenciadorDispositivos gerenciadorDispositivos;
    private final GerenciadorConexoes gerenciadorConexoes;

    public ControladorRede(Grafo grafo,
                           GerenciadorDispositivos gerenciadorDispositivos,
                           GerenciadorConexoes gerenciadorConexoes) {
        this.grafo = Objects.requireNonNull(grafo);
        this.gerenciadorDispositivos = Objects.requireNonNull(gerenciadorDispositivos);
        this.gerenciadorConexoes = Objects.requireNonNull(gerenciadorConexoes);
    }

    public ResultadoOperacao cadastrarDispositivo(String nome, String tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            return ResultadoOperacao.erro("Informe o nome do dispositivo.");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            return ResultadoOperacao.erro("Informe o tipo do dispositivo.");
        }
        nome = nome.trim();

        if (!gerenciadorDispositivos.tipoValido(tipo)) {
            return ResultadoOperacao.erro("Tipo invalido: " + tipo.trim() + ". Tipos aceitos: "
                    + String.join(", ", GerenciadorDispositivos.tiposDisponiveis()) + ".");
        }
        if (gerenciadorDispositivos.buscar(nome) != null) {
            return ResultadoOperacao.erro("Ja existe um dispositivo com o nome " + nome + ".");
        }

        Dispositivo dispositivo = gerenciadorDispositivos.cadastrar(nome, tipo);
        if (dispositivo == null) {
            return ResultadoOperacao.erro("Nao foi possivel cadastrar o dispositivo " + nome + ".");
        }
        return ResultadoOperacao.ok("Dispositivo cadastrado: " + dispositivo + ".");
    }

    public List<Dispositivo> listarDispositivos() {
        return gerenciadorDispositivos.listar();
    }

    /** Tipos para a interface montar a lista de opcoes (ex.: JComboBox). */
    public List<String> listarTipos() {
        return GerenciadorDispositivos.tiposDisponiveis();
    }

    public ResultadoOperacao conectar(String nomeA, String nomeB) {
        Dispositivo a = gerenciadorDispositivos.buscar(nomeA);
        Dispositivo b = gerenciadorDispositivos.buscar(nomeB);

        String problema = validarPar(nomeA, nomeB, a, b);
        if (problema != null) {
            return ResultadoOperacao.erro(problema);
        }
        if (gerenciadorConexoes.estaConectado(a, b)) {
            return ResultadoOperacao.erro("Ja existe conexao entre " + a.getNome()
                    + " e " + b.getNome() + ".");
        }
        if (!gerenciadorConexoes.conectar(a, b)) {
            return ResultadoOperacao.erro("Nao foi possivel conectar " + a.getNome()
                    + " e " + b.getNome() + ".");
        }
        return ResultadoOperacao.ok("Conexao criada entre " + a.getNome() + " e " + b.getNome() + ".");
    }

    public ResultadoOperacao desconectar(String nomeA, String nomeB) {
        Dispositivo a = gerenciadorDispositivos.buscar(nomeA);
        Dispositivo b = gerenciadorDispositivos.buscar(nomeB);

        String problema = validarPar(nomeA, nomeB, a, b);
        if (problema != null) {
            return ResultadoOperacao.erro(problema);
        }
        if (!gerenciadorConexoes.estaConectado(a, b)) {
            return ResultadoOperacao.erro("Nao existe conexao entre " + a.getNome()
                    + " e " + b.getNome() + ".");
        }
        if (!gerenciadorConexoes.desconectar(a, b)) {
            return ResultadoOperacao.erro("Nao foi possivel desconectar " + a.getNome()
                    + " e " + b.getNome() + ".");
        }
        return ResultadoOperacao.ok("Conexao removida entre " + a.getNome() + " e " + b.getNome() + ".");
    }

    /** Devolve a mensagem de erro ou null se o par e valido. */
    private String validarPar(String nomeA, String nomeB, Dispositivo a, Dispositivo b) {
        if (a == null) {
            return "Dispositivo inexistente: " + exibir(nomeA) + ".";
        }
        if (b == null) {
            return "Dispositivo inexistente: " + exibir(nomeB) + ".";
        }
        if (a.equals(b)) {
            return "Escolha dois dispositivos diferentes.";
        }
        return null;
    }

    private String exibir(String nome) {
        return (nome == null || nome.trim().isEmpty()) ? "(nome vazio)" : nome.trim();
    }
}