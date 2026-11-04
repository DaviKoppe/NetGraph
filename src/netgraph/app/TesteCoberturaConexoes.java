package netgraph.app;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import netgraph.conexao.AnalisadorRede;
import netgraph.conexao.GerenciadorConexoes;
import netgraph.grafo.Grafo;
import netgraph.modelo.Dispositivo;
import netgraph.modelo.GerenciadorDispositivos;
import netgraph.modelo.Servidor;

public class TesteCoberturaConexoes {

    private static int passaram;
    private static int falharam;

    public static void main(String[] args) {
        testar("construtor rejeita Grafo nulo", () ->
                esperarExcecao(NullPointerException.class,
                        () -> new GerenciadorConexoes(null, new GerenciadorDispositivos(new Grafo()))));
        testar("construtor rejeita GerenciadorDispositivos nulo", () ->
                esperarExcecao(NullPointerException.class,
                        () -> new GerenciadorConexoes(new Grafo(), null)));
        testar("construtor do analisador rejeita Grafo nulo", () ->
                esperarExcecao(NullPointerException.class,
                        () -> new AnalisadorRede(null, new GerenciadorDispositivos(new Grafo()))));
        testar("construtor do analisador rejeita GerenciadorDispositivos nulo", () ->
                esperarExcecao(NullPointerException.class,
                        () -> new AnalisadorRede(new Grafo(), null)));

        testar("conectar registra aresta nos dois sentidos", () -> {
            Fixture f = new Fixture();
            verificar(f.conexoes.conectar(f.livreA, f.livreB));
            verificar(f.conexoes.estaConectado(f.livreA, f.livreB));
            verificar(f.conexoes.estaConectado(f.livreB, f.livreA));
        });
        testar("conectar recusa aresta duplicada", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(f.pc01, f.switch01));
        });
        testar("conectar recusa laço", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(f.pc01, f.pc01));
        });
        testar("conectar recusa origem nula", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(null, f.switch01));
        });
        testar("conectar recusa destino nulo", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(f.pc01, null));
        });
        testar("conectar recusa dispositivo nao cadastrado", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(f.pc01, new Servidor("Desconhecido")));
        });
        testar("conectar recusa objeto externo com nome cadastrado", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.conectar(new Servidor("PC-01"), f.switch01));
        });

        testar("desconectar remove aresta dos dois sentidos", () -> {
            Fixture f = new Fixture();
            verificar(f.conexoes.desconectar(f.pc02, f.switch01));
            verificar(!f.conexoes.estaConectado(f.pc02, f.switch01));
            verificar(!f.conexoes.estaConectado(f.switch01, f.pc02));
        });
        testar("desconectar recusa conexao inexistente", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.desconectar(f.livreA, f.livreB));
        });
        testar("desconectar recusa argumentos nulos e dispositivos externos", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.desconectar(null, f.pc01));
            verificar(!f.conexoes.desconectar(f.pc01, null));
            verificar(!f.conexoes.desconectar(new Servidor("PC-01"), f.switch01));
        });

        testar("estaConectado reconhece aresta existente e sua simetria", () -> {
            Fixture f = new Fixture();
            verificar(f.conexoes.estaConectado(f.pc01, f.switch01));
            verificar(f.conexoes.estaConectado(f.switch01, f.pc01));
        });
        testar("estaConectado trata nulos e dispositivos nao cadastrados como falso", () -> {
            Fixture f = new Fixture();
            verificar(!f.conexoes.estaConectado(null, f.pc01));
            verificar(!f.conexoes.estaConectado(f.pc01, null));
            verificar(!f.conexoes.estaConectado(f.pc01, new Servidor("Desconhecido")));
            verificar(!f.conexoes.estaConectado(new Servidor("PC-01"), f.switch01));
        });

        testar("obterQuantidadeConexoes devolve grau e trata dispositivos invalidos", () -> {
            Fixture f = new Fixture();
            verificar(f.conexoes.obterQuantidadeConexoes(f.switch01) == 3);
            verificar(f.conexoes.obterQuantidadeConexoes(f.isolado) == 0);
            verificar(f.conexoes.obterQuantidadeConexoes(null) == 0);
            verificar(f.conexoes.obterQuantidadeConexoes(new Servidor("Desconhecido")) == 0);
            verificar(f.conexoes.obterQuantidadeConexoes(new Servidor("PC-01")) == 0);
        });
        testar("obterConexoes devolve vizinhos e trata dispositivos invalidos", () -> {
            Fixture f = new Fixture();
            verificar(iguais(f.conexoes.obterConexoes(f.switch01), "PC-01", "PC-02", "Roteador-01"));
            verificar(f.conexoes.obterConexoes(f.switch01).contains(f.roteador01));
            verificar(f.conexoes.obterConexoes(f.isolado).isEmpty());
            verificar(f.conexoes.obterConexoes(null).isEmpty());
            verificar(f.conexoes.obterConexoes(new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.obterConexoes(new Servidor("PC-01")).isEmpty());
        });

        testar("BFS devolve ordem esperada e trata origens invalidas", () -> {
            Fixture f = new Fixture();
            verificar(iguais(f.conexoes.bfs(f.pc01),
                    "PC-01", "Switch-01", "PC-02", "Roteador-01", "Servidor-01"));
            verificar(f.conexoes.bfs(f.isolado).equals(List.of(f.isolado)));
            verificar(f.conexoes.bfs(null).isEmpty());
            verificar(f.conexoes.bfs(new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.bfs(new Servidor("PC-01")).isEmpty());
        });
        testar("DFS devolve ordem esperada e trata origens invalidas", () -> {
            Fixture f = new Fixture();
            verificar(iguais(f.conexoes.dfs(f.pc01),
                    "PC-01", "Switch-01", "PC-02", "Roteador-01", "Servidor-01"));
            verificar(f.conexoes.dfs(f.isolado).equals(List.of(f.isolado)));
            verificar(f.conexoes.dfs(null).isEmpty());
            verificar(f.conexoes.dfs(new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.dfs(new Servidor("PC-01")).isEmpty());
        });

        testar("bfsComPais retorna pais da arvore BFS e vazio para origem invalida", () -> {
            Fixture f = new Fixture();
            Map<String, String> esperado = new LinkedHashMap<>();
            esperado.put("PC-01", null);
            esperado.put("Switch-01", "PC-01");
            esperado.put("PC-02", "Switch-01");
            esperado.put("Roteador-01", "Switch-01");
            esperado.put("Servidor-01", "Roteador-01");
            verificar(f.conexoes.bfsComPais(f.pc01).equals(esperado));
            verificar(f.conexoes.bfsComPais(null).isEmpty());
            verificar(f.conexoes.bfsComPais(new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.bfsComPais(new Servidor("PC-01")).isEmpty());
        });
        testar("dfsComPais retorna pais da arvore DFS e vazio para origem invalida", () -> {
            Fixture f = new Fixture();
            Map<String, String> esperado = new LinkedHashMap<>();
            esperado.put("PC-01", null);
            esperado.put("Switch-01", "PC-01");
            esperado.put("PC-02", "Switch-01");
            esperado.put("Roteador-01", "Switch-01");
            esperado.put("Servidor-01", "Roteador-01");
            verificar(f.conexoes.dfsComPais(f.pc01).equals(esperado));
            verificar(f.conexoes.dfsComPais(null).isEmpty());
            verificar(f.conexoes.dfsComPais(new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.dfsComPais(new Servidor("PC-01")).isEmpty());
        });

        testar("caminhoMaisCurto cobre caminho, origem igual, sem caminho e entradas invalidas", () -> {
            Fixture f = new Fixture();
            verificar(iguais(f.conexoes.caminhoMaisCurto(f.pc01, f.servidor01),
                    "PC-01", "Switch-01", "Roteador-01", "Servidor-01"));
            verificar(f.conexoes.caminhoMaisCurto(f.pc01, f.pc01).equals(List.of(f.pc01)));
            verificar(f.conexoes.caminhoMaisCurto(f.pc01, f.isolado).isEmpty());
            verificar(f.conexoes.caminhoMaisCurto(null, f.pc01).isEmpty());
            verificar(f.conexoes.caminhoMaisCurto(f.pc01, null).isEmpty());
            verificar(f.conexoes.caminhoMaisCurto(f.pc01, new Servidor("Desconhecido")).isEmpty());
            verificar(f.conexoes.caminhoMaisCurto(new Servidor("PC-01"), f.servidor01).isEmpty());
        });
        testar("existeCaminho cobre caminhos existentes e inexistentes", () -> {
            Fixture f = new Fixture();
            verificar(f.conexoes.existeCaminho(f.pc01, f.servidor01));
            verificar(f.conexoes.existeCaminho(f.pc01, f.pc01));
            verificar(!f.conexoes.existeCaminho(f.pc01, f.isolado));
            verificar(!f.conexoes.existeCaminho(null, f.pc01));
            verificar(!f.conexoes.existeCaminho(f.pc01, null));
            verificar(!f.conexoes.existeCaminho(f.pc01, new Servidor("Desconhecido")));
        });

        testar("listarDispositivosSemConexao lista todos os isolados", () -> {
            Fixture f = new Fixture();
            verificar(iguais(f.analise.listarDispositivosSemConexao(), "Isolado-01"));
            f.dispositivos.cadastrar("Isolado-02", "pc");
            verificar(iguais(f.analise.listarDispositivosSemConexao(), "Isolado-01", "Isolado-02"));
        });
        testar("obterDispositivoMaisConectado encontra grau maximo e empate", () -> {
            Fixture f = new Fixture();
            verificar(f.analise.obterDispositivoMaisConectado() == f.switch01);

            Grafo grafo = new Grafo();
            GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(grafo);
            Dispositivo primeiro = dispositivos.cadastrar("Primeiro", "pc");
            dispositivos.cadastrar("Segundo", "pc");
            dispositivos.cadastrar("Vizinho-1", "pc");
            dispositivos.cadastrar("Vizinho-2", "pc");
            grafo.adicionarAresta("Primeiro", "Vizinho-1");
            grafo.adicionarAresta("Segundo", "Vizinho-2");
            verificar(new AnalisadorRede(grafo, dispositivos).obterDispositivoMaisConectado() == primeiro);
        });
        testar("obterDispositivoMaisConectado devolve null sem vertices ou arestas", () -> {
            Grafo vazio = new Grafo();
            GerenciadorDispositivos dispositivosVazios = new GerenciadorDispositivos(vazio);
            verificar(new AnalisadorRede(vazio, dispositivosVazios).obterDispositivoMaisConectado() == null);

            Grafo isolado = new Grafo();
            GerenciadorDispositivos dispositivosIsolados = new GerenciadorDispositivos(isolado);
            dispositivosIsolados.cadastrar("Isolado", "pc");
            verificar(new AnalisadorRede(isolado, dispositivosIsolados).obterDispositivoMaisConectado() == null);
        });
        testar("estaRedeConectada trata rede vazia, isolados, conectada e desconectada", () -> {
            Grafo vazio = new Grafo();
            verificar(!new AnalisadorRede(vazio, new GerenciadorDispositivos(vazio)).estaRedeConectada());

            Fixture f = new Fixture();
            verificar(!f.analise.estaRedeConectada());
            f.conexoes.conectar(f.isolado, f.servidor01);
            f.conexoes.conectar(f.isolado, f.livreA);
            f.conexoes.conectar(f.livreA, f.livreB);
            verificar(f.analise.estaRedeConectada());

            Grafo singleton = new Grafo();
            GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(singleton);
            dispositivos.cadastrar("Unico", "pc");
            verificar(new AnalisadorRede(singleton, dispositivos).estaRedeConectada());
        });

        System.out.println("Total: " + (passaram + falharam)
                + ", passaram: " + passaram + ", falharam: " + falharam + ".");
        if (falharam > 0) {
            throw new AssertionError("Falharam " + falharam + " grupos de testes.");
        }
    }

    private static void testar(String nome, Runnable teste) {
        try {
            teste.run();
            passaram++;
            System.out.println("PASSOU | " + nome);
        } catch (AssertionError | RuntimeException erro) {
            falharam++;
            System.out.println("FALHOU | " + nome + " | " + erro);
        }
    }

    private static void verificar(boolean resultado) {
        if (!resultado) {
            throw new AssertionError("Resultado diferente do esperado.");
        }
    }

    private static void esperarExcecao(Class<? extends RuntimeException> tipo, Runnable operacao) {
        try {
            operacao.run();
        } catch (RuntimeException erro) {
            if (tipo.isInstance(erro)) {
                return;
            }
            throw new AssertionError("Excecao inesperada: " + erro, erro);
        }
        throw new AssertionError("Era esperada excecao " + tipo.getSimpleName() + ".");
    }

    private static boolean iguais(List<Dispositivo> dispositivos, String... nomes) {
        if (dispositivos.size() != nomes.length) {
            return false;
        }
        for (int i = 0; i < nomes.length; i++) {
            if (!nomes[i].equals(dispositivos.get(i).getNome())) {
                return false;
            }
        }
        return true;
    }

    private static final class Fixture {
        private final Grafo grafo = new Grafo();
        private final GerenciadorDispositivos dispositivos = new GerenciadorDispositivos(grafo);
        private final GerenciadorConexoes conexoes = new GerenciadorConexoes(grafo, dispositivos);
        private final AnalisadorRede analise = new AnalisadorRede(grafo, dispositivos);
        private final Dispositivo pc01 = cadastrar("PC-01");
        private final Dispositivo pc02 = cadastrar("PC-02");
        private final Dispositivo switch01 = cadastrar("Switch-01", "switch");
        private final Dispositivo roteador01 = cadastrar("Roteador-01", "roteador");
        private final Dispositivo servidor01 = cadastrar("Servidor-01", "servidor");
        private final Dispositivo isolado = cadastrar("Isolado-01");
        private final Dispositivo livreA = cadastrar("Livre-A");
        private final Dispositivo livreB = cadastrar("Livre-B");

        private Fixture() {
            conectar("PC-01", "Switch-01");
            conectar("PC-02", "Switch-01");
            conectar("Switch-01", "Roteador-01");
            conectar("Roteador-01", "Servidor-01");
        }

        private Dispositivo cadastrar(String nome) {
            return cadastrar(nome, "pc");
        }

        private Dispositivo cadastrar(String nome, String tipo) {
            Dispositivo dispositivo = dispositivos.cadastrar(nome, tipo);
            if (dispositivo == null) {
                throw new AssertionError("Falha ao preparar dispositivo " + nome + ".");
            }
            return dispositivo;
        }

        private void conectar(String origem, String destino) {
            if (!grafo.adicionarAresta(origem, destino)) {
                throw new AssertionError("Falha ao preparar conexao " + origem + " - " + destino + ".");
            }
        }
    }
}
