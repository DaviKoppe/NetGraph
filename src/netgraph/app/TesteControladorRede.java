package netgraph.app;

import java.util.List;
import java.util.stream.Collectors;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.controle.ControladorRede;
import netgraph.controle.ResultadoOperacao;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class TesteControladorRede {

    public static void main(String[] args) {
        Grafo grafo = new Grafo();
        GerenciadorDispositivos gerenciadorDispositivos = new GerenciadorDispositivos(grafo);
        GerenciadorConexoes gerenciadorConexoes =
                new GerenciadorConexoes(grafo, gerenciadorDispositivos);
        ControladorRede controlador =
                new ControladorRede(grafo, gerenciadorDispositivos, gerenciadorConexoes);

        // --- cadastro: sucesso ---
        esperarSucesso(controlador.cadastrarDispositivo("PC-01", "pc"), "cadastrar PC-01");
        esperarSucesso(controlador.cadastrarDispositivo("Switch-01", "switch"), "cadastrar Switch-01");
        esperarSucesso(controlador.cadastrarDispositivo("Servidor-01", "Servidor"), "cadastrar Servidor-01");

        // --- cadastro: falhas ---
        ResultadoOperacao r = controlador.cadastrarDispositivo("Imp-01", "impressora");
        esperarFalha(r, "recusar tipo invalido");
        verificar(r.getMensagem().contains("Tipo invalido"), "mensagem de tipo invalido");
        r = controlador.cadastrarDispositivo("PC-01", "pc");
        esperarFalha(r, "recusar nome repetido");
        verificar(r.getMensagem().contains("Ja existe"), "mensagem de nome repetido");
        esperarFalha(controlador.cadastrarDispositivo("   ", "pc"), "recusar nome vazio");
        esperarFalha(controlador.cadastrarDispositivo("PC-09", null), "recusar tipo nulo");

        // --- listagem (usa as classes reais, no mesmo Grafo) ---
        verificar(nomes(controlador.listarDispositivos())
                .equals(List.of("PC-01", "Switch-01", "Servidor-01")), "listar dispositivos na ordem");
        verificar(grafo.listarVertices().equals(List.of("PC-01", "Switch-01", "Servidor-01")),
                "controlador usa o mesmo Grafo recebido");

        // --- conectar: sucesso ---
        esperarSucesso(controlador.conectar("PC-01", "Switch-01"), "conectar PC-01 ao Switch-01");
        esperarSucesso(controlador.conectar("Switch-01", "Servidor-01"), "conectar Switch-01 ao Servidor-01");
        verificar(gerenciadorConexoes.estaConectado(
                gerenciadorDispositivos.buscar("PC-01"), gerenciadorDispositivos.buscar("Switch-01")),
                "conexao realmente criada no GerenciadorConexoes");

        // --- conectar: falhas ---
        r = controlador.conectar("Switch-01", "PC-01");
        esperarFalha(r, "recusar conexao ja existente (ordem invertida)");
        verificar(r.getMensagem().contains("Ja existe conexao"), "mensagem de conexao existente");
        r = controlador.conectar("PC-01", "PC-99");
        esperarFalha(r, "recusar dispositivo inexistente");
        verificar(r.getMensagem().contains("PC-99"), "mensagem cita o dispositivo inexistente");
        esperarFalha(controlador.conectar("PC-01", "PC-01"), "recusar conexao consigo mesmo");
        esperarFalha(controlador.conectar(null, "PC-01"), "recusar nome nulo");

        // --- desconectar ---
        esperarSucesso(controlador.desconectar("PC-01", "Switch-01"), "desconectar PC-01 do Switch-01");
        verificar(!gerenciadorConexoes.estaConectado(
                gerenciadorDispositivos.buscar("PC-01"), gerenciadorDispositivos.buscar("Switch-01")),
                "conexao realmente removida");
        r = controlador.desconectar("PC-01", "Switch-01");
        esperarFalha(r, "recusar desconexao de conexao inexistente");
        verificar(r.getMensagem().contains("Nao existe conexao"), "mensagem de conexao inexistente");
        esperarFalha(controlador.desconectar("PC-01", "PC-99"), "recusar desconexao com dispositivo inexistente");

        // --- tipos para a interface ---
        verificar(controlador.listarTipos().equals(List.of("Computador", "Servidor", "Roteador", "Switch")),
                "tipos disponiveis para a interface");

        System.out.println("Testes do ControladorRede passaram.");
    }

    private static void esperarSucesso(ResultadoOperacao resultado, String caso) {
        verificar(resultado.isSucesso(), caso + " -> " + resultado);
        System.out.println("ok   | " + caso + " -> " + resultado.getMensagem());
    }

    private static void esperarFalha(ResultadoOperacao resultado, String caso) {
        verificar(!resultado.isSucesso(), caso + " -> " + resultado);
        System.out.println("ok   | " + caso + " -> " + resultado.getMensagem());
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