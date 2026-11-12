package br.com.restaurante.view;

import br.com.restaurante.controller.ContaController;
import br.com.restaurante.controller.PedidoController;
import br.com.restaurante.model.ItemPedido;
import br.com.restaurante.model.Pedido;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;

public class FechamentoPanel extends JPanel implements Atualizavel {
    private final PedidoController pedidos = new PedidoController();
    private final ContaController contas = new ContaController();
    private final JComboBox<String> comboPedidos = new JComboBox<String>();
    private final JTextField desconto = new JTextField("0", 8);
    private final JLabel linhaSubtotal = new JLabel("R$ 0,00");
    private final JLabel rotuloDesconto = Tema.texto("Desconto");
    private final JLabel linhaDesconto = new JLabel("- R$ 0,00");
    private final JLabel total = new JLabel("R$ 0,00");
    private final JLabel tituloConta = Tema.titulo("Nenhuma conta selecionada", 17f);
    private final Etiqueta situacao = new Etiqueta();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Item", "Qtd.", "Subtotal"}, 0) {
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
                String[] linhas = comboPedidos.getItemCount() == 0
                        ? new String[]{"Não há pedidos abertos.", "Abra um pedido na tela de Pedidos."}
                        : new String[]{"Escolha um pedido para ver a conta."};
                FontMetrics fm = g2.getFontMetrics();
                int y = getHeight() / 2 + 4;
                for (String linha : linhas) {
                    g2.drawString(linha, (getWidth() - fm.stringWidth(linha)) / 2, y);
                    y += 22;
                }
                g2.dispose();
            }
        }
    };
    private Pedido pedidoAtual;
    private boolean atualizando;

    public FechamentoPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        add(criarPainelEsquerdo(), BorderLayout.WEST);
        add(criarConta(), BorderLayout.CENTER);

        comboPedidos.addActionListener(e -> { if (!atualizando) carregarPedido(); });
        desconto.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarPrevia(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarPrevia(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarPrevia(); }
        });
        atualizarLista();
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

        c.insets = new Insets(0, 0, 14, 0);
        c.gridy = linha++;
        cartao.add(Tema.titulo("Fechamento de conta", 17f), c);

        Tema.estilizarCombo(comboPedidos);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("PEDIDO ABERTO"), c);
        c.insets = new Insets(0, 0, 16, 0);
        c.gridy = linha++;
        cartao.add(comboPedidos, c);

        Tema.estilizarCampo(desconto);
        desconto.setHorizontalAlignment(SwingConstants.CENTER);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        cartao.add(Tema.rotulo("DESCONTO (%)"), c);
        c.insets = new Insets(0, 0, 8, 0);
        c.gridy = linha++;
        cartao.add(desconto, c);

        JPanel atalhos = new JPanel(new GridLayout(1, 4, 6, 0));
        atalhos.setOpaque(false);
        for (final int valor : new int[]{0, 5, 10, 15}) {
            BotaoModerno b = new BotaoModerno(valor + "%", BotaoModerno.Estilo.SECUNDARIO);
            b.setBorder(BorderFactory.createEmptyBorder(7, 4, 7, 4));
            b.setFont(Tema.fonte(Font.BOLD, 12.5f));
            b.addActionListener(e -> desconto.setText(String.valueOf(valor)));
            atalhos.add(b);
        }
        c.insets = new Insets(0, 0, 22, 0);
        c.gridy = linha++;
        cartao.add(atalhos, c);

        BotaoModerno fechar = new BotaoModerno("Finalizar conta", BotaoModerno.Estilo.PRIMARIO);
        fechar.setFont(Tema.fonte(Font.BOLD, 14.5f));
        fechar.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 18));
        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = linha++;
        cartao.add(fechar, c);

        c.gridy = linha;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        cartao.add(Box.createVerticalGlue(), c);

        fechar.addActionListener(e -> fecharConta());
        return cartao;
    }

    /** Cartão no estilo "nota fiscal": itens, subtotal, desconto e total. */
    private JComponent criarConta() {
        CartaoPanel cartao = new CartaoPanel(new BorderLayout(0, 12));

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.X_AXIS));
        cabecalho.add(tituloConta);
        cabecalho.add(Box.createHorizontalStrut(12));
        cabecalho.add(situacao);
        cabecalho.add(Box.createHorizontalGlue());

        Tema.estilizarTabela(tabela);
        tabela.setFocusable(false);
        tabela.setRowSelectionAllowed(false);
        Tema.alinharColuna(tabela, 1, SwingConstants.CENTER);
        Tema.alinharColuna(tabela, 2, SwingConstants.RIGHT);
        Tema.larguras(tabela, new int[]{300, 60, 120}, new int[]{120, 56, 110});
        JScrollPane rolagem = Tema.rolagem(tabela);
        rolagem.setBorder(new BordaArredondada(Tema.BORDA, 12, 1));

        JPanel totais = new JPanel(new GridBagLayout());
        totais.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 3, 0);

        linhaSubtotal.setFont(Tema.fonte(Font.PLAIN, 14f));
        linhaSubtotal.setForeground(Tema.TEXTO);
        rotuloDesconto.setFont(Tema.fonte(Font.PLAIN, 14f));
        linhaDesconto.setFont(Tema.fonte(Font.PLAIN, 14f));
        linhaDesconto.setForeground(Tema.SUCESSO);
        total.setFont(Tema.fonte(Font.BOLD, 30f));
        total.setForeground(Tema.PRIMARIA);

        adicionarLinhaTotal(totais, c, 0, Tema.texto("Subtotal"), linhaSubtotal);
        adicionarLinhaTotal(totais, c, 1, rotuloDesconto, linhaDesconto);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weightx = 1;
        c.insets = new Insets(8, 0, 8, 0);
        totais.add(new TracejadoLinha(), c);

        JLabel rotuloTotal = Tema.titulo("Total a pagar", 16f);
        c.gridwidth = 1; c.gridy = 3; c.gridx = 0; c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        totais.add(rotuloTotal, c);
        c.gridx = 1; c.weightx = 0;
        totais.add(total, c);
        total.setHorizontalAlignment(SwingConstants.RIGHT);

        cartao.add(cabecalho, BorderLayout.NORTH);
        cartao.add(rolagem, BorderLayout.CENTER);
        cartao.add(totais, BorderLayout.SOUTH);
        return cartao;
    }

    private void adicionarLinhaTotal(JPanel painel, GridBagConstraints c, int linha, JLabel esquerda, JLabel direita) {
        c.gridx = 0; c.gridy = linha; c.weightx = 1; c.gridwidth = 1;
        c.insets = new Insets(3, 0, 3, 0);
        painel.add(esquerda, c);
        c.gridx = 1; c.weightx = 0;
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        painel.add(direita, c);
    }

    /** Linha tracejada, como em um comprovante impresso. */
    private static class TracejadoLinha extends JComponent {
        TracejadoLinha() {
            setPreferredSize(new Dimension(10, 2));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizar(g);
            g2.setColor(new Color(0xCFC4B9));
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                    new float[]{5f, 5f}, 0f));
            g2.drawLine(0, 1, getWidth(), 1);
            g2.dispose();
        }
    }

    // ---------- ações (mesma lógica de antes) ----------

    private void atualizarLista() {
        atualizando = true;
        try {
            comboPedidos.removeAllItems();
            String rotuloAtual = null;
            for (Pedido p : pedidos.listarPedidos()) {
                if (!p.isFinalizado()) {
                    String rotulo = p.getNumero() + " - Mesa " + p.getNumeroMesa();
                    comboPedidos.addItem(rotulo);
                    if (p == pedidoAtual) rotuloAtual = rotulo;
                }
            }
            if (rotuloAtual != null) {
                comboPedidos.setSelectedItem(rotuloAtual);
            } else {
                comboPedidos.setSelectedIndex(-1);
                pedidoAtual = null;
            }
        } finally {
            atualizando = false;
        }
        atualizarPrevia();
    }

    @Override
    public void atualizar() {
        atualizarLista();
    }

    private void carregarPedido() {
        try {
            Object item = comboPedidos.getSelectedItem();
            if (item == null) { pedidoAtual = null; atualizarPrevia(); return; }
            int numero = Integer.parseInt(item.toString().substring(0, item.toString().indexOf(" -")));
            pedidoAtual = pedidos.buscarPedido(numero);
            atualizarPrevia();
        } catch (Exception ex) {
            Toast.erro(this, ex instanceof IllegalArgumentException ? ex.getMessage() : "Selecione um pedido válido.");
        }
    }

    private void atualizarPrevia() {
        modelo.setRowCount(0);
        if (pedidoAtual == null) {
            tituloConta.setText("Nenhuma conta selecionada");
            situacao.definir("", Tema.BORDA, Tema.TEXTO);
            linhaSubtotal.setText(Tema.moeda(0));
            rotuloDesconto.setText("Desconto");
            linhaDesconto.setText("- " + Tema.moeda(0));
            total.setText(Tema.moeda(0));
            tabela.repaint();
            return;
        }
        for (ItemPedido item : pedidoAtual.getItens()) {
            modelo.addRow(new Object[]{item.getProduto().getNome(), item.getQuantidade(),
                    Tema.moeda(item.getSubtotal())});
        }
        tituloConta.setText("Mesa " + pedidoAtual.getNumeroMesa() + "  |  Pedido " + pedidoAtual.getNumero());
        situacao.definir("EM ABERTO", new Color(0xFFF1D6), new Color(0x9A6410));

        double sub = pedidoAtual.calcularSubtotal();
        double desc;
        try { desc = Double.parseDouble(desconto.getText().trim().replace(",", ".")); }
        catch (NumberFormatException ex) { desc = 0; }
        if (desc < 0 || desc > 100) desc = 0;
        double valorDesconto = sub * desc / 100.0;
        linhaSubtotal.setText(Tema.moeda(sub));
        rotuloDesconto.setText(desc == 0 ? "Desconto" : String.format(Locale.forLanguageTag("pt-BR"), "Desconto (%.0f%%)", desc));
        linhaDesconto.setText("- " + Tema.moeda(valorDesconto));
        total.setText(Tema.moeda(sub - valorDesconto));
        tabela.repaint();
    }

    private void fecharConta() {
        try {
            if (pedidoAtual == null) throw new IllegalArgumentException("Carregue um pedido primeiro.");
            double percentual = Double.parseDouble(desconto.getText().trim().replace(",", "."));
            contas.fecharConta(pedidoAtual.getNumero(), percentual);
            Toast.sucesso(this, "Conta finalizada! Total: " + Tema.moeda(pedidoAtual.getValorFinal()));
            pedidoAtual = null;
            desconto.setText("0");
            atualizarLista();
        } catch (NumberFormatException ex) { Toast.erro(this, "Informe um desconto numérico."); }
        catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
    }
}
