package netgraph.modelo;

public class Computador extends Dispositivo {

    public Computador(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() {
        return "Computador";
    }
}
