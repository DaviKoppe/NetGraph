package netgraph.modelo;

/**
 * Dispositivo da rede. O nome e o mesmo identificador usado pelo Grafo (vertice).
 */
public abstract class Dispositivo {

    protected final String nome;

    public Dispositivo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Tipo do equipamento, resolvido por polimorfismo em cada subclasse. */
    public abstract String getTipo();

    @Override
    public String toString() {
        return nome + " [" + getTipo() + "]";
    }
}
