package br.com.restaurante.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botão arredondado com efeito ao passar o mouse e ao clicar. */
public class BotaoModerno extends JButton {

    public enum Estilo { PRIMARIO, SECUNDARIO, PERIGO, FANTASMA }

    private final Estilo estilo;
    private boolean sobre;
    private boolean pressionado;

    public BotaoModerno(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Tema.fonte(Font.BOLD, 13f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setForeground(corTexto());
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { sobre = false; pressionado = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressionado = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
        });
    }

    public BotaoModerno(String texto) {
        this(texto, Estilo.PRIMARIO);
    }

    @Override
    public void updateUI() {
        // Usa o desenho básico de botão para que apenas o nosso fundo apareça.
        setUI(new BasicButtonUI());
    }

    private Color corTexto() {
        switch (estilo) {
            case PRIMARIO:
                return Color.WHITE;
            case PERIGO:
                return Tema.PERIGO;
            case FANTASMA:
                return Tema.PRIMARIA;
            default:
                return Tema.TEXTO;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        boolean ativo = isEnabled();
        if (!ativo) g2.setComposite(AlphaComposite.SrcOver.derive(0.45f));

        switch (estilo) {
            case PRIMARIO:
                g2.setColor(pressionado ? Tema.PRIMARIA_ESCURA : (sobre ? mais(Tema.PRIMARIA, 0.08f) : Tema.PRIMARIA));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                break;
            case PERIGO:
                g2.setColor(pressionado ? new Color(0xF6D2CE) : (sobre ? new Color(0xFAE0DD) : new Color(0xFCEDEB)));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                break;
            case SECUNDARIO:
                g2.setColor(pressionado ? Tema.SELECAO : (sobre ? Tema.FUNDO : Color.WHITE));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g2.setColor(sobre ? Tema.PRIMARIA : Tema.BORDA);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                break;
            default:
                if (sobre || pressionado) {
                    g2.setColor(Tema.SELECAO);
                    g2.fillRoundRect(0, 0, w, h, 12, 12);
                }
                break;
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private static Color mais(Color c, float fator) {
        int r = Math.min(255, Math.round(c.getRed() + 255 * fator));
        int gg = Math.min(255, Math.round(c.getGreen() + 255 * fator));
        int b = Math.min(255, Math.round(c.getBlue() + 255 * fator));
        return new Color(r, gg, b);
    }
}
