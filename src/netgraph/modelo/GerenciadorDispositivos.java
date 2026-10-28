package netgraph.modelo;

import netgraph.grafo.Grafo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerencia os dispositivos associados aos vertices do Grafo.
 * O Grafo valida e e dono dos vertices; este gerenciador so guarda o objeto
 * Dispositivo (nome + tipo) quando o Grafo aceita a operacao.
 */
public class GerenciadorDispositivos {

    private final Grafo grafo; // mesma instancia criada no Main
    private final Map<String, Dispositivo> dispositivos = new LinkedHashMap<>();

    public GerenciadorDispositivos(Grafo grafo) {
        this.grafo = grafo;
    }

    /** Cria o dispositivo do tipo informado e o registra. Devolve null se o tipo for invalido ou o Grafo recusar. */
    public Dispositivo cadastrar(String nome, String tipo) {
        if (nome == null || tipo == null) {
            return null;
        }
        nome = nome.trim();
        Dispositivo d = criar(nome, tipo.trim());
        if (d == null || !grafo.adicionarVertice(nome)) {
            return null; // tipo invalido, nome vazio ou repetido
        }
        dispositivos.put(nome, d);
        return d;
    }

    /** Devolve o dispositivo (nome e tipo) ou null se nao existir. */
    public Dispositivo buscar(String nome) {
        if (nome == null) {
            return null;
        }
        nome = nome.trim();
        return grafo.existeVertice(nome) ? dispositivos.get(nome) : null;
    }

    /** Remove o dispositivo depois que o Grafo remove o vertice e suas arestas. */
    public boolean remover(String nome) {
        if (nome == null) {
            return false;
        }
        nome = nome.trim();
        if (!grafo.removerVertice(nome)) {
            return false;
        }
        dispositivos.remove(nome);
        return true;
    }

    /** Converte os nomes dos vertices em objetos Dispositivo, na ordem do Grafo. */
    public List<Dispositivo> listar() {
        List<Dispositivo> lista = new ArrayList<>();
        for (String nome : grafo.listarVertices()) {
            Dispositivo d = dispositivos.get(nome);
            if (d != null) {
                lista.add(d);
            }
        }
        return lista;
    }

    private Dispositivo criar(String nome, String tipo) {
        return switch (tipo.toLowerCase()) {
            case "computador", "pc" -> new Computador(nome);
            case "servidor" -> new Servidor(nome);
            case "roteador" -> new Roteador(nome);
            case "switch" -> new Switch(nome);
            default -> null;
        };
    }
}
