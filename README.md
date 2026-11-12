# Sistema de Gerenciamento de Restaurante

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

---

## 📌 Descrição

Sistema desktop de gerenciamento de restaurante desenvolvido em **Java e Java Swing**, com arquitetura **MVC** e armazenamento em memória (`ArrayList`). Permite cadastrar produtos, gerenciar pedidos por mesa, calcular contas com descontos e gerar relatórios de vendas.

---

## 🎯 Objetivo

**Objetivo geral:** Oferecer uma ferramenta com um sistema desktop para facilitar o gerenciamento de um restaurante, permitindo controlar o cardápio, registrar pedidos por mesa, calcular contas e acompanhar as vendas.

**Problema que o sistema resolve:** Dificuldade no controle manual de cardápios, pedidos, mesas e contas, que pode gerar erros e atrasos no atendimento.

**Público-alvo:** Restaurantes, lanchonetes e estabelecimentos do ramo alimentício que desejam organizar e facilitar o gerenciamento de seus pedidos e vendas

---

## ⚙️ Funcionalidades

- **Produtos:** cadastrar, listar, pesquisar, editar e excluir.
- **Pedidos:** abrir pedido por mesa, adicionar itens, alterar quantidade e remover itens.
- **Fechamento:** aplicar desconto percentual e finalizar a conta.
- **Relatórios:** pedidos finalizados, faturamento total, ticket médio e produtos mais vendidos.

---

## ✅ Validações implementadas

- Preço não pode ser negativo e o código do produto deve ser único.
- Quantidade deve ser maior que zero.
- Só pode haver um pedido aberto por mesa.
- Desconto deve estar entre 0% e 100%.
- Pedido vazio não pode ser fechado.
- Pedido finalizado não pode ser alterado.

---

## 🧑‍💻 Tecnologias

- Java 8 ou superior
- Swing e Java2D
- `ArrayList`
- Maven (opcional)

---

## 🧱 Estrutura do Projeto

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

---

### 🛠️ Frameworks e bibliotecas

- **Java SE:** linguagem utilizada no desenvolvimento.
- **Java Swing:** biblioteca utilizada para criar a interface gráfica.
- **ArrayList:** armazenamento de dados em memória.
- **MVC:** padrão arquitetural para organização do sistema.

---

## 🏗️ Arquitetura da Solução

O sistema utiliza a arquitetura **MVC (Model-View-Controller)**, dividida em três camadas:

- **Model:** responsável pelos dados, classes e regras de negócio.
- **View:** responsável pelas telas e interface gráfica desenvolvida em Java Swing.
- **Controller:** responsável por receber as ações do usuário e conectar a interface às regras de negócio.

Essa organização facilita a manutenção, a compreensão e a organização do código.

---
## 🗄️ Modelagem do Banco de Dados

O sistema não utiliza banco de dados. Os dados são armazenados temporariamente em memória, utilizando coleções `ArrayList` do Java.

As principais entidades são:

- **Produto:** código, nome, categoria e preço.
- **Pedido:** número do pedido, mesa e itens.
- **Item do Pedido:** produto e quantidade.

Os registros permanecem disponíveis durante a execução do sistema, mas não são mantidos após seu encerramento.

---

## 🔌 API

O sistema não utiliza APIs externas. Todas as funcionalidades são executadas localmente, utilizando Java Swing e armazenamento de dados em memória por meio de `ArrayList`.

---

## 🖼️ Capturas de Tela

### 💡 Tela em funcionamento (Produtos e Cardápio)
<img width="1364" height="708" alt="Captura de tela 2026-11-11 213625" src="https://github.com/user-attachments/assets/5dd30b47-0f90-4ca2-97ef-b43da015fc7c" />


### 📝 Pedidos
<img width="1359" height="703" alt="Captura de tela 2026-11-11 213700" src="https://github.com/user-attachments/assets/910313f7-67b2-4374-81e8-ad12f12ce227" />


### 💳 Fechamento de Contas
<img width="1355" height="709" alt="Captura de tela 2026-11-11 213650" src="https://github.com/user-attachments/assets/a3b81b6d-d36e-4a12-9cd1-0d2aac3139dd" />


### 📊 Relatórios
<img width="1362" height="710" alt="Captura de tela 2026-11-11 213722" src="https://github.com/user-attachments/assets/e24d146e-3593-49ea-9bf7-0b150da06eda" />

---

## 🧪 Testes realizados

| Cenário | Resultado esperado | Status |
|---|---|---|
| Cadastrar produto com dados válidos | Produto aparece na lista | ✅ |
| Cadastrar produto com preço negativo | Mensagem de erro | ✅ |
| Cadastrar código já existente | Mensagem de erro | ✅ |
| Adicionar item com quantidade zero | Mensagem de erro | ✅ |
| Fechar conta com desconto de 10% | Total com 10% de desconto | ✅ |
| Aplicar desconto acima de 100% | Mensagem de erro | ✅ |
| Alterar pedido já finalizado | Mensagem de erro | ✅ |

### Print dos testes

### ✅ Tela de Cadastrar produto com preço negativo - Mensagem de erro
<img width="1358" height="706" alt="Captura de tela 2026-11-12 130921" src="https://github.com/user-attachments/assets/0565bb9f-02a9-4c91-9baa-69a14f51ed19" />

### ✅ Tela de Cadastrar código já existente - Mensagem de erro
<img width="1351" height="709" alt="Captura de tela 2026-11-12 131020" src="https://github.com/user-attachments/assets/6b1c08aa-f753-46ee-8d5a-afe43d305023" />

### ✅ Adicionar item com quantidade zero - Mensagem de erro
<img width="1363" height="710" alt="Captura de tela 2026-11-12 131207" src="https://github.com/user-attachments/assets/f87a2576-1bbe-4fe0-88e7-4f53a3008c50" />

### ✅ Aplicar desconto acima de 100% - Mensagem de erro
<img width="1204" height="709" alt="Captura de tela 2026-11-12 132214" src="https://github.com/user-attachments/assets/501873eb-c991-4231-8ef7-cf9e2723dd0e" />

### ✅ Alterar pedido já finalizado - Mensagem de erro
<img width="1353" height="702" alt="Captura de tela 2026-11-12 131425" src="https://github.com/user-attachments/assets/424d73f8-0e23-4fd9-a27c-521982e50ed5" />

---

## ✅ Conformidade com os Requisitos do Projeto

O sistema atende aos requisitos propostos na atividade, contemplando:

- Desenvolvimento em Java SE.
- Interface gráfica utilizando Java Swing.
- Arquitetura MVC (Model-View-Controller).
- Armazenamento em memória com ArrayList.
- Cadastro, consulta, edição e exclusão de produtos.
- Gerenciamento de pedidos por mesa.
- Fechamento de contas com cálculo de descontos.
- Relatórios de vendas.
- Validação de dados e regras de negócio.

---

## 🛠️ Etapas de Desenvolvimento

1. **Planejamento:** definição dos requisitos e funcionalidades do sistema.
2. **Modelagem:** organização das classes e estrutura MVC.
3. **Desenvolvimento:** implementação das funcionalidades utilizando Java e Swing.
4. **Interface gráfica:** criação das telas e componentes de interação.
5. **Validação:** realização de testes e correção de erros.
6. **Finalização:** revisão do código e preparação para apresentação.

---

## 📈 Status do Projeto

Concluído (MVP entregue para a disciplina)

## 🔭 Melhorias Futuras

A ser definido pela equipe

## 📄 Licença

"Projeto acadêmico desenvolvido para fins educacionais na disciplina de Elicitação e Programação — UNICEPLAC. Uso restrito aos fins do curso."

