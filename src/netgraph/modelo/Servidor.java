package netgraph.modelo;

public class Servidor extends Dispositivo {

    public Servidor(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() {
        return "Servidor";
    }
}
