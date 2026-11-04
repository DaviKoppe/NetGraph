# NetGraph — Simulador de Redes de Computadores

Projeto acadêmico de Estruturas de Dados desenvolvido em Java. Representa uma rede de computadores como um **grafo não direcionado com listas de adjacência**: os vértices representam dispositivos (computadores, servidores, roteadores e switches), e as arestas representam conexões diretas.

## Funcionalidades

- Cadastrar, consultar, listar e remover dispositivos.
- Criar e remover conexões, impedindo conexões duplicadas, laços e referências a dispositivos inexistentes.
- Consultar vizinhos e grau de um dispositivo.
- Percorrer a rede usando BFS (busca em largura) e DFS (busca em profundidade).
- Analisar dispositivos isolados, dispositivo de maior grau e conectividade da rede.
- Visualizar a topologia e os percursos na interface gráfica JavaFX.

## Tecnologias

- Java 17
- JavaFX 17
- Listas de adjacência para representação do grafo

## Estrutura do projeto

```text
src/netgraph/
├── app/        Classes de execução e testes
├── grafo/      Estrutura do grafo e algoritmos BFS, DFS e caminho mínimo
├── modelo/     Dispositivos e gerenciamento
├── conexao/    Gerenciamento de conexões e análise da rede
├── controle/   Controlador e resultados das operações
└── gui/        Interface gráfica JavaFX
```

## Preparar o ambiente (Windows + VS Code)

### 1. Instalar o JDK

Instale o **JDK 17** e confirme no terminal:

```powershell
java -version
javac -version
```

Ambos devem indicar a versão 17.

### 2. Baixar o JavaFX SDK

Baixe o **JavaFX SDK 17 para Windows x64** na [página oficial da Gluon](https://gluonhq.com/products/javafx/). Extraia o ZIP.

O SDK não é incluído no repositório. Cada integrante deve configurar sua cópia local.

### 3. Disponibilizar os arquivos JavaFX no projeto

Na raiz do repositório, crie a estrutura abaixo:

```text
.javafx/
├── lib/    (arquivos .jar da pasta lib do SDK)
└── bin/    (arquivos .dll da pasta bin do SDK)
```

No PowerShell, ajuste `$fx` para o caminho da pasta `javafx-sdk-17...` extraída e execute:

```powershell
$fx = "$env:USERPROFILE\Downloads\javafx-sdk-17.0.XX"
New-Item -ItemType Directory -Force .javafx\lib
New-Item -ItemType Directory -Force .javafx\bin
Copy-Item "$fx\lib\*" ".javafx\lib\" -Recurse -Force
Copy-Item "$fx\bin\*" ".javafx\bin\" -Recurse -Force
```

Substitua o caminho de exemplo pelo caminho real do SDK. Confira se os arquivos existem:

```powershell
Test-Path .javafx\lib\javafx.controls.jar
Test-Path .javafx\bin\glass.dll
```

Os dois comandos devem retornar `True`. A pasta `.javafx/` é ignorada pelo Git e não deve ser commitada.

### 4. Abrir no VS Code

Instale a extensão **Extension Pack for Java**, abra a pasta raiz do projeto e aguarde a indexação. As configurações versionadas em `.vscode/` e as referências relativas do `.classpath` permitem que o projeto reconheça os JARs locais.

### 5. Executar a interface

Abra **Run and Debug**, selecione **NetGraph - Interface** e execute. A configuração usa `.javafx/lib` como module path e `.javafx/bin` para localizar as bibliotecas nativas do JavaFX.

Para executar a versão de console, rode a classe `netgraph.app.Main`.

## Executar os testes

As classes de teste ficam no pacote `netgraph.app`. Execute cada uma como aplicação Java:

- `TesteGrafo`
- `TesteGerenciadorConexoes`
- `TesteControladorRede`
- `TesteControladorRedeConsultas`
- `TestePercursos`

Os testes verificam cadastro e remoção de vértices, conexões, consultas, BFS, DFS, caminho mínimo e arestas reais de descoberta em grafos cíclicos. As verificações falham com `AssertionError` quando uma condição não é atendida.

## Decisões da estrutura

- O grafo mantém uma lista de adjacência e não depende dos tipos concretos dos dispositivos.
- Os gerenciadores de dispositivos e conexões compartilham a mesma instância de `Grafo`.
- O BFS e o DFS também disponibilizam o predecessor real de cada vértice descoberto, permitindo que a interface destaque as arestas corretas durante a animação.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Davi | Estrutura do grafo, algoritmos BFS/DFS e caminho mínimo |
| Rafael | Classes dos dispositivos (vértices) e gerenciamento de dispositivos |
| Renan | Gerenciamento das conexões (arestas) e análise da rede |
| Gabriel | Controlador e integração das operações entre interface e backend |
| Kaio | Interface gráfica JavaFX, visualização interativa da topologia e animação dos percursos |
