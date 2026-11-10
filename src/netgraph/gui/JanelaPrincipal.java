package netgraph.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class JanelaPrincipal extends Application {

    private final TabPane abas = new TabPane();
    private final Tab abaNova = new Tab("+");
    private int contador;
    private boolean fechando;

    @Override
    public void start(Stage palco) {
        abas.setId("abas");
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        abaNova.setId("abaNova");
        abaNova.setClosable(false);
        abas.getTabs().add(abaNova);
        abas.getTabs().add(0, criarAba());
        abas.getSelectionModel().select(0);
        atualizarFechamento();

        abas.getSelectionModel().selectedItemProperty().addListener((obs, antiga, atual) -> {
            if (atual == abaNova && !fechando) {
                adicionarAba();
            }
        });

        palco.setTitle("NetGraph");
        palco.setScene(new Scene(abas, 1100, 700));
        palco.setMinWidth(820);
        palco.setMinHeight(600);
        palco.show();
    }

    private void adicionarAba() {
        Tab aba = criarAba();
        abas.getTabs().add(abas.getTabs().size() - 1, aba);
        abas.getSelectionModel().select(aba);
        atualizarFechamento();
    }

    private Tab criarAba() {
        Tab aba = new Tab();
        aba.setContent(new AbaRede());

        Label rotulo = new Label("Rede " + (++contador));
        rotulo.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                renomear(aba, rotulo);
            }
        });
        aba.setGraphic(rotulo);

        aba.setOnCloseRequest(e -> {
            AbaRede rede = (AbaRede) aba.getContent();
            if (rede.temDispositivos() && !confirmarFechamento(rotulo.getText())) {
                e.consume();
                return;
            }
            fechando = true;
        });
        aba.setOnClosed(e -> {
            fechando = false;
            if (abas.getSelectionModel().getSelectedItem() == abaNova) {
                abas.getSelectionModel().select(abas.getTabs().size() - 2);
            }
            atualizarFechamento();
        });
        return aba;
    }

    private void renomear(Tab aba, Label rotulo) {
        TextField campo = new TextField(rotulo.getText());
        campo.setPrefColumnCount(12);
        Runnable concluir = () -> {
            String texto = campo.getText() == null ? "" : campo.getText().trim();
            if (!texto.isEmpty()) {
                rotulo.setText(texto);
            }
            aba.setGraphic(rotulo);
        };
        campo.setOnAction(e -> concluir.run());
        campo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                campo.setText(rotulo.getText());
                concluir.run();
            }
        });
        campo.focusedProperty().addListener((obs, antes, focado) -> {
            if (!focado) {
                concluir.run();
            }
        });
        aba.setGraphic(campo);
        Platform.runLater(() -> {
            campo.requestFocus();
            campo.selectAll();
        });
    }

    private boolean confirmarFechamento(String nome) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "Fechar a aba \"" + nome + "\"? Os dispositivos e conexoes dela serao perdidos.",
                ButtonType.OK, ButtonType.CANCEL);
        alerta.setHeaderText(null);
        alerta.setTitle("Fechar aba");
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void atualizarFechamento() {
        boolean podeFechar = abas.getTabs().size() > 2;
        for (Tab aba : abas.getTabs()) {
            if (aba != abaNova) {
                aba.setClosable(podeFechar);
            }
        }
    }
}
