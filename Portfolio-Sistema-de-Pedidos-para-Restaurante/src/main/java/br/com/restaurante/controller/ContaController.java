package br.com.restaurante.controller;

import br.com.restaurante.model.Pedido;

public class ContaController {
    private final PedidoController pedidoController = new PedidoController();

    public void fecharConta(int numeroPedido, double descontoPercentual) {
        if (Double.isNaN(descontoPercentual) || Double.isInfinite(descontoPercentual)
                || descontoPercentual < 0 || descontoPercentual > 100) {
            throw new IllegalArgumentException("O desconto deve estar entre 0% e 100%.");
        }
        Pedido pedido = pedidoController.buscarPedido(numeroPedido);
        if (pedido.isFinalizado()) throw new IllegalArgumentException("Esse pedido já foi finalizado.");
        if (pedido.getItens().isEmpty()) throw new IllegalArgumentException("Não é possível fechar um pedido vazio.");
        pedido.finalizar(descontoPercentual);
    }
}
