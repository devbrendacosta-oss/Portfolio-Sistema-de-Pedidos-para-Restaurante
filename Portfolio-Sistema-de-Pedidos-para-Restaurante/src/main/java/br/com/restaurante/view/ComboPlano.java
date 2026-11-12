package br.com.restaurante.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

/** Visual plano e limpo para JComboBox, combinando com o restante do tema. */
public class ComboPlano extends BasicComboBoxUI {

    @Override
    protected JButton createArrowButton() {
        JButton botao = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizar(g);
                g2.setColor(Tema.TEXTO_SUAVE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                g2.dispose();
            }
        };
        botao.setBorder(BorderFactory.createEmptyBorder());
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setPreferredSize(new Dimension(30, 0));
        return botao;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle limites, boolean temFoco) {
        g.setColor(Color.WHITE);
        g.fillRect(limites.x, limites.y, limites.width, limites.height);
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = (BasicComboPopup) super.createPopup();
        popup.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        return popup;
    }
}
