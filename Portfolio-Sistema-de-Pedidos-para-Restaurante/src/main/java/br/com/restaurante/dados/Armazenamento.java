package br.com.restaurante.dados;

import br.com.restaurante.model.Pedido;
import br.com.restaurante.model.Produto;

import java.util.ArrayList;

public final class Armazenamento {
    public static final ArrayList<Produto> PRODUTOS = new ArrayList<Produto>();
    public static final ArrayList<Pedido> PEDIDOS = new ArrayList<Pedido>();

    private Armazenamento() {
        // Evita criar instâncias desta classe.
    }
}
