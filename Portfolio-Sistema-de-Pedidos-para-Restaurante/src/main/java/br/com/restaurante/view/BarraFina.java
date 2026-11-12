package br.com.restaurante.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/** Barra de rolagem fina, sem setas, com o polegar arredondado. */
public class BarraFina extends BasicScrollBarUI {

    @Override
    protected JButton createDecreaseButton(int orientacao) {
        return botaoVazio();
    }

    @Override
    protected JButton createIncreaseButton(int orientacao) {
        return botaoVazio();
    }

    private JButton botaoVazio() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(10, 10);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle area) {
        // Sem trilho visível: deixa a barra mais discreta.
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle area) {
        if (area.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = Tema.suavizar(g);
        g2.setColor(isThumbRollover() ? Tema.TEXTO_SUAVE : Tema.BORDA);
        g2.fillRoundRect(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 8, 8);
        g2.dispose();
    }
}
