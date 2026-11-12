package br.com.restaurante.controller;

import br.com.restaurante.dados.Armazenamento;
import br.com.restaurante.model.ItemPedido;
import br.com.restaurante.model.Pedido;
import br.com.restaurante.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    public Pedido abrirPedido(int numeroMesa) {
        if (numeroMesa <= 0) throw new IllegalArgumentException("Informe um número de mesa válido.");
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (pedido.getNumeroMesa() == numeroMesa && !pedido.isFinalizado()) {
                throw new IllegalArgumentException("Essa mesa já possui um pedido aberto.");
            }
        }
        int proximoNumero = 1;
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (pedido.getNumero() >= proximoNumero) proximoNumero = pedido.getNumero() + 1;
        }
        Pedido pedido = new Pedido(proximoNumero, numeroMesa);
        Armazenamento.PEDIDOS.add(pedido);
        return pedido;
    }

    public void adicionarProduto(int numeroPedido, int codigoProduto, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        Pedido pedido = buscarPedido(numeroPedido);
        garantirAberto(pedido);
        Produto produto = buscarProduto(codigoProduto);
        if (produto == null) throw new IllegalArgumentException("Produto não encontrado.");
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto().getCodigo() == codigoProduto) {
                int indice = pedido.getItens().indexOf(item);
                pedido.definirQuantidade(indice, item.getQuantidade() + quantidade);
                return;
            }
        }
        pedido.adicionarItem(new ItemPedido(produto, quantidade));
    }

    public void alterarQuantidade(int numeroPedido, int indiceItem, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        Pedido pedido = buscarPedido(numeroPedido);
        garantirAberto(pedido);
        if (indiceItem < 0 || indiceItem >= pedido.getItens().size()) {
            throw new IllegalArgumentException("Selecione um item válido.");
        }
        pedido.definirQuantidade(indiceItem, quantidade);
    }

    public void removerItem(int numeroPedido, int indiceItem) {
        Pedido pedido = buscarPedido(numeroPedido);
        garantirAberto(pedido);
        if (indiceItem < 0 || indiceItem >= pedido.getItens().size()) {
            throw new IllegalArgumentException("Selecione um item válido.");
        }
        pedido.removerItem(indiceItem);
    }

    public Pedido buscarPedido(int numeroPedido) {
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (pedido.getNumero() == numeroPedido) return pedido;
        }
        throw new IllegalArgumentException("Pedido não encontrado.");
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<Pedido>(Armazenamento.PEDIDOS);
    }

    private Produto buscarProduto(int codigo) {
        for (Produto produto : Armazenamento.PRODUTOS) {
            if (produto.getCodigo() == codigo) return produto;
        }
        return null;
    }

    private void garantirAberto(Pedido pedido) {
        if (pedido.isFinalizado()) throw new IllegalArgumentException("Não é possível alterar um pedido finalizado.");
    }
}
