# Sistema de Gerenciamento de Restaurante

Aplicação desktop em **Java Swing** com arquitetura **MVC** para controlar cardápio, pedidos por mesa, fechamento de contas e relatórios de vendas.

![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Swing-5382A1)
![MVC](https://img.shields.io/badge/arquitetura-MVC-D9552B)

## 🎓 Identificação Acadêmica

* **Instituição de Ensino:** Centro Universitário do Planalto Central Apparecido dos Santos (UNICEPLAC)
* **Curso:** Engenharia de Software
* **Disciplina:** Elicitação e Programação 
* **Orientador:** Profº Hudson Neves

## 👥 Desenvolvedores do Projeto
* Anna Gabriela Dimas Furtado
* Brenda Sousa Costa 
* Nikoly Karoline De Paula Pereira
* Maria Vitória Pereira dos Santos 
* Matheus Souza de Jesus
* Thallys Maycon de Jesus Silva 

## 📌 Descrição

Projeto acadêmico desenvolvido apenas com a biblioteca padrão do Java (sem frameworks e sem banco de dados). Os dados ficam em memória, em `ArrayList`, e são perdidos ao fechar o programa.

## ⚙️ Funcionalidades

- **Produtos:** cadastrar, listar, pesquisar, editar e excluir.
- **Pedidos:** abrir pedido por mesa, adicionar itens, alterar quantidade e remover itens.
- **Fechamento:** aplicar desconto percentual e finalizar a conta.
- **Relatórios:** pedidos finalizados, faturamento total, ticket médio e produtos mais vendidos.
- **Atalhos:** `Ctrl+1` a `Ctrl+4` trocam de tela.

## ✅ Validações

- Preço não pode ser negativo e o código do produto deve ser único.
- Quantidade deve ser maior que zero.
- Só pode haver um pedido aberto por mesa.
- Desconto deve estar entre 0% e 100%.
- Pedido vazio não pode ser fechado.
- Pedido finalizado não pode ser alterado.

## 🧑‍💻 Tecnologias

- Java 8 ou superior
- Swing e Java2D
- `ArrayList`
- Maven (opcional)

## 🏗️ Arquitetura

```
src/main/java/br/com/restaurante/
├── Main.java          # Ponto de entrada
├── model/             # Produto, Pedido, ItemPedido
├── view/              # Telas Swing e componentes visuais
├── controller/        # Regras de negócio e validações
└── dados/             # Armazenamento em memória (ArrayList)
```

## 🚀 Como executar

**Pré-requisito:** JDK 8 ou superior.

```bash
git clone https://github.com/devbrendacosta-oss/Portfolio-Sistema-de-Pedidos-para-Restaurante.git
cd Portfolio-Sistema-de-Pedidos-para-Restaurante

mkdir -p out
javac -encoding UTF-8 -d out $(find src/main -name '*.java')
java -cp out br.com.restaurante.Main
```

Na **IntelliJ IDEA**: abra a pasta do projeto, abra `Main.java` e clique em **Run**.

## 🖼️ Capturas de tela

### Tela em funcionamento

<!-- Arraste a imagem para cá no editor do GitHub, ou use: ![Tela principal](docs/screenshots/tela-principal.png) -->

*Adicione aqui o print da aplicação em execução.*

### Cadastro de produtos

<!-- ![Produtos](docs/screenshots/produtos.png) -->

*Adicione aqui o print.*

### Pedidos

<!-- ![Pedidos](docs/screenshots/pedidos.png) -->

*Adicione aqui o print.*

### Fechamento de contas

<!-- ![Fechamento](docs/screenshots/fechamento.png) -->

*Adicione aqui o print.*

### Relatórios

<!-- ![Relatórios](docs/screenshots/relatorios.png) -->

*Adicione aqui o print.*

## 🧪 Testes realizados

| Cenário | Resultado esperado | Status |
|---|---|---|
| Cadastrar produto com dados válidos | Produto aparece na lista | ✅ |
| Cadastrar produto com preço negativo | Mensagem de erro | ✅ |
| Cadastrar código já existente | Mensagem de erro | ✅ |
| Abrir dois pedidos na mesma mesa | Mensagem de erro | ✅ |
| Adicionar item com quantidade zero | Mensagem de erro | ✅ |
| Fechar conta com desconto de 10% | Total com 10% de desconto | ✅ |
| Aplicar desconto acima de 100% | Mensagem de erro | ✅ |
| Alterar pedido já finalizado | Mensagem de erro | ✅ |

### Print dos testes

<!-- ![Testes](docs/screenshots/testes.png) -->

*Adicione aqui o print dos testes sendo executados.*

## 🔭 Melhorias futuras

- Salvar dados em arquivo ou banco de dados
- Formas de pagamento
- Controle de estoque

## 📄 Licença

Projeto de uso acadêmico e de portfólio.
