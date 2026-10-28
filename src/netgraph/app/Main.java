package netgraph.app;

import java.util.List;
import java.util.Scanner;
import netgraph.conexao.AnalisadorRede;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class Main {

    private static final Grafo grafo = new Grafo();
    private static final GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(grafo);
    private static final GerenciadorConexoes conexoes = new GerenciadorConexoes(grafo, dispositivos);
    private static final AnalisadorRede analise = new AnalisadorRede(grafo, dispositivos);
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
        System.out.println("1  - Cadastrar dispositivo");
        System.out.println("2  - Buscar dispositivo");
        System.out.println("3  - Remover dispositivo");
        System.out.println("4  - Listar dispositivos e conexoes");
        System.out.println("5  - Criar conexao");
        System.out.println("6  - Remover conexao");
        System.out.println("7  - Consultar vizinhos");
        System.out.println("8  - Executar BFS");
        System.out.println("9  - Executar DFS");
        System.out.println("10 - Caminho mais curto");
        System.out.println("11 - Dispositivos sem conexao");
        System.out.println("12 - Dispositivo mais conectado");
        System.out.println("13 - A rede esta toda conectada?");
        System.out.println("0  - Sair");
    }

    private static void executar(int opcao) {
        switch (opcao) {
            case 1:
                cadastrar();
                break;
            case 2:
                Dispositivo achado = dispositivos.buscar(lerTexto("Nome do dispositivo: "));
                System.out.println(achado != null
                        ? "Encontrado: " + achado + " (" + conexoes.obterQuantidadeConexoes(achado) + " conexoes)"
                        : "Dispositivo nao encontrado.");
                break;
            case 3:
                String remover = lerTexto("Nome do dispositivo: ");
                System.out.println(dispositivos.remover(remover)
                        ? "OK: dispositivo " + remover + " removido (e suas conexoes)."
                        : "ERRO: dispositivo inexistente.");
                break;
            case 4:
                listar();
                break;
            case 5:
                conectar();
                break;
            case 6:
                Dispositivo c = dispositivos.buscar(lerTexto("Dispositivo A: "));
                Dispositivo d = dispositivos.buscar(lerTexto("Dispositivo B: "));
                System.out.println(conexoes.desconectar(c, d)
                        ? "OK: conexao " + c.getNome() + " - " + d.getNome() + " removida."
                        : "ERRO: essa conexao nao existe.");
                break;
            case 7:
                Dispositivo v = dispositivos.buscar(lerTexto("Dispositivo: "));
                System.out.println(v == null
                        ? "ERRO: dispositivo inexistente."
                        : "Vizinhos de " + v.getNome() + ": " + conexoes.obterConexoes(v));
                break;
            case 8:
                percorrer("BFS", true);
                break;
            case 9:
                percorrer("DFS", false);
                break;
            case 10:
                caminhoMaisCurto();
                break;
            case 11:
                System.out.println("Sem conexao: " + analise.listarDispositivosSemConexao());
                break;
            case 12:
                Dispositivo top = analise.obterDispositivoMaisConectado();
                System.out.println(top == null
                        ? "Nenhum dispositivo cadastrado."
                        : "Mais conectado: " + top + " (" + conexoes.obterQuantidadeConexoes(top) + " conexoes)");
                break;
            case 13:
                System.out.println(analise.estaRedeConectada()
                        ? "Sim, todos os dispositivos se alcancam."
                        : "Nao, existem dispositivos isolados ou grupos separados.");
                break;
            case 0:
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrar() {
        String nome = lerTexto("Nome do dispositivo: ");
        String tipo = lerTexto("Tipo " + GerenciadorDispositivos.tiposDisponiveis() + ": ");
        if (nome.isEmpty())
            System.out.println("ERRO: o nome nao pode ser vazio.");
        else if (!dispositivos.tipoValido(tipo))
            System.out.println("ERRO: tipo invalido. Use um de " + GerenciadorDispositivos.tiposDisponiveis() + ".");
        else if (dispositivos.buscar(nome) != null)
            System.out.println("ERRO: dispositivo ja cadastrado.");
        else if (dispositivos.cadastrar(nome, tipo) != null)
            System.out.println("OK: dispositivo " + nome + " cadastrado.");
        else
            System.out.println("ERRO: nao foi possivel cadastrar.");
    }

    private static void listar() {
        List<Dispositivo> todos = dispositivos.listar();
        if (todos.isEmpty()) {
            System.out.println("Nenhum dispositivo cadastrado.");
            return;
        }
        for (Dispositivo dispositivo : todos)
            System.out.println(dispositivo + " -> " + conexoes.obterConexoes(dispositivo));
    }

    private static void conectar() {
        Dispositivo a = dispositivos.buscar(lerTexto("Dispositivo A: "));
        Dispositivo b = dispositivos.buscar(lerTexto("Dispositivo B: "));
        if (a == null || b == null)
            System.out.println("ERRO: dispositivo inexistente.");
        else if (a.equals(b))
            System.out.println("ERRO: nao e possivel conectar um dispositivo a ele mesmo.");
        else if (conexoes.estaConectado(a, b))
            System.out.println("ERRO: essa conexao ja existe.");
        else if (conexoes.conectar(a, b))
            System.out.println("OK: conexao " + a.getNome() + " - " + b.getNome() + " criada.");
        else
            System.out.println("ERRO: nao foi possivel criar a conexao.");
    }

    private static void percorrer(String nome, boolean largura) {
        Dispositivo origem = dispositivos.buscar(lerTexto("Origem: "));
        if (origem == null) {
            System.out.println("ERRO: dispositivo de origem inexistente.");
            return;
        }
        List<Dispositivo> ordem = largura ? conexoes.bfs(origem) : conexoes.dfs(origem);
        System.out.println(nome + " a partir de " + origem.getNome() + ": " + ordem);
    }

    private static void caminhoMaisCurto() {
        Dispositivo origem = dispositivos.buscar(lerTexto("Origem: "));
        Dispositivo destino = dispositivos.buscar(lerTexto("Destino: "));
        if (origem == null || destino == null) {
            System.out.println("ERRO: dispositivo inexistente.");
            return;
        }
        List<Dispositivo> caminho = conexoes.caminhoMaisCurto(origem, destino);
        System.out.println(caminho.isEmpty()
                ? "Nao existe caminho entre " + origem.getNome() + " e " + destino.getNome() + "."
                : "Caminho (" + (caminho.size() - 1) + " saltos): " + caminho);
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
