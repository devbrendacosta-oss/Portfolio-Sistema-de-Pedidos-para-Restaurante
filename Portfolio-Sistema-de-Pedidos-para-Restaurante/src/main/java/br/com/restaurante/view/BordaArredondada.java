package br.com.restaurante.view;

import javax.swing.border.Border;
import java.awt.*;

/** Borda com cantos arredondados usada em campos de texto e listas suspensas. */
public class BordaArredondada implements Border {
    private final Color cor;
    private final int raio;
    private final int espessura;

    public BordaArredondada(Color cor, int raio, int espessura) {
        this.cor = cor;
        this.raio = raio;
        this.espessura = espessura;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
        Graphics2D g2 = Tema.suavizar(g);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(espessura));
        float meio = espessura / 2f;
        g2.drawRoundRect(Math.round(x + meio), Math.round(y + meio),
                largura - espessura - 1, altura - espessura - 1, raio, raio);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(espessura + 1, espessura + 1, espessura + 1, espessura + 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
