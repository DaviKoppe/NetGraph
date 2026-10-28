package netgraph.app;

import java.util.List;
import java.util.Scanner;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class Main {

    private static final Grafo grafo = new Grafo();
    private static final GerenciadorDispositivos gerenciadorDispositivos =
            new GerenciadorDispositivos(grafo);
    private static final GerenciadorConexoes gerenciadorConexoes =
            new GerenciadorConexoes(grafo, gerenciadorDispositivos);
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha: ");
            executar(opcao);
        } while (opcao != 0);
        System.out.println("Encerrando o NetGraph.");
    }

    private static void exibirMenu() {
        System.out.println("\n===== NetGraph =====");
        System.out.println("1 - Cadastrar dispositivo");
        System.out.println("2 - Buscar dispositivo");
        System.out.println("3 - Remover dispositivo");
        System.out.println("4 - Listar dispositivos e conexoes");
        System.out.println("5 - Criar conexao");
        System.out.println("6 - Remover conexao");
        System.out.println("7 - Consultar vizinhos");
        System.out.println("8 - Executar BFS");
        System.out.println("9 - Executar DFS");
        System.out.println("0 - Sair");
    }

    private static void executar(int opcao) {
        switch (opcao) {
            case 1:
                String novo = lerTexto("Nome do dispositivo: ");
                String tipo = lerTexto("Tipo (computador, servidor, roteador ou switch): ");
                if (gerenciadorDispositivos.cadastrar(novo, tipo) != null)
                    System.out.println("OK: dispositivo " + novo + " cadastrado.");
                else
                    System.out.println("ERRO: tipo invalido, nome vazio ou dispositivo ja cadastrado.");
                break;
            case 2:
                String busca = lerTexto("Nome do dispositivo: ");
                Dispositivo dispositivoBuscado = gerenciadorDispositivos.buscar(busca);
                System.out.println(dispositivoBuscado != null
                        ? "Encontrado: " + dispositivoBuscado + " ("
                                + gerenciadorConexoes.obterQuantidadeConexoes(dispositivoBuscado)
                                + " conexoes)"
                        : "Dispositivo nao encontrado.");
                break;
            case 3:
                String remover = lerTexto("Nome do dispositivo: ");
                System.out.println(gerenciadorDispositivos.remover(remover)
                        ? "OK: dispositivo " + remover + " removido (e suas conexoes)."
                        : "ERRO: dispositivo inexistente.");
                break;
            case 4:
                if (grafo.listarVertices().isEmpty())
                    System.out.println("Nenhum dispositivo cadastrado.");
                else
                    System.out.print(grafo);
                break;
            case 5:
                String nomeA = lerTexto("Dispositivo A: ");
                String nomeB = lerTexto("Dispositivo B: ");
                Dispositivo a = gerenciadorDispositivos.buscar(nomeA);
                Dispositivo b = gerenciadorDispositivos.buscar(nomeB);
                System.out.println(gerenciadorConexoes.conectar(a, b)
                        ? "OK: conexao " + nomeA + " - " + nomeB + " criada."
                        : "ERRO: dispositivo inexistente, conexao com ele mesmo ou ja existente.");
                break;
            case 6:
                String nomeC = lerTexto("Dispositivo A: ");
                String nomeD = lerTexto("Dispositivo B: ");
                Dispositivo c = gerenciadorDispositivos.buscar(nomeC);
                Dispositivo d = gerenciadorDispositivos.buscar(nomeD);
                System.out.println(gerenciadorConexoes.desconectar(c, d)
                        ? "OK: conexao " + nomeC + " - " + nomeD + " removida."
                        : "ERRO: essa conexao nao existe.");
                break;
            case 7:
                String nome = lerTexto("Dispositivo: ");
                Dispositivo v = gerenciadorDispositivos.buscar(nome);
                if (v == null)
                    System.out.println("ERRO: dispositivo inexistente.");
                else
                    System.out.println("Vizinhos de " + v.getNome() + ": "
                            + gerenciadorConexoes.obterConexoes(v));
                break;
            case 8:
                percorrer("BFS", lerTexto("Origem: "), true);
                break;
            case 9:
                percorrer("DFS", lerTexto("Origem: "), false);
                break;
            case 0:
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    private static void percorrer(String nome, String origem, boolean largura) {
        Dispositivo dispositivo = gerenciadorDispositivos.buscar(origem);
        if (dispositivo == null) {
            System.out.println("ERRO: dispositivo de origem inexistente.");
            return;
        }
        List<Dispositivo> ordem = largura
                ? gerenciadorConexoes.bfs(dispositivo)
                : gerenciadorConexoes.dfs(dispositivo);
        System.out.println(nome + " a partir de " + origem + ": " + ordem);
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return teclado.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        try {
            return Integer.parseInt(lerTexto(mensagem));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
