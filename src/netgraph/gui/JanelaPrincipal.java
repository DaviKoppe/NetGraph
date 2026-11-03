package netgraph.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.controle.ControladorRede;
import netgraph.controle.ResultadoOperacao;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class JanelaPrincipal extends Application {

    private static final String ESTILO_OK = "-fx-text-fill: #1e7e34;";
    private static final String ESTILO_ERRO = "-fx-text-fill: #c0392b;";

    private final Grafo grafo = new Grafo();
    private final GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(grafo);
    private final GerenciadorConexoes conexoes = new GerenciadorConexoes(grafo, dispositivos);
    private final ControladorRede controlador = new ControladorRede(grafo, dispositivos, conexoes);
    private final PainelRede painel = new PainelRede();

    private final TextField campoNome = new TextField();
    private final ComboBox<String> comboTipo = new ComboBox<>();
    private final ComboBox<String> comboA = new ComboBox<>();
    private final ComboBox<String> comboB = new ComboBox<>();
    private final Label status = new Label("Cadastre dispositivos e conecte-os para montar a rede.");
    private boolean proximoCliqueEhA = true;

    @Override
    public void start(Stage palco) {
        BorderPane raiz = new BorderPane();
        raiz.setCenter(painel.getView());
        raiz.setRight(montarMenuLateral());
        raiz.setBottom(montarBarraStatus());

        painel.setAoClicarDispositivo(this::aoClicarDispositivo);
        atualizar();

        palco.setTitle("NetGraph");
        palco.setScene(new Scene(raiz, 1100, 660));
        palco.setMinWidth(820);
        palco.setMinHeight(560);
        palco.show();
    }

    private Node montarMenuLateral() {
        campoNome.setId("campoNome");
        campoNome.setPromptText("Nome (ex.: PC-01)");
        campoNome.setOnAction(e -> cadastrar());

        comboTipo.setId("comboTipo");
        comboTipo.getItems().setAll(controlador.listarTipos());
        comboTipo.getSelectionModel().selectFirst();
        comboTipo.setMaxWidth(Double.MAX_VALUE);

        comboA.setId("comboA");
        comboA.setPromptText("Dispositivo A");
        comboA.setMaxWidth(Double.MAX_VALUE);
        comboB.setId("comboB");
        comboB.setPromptText("Dispositivo B");
        comboB.setMaxWidth(Double.MAX_VALUE);

        Label dica = new Label("Dica: clique nos dispositivos do desenho para escolher A e B.");
        dica.setWrapText(true);
        dica.setStyle("-fx-text-fill: #636e72; -fx-font-size: 11px;");

        VBox menu = new VBox(16,
                secao("Cadastrar dispositivo",
                        campoNome, comboTipo, botao("btnCadastrar", "Cadastrar", this::cadastrar)),
                secao("Dispositivos selecionados", comboA, comboB, dica),
                secao("Conexoes",
                        botao("btnConectar", "Conectar A e B", this::conectar),
                        botao("btnDesconectar", "Desconectar A e B", this::desconectar),
                        botao("btnRemover", "Remover dispositivo A", this::remover)),
                secao("Percursos",
                        botao("btnCaminho", "Caminho mais curto (A -> B)", this::caminhoMaisCurto),
                        botao("btnBfs", "BFS a partir de A", this::bfs),
                        botao("btnDfs", "DFS a partir de A", this::dfs),
                        botao("btnLimpar", "Limpar destaque", painel::limparDestaques)));
        menu.setPadding(new Insets(16));

        ScrollPane rolagem = new ScrollPane(menu);
        rolagem.setFitToWidth(true);
        rolagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rolagem.setPrefWidth(290);
        rolagem.setMinWidth(290);
        rolagem.setStyle("-fx-background: #f4f5f7; -fx-background-color: #f4f5f7; "
                + "-fx-border-color: #dfe2e6; -fx-border-width: 0 0 0 1;");
        return rolagem;
    }

    private Node montarBarraStatus() {
        status.setId("status");
        status.setWrapText(true);
        status.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(status, Priority.ALWAYS);
        HBox barra = new HBox(status);
        barra.setPadding(new Insets(10, 16, 10, 16));
        barra.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dfe2e6; -fx-border-width: 1 0 0 0;");
        return barra;
    }

    private VBox secao(String titulo, Node... conteudo) {
        Label rotulo = new Label(titulo);
        rotulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        VBox caixa = new VBox(8);
        caixa.getChildren().add(rotulo);
        caixa.getChildren().addAll(conteudo);
        return caixa;
    }

    private Button botao(String id, String texto, Runnable acao) {
        Button botao = new Button(texto);
        botao.setId(id);
        botao.setMaxWidth(Double.MAX_VALUE);
        botao.setOnAction(e -> acao.run());
        return botao;
    }

    private void cadastrar() {
        ResultadoOperacao resultado = controlador.cadastrarDispositivo(campoNome.getText(), comboTipo.getValue());
        mostrar(resultado);
        if (resultado.isSucesso()) {
            campoNome.clear();
            atualizar();
        }
        campoNome.requestFocus();
    }

    private void conectar() {
        if (exigirDois()) {
            alterarRede(controlador.conectar(comboA.getValue(), comboB.getValue()));
        }
    }

    private void desconectar() {
        if (exigirDois()) {
            alterarRede(controlador.desconectar(comboA.getValue(), comboB.getValue()));
        }
    }

    private void remover() {
        if (exigirA()) {
            alterarRede(controlador.removerDispositivo(comboA.getValue()));
        }
    }

    private void caminhoMaisCurto() {
        if (!exigirDois()) {
            return;
        }
        String a = comboA.getValue();
        String b = comboB.getValue();
        List<String> caminho = nomes(controlador.caminhoMaisCurto(a, b));
        if (caminho.isEmpty()) {
            painel.limparDestaques();
            mostrar(false, "Nao existe caminho entre " + a + " e " + b + ".");
            return;
        }
        painel.destacarCaminho(caminho);
        mostrar(true, "Caminho mais curto (" + (caminho.size() - 1) + " saltos): "
                + String.join(" -> ", caminho));
    }

    private void bfs() {
        if (exigirA()) {
            percorrer("BFS", nomes(controlador.bfs(comboA.getValue())));
        }
    }

    private void dfs() {
        if (exigirA()) {
            percorrer("DFS", nomes(controlador.dfs(comboA.getValue())));
        }
    }

    private void percorrer(String nome, List<String> ordem) {
        painel.animarPercurso(ordem);
        mostrar(true, nome + " a partir de " + comboA.getValue() + ": " + String.join(", ", ordem));
    }

    private void alterarRede(ResultadoOperacao resultado) {
        mostrar(resultado);
        if (resultado.isSucesso()) {
            atualizar();
        }
    }

    private void aoClicarDispositivo(String nome) {
        if (proximoCliqueEhA) {
            comboA.setValue(nome);
        } else {
            comboB.setValue(nome);
        }
        proximoCliqueEhA = !proximoCliqueEhA;
    }

    private void atualizar() {
        List<String> nomes = nomes(controlador.listarDispositivos());
        painel.mostrar(controlador.listarDispositivos(), controlador.listarConexoes());

        String a = comboA.getValue();
        String b = comboB.getValue();
        comboA.getItems().setAll(nomes);
        comboB.getItems().setAll(nomes);
        comboA.setValue(nomes.contains(a) ? a : null);
        comboB.setValue(nomes.contains(b) ? b : null);
    }

    private boolean exigirA() {
        if (comboA.getValue() == null) {
            mostrar(false, "Escolha o dispositivo A.");
            return false;
        }
        return true;
    }

    private boolean exigirDois() {
        if (!exigirA()) {
            return false;
        }
        if (comboB.getValue() == null) {
            mostrar(false, "Escolha o dispositivo B.");
            return false;
        }
        if (comboA.getValue().equals(comboB.getValue())) {
            mostrar(false, "Escolha dois dispositivos diferentes.");
            return false;
        }
        return true;
    }

    private void mostrar(ResultadoOperacao resultado) {
        mostrar(resultado.isSucesso(), resultado.getMensagem());
    }

    private void mostrar(boolean sucesso, String mensagem) {
        status.setStyle(sucesso ? ESTILO_OK : ESTILO_ERRO);
        status.setText(mensagem);
    }

    private static List<String> nomes(List<Dispositivo> lista) {
        List<String> nomes = new ArrayList<>();
        for (Dispositivo dispositivo : lista) {
            nomes.add(dispositivo.getNome());
        }
        return nomes;
    }
}
