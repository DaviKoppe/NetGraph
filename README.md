# NetGraph — Simulador de Redes de Computadores

Projeto acadêmico de Estruturas de Dados desenvolvido em Java 17 e JavaFX 17. O NetGraph representa redes de computadores como **grafos não direcionados com listas de adjacência**: os vértices representam dispositivos e as arestas representam conexões bidirecionais.

## Funcionalidades

- Criar e gerenciar redes independentes em abas, que podem ser renomeadas.
- Cadastrar, buscar, listar e remover computadores, servidores, roteadores e switches.
- Criar e remover conexões, impedindo duplicidades, laços e referências a dispositivos não cadastrados.
- Consultar vizinhos e grau dos dispositivos.
- Executar BFS (busca em largura), DFS (busca em profundidade) e caminho mais curto.
- Identificar dispositivos isolados, o dispositivo mais conectado e se a rede é conectada.
- Visualizar e manipular a topologia: selecionar e arrastar dispositivos, destacar caminhos e acompanhar animações de BFS e DFS.

## Tecnologias

- Java 17
- JavaFX 17
- Listas de adjacência

## Organização do código

```text
src/netgraph/
├── app/        Entradas da aplicação e testes
├── grafo/      Grafo e algoritmos de percurso
├── modelo/     Dispositivos e gerenciamento dos vértices
├── conexao/    Gerenciamento das arestas e análise da rede
├── controle/   Integração entre interface e lógica
└── gui/        Interface JavaFX e visualização da topologia
```

## Executar a versão empacotada

Se você recebeu a distribuição em ZIP, extraia o conteúdo completo e execute o arquivo `.exe` incluído. Mantenha os arquivos e pastas auxiliares na estrutura extraída. Essa distribuição é destinada à execução sem precisar configurar manualmente o JDK e o JavaFX.

O ZIP do executável é distribuído separadamente; este repositório contém o código-fonte.

## Preparar o ambiente de desenvolvimento (Windows + VS Code)

### 1. Instalar o JDK 17

Instale o JDK 17 e confirme no terminal:

```powershell
java -version
javac -version
```

### 2. Baixar o JavaFX SDK 17

Baixe o **JavaFX SDK 17 para Windows x64** na [página oficial da Gluon](https://gluonhq.com/products/javafx/) e extraia o arquivo. O SDK não é versionado no repositório.

Na raiz do projeto, crie a estrutura local:

```text
.javafx/
├── lib/    arquivos .jar do SDK
└── bin/    bibliotecas nativas .dll do SDK
```

No PowerShell, ajuste o caminho de `$fx` para a pasta extraída:

```powershell
$fx = "$env:USERPROFILE\Downloads\javafx-sdk-17.0.XX"
New-Item -ItemType Directory -Force .javafx\lib
New-Item -ItemType Directory -Force .javafx\bin
Copy-Item "$fx\lib\javafx.base.jar", "$fx\lib\javafx.graphics.jar", "$fx\lib\javafx.controls.jar" ".javafx\lib\" -Force
Copy-Item "$fx\bin\*" ".javafx\bin\" -Recurse -Force
```

A pasta `.javafx/` é local e ignorada pelo Git.

### 3. Abrir e executar no VS Code

Instale a extensão **Extension Pack for Java**, abra a pasta raiz do projeto e aguarde a indexação.

Em **Run and Debug**, selecione **NetGraph - Interface** para iniciar a aplicação gráfica. A configuração utiliza os módulos JavaFX Controls, Graphics e Base a partir de `.javafx/lib`, além das bibliotecas nativas em `.javafx/bin`.

Para iniciar a versão de console, execute a classe `netgraph.app.Main`.

## Executar os testes

As classes de teste ficam em `src/netgraph/app`. Execute cada uma como aplicação Java:

- `TesteGrafo` — demonstração manual das operações básicas do grafo.
- `TestePercursos` — verifica a ordem e os predecessores reais de BFS e DFS.
- `TesteGerenciadorConexoes` — verifica conexões, consultas, percursos, caminho mínimo e análise da rede.
- `TesteCoberturaConexoes` — verifica casos adicionais e entradas inválidas do gerenciamento e da análise de conexões.
- `TesteControladorRede` — verifica as operações do controlador.
- `TesteControladorRedeConsultas` — verifica listagem de conexões, percursos e consultas do controlador.

Os testes automatizados usam verificações explícitas e lançam `AssertionError` quando uma condição não é atendida. A execução de `TesteGrafo` é demonstrativa e deve ser conferida pela saída apresentada.

## Decisões da implementação

- O grafo armazena nomes e listas de vizinhos, sem depender dos tipos concretos dos dispositivos.
- Os gerenciadores compartilham a mesma instância de `Grafo`, evitando manter estruturas de adjacência duplicadas.
- BFS e DFS disponibilizam os predecessores reais de cada vértice descoberto, permitindo que a interface destaque as arestas efetivamente percorridas.
- A interface mantém cada rede em uma aba independente.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Davi | Estrutura do grafo, algoritmos BFS/DFS e caminho mínimo |
| Rafael | Classes dos dispositivos (vértices) e gerenciamento de dispositivos |
| Renan | Gerenciamento das conexões (arestas) e análise da rede |
| Gabriel | Controlador e integração das operações entre interface e backend |
| Kaio | Interface gráfica JavaFX, visualização interativa da topologia e animação dos percursos |
