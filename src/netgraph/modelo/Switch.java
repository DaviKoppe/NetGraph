package netgraph.modelo;

public class Switch extends Dispositivo {

    public Switch(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() {
        return "Switch";
    }
}
