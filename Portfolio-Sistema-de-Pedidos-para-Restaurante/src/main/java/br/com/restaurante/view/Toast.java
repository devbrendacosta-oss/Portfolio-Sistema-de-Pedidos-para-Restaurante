package br.com.restaurante.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * Aviso flutuante que aparece no canto da janela e some sozinho.
 * Substitui as caixas de mensagem comuns e não interrompe o uso do sistema.
 */
public class Toast extends JComponent {
    private String texto = "";
    private boolean erro;
    private float opacidade;
    private long inicio;
    private long duracao;
    private final Timer relogio;

    private Toast() {
        setOpaque(false);
        relogio = new Timer(16, e -> animar());
    }

    /** Evita que o aviso bloqueie cliques nos componentes que estão por baixo. */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    public static void sucesso(Component origem, String texto) {
        mostrar(origem, texto, false);
    }

    public static void erro(Component origem, String texto) {
        mostrar(origem, texto, true);
    }

    private static void mostrar(Component origem, String texto, boolean erro) {
        JRootPane raiz = SwingUtilities.getRootPane(origem);
        if (raiz == null) return;
        Toast toast = (Toast) raiz.getClientProperty("toast");
        if (toast == null) {
            toast = new Toast();
            raiz.setGlassPane(toast);
            raiz.putClientProperty("toast", toast);
        }
        toast.exibir(texto, erro);
    }

    private void exibir(String texto, boolean erro) {
        this.texto = texto;
        this.erro = erro;
        this.duracao = erro ? 4200 : 2600;
        this.inicio = System.currentTimeMillis();
        this.opacidade = 0f;
        setVisible(true);
        relogio.restart();
    }

    private void animar() {
        long t = System.currentTimeMillis() - inicio;
        if (t < 200) {
            opacidade = t / 200f;
        } else if (t < 200 + duracao) {
            opacidade = 1f;
        } else if (t < 200 + duracao + 350) {
            opacidade = 1f - (t - 200 - duracao) / 350f;
        } else {
            relogio.stop();
            setVisible(false);
            return;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (texto.isEmpty() || opacidade <= 0f) return;
        Graphics2D g2 = Tema.suavizar(g);
        g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, opacidade))));
        Font fonte = Tema.fonte(Font.BOLD, 13.5f);
        g2.setFont(fonte);
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 78;
        int altura = 52;
        int x = getWidth() - largura - 28;
        int y = 28 + Math.round((1f - opacidade) * -12);

        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(x + 2, y + 4, largura, altura, 18, 18);
        g2.setColor(Tema.BARRA);
        g2.fillRoundRect(x, y, largura, altura, 18, 18);

        Color corIcone = erro ? Tema.PERIGO : Tema.SUCESSO;
        g2.setColor(corIcone);
        g2.fill(new Ellipse2D.Double(x + 14, y + 13, 26, 26));
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (erro) {
            g2.draw(new Line2D.Double(x + 27, y + 20, x + 27, y + 28));
            g2.fill(new Ellipse2D.Double(x + 25.8, y + 31, 2.4, 2.4));
        } else {
            g2.draw(new Line2D.Double(x + 21, y + 26.5, x + 25.5, y + 31));
            g2.draw(new Line2D.Double(x + 25.5, y + 31, x + 33.5, y + 22));
        }
        g2.setColor(Color.WHITE);
        g2.drawString(texto, x + 54, y + (altura + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
