package br.com.restaurante.controller;

import br.com.restaurante.dados.Armazenamento;
import br.com.restaurante.model.ItemPedido;
import br.com.restaurante.model.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioController {

    public int quantidadePedidosFinalizados() {
        int quantidade = 0;
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (pedido.isFinalizado()) quantidade++;
        }
        return quantidade;
    }

    public double faturamentoTotal() {
        double total = 0.0;
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (pedido.isFinalizado()) total += pedido.getValorFinal();
        }
        return total;
    }

    /**
     * Ranking dos produtos mais vendidos (somando as quantidades dos pedidos finalizados).
     * Retorna no máximo "limite" produtos, do mais vendido para o menos vendido.
     */
    public Map<String, Integer> produtosMaisVendidos(int limite) {
        Map<String, Integer> soma = new LinkedHashMap<String, Integer>();
        for (Pedido pedido : Armazenamento.PEDIDOS) {
            if (!pedido.isFinalizado()) continue;
            for (ItemPedido item : pedido.getItens()) {
                String nome = item.getProduto().getNome();
                Integer atual = soma.get(nome);
                soma.put(nome, (atual == null ? 0 : atual) + item.getQuantidade());
            }
        }
        List<Map.Entry<String, Integer>> entradas = new ArrayList<Map.Entry<String, Integer>>(soma.entrySet());
        Collections.sort(entradas, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });
        Map<String, Integer> ranking = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entrada : entradas) {
            if (ranking.size() >= limite) break;
            ranking.put(entrada.getKey(), entrada.getValue());
        }
        return ranking;
    }
}
