package br.com.restaurante.view;

import br.com.restaurante.controller.ProdutoController;
import br.com.restaurante.model.Produto;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class ProdutosPanel extends JPanel {
    private final ProdutoController controller = new ProdutoController();
    private final JTextField codigo = new JTextField(8);
    private final JTextField nome = new JTextField(18);
    private final JTextField categoria = new JTextField(14);
    private final JTextField preco = new JTextField(8);
    private final JTextField pesquisa = new JTextField(18) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setFont(Tema.fonte(Font.PLAIN, 13.5f));
                g2.setColor(new Color(0xB5ABA3));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Pesquisar por código, nome ou categoria", getInsets().left,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        }
    };
    private final JLabel contador = new JLabel();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Nome", "Categoria", "Preço (R$)"}, 0) {
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
                String[] linhas = pesquisa.getText().trim().isEmpty()
                        ? new String[]{"Nenhum produto cadastrado ainda.", "Use o formulário ao lado para criar o primeiro item do cardápio."}
                        : new String[]{"Nenhum produto encontrado para essa pesquisa."};
                FontMetrics fm = g2.getFontMetrics();
                int y = getHeight() / 2 - (linhas.length * 22) / 2 + 24;
                for (String linha : linhas) {
                    g2.drawString(linha, (getWidth() - fm.stringWidth(linha)) / 2, y);
                    y += 22;
                }
                g2.dispose();
            }
        }
    };

    public ProdutosPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));

        add(criarFormulario(), BorderLayout.WEST);
        add(criarListagem(), BorderLayout.CENTER);

        pesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarTabela(pesquisa.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarTabela(pesquisa.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarTabela(pesquisa.getText()); }
        });
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherCamposSelecionados();
        });
        atualizarTabela("");
    }

    // ---------- montagem da tela ----------

    private JComponent criarFormulario() {
        CartaoPanel cartao = new CartaoPanel(new GridBagLayout());
        cartao.setPreferredSize(new Dimension(340, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        int linha = 0;
        c.gridy = linha++;
        cartao.add(Tema.titulo("Dados do produto", 17f), c);
        c.gridy = linha++;
        JLabel dica = new JLabel("<html>Selecione uma linha da tabela para editar ou excluir.</html>");
        dica.setFont(Tema.fonte(Font.PLAIN, 12f));
        dica.setForeground(Tema.TEXTO_SUAVE);
        c.insets = new Insets(2, 0, 14, 0);
        cartao.add(dica, c);

        linha = adicionarCampo(cartao, c, linha, "CÓDIGO", codigo);
        linha = adicionarCampo(cartao, c, linha, "NOME", nome);
        linha = adicionarCampo(cartao, c, linha, "CATEGORIA", categoria);
        linha = adicionarCampo(cartao, c, linha, "PREÇO (R$)", preco);

        BotaoModerno cadastrar = new BotaoModerno("Cadastrar produto", BotaoModerno.Estilo.PRIMARIO);
        BotaoModerno editar = new BotaoModerno("Salvar edição", BotaoModerno.Estilo.SECUNDARIO);
        BotaoModerno excluir = new BotaoModerno("Excluir selecionado", BotaoModerno.Estilo.PERIGO);
        BotaoModerno limpar = new BotaoModerno("Limpar campos", BotaoModerno.Estilo.FANTASMA);

        c.insets = new Insets(10, 0, 0, 0);
        c.gridy = linha++;
        cartao.add(cadastrar, c);
        c.insets = new Insets(8, 0, 0, 0);
        c.gridy = linha++;
        cartao.add(editar, c);
        c.gridy = linha++;
        cartao.add(excluir, c);
        c.gridy = linha++;
        cartao.add(limpar, c);

        c.gridy = linha;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        cartao.add(Box.createVerticalGlue(), c);

        cadastrar.addActionListener(e -> executarCadastro());
        editar.addActionListener(e -> executarEdicao());
        excluir.addActionListener(e -> executarExclusao());
        limpar.addActionListener(e -> limparCampos());
        return cartao;
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints c, int linha, String rotulo, JTextField campo) {
        Tema.estilizarCampo(campo);
        c.insets = new Insets(0, 0, 4, 0);
        c.gridy = linha++;
        painel.add(Tema.rotulo(rotulo), c);
        c.insets = new Insets(0, 0, 12, 0);
        c.gridy = linha++;
        painel.add(campo, c);
        return linha;
    }

    private JComponent criarListagem() {
        CartaoPanel cartao = new CartaoPanel(new BorderLayout(0, 12));

        Tema.estilizarCampo(pesquisa);
        contador.setFont(Tema.fonte(Font.BOLD, 12.5f));
        contador.setForeground(Tema.TEXTO_SUAVE);
        contador.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 4));

        JPanel busca = new JPanel(new BorderLayout());
        busca.setOpaque(false);
        busca.add(pesquisa, BorderLayout.CENTER);
        busca.add(contador, BorderLayout.EAST);

        Tema.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFocusable(true);
        Tema.alinharColuna(tabela, 0, SwingConstants.CENTER);
        Tema.alinharColuna(tabela, 3, SwingConstants.RIGHT);
        tabela.getColumnModel().getColumn(2).setCellRenderer(new CategoriaRenderer());
        Tema.larguras(tabela, new int[]{80, 230, 150, 112}, new int[]{76, 120, 120, 110});

        JScrollPane rolagem = Tema.rolagem(tabela);
        rolagem.setBorder(new BordaArredondada(Tema.BORDA, 12, 1));

        cartao.add(busca, BorderLayout.NORTH);
        cartao.add(rolagem, BorderLayout.CENTER);
        return cartao;
    }

    // ---------- ações (mesma lógica de antes) ----------

    private int lerCodigo() { return Integer.parseInt(codigo.getText().trim()); }
    private double lerPreco() { return Double.parseDouble(preco.getText().trim().replace(",", ".")); }

    private void executarCadastro() {
        try {
            controller.cadastrar(lerCodigo(), nome.getText(), categoria.getText(), lerPreco());
            atualizarTabela(pesquisa.getText());
            limparCampos();
            Toast.sucesso(this, "Produto cadastrado com sucesso.");
        } catch (NumberFormatException ex) {
            Toast.erro(this, "Código e preço devem ser números válidos.");
        } catch (IllegalArgumentException ex) {
            Toast.erro(this, ex.getMessage());
        }
    }

    private void executarEdicao() {
        try {
            controller.editar(lerCodigo(), nome.getText(), categoria.getText(), lerPreco());
            atualizarTabela(pesquisa.getText());
            limparCampos();
            Toast.sucesso(this, "Produto atualizado com sucesso.");
        } catch (NumberFormatException ex) {
            Toast.erro(this, "Código e preço devem ser números válidos.");
        } catch (IllegalArgumentException ex) {
            Toast.erro(this, ex.getMessage());
        }
    }

    private void executarExclusao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { Toast.erro(this, "Selecione um produto na tabela."); return; }
        int codigoSelecionado = (Integer) modelo.getValueAt(linha, 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir o produto selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.excluir(codigoSelecionado);
                atualizarTabela(pesquisa.getText());
                limparCampos();
                Toast.sucesso(this, "Produto excluído.");
            } catch (IllegalArgumentException ex) { Toast.erro(this, ex.getMessage()); }
        }
    }

    private void preencherCamposSelecionados() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            codigo.setText(String.valueOf(modelo.getValueAt(linha, 0)));
            nome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            categoria.setText(String.valueOf(modelo.getValueAt(linha, 2)));
            preco.setText(String.valueOf(modelo.getValueAt(linha, 3)).replace(",", "."));
        }
    }

    private void atualizarTabela(String termo) {
        modelo.setRowCount(0);
        List<Produto> produtos = controller.listar(termo);
        for (Produto p : produtos) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getCategoria(),
                    String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getPreco())});
        }
        contador.setText(produtos.size() == 1 ? "1 produto" : produtos.size() + " produtos");
    }

    private void limparCampos() {
        codigo.setText(""); nome.setText(""); categoria.setText(""); preco.setText("");
        tabela.clearSelection();
    }

    // ---------- etiqueta colorida de categoria ----------

    /** Mostra a categoria como uma etiqueta arredondada; a cor depende do nome da categoria. */
    private static class CategoriaRenderer extends JPanel implements TableCellRenderer {
        private static final Color[][] PALETA = {
                {new Color(0xFDE8DF), new Color(0xB84120)},
                {new Color(0xE3F2E8), new Color(0x2F7A4A)},
                {new Color(0xFFF1D6), new Color(0x9A6410)},
                {new Color(0xE4ECF8), new Color(0x3B5F9E)},
                {new Color(0xF3E6F5), new Color(0x7B3F8C)},
                {new Color(0xE0F0F0), new Color(0x2F7478)}
        };
        private String texto = "";
        private Color fundoEtiqueta = PALETA[0][0];
        private Color corTexto = PALETA[0][1];

        CategoriaRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco,
                                                        int linha, int coluna) {
            texto = valor == null ? "" : valor.toString();
            int indice = (texto.toLowerCase().hashCode() & 0x7fffffff) % PALETA.length;
            fundoEtiqueta = PALETA[indice][0];
            corTexto = PALETA[indice][1];
            setBackground(sel ? Tema.SELECAO : (linha % 2 == 0 ? Color.WHITE : Tema.ZEBRA));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = Tema.suavizar(g);
            g2.setFont(Tema.fonte(Font.BOLD, 12f));
            FontMetrics fm = g2.getFontMetrics();
            int largura = Math.min(getWidth() - 20, fm.stringWidth(texto) + 22);
            int altura = 24;
            int y = (getHeight() - altura) / 2;
            g2.setColor(fundoEtiqueta);
            g2.fillRoundRect(12, y, largura, altura, altura, altura);
            g2.setColor(corTexto);
            g2.drawString(texto, 12 + 11, y + (altura + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
