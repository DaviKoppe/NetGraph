# NetGraph — Simulador de Redes de Computadores

Projeto acadêmico de Estruturas de Dados (Java). Representa uma rede de computadores como um **grafo não direcionado com listas de adjacência**: os vértices são dispositivos (computadores, servidores, roteadores e switches) e as arestas são as conexões entre eles.

## Funcionalidades

- Cadastrar, buscar, remover e listar dispositivos (vértices)
- Criar e remover conexões (arestas), recusando duplicadas, laços e dispositivos inexistentes
- Consultar vizinhos e grau de um dispositivo
- Percorrer a rede com BFS (busca em largura) e DFS (busca em profundidade)

## Estrutura do projeto

```
src/netgraph/
├── app/        Main (execução pelo console)
├── grafo/      Grafo (estrutura principal, BFS e DFS)
├── modelo/     Dispositivo
├── conexao/    GerenciadorConexoes
├── controle/   ControladorRede, ResultadoOperacao
└── gui/        Telas (Swing)
```

## Como executar

1. Importar a pasta no Eclipse (*File > Import > Existing Projects into Workspace*) ou abrir no VS Code com o Extension Pack for Java.
2. Executar `netgraph.app.Main`.

## Estado atual

- Pronto e testado: `Grafo` (vértices, arestas, vizinhos, BFS, DFS) e `Main` de teste no console.
- Em desenvolvimento: integração com `Dispositivo`, `GerenciadorConexoes`, controlador e interface gráfica.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Davi | Estrutura do grafo e algoritmos BFS/DFS |
| Rafael | Classes dos dispositivos (vértices) |
| Renan | Lógica das conexões (arestas) |
| Gabriel | Interface do sistema |
