package br.com.restaurante.controller;

import br.com.restaurante.dados.Armazenamento;
import br.com.restaurante.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoController {

    public void cadastrar(int codigo, String nome, String categoria, double preco) {
        validarDados(codigo, nome, categoria, preco);
        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException("Já existe um produto com esse código.");
        }
        Armazenamento.PRODUTOS.add(new Produto(codigo, nome.trim(), categoria.trim(), preco));
    }

    public void editar(int codigo, String nome, String categoria, double preco) {
        validarDados(codigo, nome, categoria, preco);
        Produto produto = buscarPorCodigo(codigo);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        produto.setNome(nome.trim());
        produto.setCategoria(categoria.trim());
        produto.setPreco(preco);
    }

    public void excluir(int codigo) {
        Produto produto = buscarPorCodigo(codigo);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        Armazenamento.PRODUTOS.remove(produto);
    }

    public Produto buscarPorCodigo(int codigo) {
        for (Produto produto : Armazenamento.PRODUTOS) {
            if (produto.getCodigo() == codigo) return produto;
        }
        return null;
    }

    public List<Produto> listar(String termo) {
        ArrayList<Produto> resultado = new ArrayList<Produto>();
        String busca = termo == null ? "" : termo.trim().toLowerCase();
        for (Produto produto : Armazenamento.PRODUTOS) {
            if (String.valueOf(produto.getCodigo()).contains(busca)
                    || produto.getNome().toLowerCase().contains(busca)
                    || produto.getCategoria().toLowerCase().contains(busca)) {
                resultado.add(produto);
            }
        }
        return resultado;
    }

    private void validarDados(int codigo, String nome, String categoria, double preco) {
        if (codigo <= 0) throw new IllegalArgumentException("O código deve ser maior que zero.");
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome.");
        if (categoria == null || categoria.trim().isEmpty()) throw new IllegalArgumentException("Informe a categoria.");
        if (Double.isNaN(preco) || Double.isInfinite(preco) || preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo ou inválido.");
        }
    }
}
