package netgraph.conexao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class GerenciadorConexoes {

    private final Grafo grafo;
    private final GerenciadorDispositivos gerenciadorDispositivos;

    public GerenciadorConexoes(Grafo grafo, GerenciadorDispositivos gerenciadorDispositivos) {
        this.grafo = Objects.requireNonNull(grafo);
        this.gerenciadorDispositivos = Objects.requireNonNull(gerenciadorDispositivos);
    }

    public boolean conectar(Dispositivo origem, Dispositivo destino) {
        return grafo.adicionarAresta(nomeDe(origem), nomeDe(destino));
    }

    public boolean desconectar(Dispositivo origem, Dispositivo destino) {
        return grafo.removerAresta(nomeDe(origem), nomeDe(destino));
    }

    public boolean estaConectado(Dispositivo origem, Dispositivo destino) {
        return grafo.existeAresta(nomeDe(origem), nomeDe(destino));
    }

    public int obterQuantidadeConexoes(Dispositivo dispositivo) {
        return grafo.grau(nomeDe(dispositivo));
    }

    public List<Dispositivo> obterConexoes(Dispositivo dispositivo) {
        return converter(grafo.obterVizinhos(nomeDe(dispositivo)));
    }

    public List<Dispositivo> bfs(Dispositivo origem) {
        return converter(grafo.bfs(nomeDe(origem)));
    }

    public List<Dispositivo> dfs(Dispositivo origem) {
        return converter(grafo.dfs(nomeDe(origem)));
    }

    private String nomeDe(Dispositivo dispositivo) {
        return dispositivo == null ? null : dispositivo.getNome();
    }

    private List<Dispositivo> converter(List<String> nomes) {
        List<Dispositivo> dispositivos = new ArrayList<>();
        for (String nome : nomes) {
            Dispositivo dispositivo = gerenciadorDispositivos.buscar(nome);
            if (dispositivo == null) {
                throw new IllegalStateException("Vertice sem dispositivo cadastrado: " + nome);
            }
            dispositivos.add(dispositivo);
        }
        return dispositivos;
    }
}
