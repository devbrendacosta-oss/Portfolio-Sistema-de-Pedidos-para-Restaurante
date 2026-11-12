package br.com.restaurante.view;

import br.com.restaurante.controller.PedidoController;
import br.com.restaurante.model.Pedido;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JanelaPrincipal extends JFrame {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String[] NOMES = {"Produtos e cardápio", "Pedidos", "Fechamento de contas", "Relatórios"};
    private static final String[] DESCRICOES = {
            "Cadastre, pesquise e edite os itens do cardápio",
            "Abra pedidos por mesa e lance o que foi consumido",
            "Aplique o desconto e finalize a conta da mesa",
            "Acompanhe o desempenho das vendas"};
    private static final Icones.Tipo[] ICONES = {
            Icones.Tipo.CARDAPIO, Icones.Tipo.PEDIDOS, Icones.Tipo.CONTA, Icones.Tipo.RELATORIOS};

    private final PedidoController pedidos = new PedidoController();
    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final ItemMenu[] menu = new ItemMenu[NOMES.length];
    private final JComponent[] telas = new JComponent[NOMES.length];
    private final JLabel titulo = Tema.titulo("", 26f);
    private final JLabel descricao = new JLabel();
    private final JLabel data = new JLabel();
    private final Selo selo = new Selo();

    public JanelaPrincipal() {
        setTitle("Sistema de Gerenciamento de Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 760);
        setMinimumSize(new Dimension(1060, 660));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO);
        setLayout(new BorderLayout());

        telas[0] = new ProdutosPanel();
        telas[1] = new PedidosPanel();
        telas[2] = new FechamentoPanel();
        telas[3] = new RelatoriosPanel();

        add(criarBarraLateral(), BorderLayout.WEST);
        add(criarAreaPrincipal(), BorderLayout.CENTER);

        for (int i = 0; i < telas.length; i++) {
            telas[i].setOpaque(false);
            conteudo.add(telas[i], "tela" + i);
        }
        configurarAtalhos();
        selecionar(0);

        Timer relogio = new Timer(1000, e -> atualizarRelogio());
        relogio.start();
        atualizarRelogio();
    }

    // ---------- barra lateral ----------

    private JComponent criarBarraLateral() {
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setColor(Tema.BARRA);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Pratos decorativos, em transparência, no rodapé da barra.
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(255, 255, 255, 12));
                g2.drawOval(-70, getHeight() - 170, 240, 240);
                g2.setColor(new Color(255, 255, 255, 9));
                g2.drawOval(-35, getHeight() - 135, 170, 170);
                g2.setColor(new Color(217, 83, 43, 34));
                g2.fillOval(110, getHeight() - 90, 150, 150);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setPreferredSize(new Dimension(250, 0));
        barra.setBorder(BorderFactory.createEmptyBorder(26, 16, 20, 16));

        barra.add(criarMarca());
        barra.add(Box.createVerticalStrut(34));

        JLabel secao = new JLabel("  MENU");
        secao.setFont(Tema.fonte(Font.BOLD, 11f));
        secao.setForeground(new Color(255, 255, 255, 110));
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.add(secao);
        barra.add(Box.createVerticalStrut(8));

        for (int i = 0; i < NOMES.length; i++) {
            final int indice = i;
            menu[i] = new ItemMenu(NOMES[i], ICONES[i]);
            menu[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            menu[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selecionar(indice); }
            });
            barra.add(menu[i]);
            barra.add(Box.createVerticalStrut(4));
        }

        barra.add(Box.createVerticalGlue());
        JLabel nota = new JLabel("<html><div style='width:165px'>Os dados ficam armazenados somente "
                + "enquanto o programa estiver aberto.</div></html>");
        nota.setFont(Tema.fonte(Font.PLAIN, 11.5f));
        nota.setForeground(new Color(255, 255, 255, 120));
        nota.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        nota.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.add(nota);
        return barra;
    }

    private JComponent criarMarca() {
        JPanel marca = new JPanel(new BorderLayout(12, 0));
        marca.setOpaque(false);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JComponent logo = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setPaint(new GradientPaint(0, 0, Tema.DESTAQUE, getWidth(), getHeight(), Tema.PRIMARIA));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                Icones.desenhar(g2, Icones.Tipo.CLOCHE, 9, 9, 26, Color.WHITE);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(44, 44));
        marca.add(logo, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel("Restaurante");
        nome.setFont(Tema.fonte(Font.BOLD, 19f));
        nome.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Painel de gerenciamento");
        sub.setFont(Tema.fonte(Font.PLAIN, 11.5f));
        sub.setForeground(new Color(255, 255, 255, 130));
        textos.add(Box.createVerticalGlue());
        textos.add(nome);
        textos.add(sub);
        textos.add(Box.createVerticalGlue());
        marca.add(textos, BorderLayout.CENTER);
        return marca;
    }

    // ---------- área principal ----------

    private JComponent criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(Tema.FUNDO);

        descricao.setFont(Tema.fonte(Font.PLAIN, 13.5f));
        descricao.setForeground(Tema.TEXTO_SUAVE);
        data.setFont(Tema.fonte(Font.PLAIN, 12.5f));
        data.setForeground(Tema.TEXTO_SUAVE);
        data.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(descricao);

        JPanel direita = new JPanel();
        direita.setOpaque(false);
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));
        selo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        data.setAlignmentX(Component.RIGHT_ALIGNMENT);
        direita.add(selo);
        direita.add(Box.createVerticalStrut(6));
        direita.add(data);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(24, 30, 4, 30));
        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(direita, BorderLayout.EAST);

        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(6, 24, 20, 24));
        area.add(cabecalho, BorderLayout.NORTH);
        area.add(conteudo, BorderLayout.CENTER);
        return area;
    }

    private void selecionar(int indice) {
        for (int i = 0; i < menu.length; i++) menu[i].setSelecionado(i == indice);
        titulo.setText(NOMES[indice]);
        descricao.setText(DESCRICOES[indice]);
        if (telas[indice] instanceof Atualizavel) ((Atualizavel) telas[indice]).atualizar();
        cartas.show(conteudo, "tela" + indice);
        atualizarRelogio();
    }

    private void configurarAtalhos() {
        JRootPane raiz = getRootPane();
        for (int i = 0; i < NOMES.length; i++) {
            final int indice = i;
            String chave = "irPara" + i;
            raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ctrl " + (i + 1)), chave);
            raiz.getActionMap().put(chave, new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { selecionar(indice); }
            });
        }
    }

    private void atualizarRelogio() {
        String dia = new SimpleDateFormat("EEEE, d 'de' MMMM", PT_BR).format(new Date());
        String hora = new SimpleDateFormat("HH:mm", PT_BR).format(new Date());
        data.setText(Character.toUpperCase(dia.charAt(0)) + dia.substring(1) + "   |   " + hora);
        int abertos = 0;
        for (Pedido p : pedidos.listarPedidos()) {
            if (!p.isFinalizado()) abertos++;
        }
        selo.definir(abertos);
    }

    // ---------- componentes internos ----------

    /** Item do menu lateral com ícone, texto e indicador de seleção. */
    private static class ItemMenu extends JComponent {
        private final String texto;
        private final Icones.Tipo icone;
        private boolean selecionado;
        private boolean sobre;

        ItemMenu(String texto, Icones.Tipo icone) {
            this.texto = texto;
            this.icone = icone;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(218, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
            });
        }

        void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizar(g);
            int w = getWidth();
            int h = getHeight();
            if (selecionado || sobre) {
                g2.setColor(selecionado ? Tema.BARRA_HOVER : new Color(255, 255, 255, 10));
                g2.fillRoundRect(0, 0, w, h, 14, 14);
            }
            if (selecionado) {
                g2.setColor(Tema.PRIMARIA);
                g2.fillRoundRect(0, 11, 4, h - 22, 4, 4);
            }
            Color cor = selecionado ? Color.WHITE : new Color(255, 255, 255, 170);
            Icones.desenhar(g2, icone, 18, (h - 22) / 2, 22, selecionado ? Tema.DESTAQUE : cor);
            g2.setFont(Tema.fonte(selecionado ? Font.BOLD : Font.PLAIN, 14f));
            g2.setColor(cor);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, 52, (h + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    /** Selo arredondado que mostra quantas mesas estão com pedido aberto. */
    private static class Selo extends JComponent {
        private int abertos;

        Selo() {
            setPreferredSize(new Dimension(190, 30));
            setMaximumSize(new Dimension(190, 30));
        }

        void definir(int abertos) {
            if (this.abertos != abertos) {
                this.abertos = abertos;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizar(g);
            boolean ativo = abertos > 0;
            g2.setColor(ativo ? new Color(0xE3F2E8) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.setColor(ativo ? new Color(0xBFDECB) : Tema.BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.setColor(ativo ? Tema.SUCESSO : Tema.TEXTO_SUAVE);
            g2.fillOval(13, getHeight() / 2 - 4, 8, 8);
            String t = abertos == 1 ? "1 mesa aberta" : abertos + " mesas abertas";
            if (abertos == 0) t = "Nenhuma mesa aberta";
            g2.setFont(Tema.fonte(Font.BOLD, 12.5f));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(t, 29, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
