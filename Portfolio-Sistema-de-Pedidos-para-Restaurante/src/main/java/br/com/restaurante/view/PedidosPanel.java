package br.com.restaurante.view;

import br.com.restaurante.controller.PedidoController;
import br.com.restaurante.controller.ProdutoController;
import br.com.restaurante.model.ItemPedido;
import br.com.restaurante.model.Pedido;
import br.com.restaurante.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PedidosPanel extends JPanel implements Atualizavel {
    private final PedidoController pedidos = new PedidoController();
    private final ProdutoController produtos = new ProdutoController();
    private final JTextField mesa = new JTextField(5);
    private final JComboBox<String> comboPedidos = new JComboBox<String>();
    private final JComboBox<ProdutoOpcao> comboProdutos = new JComboBox<ProdutoOpcao>();
    private final JTextField quantidade = new JTextField("1", 5);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Cód.", "Produto", "Qtd.", "Preço unit.", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getRowCount() == 0) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setFont(Tema.fonte(Font.PLAIN, 14f));
                g2.setColor(Tema.TEXTO_SUAVE);
                String[] linhas = pedidoAtual == null
                        ? new String[]{"Nenhum pedido selecionado.", "Abra um pedido ou escolha um da lista."}
                        : new String[]{"Este pedido ainda não tem itens.", "Escolha um produto e clique em Adicionar."};
                FontMetrics fm = g2.getFontMetrics();
                int y = getHeight() / 2 - 11 + 12;
                for (String linha : linhas) {
                    g2.drawString(linha, (getWidth() - fm.stringWidth(linha)) / 2, y);
                    y += 22;
                }
                g2.dispose();
            }
        }
    };
    private final JLabel tituloPedido = Tema.titulo("Nenhum pedido", 17f);
    private final Etiqueta situacao = new Etiqueta();
    private final JLabel itens = new JLabel("0 itens");
    private final JLabel subtotal = new JLabel("R$ 0,00");
    private Pedido pedidoAtual;
    private boolean atualizando;

    public PedidosPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        add(criarPainelEsquerdo(), BorderLayout.WEST);
        add(criarComanda(), BorderLayout.CENTER);

        comboPedidos.addActionListener(e -> { if (!atualizando) carregarPedidoSelecionado(); });
        atualizarTabela();
    }

    // ---------- montagem da tela ----------

    private JComponent criarPainelEsquerdo() {
        CartaoPanel cartao = new CartaoPanel(new GridBagLayout());
        cartao.setPreferredSize(new Dimension(340, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        int linha = 0;

        // Bloco 1: abrir ou selecionar pedido
        c.insets = new Insets(0, 0, 10, 0);
        c.gridy = linha++;
        cartao.add(Tema.titulo("Abrir ou selecionar pedido", 16f), c);

        Tema.estilizarCampo(mesa);
        BotaoModerno abrirBtn = new BotaoModerno("Abrir pedido", BotaoModerno.Estilo.PRIMARIO);
        JPanel linhaMesa = new JPanel(new BorderLayout(8, 0));
        linhaMesa.setOpaque(false);
        linhaMesa.add(mesa, BorderLayout.CENTER);
        linhaMesa.add(abrirBtn, BorderLayout.EAST);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("NÚMERO DA MESA"), c);
        c.insets = new Insets(0, 0, 12, 0);
        c.gridy = linha++;
        cartao.add(linhaMesa, c);

        Tema.estilizarCombo(comboPedidos);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("PEDIDOS EXISTENTES"), c);
        c.insets = new Insets(0, 0, 16, 0);
        c.gridy = linha++;
        cartao.add(comboPedidos, c);

        c.insets = new Insets(0, 0, 14, 0);
        c.gridy = linha++;
        cartao.add(new JSeparator() {
            @Override public void paint(Graphics g) {
                g.setColor(Tema.BORDA);
                g.fillRect(0, 0, getWidth(), 1);
            }
        }, c);

        // Bloco 2: adicionar produto
        c.insets = new Insets(0, 0, 10, 0);
        c.gridy = linha++;
        cartao.add(Tema.titulo("Adicionar produto ao pedido", 16f), c);

        Tema.estilizarCombo(comboProdutos);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("PRODUTO"), c);
        c.insets = new Insets(0, 0, 12, 0);
        c.gridy = linha++;
        cartao.add(comboProdutos, c);

        Tema.estilizarCampo(quantidade);
        quantidade.setHorizontalAlignment(SwingConstants.CENTER);
        BotaoModerno adicionarBtn = new BotaoModerno("Adicionar", BotaoModerno.Estilo.PRIMARIO);
        JPanel linhaQtd = new JPanel(new BorderLayout(8, 0));
        linhaQtd.setOpaque(false);
        quantidade.setPreferredSize(new Dimension(90, 38));
        linhaQtd.add(quantidade, BorderLayout.WEST);
        linhaQtd.add(adicionarBtn, BorderLayout.CENTER);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("QUANTIDADE"), c);
        c.insets = new Insets(0, 0, 12, 0);
        c.gridy = linha++;
        cartao.add(linhaQtd, c);

        BotaoModerno alterarBtn = new BotaoModerno("Alterar quantidade do item", BotaoModerno.Estilo.SECUNDARIO);
        BotaoModerno removerBtn = new BotaoModerno("Remover item selecionado", BotaoModerno.Estilo.FANTASMA);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = linha++;
        cartao.add(alterarBtn, c);
        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = linha++;
        cartao.add(removerBtn, c);

        c.gridy = linha;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        cartao.add(Box.createVerticalGlue(), c);

        abrirBtn.addActionListener(e -> abrirPedido());
        adicionarBtn.addActionListener(e -> adicionarProduto());
        alterarBtn.addActionListener(e -> alterarQuantidade());
        removerBtn.addActionListener(e -> removerItem());
        mesa.addActionListener(e -> abrirPedido());
        return cartao;
    }

    private JComponent criarComanda() {
        CartaoPanel cartao = new CartaoPanel(new BorderLayout(0, 12));

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.X_AXIS));
        cabecalho.add(tituloPedido);
        cabecalho.add(Box.createHorizontalStrut(12));
        cabecalho.add(situacao);
        cabecalho.add(Box.createHorizontalGlue());

        Tema.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFocusable(true);
        Tema.alinharColuna(tabela, 0, SwingConstants.CENTER);
        Tema.alinharColuna(tabela, 2, SwingConstants.CENTER);
        Tema.alinharColuna(tabela, 3, SwingConstants.RIGHT);
        Tema.alinharColuna(tabela, 4, SwingConstants.RIGHT);
        Tema.larguras(tabela, new int[]{60, 230, 60, 112, 112}, new int[]{56, 110, 56, 108, 108});
        JScrollPane rolagem = Tema.rolagem(tabela);
        rolagem.setBorder(new BordaArredondada(Tema.BORDA, 12, 1));

        itens.setFont(Tema.fonte(Font.PLAIN, 13f));
        itens.setForeground(Tema.TEXTO_SUAVE);
        JLabel rotuloSubtotal = Tema.rotulo("SUBTOTAL");
        subtotal.setFont(Tema.fonte(Font.BOLD, 26f));
        subtotal.setForeground(Tema.PRIMARIA);
        JPanel total = new JPanel();
        total.setOpaque(false);
        total.setLayout(new BoxLayout(total, BoxLayout.Y_AXIS));
        rotuloSubtotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        subtotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        total.add(rotuloSubtotal);
        total.add(subtotal);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(itens, BorderLayout.WEST);
        rodape.add(total, BorderLayout.EAST);

        cartao.add(cabecalho, BorderLayout.NORTH);
        cartao.add(rolagem, BorderLayout.CENTER);
        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    // ---------- ações (mesma lógica de antes) ----------

    private void abrirPedido() {
        try {
            pedidoAtual = pedidos.abrirPedido(Integer.parseInt(mesa.getText().trim()));
            atualizarTabela();
            mesa.setText("");
            Toast.sucesso(this, "Pedido aberto com sucesso.");
        } catch (NumberFormatException ex) { Toast.erro(this, "Informe um número de mesa válido."); }
        catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
    }

    private void carregarPedidoSelecionado() {
        Object selecionado = comboPedidos.getSelectedItem();
        if (selecionado == null) return;
        try {
            String texto = selecionado.toString();
            int numero = Integer.parseInt(texto.substring(0, texto.indexOf(" -")));
            pedidoAtual = pedidos.buscarPedido(numero);
            atualizarTabela();
        } catch (Exception ex) { Toast.erro(this, "Não foi possível carregar esse pedido."); }
    }

    private void adicionarProduto() {
        try {
            if (pedidoAtual == null) throw new IllegalArgumentException("Abra ou carregue um pedido primeiro.");
            ProdutoOpcao opcao = (ProdutoOpcao) comboProdutos.getSelectedItem();
            if (opcao == null) throw new IllegalArgumentException("Cadastre um produto antes de adicionar itens.");
            pedidos.adicionarProduto(pedidoAtual.getNumero(), opcao.produto.getCodigo(),
                    Integer.parseInt(quantidade.getText().trim()));
            atualizarTabela();
        } catch (NumberFormatException ex) { Toast.erro(this, "A quantidade deve ser um número inteiro."); }
        catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
    }

    private void removerItem() {
        try {
            if (pedidoAtual == null) throw new IllegalArgumentException("Carregue um pedido primeiro.");
            int linha = tabela.getSelectedRow();
            if (linha < 0) throw new IllegalArgumentException("Selecione um item.");
            pedidos.removerItem(pedidoAtual.getNumero(), linha);
            atualizarTabela();
        } catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
    }

    private void alterarQuantidade() {
        try {
            if (pedidoAtual == null) throw new IllegalArgumentException("Carregue um pedido primeiro.");
            int linha = tabela.getSelectedRow();
            if (linha < 0) throw new IllegalArgumentException("Selecione um item.");
            pedidos.alterarQuantidade(pedidoAtual.getNumero(), linha,
                    Integer.parseInt(quantidade.getText().trim()));
            atualizarTabela();
        } catch (NumberFormatException ex) { Toast.erro(this, "A quantidade deve ser um número inteiro."); }
        catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
    }

    @Override
    public void atualizar() {
        atualizarTabela();
    }

    /** Recarrega as listas mantendo o pedido e o produto que já estavam escolhidos. */
    private void atualizarCombos() {
        atualizando = true;
        try {
            comboPedidos.removeAllItems();
            String rotuloAtual = null;
            for (Pedido p : pedidos.listarPedidos()) {
                String rotulo = p.getNumero() + " - Mesa " + p.getNumeroMesa()
                        + (p.isFinalizado() ? " (Finalizado)" : " (Aberto)");
                comboPedidos.addItem(rotulo);
                if (p == pedidoAtual) rotuloAtual = rotulo;
            }
            if (rotuloAtual != null) comboPedidos.setSelectedItem(rotuloAtual);
            else comboPedidos.setSelectedIndex(-1);

            ProdutoOpcao anterior = (ProdutoOpcao) comboProdutos.getSelectedItem();
            comboProdutos.removeAllItems();
            List<Produto> lista = produtos.listar("");
            for (Produto p : lista) {
                ProdutoOpcao opcao = new ProdutoOpcao(p);
                comboProdutos.addItem(opcao);
                if (anterior != null && anterior.produto.getCodigo() == p.getCodigo()) {
                    comboProdutos.setSelectedItem(opcao);
                }
            }
        } finally {
            atualizando = false;
        }
    }

    private void atualizarTabela() {
        atualizarCombos();
        modelo.setRowCount(0);
        if (pedidoAtual == null) {
            tituloPedido.setText("Nenhum pedido");
            situacao.definir("", Tema.BORDA, Tema.TEXTO);
            itens.setText("0 itens");
            subtotal.setText(Tema.moeda(0));
            tabela.repaint();
            return;
        }
        int quantidadeTotal = 0;
        for (ItemPedido item : pedidoAtual.getItens()) {
            quantidadeTotal += item.getQuantidade();
            modelo.addRow(new Object[]{item.getProduto().getCodigo(), item.getProduto().getNome(),
                    item.getQuantidade(), Tema.moeda(item.getPrecoUnitario()),
                    Tema.moeda(item.getSubtotal())});
        }
        tituloPedido.setText("Pedido " + pedidoAtual.getNumero() + "  |  Mesa " + pedidoAtual.getNumeroMesa());
        if (pedidoAtual.isFinalizado()) situacao.definir("FINALIZADO", new Color(0xECE6DF), Tema.TEXTO_SUAVE);
        else situacao.definir("ABERTO", new Color(0xE3F2E8), Tema.SUCESSO);
        itens.setText(quantidadeTotal == 1 ? "1 unidade no pedido" : quantidadeTotal + " unidades no pedido");
        subtotal.setText(Tema.moeda(pedidoAtual.calcularSubtotal()));
        tabela.repaint();
    }

    private static class ProdutoOpcao {
        private final Produto produto;
        private ProdutoOpcao(Produto produto) { this.produto = produto; }
        @Override public String toString() {
            return produto.getCodigo() + " - " + produto.getNome() + " (" + Tema.moeda(produto.getPreco()) + ")";
        }
    }
}
