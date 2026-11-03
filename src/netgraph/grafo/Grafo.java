package netgraph.grafo;

import java.util.*;

public class Grafo {

    private final Map<String, List<String>> adjacencias = new LinkedHashMap<>();

    public boolean adicionarVertice(String nome) {
        if (nome == null || nome.trim().isEmpty()) return false;
        if (adjacencias.containsKey(nome)) return false;
        adjacencias.put(nome, new ArrayList<>());
        return true;
    }

    public boolean removerVertice(String nome) {
        if (!existeVertice(nome)) return false;
        for (String vizinho : adjacencias.get(nome)) {
            adjacencias.get(vizinho).remove(nome); // tira "nome" da lista do vizinho
        }
        adjacencias.remove(nome);
        return true;
    }

    public boolean existeVertice(String nome) {
        return nome != null && adjacencias.containsKey(nome);
    }

    public List<String> listarVertices() {
        return new ArrayList<>(adjacencias.keySet());
    }

    public boolean adicionarAresta(String a, String b) {
        if (!existeVertice(a) || !existeVertice(b)) return false;
        if (a.equals(b)) return false;
        if (existeAresta(a, b)) return false;
        adjacencias.get(a).add(b);
        adjacencias.get(b).add(a);
        return true;
    }

    public boolean removerAresta(String a, String b) {
        if (!existeAresta(a, b)) return false;
        adjacencias.get(a).remove(b);
        adjacencias.get(b).remove(a);
        return true;
    }

    public boolean existeAresta(String a, String b) {
        if (!existeVertice(a) || !existeVertice(b)) return false;
        return adjacencias.get(a).contains(b);
    }

    public List<String> obterVizinhos(String nome) {
        if (!existeVertice(nome)) return new ArrayList<>();
        return new ArrayList<>(adjacencias.get(nome));
    }

    public int grau(String nome) {
        return existeVertice(nome) ? adjacencias.get(nome).size() : 0;
    }

    public List<String> bfs(String origem) {
        return new ArrayList<>(bfsComPais(origem).keySet());
    }

    /** Retorna a ordem BFS e, para cada vertice (exceto a origem), seu pai real na arvore de busca. */
    public Map<String, String> bfsComPais(String origem) {
        Map<String, String> pais = new LinkedHashMap<>();
        if (!existeVertice(origem)) return pais;

        Set<String> visitados = new HashSet<>();
        Queue<String> fila = new LinkedList<>();
        visitados.add(origem);
        pais.put(origem, null);
        fila.add(origem);

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            for (String vizinho : adjacencias.get(atual)) {
                if (visitados.add(vizinho)) {
                    pais.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        return pais;
    }

    public List<String> dfs(String origem) {
        return new ArrayList<>(dfsComPais(origem).keySet());
    }

    /** Retorna a ordem DFS e os pais reais usados na descoberta de cada vertice. */
    public Map<String, String> dfsComPais(String origem) {
        Map<String, String> pais = new LinkedHashMap<>();
        if (!existeVertice(origem)) return pais;
        dfsRecursivo(origem, null, new HashSet<>(), pais);
        return pais;
    }

    private void dfsRecursivo(String atual, String pai, Set<String> visitados,
            Map<String, String> pais) {
        visitados.add(atual);
        pais.put(atual, pai);
        for (String vizinho : adjacencias.get(atual)) {
            if (!visitados.contains(vizinho)) {
                dfsRecursivo(vizinho, atual, visitados, pais);
            }
        }
    }

    public List<String> caminhoMaisCurto(String origem, String destino) {
        List<String> caminho = new ArrayList<>();
        if (!existeVertice(origem) || !existeVertice(destino)) return caminho;

        Map<String, String> veioDe = new HashMap<>();
        Queue<String> fila = new LinkedList<>();
        veioDe.put(origem, null);
        fila.add(origem);

        while (!fila.isEmpty() && !veioDe.containsKey(destino)) {
            String atual = fila.poll();
            for (String vizinho : adjacencias.get(atual)) {
                if (!veioDe.containsKey(vizinho)) {
                    veioDe.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        if (!veioDe.containsKey(destino)) return caminho; // sem caminho

        for (String v = destino; v != null; v = veioDe.get(v)) {
            caminho.add(0, v);
        }
        return caminho;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> e : adjacencias.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}
