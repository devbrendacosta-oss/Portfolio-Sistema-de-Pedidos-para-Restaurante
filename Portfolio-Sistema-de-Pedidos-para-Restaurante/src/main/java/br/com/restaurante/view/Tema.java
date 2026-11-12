package br.com.restaurante.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Locale;

/**
 * Paleta de cores, fontes e utilitários visuais compartilhados por todas as telas.
 * Centralizar o estilo aqui permite trocar o visual do sistema inteiro mudando poucas linhas.
 */
public final class Tema {

    public static final Color FUNDO = new Color(0xFAF6F0);
    public static final Color CARTAO = Color.WHITE;
    public static final Color BARRA = new Color(0x2A1E1A);
    public static final Color BARRA_HOVER = new Color(0x3A2A24);
    public static final Color PRIMARIA = new Color(0xD9532B);
    public static final Color PRIMARIA_ESCURA = new Color(0xB84120);
    public static final Color DESTAQUE = new Color(0xF2A93B);
    public static final Color TEXTO = new Color(0x2B2523);
    public static final Color TEXTO_SUAVE = new Color(0x8A7F78);
    public static final Color BORDA = new Color(0xE8DFD5);
    public static final Color SUCESSO = new Color(0x3E8E5A);
    public static final Color PERIGO = new Color(0xC8453B);
    public static final Color ZEBRA = new Color(0xFCF9F5);
    public static final Color SELECAO = new Color(0xFCE3D8);

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static String familia;

    private Tema() {
    }

    /** Instala o visual Nimbus com as cores do sistema. Deve ser chamado antes de criar janelas. */
    public static void instalar() {
        UIManager.put("nimbusBase", new Color(0x6B5B53));
        UIManager.put("nimbusFocus", PRIMARIA);
        UIManager.put("nimbusSelectionBackground", PRIMARIA);
        UIManager.put("nimbusLightBackground", Color.WHITE);
        UIManager.put("control", FUNDO);
        UIManager.put("text", TEXTO);
        UIManager.put("OptionPane.background", FUNDO);
        UIManager.put("Panel.background", FUNDO);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se nenhum visual puder ser instalado, o Swing usa o padrão.
        }
    }

    /** Escolhe a melhor fonte disponível no computador, com SansSerif como reserva. */
    public static Font fonte(int estilo, float tamanho) {
        if (familia == null) {
            familia = "SansSerif";
            String[] preferidas = {"Segoe UI", "Inter", "Helvetica Neue", "Ubuntu", "Noto Sans"};
            String[] instaladas = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            for (String preferida : preferidas) {
                for (String instalada : instaladas) {
                    if (instalada.equalsIgnoreCase(preferida)) {
                        familia = instalada;
                        return new Font(familia, estilo, 12).deriveFont(tamanho);
                    }
                }
            }
        }
        return new Font(familia, estilo, 12).deriveFont(tamanho);
    }

    public static String moeda(double valor) {
        return String.format(PT_BR, "R$ %.2f", valor);
    }

    public static Graphics2D suavizar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // ---------- componentes de texto prontos ----------

    /** Rótulo pequeno e discreto usado acima dos campos de formulário. */
    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 11.5f));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JLabel titulo(String texto, float tamanho) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, tamanho));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel texto(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.PLAIN, 13f));
        l.setForeground(TEXTO);
        return l;
    }

    // ---------- campos ----------

    public static void estilizarCampo(JTextField campo) {
        campo.setFont(fonte(Font.PLAIN, 13.5f));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(PRIMARIA);
        campo.setSelectionColor(SELECAO);
        campo.setSelectedTextColor(TEXTO);
        campo.setBorder(bordaCampo(false));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 38));
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(bordaCampo(true));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(bordaCampo(false));
            }
        });
    }

    private static Border bordaCampo(boolean foco) {
        return BorderFactory.createCompoundBorder(
                new BordaArredondada(foco ? PRIMARIA : BORDA, 10, foco ? 2 : 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(fonte(Font.PLAIN, 13.5f));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXTO);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 38));
        combo.setBorder(new BordaArredondada(BORDA, 10, 1));
        combo.setUI(new ComboPlano());
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean foco) {
                JLabel l = (JLabel) super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                l.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
                l.setFont(fonte(Font.PLAIN, 13.5f));
                l.setForeground(TEXTO);
                l.setBackground(selecionado ? SELECAO : Color.WHITE);
                return l;
            }
        });
    }

    // ---------- tabelas ----------

    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(38);
        tabela.setFont(fonte(Font.PLAIN, 13.5f));
        tabela.setForeground(TEXTO);
        tabela.setBackground(Color.WHITE);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(SELECAO);
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setFocusable(false);
        tabela.setDefaultRenderer(Object.class, new CelulaZebrada(SwingConstants.LEFT));
        tabela.setDefaultRenderer(Integer.class, new CelulaZebrada(SwingConstants.CENTER));

        JTableHeader cab = tabela.getTableHeader();
        cab.setReorderingAllowed(false);
        cab.setPreferredSize(new Dimension(cab.getPreferredSize().width, 40));
        final TableCellRenderer padrao = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco,
                                                            int linha, int coluna) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, linha, coluna);
                l.setFont(fonte(Font.BOLD, 11.5f));
                l.setForeground(TEXTO_SUAVE);
                l.setBackground(FUNDO);
                l.setText(v == null ? "" : v.toString().toUpperCase(PT_BR));
                l.setHorizontalAlignment(alinhamentoColuna(t, coluna));
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                return l;
            }
        };
        cab.setDefaultRenderer(padrao);
    }

    static int alinhamentoColuna(JTable t, int coluna) {
        TableCellRenderer r = t.getCellRenderer(0, coluna);
        if (r instanceof CelulaZebrada) return ((CelulaZebrada) r).alinhamento;
        return SwingConstants.LEFT;
    }

    /** Define largura preferida e mínima de cada coluna, evitando títulos cortados. */
    public static void larguras(JTable tabela, int[] preferidas, int[] minimas) {
        for (int i = 0; i < preferidas.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(preferidas[i]);
            tabela.getColumnModel().getColumn(i).setMinWidth(minimas[i]);
        }
    }

    /** Alinha uma coluna (por exemplo, valores em reais) à direita ou ao centro. */
    public static void alinharColuna(JTable tabela, int coluna, int alinhamento) {
        tabela.getColumnModel().getColumn(coluna).setCellRenderer(new CelulaZebrada(alinhamento));
    }

    public static JScrollPane rolagem(Component conteudo) {
        JScrollPane sp = new JScrollPane(conteudo);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        sp.getVerticalScrollBar().setUI(new BarraFina());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUI(new BarraFina());
        return sp;
    }

    /** Célula de tabela com linhas alternadas e margem interna confortável. */
    static class CelulaZebrada extends DefaultTableCellRenderer {
        final int alinhamento;

        CelulaZebrada(int alinhamento) {
            this.alinhamento = alinhamento;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco,
                                                        int linha, int coluna) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, false, linha, coluna);
            l.setHorizontalAlignment(alinhamento);
            l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            l.setForeground(TEXTO);
            l.setBackground(sel ? SELECAO : (linha % 2 == 0 ? Color.WHITE : ZEBRA));
            return l;
        }
    }
}
