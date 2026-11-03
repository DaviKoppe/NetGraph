package netgraph.modelo;

import java.util.Objects;

/**
 * Dispositivo da rede. O nome e o mesmo identificador usado pelo Grafo (vertice).
 */
public abstract class Dispositivo {

    protected final String nome;

    public Dispositivo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do dispositivo nao pode ser vazio.");
        }
        this.nome = nome.trim();
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
        return Objects.equals(nome, ((Dispositivo) o).nome);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nome);
    }

    @Override
    public String toString() {
        return nome + " [" + getTipo() + "]";
    }
}
