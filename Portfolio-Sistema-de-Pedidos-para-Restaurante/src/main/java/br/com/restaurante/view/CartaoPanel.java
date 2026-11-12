package br.com.restaurante.view;

import javax.swing.*;
import java.awt.*;

/** Painel branco com cantos arredondados e sombra suave, usado como "cartão" de conteúdo. */
public class CartaoPanel extends JPanel {
    private static final int SOMBRA = 5;
    private final Color fundo;

    public CartaoPanel(LayoutManager layout) {
        this(layout, 18, Color.WHITE);
    }

    public CartaoPanel(LayoutManager layout, int margem, Color fundo) {
        super(layout);
        this.fundo = fundo;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(SOMBRA + margem, SOMBRA + margem,
                SOMBRA + margem + 2, SOMBRA + margem));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        for (int i = SOMBRA; i >= 1; i--) {
            g2.setColor(new Color(60, 35, 20, 7));
            g2.fillRoundRect(SOMBRA - i, SOMBRA - i + 2, w - 2 * (SOMBRA - i) - 1,
                    h - 2 * (SOMBRA - i) - 3, 22 + i, 22 + i);
        }
        g2.setColor(fundo);
        g2.fillRoundRect(SOMBRA, SOMBRA, w - 2 * SOMBRA - 1, h - 2 * SOMBRA - 3, 18, 18);
        g2.setColor(Tema.BORDA);
        g2.drawRoundRect(SOMBRA, SOMBRA, w - 2 * SOMBRA - 1, h - 2 * SOMBRA - 3, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}
