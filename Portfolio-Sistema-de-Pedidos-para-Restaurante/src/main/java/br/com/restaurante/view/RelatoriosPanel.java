package br.com.restaurante.view;

import br.com.restaurante.controller.RelatorioController;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RelatoriosPanel extends JPanel implements Atualizavel {
    private final RelatorioController controller = new RelatorioController();
    private final JLabel quantidade = new JLabel("0");
    private final JLabel faturamento = new JLabel("R$ 0,00");
    private final JLabel ticket = new JLabel("R$ 0,00");
    private final RankingProdutos ranking = new RankingProdutos();

    public RelatoriosPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        JPanel indicadores = new JPanel(new GridLayout(1, 3, 10, 0));
        indicadores.setOpaque(false);
        indicadores.add(criarIndicador("PEDIDOS FINALIZADOS", quantidade, "contas fechadas até agora",
                Icones.Tipo.PEDIDOS, Tema.PRIMARIA));
        indicadores.add(criarIndicador("FATURAMENTO TOTAL", faturamento, "soma dos valores finais",
                Icones.Tipo.CONTA, Tema.SUCESSO));
        indicadores.add(criarIndicador("TICKET MÉDIO", ticket, "faturamento por pedido",
                Icones.Tipo.RELATORIOS, new Color(0xC98A1B)));

        CartaoPanel cartaoRanking = new CartaoPanel(new BorderLayout(0, 10));
        cartaoRanking.add(Tema.titulo("Produtos mais vendidos", 17f), BorderLayout.NORTH);
        cartaoRanking.add(ranking, BorderLayout.CENTER);

        add(indicadores, BorderLayout.NORTH);
        add(cartaoRanking, BorderLayout.CENTER);
        atualizar();
    }

    private JComponent criarIndicador(String titulo, JLabel valor, String legenda, Icones.Tipo icone, Color cor) {
        CartaoPanel cartao = new CartaoPanel(new BorderLayout(10, 0), 16, Color.WHITE);

        JComponent medalha = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                Icones.desenhar(g2, icone, (getWidth() - 24) / 2, (getHeight() - 24) / 2, 24, cor);
                g2.dispose();
            }
        };
        medalha.setPreferredSize(new Dimension(48, 48));
        JPanel lado = new JPanel(new BorderLayout());
        lado.setOpaque(false);
        lado.add(medalha, BorderLayout.NORTH);

        valor.setFont(Tema.fonte(Font.BOLD, 28f));
        valor.setForeground(Tema.TEXTO);
        JLabel sub = new JLabel(legenda);
        sub.setFont(Tema.fonte(Font.PLAIN, 12.5f));
        sub.setForeground(Tema.TEXTO_SUAVE);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Tema.rotulo(titulo));
        textos.add(Box.createVerticalStrut(4));
        textos.add(valor);
        textos.add(sub);

        cartao.add(lado, BorderLayout.WEST);
        cartao.add(textos, BorderLayout.CENTER);
        return cartao;
    }

    @Override
    public void atualizar() {
        int pedidos = controller.quantidadePedidosFinalizados();
        double total = controller.faturamentoTotal();
        quantidade.setText(String.valueOf(pedidos));
        faturamento.setText(Tema.moeda(total));
        ticket.setText(Tema.moeda(pedidos == 0 ? 0 : total / pedidos));
        ranking.definir(controller.produtosMaisVendidos(6));
    }

    /** Gráfico de barras horizontais desenhado com Java2D. */
    private static class RankingProdutos extends JComponent {
        private static final Color[] CORES = {
                new Color(0xD9532B), new Color(0xE5793A), new Color(0xF2A93B),
                new Color(0xE9BD5C), new Color(0xD9C080), new Color(0xCDBF9E)};
        private Map<String, Integer> dados;

        void definir(Map<String, Integer> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizar(g);
            if (dados == null || dados.isEmpty()) {
                g2.setFont(Tema.fonte(Font.PLAIN, 14f));
                g2.setColor(Tema.TEXTO_SUAVE);
                FontMetrics fm = g2.getFontMetrics();
                String a = "Ainda não há vendas finalizadas.";
                String b = "Feche uma conta para ver o ranking dos produtos aqui.";
                g2.drawString(a, (getWidth() - fm.stringWidth(a)) / 2, getHeight() / 2 - 4);
                g2.drawString(b, (getWidth() - fm.stringWidth(b)) / 2, getHeight() / 2 + 20);
                g2.dispose();
                return;
            }
            int maximo = 1;
            for (int v : dados.values()) maximo = Math.max(maximo, v);

            int larguraNome = 190;
            int larguraNumero = 70;
            int altura = 26;
            int passo = 42;
            int y = 8;
            int indice = 0;
            Font fonte = Tema.fonte(Font.PLAIN, 13.5f);
            for (Map.Entry<String, Integer> e : dados.entrySet()) {
                g2.setFont(fonte);
                FontMetrics fm = g2.getFontMetrics();
                int baseTexto = y + (altura + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Tema.TEXTO);
                g2.drawString(corta(e.getKey(), fm, larguraNome - 12), 0, baseTexto);

                int areaBarra = getWidth() - larguraNome - larguraNumero;
                g2.setColor(new Color(0xF4EEE7));
                g2.fillRoundRect(larguraNome, y, areaBarra, altura, altura, altura);
                int largura = Math.max(altura, Math.round(areaBarra * (e.getValue() / (float) maximo)));
                g2.setColor(CORES[Math.min(indice, CORES.length - 1)]);
                g2.fillRoundRect(larguraNome, y, largura, altura, altura, altura);

                g2.setFont(Tema.fonte(Font.BOLD, 13.5f));
                g2.setColor(Tema.TEXTO);
                String n = e.getValue() + (e.getValue() == 1 ? " un." : " un.");
                g2.drawString(n, getWidth() - larguraNumero + 14, baseTexto);
                y += passo;
                indice++;
            }
            g2.dispose();
        }

        private String corta(String texto, FontMetrics fm, int largura) {
            if (fm.stringWidth(texto) <= largura) return texto;
            String t = texto;
            while (t.length() > 1 && fm.stringWidth(t + "...") > largura) t = t.substring(0, t.length() - 1);
            return t + "...";
        }
    }
}
