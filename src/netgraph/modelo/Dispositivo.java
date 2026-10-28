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

    /** O nome identifica o dispositivo (e o vertice no Grafo). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dispositivo)) return false;
        return nome.equals(((Dispositivo) o).nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public String toString() {
        return nome + " [" + getTipo() + "]";
    }
}
