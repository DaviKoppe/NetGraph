import java.util.List;

public class Main {
    public static void main(String[] args) {
        Grafo g = new Grafo();

        System.out.println("== Vértices ==");
        for (String v : new String[]{"PC-01", "PC-02", "PC-03", "Switch-01", "Roteador-01", "Servidor-01"})
            System.out.println("add " + v + ": " + g.adicionarVertice(v));
        System.out.println("add PC-01 (repetido): " + g.adicionarVertice("PC-01"));
        System.out.println("add vazio: " + g.adicionarVertice(""));

        System.out.println("\n== Arestas ==");
        System.out.println(g.adicionarAresta("PC-01", "Switch-01"));
        System.out.println(g.adicionarAresta("PC-02", "Switch-01"));
        System.out.println(g.adicionarAresta("Switch-01", "Roteador-01"));
        System.out.println(g.adicionarAresta("Roteador-01", "Servidor-01"));
        System.out.println("duplicada: " + g.adicionarAresta("Switch-01", "PC-01"));
        System.out.println("laço: " + g.adicionarAresta("PC-01", "PC-01"));
        System.out.println("inexistente: " + g.adicionarAresta("PC-01", "Servidor-09"));

        System.out.println("\n== Lista de adjacência ==");
        System.out.print(g);

        System.out.println("== Consultas ==");
        System.out.println("vizinhos Switch-01: " + g.obterVizinhos("Switch-01"));
        System.out.println("grau Switch-01: " + g.grau("Switch-01"));

        System.out.println("\n== Percursos a partir de Roteador-01 ==");
        System.out.println("BFS: " + g.bfs("Roteador-01"));
        System.out.println("DFS: " + g.dfs("Roteador-01"));

        System.out.println("\n== Remoções ==");
        System.out.println("remover aresta Roteador-01 - Servidor-01: " + g.removerAresta("Roteador-01", "Servidor-01"));
        System.out.println("BFS PC-01 alcança Servidor-01? " + g.bfs("PC-01").contains("Servidor-01"));
        System.out.println("remover vértice PC-02: " + g.removerVertice("PC-02"));
        System.out.println("vizinhos Switch-01: " + g.obterVizinhos("Switch-01"));
    }
}
