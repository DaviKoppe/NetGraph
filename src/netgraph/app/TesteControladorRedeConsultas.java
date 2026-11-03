package netgraph.app;

import java.util.List;
import java.util.stream.Collectors;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.controle.Conexao;
import netgraph.controle.ControladorRede;
import netgraph.controle.ResultadoOperacao;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class TesteControladorRedeConsultas {

    public static void main(String[] args) {
        Grafo grafo = new Grafo();
        GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(grafo);
        GerenciadorConexoes conexoes = new GerenciadorConexoes(grafo, dispositivos);
        ControladorRede controlador = new ControladorRede(grafo, dispositivos, conexoes);

        controlador.cadastrarDispositivo("PC-01", "pc");
        controlador.cadastrarDispositivo("PC-02", "pc");
        controlador.cadastrarDispositivo("Switch-01", "switch");
        controlador.cadastrarDispositivo("Roteador-01", "roteador");
        controlador.cadastrarDispositivo("Servidor-01", "servidor");
        controlador.conectar("PC-01", "Switch-01");
        controlador.conectar("PC-02", "Switch-01");
        controlador.conectar("Switch-01", "Roteador-01");
        controlador.conectar("Roteador-01", "Servidor-01");

        // --- listarConexoes ---
        List<Conexao> lista = controlador.listarConexoes();
        verificar(lista.size() == 4, "listarConexoes devolve cada conexao uma unica vez");
        verificar(temConexao(lista, "PC-01", "Switch-01"), "conexao PC-01 / Switch-01");
        verificar(temConexao(lista, "Roteador-01", "Servidor-01"), "conexao Roteador-01 / Servidor-01");
        System.out.println("ok   | listarConexoes -> " + lista);

        // --- BFS e DFS ---
        List<String> esperado = List.of("PC-01", "Switch-01", "PC-02", "Roteador-01", "Servidor-01");
        verificar(nomes(controlador.bfs("PC-01")).equals(esperado), "bfs a partir de PC-01");
        verificar(nomes(controlador.dfs("PC-01")).equals(esperado), "dfs a partir de PC-01");
        verificar(controlador.bfs("PC-99").isEmpty(), "bfs com origem inexistente");
        verificar(controlador.dfs(null).isEmpty(), "dfs com origem nula");
        System.out.println("ok   | bfs e dfs -> " + nomes(controlador.bfs("PC-01")));

        // --- caminho mais curto ---
        verificar(nomes(controlador.caminhoMaisCurto("PC-01", "Servidor-01"))
                .equals(List.of("PC-01", "Switch-01", "Roteador-01", "Servidor-01")),
                "caminho PC-01 -> Servidor-01");
        verificar(nomes(controlador.caminhoMaisCurto("PC-01", "PC-01")).equals(List.of("PC-01")),
                "caminho de um dispositivo ate ele mesmo");
        verificar(controlador.caminhoMaisCurto("PC-01", "PC-99").isEmpty(), "caminho com destino inexistente");
        controlador.cadastrarDispositivo("Isolado-01", "pc");
        verificar(controlador.caminhoMaisCurto("PC-01", "Isolado-01").isEmpty(), "sem caminho para isolado");
        System.out.println("ok   | caminho mais curto");

        // --- removerDispositivo ---
        ResultadoOperacao r = controlador.removerDispositivo("PC-02");
        verificar(r.isSucesso(), "remover PC-02 -> " + r);
        verificar(!nomes(controlador.listarDispositivos()).contains("PC-02"), "PC-02 saiu da listagem");
        verificar(!temConexao(controlador.listarConexoes(), "PC-02", "Switch-01"),
                "conexoes do PC-02 foram removidas");
        verificar(controlador.listarConexoes().size() == 3, "restam 3 conexoes");
        System.out.println("ok   | " + r.getMensagem());

        r = controlador.removerDispositivo("PC-02");
        verificar(!r.isSucesso() && r.getMensagem().contains("inexistente"), "remover de novo -> " + r);
        verificar(!controlador.removerDispositivo(null).isSucesso(), "remover nome nulo");
        System.out.println("ok   | " + r.getMensagem());

        System.out.println("Testes de consulta do ControladorRede passaram.");
    }

    private static boolean temConexao(List<Conexao> lista, String a, String b) {
        return lista.stream().anyMatch(c ->
                (c.origem().equals(a) && c.destino().equals(b))
                || (c.origem().equals(b) && c.destino().equals(a)));
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