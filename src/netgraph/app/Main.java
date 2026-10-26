package netgraph.app;

import java.util.List;
import java.util.Scanner;
import netgraph.grafo.Grafo;

public class Main {

    private static final Grafo grafo = new Grafo();
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
                if (grafo.adicionarVertice(novo))
                    System.out.println("OK: dispositivo " + novo + " cadastrado.");
                else
                    System.out.println("ERRO: nome vazio ou dispositivo ja cadastrado.");
                break;
            case 2:
                String busca = lerTexto("Nome do dispositivo: ");
                System.out.println(grafo.existeVertice(busca)
                        ? "Encontrado: " + busca + " (" + grafo.grau(busca) + " conexoes)"
                        : "Dispositivo nao encontrado.");
                break;
            case 3:
                String remover = lerTexto("Nome do dispositivo: ");
                System.out.println(grafo.removerVertice(remover)
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
                String a = lerTexto("Dispositivo A: ");
                String b = lerTexto("Dispositivo B: ");
                System.out.println(grafo.adicionarAresta(a, b)
                        ? "OK: conexao " + a + " - " + b + " criada."
                        : "ERRO: dispositivo inexistente, conexao com ele mesmo ou ja existente.");
                break;
            case 6:
                String c = lerTexto("Dispositivo A: ");
                String d = lerTexto("Dispositivo B: ");
                System.out.println(grafo.removerAresta(c, d)
                        ? "OK: conexao " + c + " - " + d + " removida."
                        : "ERRO: essa conexao nao existe.");
                break;
            case 7:
                String v = lerTexto("Dispositivo: ");
                if (!grafo.existeVertice(v))
                    System.out.println("ERRO: dispositivo inexistente.");
                else
                    System.out.println("Vizinhos de " + v + ": " + grafo.obterVizinhos(v));
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
        if (!grafo.existeVertice(origem)) {
            System.out.println("ERRO: dispositivo de origem inexistente.");
            return;
        }
        List<String> ordem = largura ? grafo.bfs(origem) : grafo.dfs(origem);
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
