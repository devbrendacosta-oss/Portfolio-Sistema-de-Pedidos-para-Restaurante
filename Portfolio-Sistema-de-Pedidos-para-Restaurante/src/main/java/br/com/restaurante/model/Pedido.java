package br.com.restaurante.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int numero;
    private int numeroMesa;
    private ArrayList<ItemPedido> itens;
    private boolean finalizado;
    private double percentualDesconto;
    private double valorFinal;
    private LocalDateTime dataFechamento;

    public Pedido(int numero, int numeroMesa) {
        this.numero = numero;
        this.numeroMesa = numeroMesa;
        this.itens = new ArrayList<ItemPedido>();
        this.finalizado = false;
        this.percentualDesconto = 0.0;
        this.valorFinal = 0.0;
    }

    public int getNumero() { return numero; }
    public int getNumeroMesa() { return numeroMesa; }
    public List<ItemPedido> getItens() { return new ArrayList<ItemPedido>(itens); }
    public boolean isFinalizado() { return finalizado; }
    public double getPercentualDesconto() { return percentualDesconto; }
    public double getValorFinal() { return valorFinal; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }

    public double calcularSubtotal() {
        double subtotal = 0.0;
        for (ItemPedido item : itens) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public void adicionarItem(ItemPedido item) { itens.add(item); }

    public void removerItem(int indice) { itens.remove(indice); }

    public void definirQuantidade(int indice, int quantidade) {
        itens.get(indice).setQuantidade(quantidade);
    }

    public void finalizar(double desconto) {
        double subtotal = calcularSubtotal();
        this.percentualDesconto = desconto;
        this.valorFinal = subtotal - (subtotal * desconto / 100.0);
        this.finalizado = true;
        this.dataFechamento = LocalDateTime.now();
    }
}
