# Relatório de testes — Gerenciador de Conexões

## Situação da execução

Não foi possível confirmar a compilação nem executar `netgraph.app.TesteGerenciadorConexoes`. A tentativa de iniciar o teste expirou sem confirmar que a aplicação iniciou; a consulta posterior não encontrou uma sessão Java ativa. Portanto, não é possível afirmar que o teste existente passou ou falhou.

Foi criado o teste complementar `netgraph.app.TesteCoberturaConexoes`, com casos adicionais para os métodos públicos de `GerenciadorConexoes` e `AnalisadorRede`. Ele também não foi executado. A análise estática da IDE não reportou erros nos arquivos verificados, mas isso não substitui compilação e execução.

As classes `GerenciadorConexoes`, `AnalisadorRede` e o teste existente `TesteGerenciadorConexoes` não foram alterados durante esta etapa.

## Casos verificados ou planejados

| Método/caso | Entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|
| `TesteGerenciadorConexoes.main` | Executar a classe de teste existente | Compilar e imprimir mensagem de sucesso | Inicialização expirou; execução não confirmada | Inconclusivo |
| Construtor de `GerenciadorConexoes` | `Grafo` nulo | Lançar `NullPointerException` | Não executado | Pendente |
| Construtor de `GerenciadorConexoes` | `GerenciadorDispositivos` nulo | Lançar `NullPointerException` | Não executado | Pendente |
| Construtor de `AnalisadorRede` | `Grafo` nulo | Lançar `NullPointerException` | Não executado | Pendente |
| Construtor de `AnalisadorRede` | `GerenciadorDispositivos` nulo | Lançar `NullPointerException` | Não executado | Pendente |
| `conectar` | Dois dispositivos cadastrados e sem ligação | Criar aresta nos dois sentidos | Não executado | Pendente |
| `conectar` | Aresta já existente | Retornar `false` | Não executado | Pendente |
| `conectar` | Mesmo dispositivo nos dois argumentos | Retornar `false` | Não executado | Pendente |
| `conectar` | Origem nula | Retornar `false` | Não executado | Pendente |
| `conectar` | Destino nulo | Retornar `false` | Não executado | Pendente |
| `conectar` | Destino não cadastrado | Retornar `false` | Não executado | Pendente |
| `conectar` | Objeto externo com nome igual ao de um cadastrado | Retornar `false` | Não executado | Pendente |
| `desconectar` | Aresta existente | Remover a ligação nos dois sentidos | Não executado | Pendente |
| `desconectar` | Aresta inexistente | Retornar `false` | Não executado | Pendente |
| `desconectar` | Argumentos nulos ou dispositivo externo | Retornar `false` | Não executado | Pendente |
| `estaConectado` | Aresta existente, em ambos os sentidos | Retornar `true` | Não executado | Pendente |
| `estaConectado` | Nulos ou dispositivo não cadastrado | Retornar `false` | Não executado | Pendente |
| `obterQuantidadeConexoes` | Vértice conectado, isolado e argumentos inválidos | Retornar o grau; `0` para inválidos ou isolados | Não executado | Pendente |
| `obterConexoes` | Vértice com vizinhos, isolado e argumentos inválidos | Listar os vizinhos; lista vazia para inválidos ou isolados | Não executado | Pendente |
| `bfs` | Rede de exemplo, isolado, nulo e origem não cadastrada | Ordem esperada; lista vazia para entradas inválidas | Não executado | Pendente |
| `dfs` | Rede de exemplo, isolado, nulo e origem não cadastrada | Ordem esperada; lista vazia para entradas inválidas | Não executado | Pendente |
| `bfsComPais` | Rede de exemplo e origens inválidas | Mapa de pais BFS; mapa vazio para entradas inválidas | Não executado | Pendente |
| `dfsComPais` | Rede de exemplo e origens inválidas | Mapa de pais DFS; mapa vazio para entradas inválidas | Não executado | Pendente |
| `caminhoMaisCurto` | Caminho existente, origem igual ao destino, sem caminho e entradas inválidas | Caminho mínimo ou lista vazia conforme o caso | Não executado | Pendente |
| `existeCaminho` | Caminhos existente, origem igual ao destino, inexistente e entradas inválidas | `true` para caminho existente; `false` quando não há caminho | Não executado | Pendente |
| `listarDispositivosSemConexao` | Um ou mais dispositivos isolados | Listar todos os isolados | Não executado | Pendente |
| `obterDispositivoMaisConectado` | Graus diferentes e empate | Retornar o de maior grau; em empate, o primeiro cadastrado | Não executado | Pendente |
| `obterDispositivoMaisConectado` | Rede vazia ou apenas com vértices isolados | Retornar `null` | Não executado | Pendente |
| `estaRedeConectada` | Rede vazia, com isolados, conectada e com um vértice | `false` para vazia ou desconectada; `true` para conectada, inclusive um vértice | Não executado | Pendente |

## Totais

- Casos cuja execução foi confirmada: **0**
- Passaram: **0**
- Falharam: **0**
- Inconclusivos: **1 tentativa** de executar `TesteGerenciadorConexoes`
- Casos complementares criados, ainda pendentes de execução: **28**
- Erros de comportamento confirmados: **nenhum** — a execução não foi concluída para confirmar resultados.

## Arquivos de teste

- `src/netgraph/app/TesteGerenciadorConexoes.java` — teste existente solicitado.
- `src/netgraph/app/TesteCoberturaConexoes.java` — teste complementar criado para cobrir casos adicionais.
