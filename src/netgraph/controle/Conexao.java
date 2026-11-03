package netgraph.controle;

/** Uma conexao entre dois dispositivos, identificados pelo nome (o mesmo nome usado no Grafo). */
public record Conexao(String origem, String destino) { }