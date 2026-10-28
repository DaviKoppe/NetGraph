# NetGraph — Simulador de Redes de Computadores

Projeto acadêmico de Estruturas de Dados (Java). Representa uma rede de computadores como um **grafo não direcionado com listas de adjacência**: os vértices são dispositivos (computadores, servidores, roteadores e switches) e as arestas são as conexões entre eles.

## Funcionalidades

- Cadastrar, buscar, remover e listar dispositivos (vértices)
- Criar e remover conexões (arestas), recusando duplicadas, laços e dispositivos inexistentes
- Consultar vizinhos e grau de um dispositivo
- Percorrer a rede com BFS (busca em largura) e DFS (busca em profundidade)
- Analisar dispositivos isolados, o dispositivo de maior grau e a conectividade da rede

## Estrutura do projeto

```
src/netgraph/
├── app/        Main (execução pelo console)
├── grafo/      Grafo (estrutura principal, BFS e DFS)
├── modelo/     Dispositivo
├── conexao/    GerenciadorConexoes, AnalisadorRede
├── controle/   ControladorRede, ResultadoOperacao
└── gui/        Telas (Swing)
```

## Como executar

1. Importar a pasta no Eclipse (*File > Import > Existing Projects into Workspace*) ou abrir no VS Code com o Extension Pack for Java.
2. Executar `netgraph.app.Main`.
3. Para executar os testes de conexões e análises no console, execute `netgraph.app.TesteGerenciadorConexoes`.

## Estado atual

- `GerenciadorConexoes` delega ao `Grafo` e converte nomes em objetos `Dispositivo` por meio do `GerenciadorDispositivos`, sem manter adjacências próprias.
- `AnalisadorRede` consulta o grafo para listar dispositivos isolados, obter o de maior grau e verificar conectividade. Em empate, retorna o primeiro vértice cadastrado; em uma rede vazia, considera a rede conectada e não há dispositivo de maior grau (`null`).
- `TesteGerenciadorConexoes` cobre conexão, remoção, duplicidade, laços, dispositivos inexistentes, BFS, DFS e análises da rede de exemplo.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Davi | Estrutura do grafo e algoritmos BFS/DFS |
| Rafael | Classes dos dispositivos (vértices) |
| Renan | Lógica das conexões (arestas) |
| Gabriel | Interface do sistema |
