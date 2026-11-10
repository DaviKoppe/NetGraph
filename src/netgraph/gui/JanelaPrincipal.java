package netgraph.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import netgraph.conexao.AnalisadorRede;
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
    private final AnalisadorRede analisador = new AnalisadorRede(grafo, dispositivos);
    private final ControladorRede controlador = new ControladorRede(grafo, dispositivos, conexoes);
    private final PainelRede painel = new PainelRede();

    private final TextField campoNome = new TextField();
    private final TextField campoBusca = new TextField();
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
        campoNome.setPromptText("Nome do dispositivo");
        campoNome.setMaxWidth(Double.MAX_VALUE);

        campoBusca.setId("campoBusca");
        campoBusca.setPromptText("Nome para buscar");
        campoBusca.setMaxWidth(Double.MAX_VALUE);
        campoBusca.setOnAction(e -> buscar());

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

        Label dica = new Label("Dica: selecione A e B nas listas ou clique nos dispositivos do desenho. Os cliques alternam entre A e B.");
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
                        botao("btnLimpar", "Limpar destaque", painel::limparDestaques)),
                secao("Buscar e analisar",
                        campoBusca,
                        botao("btnBuscar", "Buscar dispositivo", this::buscar),
                        botao("btnVizinhos", "Vizinhos de A", this::vizinhos),
                        botao("btnSemConexao", "Dispositivos sem conexao", this::semConexao),
                        botao("btnMaisConectado", "Dispositivo mais conectado", this::maisConectado),
                        botao("btnRedeConectada", "A rede esta toda conectada?", this::redeConectada)));
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
        String tipo = comboTipo.getValue();
        if (tipo == null) {
            mostrar(false, "Escolha o tipo do dispositivo.");
            return;
        }
        String nome = campoNome.getText();
        if (nome == null || nome.trim().isEmpty()) {
            mostrar(false, "Informe o nome do dispositivo.");
            campoNome.requestFocus();
            return;
        }
        ResultadoOperacao resultado = controlador.cadastrarDispositivo(nome, tipo);
        alterarRede(resultado);
        if (resultado.isSucesso()) {
            campoNome.clear();
            campoNome.requestFocus();
        }
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
            percorrer("BFS", controlador.bfsComPais(comboA.getValue()));
        }
    }

    private void dfs() {
        if (exigirA()) {
            percorrer("DFS", controlador.dfsComPais(comboA.getValue()));
        }
    }

    private void percorrer(String nome, Map<String, String> pais) {
        List<String> ordem = new ArrayList<>(pais.keySet());
        painel.animarPercurso(ordem, pais);
        mostrar(true, nome + " a partir de " + comboA.getValue() + ": " + String.join(", ", ordem));
    }

    private void buscar() {
        String nome = campoBusca.getText() == null ? "" : campoBusca.getText().trim();
        if (nome.isEmpty()) {
            mostrar(false, "Informe o nome do dispositivo para buscar.");
            campoBusca.requestFocus();
            return;
        }
        Dispositivo achado = dispositivos.buscar(nome);
        if (achado == null) {
            painel.limparDestaques();
            mostrar(false, "Dispositivo nao encontrado: " + nome + ".");
            return;
        }
        comboA.setValue(achado.getNome());
        painel.destacarDispositivos(List.of(achado.getNome()));
        mostrar(true, "Encontrado: " + achado + " (" + conexoes.obterQuantidadeConexoes(achado)
                + " conexoes).");
    }

    private void vizinhos() {
        if (!exigirA()) {
            return;
        }
        Dispositivo a = dispositivos.buscar(comboA.getValue());
        List<String> vizinhos = nomes(conexoes.obterConexoes(a));
        painel.destacarVizinhos(a.getNome(), vizinhos);
        mostrar(true, vizinhos.isEmpty()
                ? a.getNome() + " nao possui conexoes."
                : "Vizinhos de " + a.getNome() + " (" + vizinhos.size() + "): " + String.join(", ", vizinhos));
    }

    private void semConexao() {
        if (dispositivos.listar().isEmpty()) {
            mostrar(false, "Nenhum dispositivo cadastrado.");
            return;
        }
        List<String> isolados = nomes(analisador.listarDispositivosSemConexao());
        painel.destacarDispositivos(isolados);
        mostrar(true, isolados.isEmpty()
                ? "Todos os dispositivos possuem conexao."
                : "Sem conexao (" + isolados.size() + "): " + String.join(", ", isolados));
    }

    private void maisConectado() {
        Dispositivo topo = analisador.obterDispositivoMaisConectado();
        if (topo == null) {
            painel.limparDestaques();
            mostrar(false, dispositivos.listar().isEmpty()
                    ? "Nenhum dispositivo cadastrado."
                    : "Nao ha dispositivos com conexoes.");
            return;
        }
        painel.destacarDispositivos(List.of(topo.getNome()));
        mostrar(true, "Mais conectado: " + topo + " (" + conexoes.obterQuantidadeConexoes(topo)
                + " conexoes).");
    }

    private void redeConectada() {
        List<String> todos = nomes(dispositivos.listar());
        if (todos.isEmpty()) {
            painel.limparDestaques();
            mostrar(false, "Nenhum dispositivo cadastrado.");
            return;
        }
        if (analisador.estaRedeConectada()) {
            painel.limparDestaques();
            mostrar(true, "Sim, todos os dispositivos se alcancam.");
            return;
        }
        List<String> alcancaveis = nomes(controlador.bfs(todos.get(0)));
        List<String> fora = new ArrayList<>(todos);
        fora.removeAll(alcancaveis);
        painel.destacarDispositivos(fora);
        mostrar(false, "Nao, existem dispositivos isolados ou grupos separados. Fora do grupo de "
                + todos.get(0) + ": " + String.join(", ", fora));
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
            mostrar(true, "Dispositivo A selecionado: " + nome + ". Clique em outro dispositivo para selecionar B.");
        } else {
            comboB.setValue(nome);
            mostrar(true, "Dispositivo B selecionado: " + nome + ".");
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
