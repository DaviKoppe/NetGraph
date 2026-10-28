package netgraph.app;

import java.util.List;
import java.util.stream.Collectors;
import netgraph.conexao.AnalisadorRede;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;
import netgraph.modelo.Servidor;

public class TesteGerenciadorConexoes {

    public static void main(String[] args) {
        Grafo grafo = new Grafo();
        GerenciadorDispositivos gerenciadorDispositivos = new GerenciadorDispositivos(grafo);
        GerenciadorConexoes gerenciadorConexoes =
                new GerenciadorConexoes(grafo, gerenciadorDispositivos);
        AnalisadorRede analisadorRede = new AnalisadorRede(grafo, gerenciadorDispositivos);

        Dispositivo pc01 = cadastrar(gerenciadorDispositivos, "PC-01", "pc");
        Dispositivo pc02 = cadastrar(gerenciadorDispositivos, "PC-02", "pc");
        Dispositivo switch01 = cadastrar(gerenciadorDispositivos, "Switch-01", "switch");
        Dispositivo roteador01 = cadastrar(gerenciadorDispositivos, "Roteador-01", "roteador");
        Dispositivo servidor01 = cadastrar(gerenciadorDispositivos, "Servidor-01", "servidor");
        Dispositivo servidor09 = new Servidor("Servidor-09");

        verificar(gerenciadorConexoes.conectar(pc01, switch01), "conectar PC-01 ao Switch-01");
        verificar(gerenciadorConexoes.conectar(pc02, switch01), "conectar PC-02 ao Switch-01");
        verificar(gerenciadorConexoes.conectar(switch01, roteador01), "conectar Switch-01 ao Roteador-01");
        verificar(gerenciadorConexoes.conectar(roteador01, servidor01), "conectar Roteador-01 ao Servidor-01");

        verificar(!gerenciadorConexoes.conectar(switch01, pc01), "recusar conexao duplicada");
        verificar(!gerenciadorConexoes.conectar(pc01, pc01), "recusar conexao do dispositivo consigo mesmo");
        verificar(!gerenciadorConexoes.conectar(pc01, servidor09), "recusar dispositivo nao cadastrado");
        verificar(gerenciadorConexoes.estaConectado(pc01, switch01), "consultar conexao existente");
        verificar(!gerenciadorConexoes.estaConectado(pc01, servidor09), "consultar conexao inexistente");
        verificar(gerenciadorConexoes.obterQuantidadeConexoes(switch01) == 3,
                "obter quantidade de conexoes");
        verificar(nomes(gerenciadorConexoes.obterConexoes(switch01))
                .equals(List.of("PC-01", "PC-02", "Roteador-01")), "listar vizinhos do Switch-01");

        verificar(nomes(gerenciadorConexoes.bfs(pc01))
                .equals(List.of("PC-01", "Switch-01", "PC-02", "Roteador-01", "Servidor-01")),
                "BFS convertida para dispositivos");
        verificar(nomes(gerenciadorConexoes.dfs(pc01))
                .equals(List.of("PC-01", "Switch-01", "PC-02", "Roteador-01", "Servidor-01")),
                "DFS convertida para dispositivos");
        verificar(analisadorRede.listarDispositivosSemConexao().isEmpty(),
                "rede de exemplo nao possui dispositivo isolado");
        verificar(analisadorRede.obterDispositivoMaisConectado() == switch01,
                "Switch-01 e o dispositivo mais conectado");
        verificar(analisadorRede.estaRedeConectada(), "rede de exemplo e conectada");

        verificar(gerenciadorConexoes.desconectar(pc02, switch01), "remover conexao de PC-02");
        verificar(!gerenciadorConexoes.estaConectado(pc02, switch01), "confirmar remocao da conexao");
        verificar(nomes(gerenciadorConexoes.obterConexoes(switch01))
                .equals(List.of("PC-01", "Roteador-01")), "vizinhos atualizados depois da remocao");
        verificar(!gerenciadorConexoes.desconectar(pc02, switch01), "recusar remocao repetida");
        verificar(nomes(analisadorRede.listarDispositivosSemConexao()).equals(List.of("PC-02")),
                "identificar dispositivo sem conexao");
        verificar(!analisadorRede.estaRedeConectada(), "identificar rede desconectada");
        verificar(analisadorRede.obterDispositivoMaisConectado() == switch01,
                "calcular maior grau depois da remocao");

        // --- caminho mais curto (Grafo + fachada) ---
        // Estado neste ponto: PC-02 esta isolado; PC-01 - Switch-01 - Roteador-01 - Servidor-01
        verificar(nomes(gerenciadorConexoes.caminhoMaisCurto(pc01, servidor01))
                .equals(List.of("PC-01", "Switch-01", "Roteador-01", "Servidor-01")),
                "caminho mais curto PC-01 -> Servidor-01");
        verificar(nomes(gerenciadorConexoes.caminhoMaisCurto(pc01, pc01)).equals(List.of("PC-01")),
                "caminho de um dispositivo ate ele mesmo");
        verificar(gerenciadorConexoes.caminhoMaisCurto(pc01, pc02).isEmpty(),
                "sem caminho para dispositivo isolado");
        verificar(!gerenciadorConexoes.existeCaminho(pc01, pc02), "existeCaminho falso para isolado");
        verificar(gerenciadorConexoes.existeCaminho(pc01, servidor01), "existeCaminho verdadeiro");
        verificar(gerenciadorConexoes.caminhoMaisCurto(pc01, servidor09).isEmpty(),
                "sem caminho para dispositivo nao cadastrado");
        // atalho: com Switch-01 - Servidor-01 o caminho passa a ter 2 saltos
        gerenciadorConexoes.conectar(switch01, servidor01);
        verificar(nomes(gerenciadorConexoes.caminhoMaisCurto(pc01, servidor01))
                .equals(List.of("PC-01", "Switch-01", "Servidor-01")),
                "caminho mais curto escolhe o atalho");
        gerenciadorConexoes.desconectar(switch01, servidor01);

        // --- tipos e igualdade ---
        verificar(GerenciadorDispositivos.tiposDisponiveis()
                .equals(List.of("Computador", "Servidor", "Roteador", "Switch")), "tipos disponiveis");
        verificar(gerenciadorDispositivos.tipoValido("switch") && gerenciadorDispositivos.tipoValido("Computador"),
                "tipos validos");
        verificar(!gerenciadorDispositivos.tipoValido("impressora") && !gerenciadorDispositivos.tipoValido(null),
                "tipos invalidos");
        verificar(new Servidor("PC-01").equals(pc01) && new Servidor("PC-01").hashCode() == pc01.hashCode(),
                "dispositivos com o mesmo nome sao iguais");

        Grafo grafoVazio = new Grafo();
        GerenciadorDispositivos dispositivosVazios = new GerenciadorDispositivos(grafoVazio);
        AnalisadorRede analisadorVazio = new AnalisadorRede(grafoVazio, dispositivosVazios);
        verificar(analisadorVazio.estaRedeConectada(), "rede vazia e considerada conectada");
        verificar(analisadorVazio.obterDispositivoMaisConectado() == null,
                "rede vazia nao possui dispositivo mais conectado");

        System.out.println("Testes do GerenciadorConexoes e AnalisadorRede passaram.");
    }

    private static Dispositivo cadastrar(GerenciadorDispositivos gerenciador, String nome, String tipo) {
        Dispositivo dispositivo = gerenciador.cadastrar(nome, tipo);
        if (dispositivo == null) {
            throw new AssertionError("Falha ao cadastrar dispositivo: " + nome);
        }
        return dispositivo;
    }

    private static List<String> nomes(List<Dispositivo> dispositivos) {
        return dispositivos.stream().map(Dispositivo::getNome).collect(Collectors.toList());
    }

    private static void verificar(boolean resultado, String caso) {
        if (!resultado) {
            throw new AssertionError("Falha no teste: " + caso);
        }
    }
}
