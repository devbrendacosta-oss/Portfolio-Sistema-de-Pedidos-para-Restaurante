package br.com.restaurante;

import br.com.restaurante.controller.ContaController;
import br.com.restaurante.controller.PedidoController;
import br.com.restaurante.controller.ProdutoController;
import br.com.restaurante.controller.RelatorioController;
import br.com.restaurante.dados.Armazenamento;
import br.com.restaurante.model.Pedido;

/**
 * Testes das regras de negócio sem dependências externas.
 * Execute com: java -cp out br.com.restaurante.RegrasDeNegocioTest
 * Termina com código 1 se algum teste falhar (usado pelo CI).
 */
public class RegrasDeNegocioTest {

    private static int total = 0;
    private static int falhas = 0;

    private final ProdutoController produtos = new ProdutoController();
    private final PedidoController pedidos = new PedidoController();
    private final ContaController contas = new ContaController();
    private final RelatorioController relatorios = new RelatorioController();

    public static void main(String[] args) {
        RegrasDeNegocioTest t = new RegrasDeNegocioTest();
        t.executar("RF01 cadastra produto válido", t::cadastraProduto);
        t.executar("RF01 rejeita código duplicado", t::rejeitaCodigoDuplicado);
        t.executar("RF01 rejeita preço negativo", t::rejeitaPrecoNegativo);
        t.executar("RF01 rejeita nome vazio", t::rejeitaNomeVazio);
        t.executar("RF02 pesquisa por nome, categoria e código", t::pesquisaProdutos);
        t.executar("RF03 não abre duas mesas iguais ao mesmo tempo", t::rejeitaMesaDuplicada);
        t.executar("RF03 rejeita número de mesa inválido", t::rejeitaMesaInvalida);
        t.executar("RF04 soma quantidade do mesmo produto", t::somaQuantidadeMesmoProduto);
        t.executar("RF04 rejeita quantidade zero ou negativa", t::rejeitaQuantidadeInvalida);
        t.executar("RF05 aplica desconto de 10% no fechamento", t::aplicaDesconto);
        t.executar("RF05 rejeita desconto fora de 0-100%", t::rejeitaDescontoInvalido);
        t.executar("RF05 rejeita fechar pedido vazio", t::rejeitaPedidoVazio);
        t.executar("RF06 bloqueia alteração de pedido finalizado", t::bloqueiaPedidoFinalizado);
        t.executar("RF07 preço do item não muda após editar o cardápio", t::precoCongeladoNoItem);
        t.executar("RF08 relatório considera só pedidos finalizados", t::relatorioSoFinalizados);
        t.executar("RF08 ranking ordena por quantidade vendida", t::rankingOrdenado);

        System.out.println();
        System.out.println(total + " testes, " + (total - falhas) + " passaram, " + falhas + " falharam.");
        if (falhas > 0) System.exit(1);
    }

    // ---------------------------------------------------------------- testes

    private void cadastraProduto() {
        produtos.cadastrar(1, "  Pizza ", " Pratos ", 40.0);
        igual(1, Armazenamento.PRODUTOS.size());
        igual("Pizza", produtos.buscarPorCodigo(1).getNome());
        igual("Pratos", produtos.buscarPorCodigo(1).getCategoria());
    }

    private void rejeitaCodigoDuplicado() {
        produtos.cadastrar(1, "Pizza", "Pratos", 40.0);
        erro(() -> produtos.cadastrar(1, "Outra", "Pratos", 10.0));
    }

    private void rejeitaPrecoNegativo() {
        erro(() -> produtos.cadastrar(1, "Pizza", "Pratos", -1.0));
        erro(() -> produtos.cadastrar(1, "Pizza", "Pratos", Double.NaN));
    }

    private void rejeitaNomeVazio() {
        erro(() -> produtos.cadastrar(1, "   ", "Pratos", 10.0));
        erro(() -> produtos.cadastrar(1, "Pizza", "", 10.0));
        erro(() -> produtos.cadastrar(0, "Pizza", "Pratos", 10.0));
    }

    private void pesquisaProdutos() {
        produtos.cadastrar(10, "Suco de laranja", "Bebidas", 12.0);
        produtos.cadastrar(20, "Pudim", "Sobremesas", 16.0);
        igual(1, produtos.listar("LARANJA").size());
        igual(1, produtos.listar("sobremesas").size());
        igual(1, produtos.listar("20").size());
        igual(2, produtos.listar("").size());
    }

    private void rejeitaMesaDuplicada() {
        pedidos.abrirPedido(5);
        erro(() -> pedidos.abrirPedido(5));
    }

    private void rejeitaMesaInvalida() {
        erro(() -> pedidos.abrirPedido(0));
        erro(() -> pedidos.abrirPedido(-3));
    }

    private void somaQuantidadeMesmoProduto() {
        produtos.cadastrar(1, "Pizza", "Pratos", 40.0);
        int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 2);
        pedidos.adicionarProduto(n, 1, 3);
        Pedido p = pedidos.buscarPedido(n);
        igual(1, p.getItens().size());
        igual(5, p.getItens().get(0).getQuantidade());
        igual(200.0, p.calcularSubtotal());
    }

    private void rejeitaQuantidadeInvalida() {
        produtos.cadastrar(1, "Pizza", "Pratos", 40.0);
        final int n = pedidos.abrirPedido(1).getNumero();
        erro(() -> pedidos.adicionarProduto(n, 1, 0));
        erro(() -> pedidos.adicionarProduto(n, 1, -2));
        erro(() -> pedidos.adicionarProduto(n, 99, 1)); // produto inexistente
    }

    private void aplicaDesconto() {
        produtos.cadastrar(1, "Pizza", "Pratos", 100.0);
        int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 2);
        contas.fecharConta(n, 10);
        Pedido p = pedidos.buscarPedido(n);
        verdadeiro(p.isFinalizado());
        igual(180.0, p.getValorFinal());
    }

    private void rejeitaDescontoInvalido() {
        produtos.cadastrar(1, "Pizza", "Pratos", 100.0);
        final int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 1);
        erro(() -> contas.fecharConta(n, -1));
        erro(() -> contas.fecharConta(n, 100.01));
        erro(() -> contas.fecharConta(n, Double.NaN));
        contas.fecharConta(n, 100); // limite válido: conta gratuita
        igual(0.0, pedidos.buscarPedido(n).getValorFinal());
    }

    private void rejeitaPedidoVazio() {
        final int n = pedidos.abrirPedido(1).getNumero();
        erro(() -> contas.fecharConta(n, 0));
    }

    private void bloqueiaPedidoFinalizado() {
        produtos.cadastrar(1, "Pizza", "Pratos", 40.0);
        final int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 1);
        contas.fecharConta(n, 0);
        erro(() -> pedidos.adicionarProduto(n, 1, 1));
        erro(() -> pedidos.alterarQuantidade(n, 0, 3));
        erro(() -> pedidos.removerItem(n, 0));
        erro(() -> contas.fecharConta(n, 0));
    }

    private void precoCongeladoNoItem() {
        produtos.cadastrar(1, "Pizza", "Pratos", 40.0);
        int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 1);
        produtos.editar(1, "Pizza", "Pratos", 99.0);
        igual(40.0, pedidos.buscarPedido(n).getItens().get(0).getPrecoUnitario());
    }

    private void relatorioSoFinalizados() {
        produtos.cadastrar(1, "Pizza", "Pratos", 50.0);
        int a = pedidos.abrirPedido(1).getNumero();
        int b = pedidos.abrirPedido(2).getNumero();
        pedidos.adicionarProduto(a, 1, 2);
        pedidos.adicionarProduto(b, 1, 4); // continua aberto
        contas.fecharConta(a, 0);
        igual(1, relatorios.quantidadePedidosFinalizados());
        igual(100.0, relatorios.faturamentoTotal());
    }

    private void rankingOrdenado() {
        produtos.cadastrar(1, "Pizza", "Pratos", 50.0);
        produtos.cadastrar(2, "Suco", "Bebidas", 10.0);
        produtos.cadastrar(3, "Pudim", "Sobremesas", 15.0);
        int n = pedidos.abrirPedido(1).getNumero();
        pedidos.adicionarProduto(n, 1, 1);
        pedidos.adicionarProduto(n, 2, 5);
        pedidos.adicionarProduto(n, 3, 3);
        contas.fecharConta(n, 0);
        java.util.Iterator<java.util.Map.Entry<String, Integer>> it =
                relatorios.produtosMaisVendidos(2).entrySet().iterator();
        igual("Suco", it.next().getKey());
        igual("Pudim", it.next().getKey());
        verdadeiro(!it.hasNext()); // respeita o limite
    }

    // --------------------------------------------------------------- infra

    private void executar(String nome, Runnable teste) {
        Armazenamento.PRODUTOS.clear();
        Armazenamento.PEDIDOS.clear();
        total++;
        try {
            teste.run();
            System.out.println("  OK     " + nome);
        } catch (Throwable e) {
            falhas++;
            System.out.println("  FALHOU " + nome + " -> " + e);
        }
    }

    private static void erro(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException esperado) {
            return;
        }
        throw new AssertionError("esperava IllegalArgumentException");
    }

    private static void verdadeiro(boolean condicao) {
        if (!condicao) throw new AssertionError("condição falsa");
    }

    private static void igual(Object esperado, Object atual) {
        if (!esperado.equals(atual)) throw new AssertionError("esperado " + esperado + " mas foi " + atual);
    }

    private static void igual(double esperado, double atual) {
        if (Math.abs(esperado - atual) > 0.0001) throw new AssertionError("esperado " + esperado + " mas foi " + atual);
    }
}
