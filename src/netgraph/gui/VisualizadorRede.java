package netgraph.gui;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;
import netgraph.controle.Conexao;
import netgraph.modelo.Dispositivo;

public interface VisualizadorRede {

    /** Componente que a janela coloca na tela. */
    Node getView();

    /** Redesenha tudo do zero e limpa qualquer destaque anterior. */
    void mostrar(List<Dispositivo> dispositivos, List<Conexao> conexoes);

    /** Destaca os dispositivos e as conexoes entre eles, na ordem recebida. Lista vazia: limpa o destaque. */
    void destacarCaminho(List<String> nomes);

    /** Anima a visita passo a passo, na ordem recebida. O ritmo fica por conta de quem implementa. */
    void animarPercurso(List<String> ordemDeVisita);

    /** Remove qualquer destaque ou animacao. */
    void limparDestaques();

    /** Chamado com o nome do dispositivo clicado. */
    void setAoClicarDispositivo(Consumer<String> acao);
}