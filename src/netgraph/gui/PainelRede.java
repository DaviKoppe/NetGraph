package netgraph.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import netgraph.controle.Conexao;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;

public class PainelRede implements VisualizadorRede {

    private static final double RAIO = 24;
    private static final double MARGEM = RAIO + 24;
    private static final double LIMIAR_ARRASTO = 4;
    private static final int INTERVALO_ANIMACAO_MS = 700;

    private static final Color COR_ARESTA = Color.rgb(160, 160, 160);
    private static final Color COR_PERCORRIDA = Color.rgb(41, 128, 185);
    private static final Color COR_CAMINHO = Color.rgb(214, 48, 49);
    private static final Color COR_ATUAL = Color.rgb(253, 203, 110);
    private static final Color COR_TEXTO = Color.rgb(45, 52, 54);

    private static final Font FONTE_NOME = Font.font("System", FontWeight.NORMAL, 12);
    private static final Font FONTE_NUMERO = Font.font("System", FontWeight.BOLD, 13);

    private final Pane raiz = new Pane();
    private final Canvas canvas = new Canvas();

    private final Map<String, String> tipos = new LinkedHashMap<>();
    private final List<Conexao> ligacoes = new ArrayList<>();
    private final Set<String> arestas = new HashSet<>();
    private final Map<String, Point2D> posicoes = new HashMap<>();

    private final Set<String> nosCaminho = new HashSet<>();
    private final Set<String> arestasCaminho = new HashSet<>();
    private final Set<String> arestasPercorridas = new HashSet<>();
    private final List<String> visitados = new ArrayList<>();
    private final List<String> ordemAnimacao = new ArrayList<>();
    private final Map<String, String> paisAnimacao = new HashMap<>();

    private Timeline animacao;
    private Consumer<String> aoClicar;
    private String arrastando;
    private boolean arrastou;
    private double pressionadoX;
    private double pressionadoY;

    public PainelRede() {
        raiz.setMinSize(0, 0);
        raiz.setPrefSize(720, 520);
        raiz.getChildren().add(canvas);
        canvas.widthProperty().bind(raiz.widthProperty());
        canvas.heightProperty().bind(raiz.heightProperty());
        canvas.widthProperty().addListener((o, antigo, novo) -> desenhar());
        canvas.heightProperty().addListener((o, antigo, novo) -> desenhar());
        canvas.setOnMousePressed(this::aoPressionar);
        canvas.setOnMouseDragged(this::aoArrastar);
        canvas.setOnMouseReleased(this::aoSoltar);
        canvas.setOnMouseMoved(e -> canvas.setCursor(
                verticeEm(e.getX(), e.getY()) != null ? Cursor.HAND : Cursor.DEFAULT));
    }

    @Override
    public Node getView() {
        return raiz;
    }

    @Override
    public void mostrar(List<Dispositivo> dispositivos, List<Conexao> conexoes) {
        limpar();
        tipos.clear();
        ligacoes.clear();
        arestas.clear();
        for (Dispositivo dispositivo : dispositivos) {
            tipos.put(dispositivo.getNome(), dispositivo.getTipo());
        }
        for (Conexao conexao : conexoes) {
            if (tipos.containsKey(conexao.origem()) && tipos.containsKey(conexao.destino())
                    && arestas.add(chave(conexao.origem(), conexao.destino()))) {
                ligacoes.add(conexao);
            }
        }
        sincronizarPosicoes();
        desenhar();
    }

    @Override
    public void destacarCaminho(List<String> nomes) {
        limpar();
        if (nomes != null) {
            String anterior = null;
            for (String nome : nomes) {
                if (!tipos.containsKey(nome)) {
                    continue;
                }
                nosCaminho.add(nome);
                if (anterior != null) {
                    arestasCaminho.add(chave(anterior, nome));
                }
                anterior = nome;
            }
        }
        desenhar();
    }

    public void destacarDispositivos(List<String> nomes) {
        limpar();
        if (nomes != null) {
            for (String nome : nomes) {
                if (tipos.containsKey(nome)) {
                    nosCaminho.add(nome);
                }
            }
        }
        desenhar();
    }

    public void destacarVizinhos(String centro, List<String> vizinhos) {
        destacarDispositivos(vizinhos);
        if (tipos.containsKey(centro)) {
            nosCaminho.add(centro);
            for (String vizinho : vizinhos) {
                if (tipos.containsKey(vizinho)) {
                    arestasCaminho.add(chave(centro, vizinho));
                }
            }
            desenhar();
        }
    }

    @Override
    public void animarPercurso(List<String> ordemDeVisita, Map<String, String> pais) {
        limpar();
        if (ordemDeVisita != null) {
            for (String nome : ordemDeVisita) {
                if (tipos.containsKey(nome)) {
                    ordemAnimacao.add(nome);
                }
            }
        }
        if (ordemAnimacao.isEmpty()) {
            desenhar();
            return;
        }
        if (pais != null) paisAnimacao.putAll(pais);
        if (ordemAnimacao.size() > 1) {
            animacao = new Timeline(new KeyFrame(Duration.millis(INTERVALO_ANIMACAO_MS), e -> avancar()));
            animacao.setCycleCount(ordemAnimacao.size() - 1);
            animacao.play();
        }
        avancar();
    }

    @Override
    public void limparDestaques() {
        limpar();
        desenhar();
    }

    @Override
    public void setAoClicarDispositivo(Consumer<String> acao) {
        aoClicar = acao;
    }

    private void parar() {
        if (animacao != null) {
            animacao.stop();
            animacao = null;
        }
    }

    private void limpar() {
        parar();
        nosCaminho.clear();
        arestasCaminho.clear();
        arestasPercorridas.clear();
        visitados.clear();
        ordemAnimacao.clear();
        paisAnimacao.clear();
    }

    private void avancar() {
        int indice = visitados.size();
        if (indice >= ordemAnimacao.size()) {
            parar();
            return;
        }
        String nome = ordemAnimacao.get(indice);
        visitados.add(nome);
        String pai = paisAnimacao.get(nome);
        if (pai != null) {
            arestasPercorridas.add(chave(pai, nome));
        }
        desenhar();

        if (visitados.size() == ordemAnimacao.size()) {
            parar();
        }
    }

    private void sincronizarPosicoes() {
        posicoes.keySet().retainAll(tipos.keySet());
        List<String> novos = new ArrayList<>();
        for (String nome : tipos.keySet()) {
            if (!posicoes.containsKey(nome)) {
                novos.add(nome);
            }
        }
        if (posicoes.isEmpty()) {
            distribuirEmCirculo(novos);
            return;
        }
        for (String nome : novos) {
            posicoes.put(nome, pontoMaisLivre());
        }
    }

    private void distribuirEmCirculo(List<String> nomes) {
        int n = nomes.size();
        for (int i = 0; i < n; i++) {
            if (n == 1) {
                posicoes.put(nomes.get(i), new Point2D(0.5, 0.5));
                continue;
            }
            double angulo = -Math.PI / 2 + 2 * Math.PI * i / n;
            posicoes.put(nomes.get(i), new Point2D(0.5 + 0.5 * Math.cos(angulo), 0.5 + 0.5 * Math.sin(angulo)));
        }
    }

    private Point2D pontoMaisLivre() {
        Point2D melhor = new Point2D(0.5, 0.5);
        double maiorDistancia = -1;
        for (double raio : new double[] {0.5, 0.28, 0}) {
            int passos = raio == 0 ? 1 : 24;
            for (int k = 0; k < passos; k++) {
                double angulo = -Math.PI / 2 + 2 * Math.PI * k / passos;
                Point2D candidato = new Point2D(0.5 + raio * Math.cos(angulo), 0.5 + raio * Math.sin(angulo));
                double menor = Double.MAX_VALUE;
                for (Point2D existente : posicoes.values()) {
                    menor = Math.min(menor, Math.hypot((candidato.getX() - existente.getX()) * 4,
                            (candidato.getY() - existente.getY()) * 3));
                }
                menor *= raio == 0.5 ? 1.6 : 1;
                if (menor > maiorDistancia) {
                    maiorDistancia = menor;
                    melhor = candidato;
                }
            }
        }
        return melhor;
    }

    private double larguraUtil() {
        return Math.max(1, canvas.getWidth() - 2 * MARGEM);
    }

    private double alturaUtil() {
        return Math.max(1, canvas.getHeight() - 2 * MARGEM);
    }

    private Point2D pixel(String nome) {
        Point2D p = posicoes.get(nome);
        return new Point2D(MARGEM + p.getX() * larguraUtil(), MARGEM + p.getY() * alturaUtil());
    }

    private static double limitar(double valor) {
        return Math.max(0, Math.min(1, valor));
    }

    private String verticeEm(double x, double y) {
        String achado = null;
        for (String nome : tipos.keySet()) {
            Point2D centro = pixel(nome);
            if (contem(centro.getX(), centro.getY(), RAIO, x, y)) {
                achado = nome;
            }
        }
        return achado;
    }

    private void aoPressionar(MouseEvent e) {
        arrastando = verticeEm(e.getX(), e.getY());
        arrastou = false;
        pressionadoX = e.getX();
        pressionadoY = e.getY();
    }

    private void aoArrastar(MouseEvent e) {
        if (arrastando == null) {
            return;
        }
        if (Math.hypot(e.getX() - pressionadoX, e.getY() - pressionadoY) > LIMIAR_ARRASTO) {
            arrastou = true;
        }
        if (arrastou) {
            posicoes.put(arrastando, new Point2D(limitar((e.getX() - MARGEM) / larguraUtil()),
                    limitar((e.getY() - MARGEM) / alturaUtil())));
            desenhar();
        }
    }

    private void aoSoltar(MouseEvent e) {
        if (arrastando != null && !arrastou && aoClicar != null) {
            aoClicar.accept(arrastando);
        }
        arrastando = null;
    }

    private void desenhar() {
        double largura = canvas.getWidth();
        double altura = canvas.getHeight();
        if (largura <= 0 || altura <= 0) {
            return;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setGlobalAlpha(1);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, largura, altura);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        desenharLegenda(gc);
        desenharArestas(gc);
        desenharNos(gc);
    }

    private void desenharArestas(GraphicsContext gc) {
        for (int nivel = 0; nivel <= 2; nivel++) {
            for (Conexao conexao : ligacoes) {
                String chave = chave(conexao.origem(), conexao.destino());
                int nivelAresta = arestasCaminho.contains(chave) ? 2
                        : arestasPercorridas.contains(chave) ? 1 : 0;
                if (nivelAresta != nivel) {
                    continue;
                }
                gc.setStroke(nivel == 2 ? COR_CAMINHO : nivel == 1 ? COR_PERCORRIDA : COR_ARESTA);
                gc.setLineWidth(nivel == 2 ? 4.5 : nivel == 1 ? 3.5 : 2);
                Point2D a = pixel(conexao.origem());
                Point2D b = pixel(conexao.destino());
                gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }
        for (String chave : arestasCaminho) {
            if (!arestas.contains(chave)) {
                String[] par = chave.split("\n");
                Point2D a = pixel(par[0]);
                Point2D b = pixel(par[1]);
                gc.setStroke(COR_CAMINHO);
                gc.setLineWidth(4.5);
                gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }
    }

    private void desenharNos(GraphicsContext gc) {
        boolean emPercurso = !visitados.isEmpty();
        String atual = emPercurso ? visitados.get(visitados.size() - 1) : null;

        for (Map.Entry<String, String> no : tipos.entrySet()) {
            String nome = no.getKey();
            String tipo = no.getValue();
            boolean visitado = visitados.contains(nome);
            boolean noCaminho = nosCaminho.contains(nome);
            Point2D centro = pixel(nome);
            double cx = centro.getX();
            double cy = centro.getY();

            gc.setGlobalAlpha(emPercurso && !visitado ? 0.35 : 1);

            if (nome.equals(atual)) {
                gc.setStroke(COR_ATUAL);
                gc.setLineWidth(10);
                gc.strokeRoundRect(cx - RAIO, cy - RAIO, RAIO * 2, RAIO * 2, 16, 16);
            }
            desenharPlaca(gc, tipo, cx, cy, RAIO, noCaminho ? COR_CAMINHO : corDoTipo(tipo).darker(),
                    noCaminho ? 4 : 2);
            desenharIcone(gc, tipo, cx, cy, RAIO);

            if (visitado) {
                double bx = cx + RAIO * 0.82;
                double by = cy - RAIO * 0.82;
                gc.setFill(corDoTipo(tipo).darker());
                gc.fillOval(bx - 10, by - 10, 20, 20);
                gc.setFont(FONTE_NUMERO);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setFill(Color.WHITE);
                gc.fillText(String.valueOf(visitados.indexOf(nome) + 1), bx, by + 5);
            }
            desenharRotulo(gc, nome, cx, cy + RAIO + 18);
        }
        gc.setGlobalAlpha(1);
    }

    private void desenharRotulo(GraphicsContext gc, String texto, double x, double y) {
        Text medida = new Text(texto);
        medida.setFont(FONTE_NOME);
        double largura = medida.getLayoutBounds().getWidth();
        gc.setFill(Color.rgb(255, 255, 255, 0.84));
        gc.fillRoundRect(x - largura / 2 - 3, y - 12, largura + 6, 16, 6, 6);
        gc.setFont(FONTE_NOME);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(COR_TEXTO);
        gc.fillText(texto, x, y);
    }

    private void desenharLegenda(GraphicsContext gc) {
        gc.setFont(FONTE_NOME);
        double y = 28;
        for (String tipo : GerenciadorDispositivos.tiposDisponiveis()) {
            desenharPlaca(gc, tipo, 28, y, 13, corDoTipo(tipo).darker(), 1.5);
            desenharIcone(gc, tipo, 28, y, 13);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setFill(COR_TEXTO);
            gc.fillText(tipo, 50, y + 4);
            y += 32;
        }
    }

    private static void desenharPlaca(GraphicsContext gc, String tipo, double cx, double cy, double r,
            Color borda, double espessura) {
        gc.setFill(Color.WHITE.interpolate(corDoTipo(tipo), 0.14));
        gc.fillRoundRect(cx - r, cy - r, r * 2, r * 2, r * 0.7, r * 0.7);
        gc.setStroke(borda);
        gc.setLineWidth(espessura);
        gc.strokeRoundRect(cx - r, cy - r, r * 2, r * 2, r * 0.7, r * 0.7);
    }

    private static void desenharIcone(GraphicsContext gc, String tipo, double cx, double cy, double r) {
        Color cor = corDoTipo(tipo);
        Color claro = Color.WHITE;
        gc.setFill(cor);
        gc.setStroke(cor);
        gc.setLineWidth(Math.max(1.2, r * 0.09));
        switch (tipo) {
            case "Servidor":
                for (int i = 0; i < 3; i++) {
                    double y = cy - r * 0.68 + i * r * 0.47;
                    gc.setFill(cor);
                    gc.fillRoundRect(cx - r * 0.6, y, r * 1.2, r * 0.38, r * 0.14, r * 0.14);
                    gc.setFill(claro);
                    gc.fillRect(cx - r * 0.46, y + r * 0.16, r * 0.4, r * 0.06);
                    gc.fillOval(cx + r * 0.3, y + r * 0.13, r * 0.12, r * 0.12);
                }
                break;
            case "Roteador":
                gc.strokeLine(cx - r * 0.42, cy + r * 0.05, cx - r * 0.55, cy - r * 0.62);
                gc.strokeLine(cx + r * 0.42, cy + r * 0.05, cx + r * 0.55, cy - r * 0.62);
                gc.fillOval(cx - r * 0.55 - r * 0.07, cy - r * 0.62 - r * 0.07, r * 0.14, r * 0.14);
                gc.fillOval(cx + r * 0.55 - r * 0.07, cy - r * 0.62 - r * 0.07, r * 0.14, r * 0.14);
                gc.strokeArc(cx - r * 0.3, cy - r * 0.45, r * 0.6, r * 0.6, 40, 100, ArcType.OPEN);
                gc.strokeArc(cx - r * 0.5, cy - r * 0.65, r * 1.0, r * 1.0, 45, 90, ArcType.OPEN);
                gc.fillRoundRect(cx - r * 0.7, cy + r * 0.05, r * 1.4, r * 0.55, r * 0.16, r * 0.16);
                gc.setFill(claro);
                for (int i = 0; i < 3; i++) {
                    gc.fillOval(cx - r * 0.42 + i * r * 0.3, cy + r * 0.27, r * 0.12, r * 0.12);
                }
                break;
            case "Switch":
                gc.fillRoundRect(cx - r * 0.72, cy - r * 0.12, r * 1.44, r * 0.6, r * 0.16, r * 0.16);
                gc.setFill(claro);
                for (int i = 0; i < 4; i++) {
                    gc.fillRect(cx - r * 0.5 + i * r * 0.28, cy + r * 0.08, r * 0.17, r * 0.22);
                }
                gc.strokeLine(cx - r * 0.45, cy - r * 0.45, cx + r * 0.45, cy - r * 0.45);
                gc.strokeLine(cx - r * 0.45, cy - r * 0.45, cx - r * 0.28, cy - r * 0.58);
                gc.strokeLine(cx - r * 0.45, cy - r * 0.45, cx - r * 0.28, cy - r * 0.32);
                gc.strokeLine(cx + r * 0.45, cy - r * 0.45, cx + r * 0.28, cy - r * 0.58);
                gc.strokeLine(cx + r * 0.45, cy - r * 0.45, cx + r * 0.28, cy - r * 0.32);
                break;
            default:
                gc.fillRoundRect(cx - r * 0.62, cy - r * 0.62, r * 1.24, r * 0.82, r * 0.12, r * 0.12);
                gc.setFill(claro);
                gc.fillRect(cx - r * 0.5, cy - r * 0.5, r * 1.0, r * 0.58);
                gc.setFill(cor);
                gc.fillRect(cx - r * 0.09, cy + r * 0.2, r * 0.18, r * 0.2);
                gc.fillRoundRect(cx - r * 0.4, cy + r * 0.4, r * 0.8, r * 0.14, r * 0.08, r * 0.08);
        }
    }

    private static boolean contem(double cx, double cy, double r, double x, double y) {
        return Math.abs(x - cx) <= r && Math.abs(y - cy) <= r;
    }

    private static Color corDoTipo(String tipo) {
        switch (tipo) {
            case "Computador":
                return Color.rgb(52, 152, 219);
            case "Servidor":
                return Color.rgb(39, 174, 96);
            case "Roteador":
                return Color.rgb(243, 156, 18);
            case "Switch":
                return Color.rgb(142, 68, 173);
            default:
                return Color.GRAY;
        }
    }

    private static String chave(String a, String b) {
        return a.compareTo(b) < 0 ? a + "\n" + b : b + "\n" + a;
    }
}
