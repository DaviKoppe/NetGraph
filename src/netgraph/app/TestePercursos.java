package netgraph.app;

import java.util.Map;
import netgraph.grafo.Grafo;

public class TestePercursos {

    public static void main(String[] args) {
        Grafo grafo = new Grafo();
        for (String nome : new String[] {"A", "B", "C"}) {
            verificar(grafo.adicionarVertice(nome), "cadastrar " + nome);
        }
        verificar(grafo.adicionarAresta("A", "B"), "aresta A-B");
        verificar(grafo.adicionarAresta("A", "C"), "aresta A-C");
        verificar(grafo.adicionarAresta("B", "C"), "aresta B-C");

        Map<String, String> paisBfs = grafo.bfsComPais("A");
        verificar(paisBfs.get("A") == null, "A e raiz do BFS");
        verificar("A".equals(paisBfs.get("B")), "B foi descoberto por A no BFS");
        verificar("A".equals(paisBfs.get("C")), "C foi descoberto por A no BFS");

        Map<String, String> paisDfs = grafo.dfsComPais("A");
        verificar(paisDfs.get("A") == null, "A e raiz do DFS");
        verificar("A".equals(paisDfs.get("B")), "B foi descoberto por A no DFS");
        verificar("B".equals(paisDfs.get("C")), "C foi descoberto por B no DFS");

        verificar(grafo.bfs("A").equals(java.util.List.of("A", "B", "C")),
                "ordem BFS preservada");
        verificar(grafo.dfs("A").equals(java.util.List.of("A", "B", "C")),
                "ordem DFS preservada");

        System.out.println("TestePercursos passou: BFS e DFS preservam as arestas reais de descoberta.");
    }

    private static void verificar(boolean condicao, String caso) {
        if (!condicao) {
            throw new AssertionError("Falha: " + caso);
        }
    }
}
