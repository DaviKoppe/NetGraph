package netgraph.conexao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class AnalisadorRede {

    private final Grafo grafo;
    private final GerenciadorDispositivos gerenciadorDispositivos;

    public AnalisadorRede(Grafo grafo, GerenciadorDispositivos gerenciadorDispositivos) {
        this.grafo = Objects.requireNonNull(grafo);
        this.gerenciadorDispositivos = Objects.requireNonNull(gerenciadorDispositivos);
    }

    public List<Dispositivo> listarDispositivosSemConexao() {
        List<Dispositivo> semConexao = new ArrayList<>();
        for (String nome : grafo.listarVertices()) {
            if (grafo.grau(nome) == 0) {
                semConexao.add(buscarDispositivo(nome));
            }
        }
        return semConexao;
    }

    public Dispositivo obterDispositivoMaisConectado() {
        List<String> vertices = grafo.listarVertices();
        if (vertices.isEmpty()) {
            return null;
        }

        String maisConectado = vertices.get(0);
        for (int i = 1; i < vertices.size(); i++) {
            String atual = vertices.get(i);
            if (grafo.grau(atual) > grafo.grau(maisConectado)) {
                maisConectado = atual;
            }
        }
        return buscarDispositivo(maisConectado);
    }

    public boolean estaRedeConectada() {
        List<String> vertices = grafo.listarVertices();
        return vertices.isEmpty() || grafo.bfs(vertices.get(0)).size() == vertices.size();
    }

    private Dispositivo buscarDispositivo(String nome) {
        Dispositivo dispositivo = gerenciadorDispositivos.buscar(nome);
        if (dispositivo == null) {
            throw new IllegalStateException("Vertice sem dispositivo cadastrado: " + nome);
        }
        return dispositivo;
    }
}
