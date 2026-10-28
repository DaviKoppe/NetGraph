package netgraph.modelo;

public class Roteador extends Dispositivo {

    public Roteador(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() {
        return "Roteador";
    }
}
