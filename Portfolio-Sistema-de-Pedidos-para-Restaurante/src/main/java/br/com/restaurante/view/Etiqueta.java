package br.com.restaurante.view;

import javax.swing.*;
import java.awt.*;

/** Etiqueta arredondada (como "Aberto" ou "Finalizado") com cores configuráveis. */
public class Etiqueta extends JComponent {
    private String texto = "";
    private Color fundo = Tema.BORDA;
    private Color cor = Tema.TEXTO;

    public Etiqueta() {
        setFont(Tema.fonte(Font.BOLD, 11.5f));
    }

    public void definir(String texto, Color fundo, Color cor) {
        this.texto = texto;
        this.fundo = fundo;
        this.cor = cor;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(texto) + 24, 24);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (texto.isEmpty()) return;
        Graphics2D g2 = Tema.suavizar(g);
        g2.setColor(fundo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.setColor(cor);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
