package br.com.restaurante.view;

import java.awt.*;
import java.awt.geom.*;

/** Ícones simples desenhados com Java2D (sem arquivos de imagem), em uma grade de 24x24. */
public final class Icones {

    public enum Tipo { CARDAPIO, PEDIDOS, CONTA, RELATORIOS, CLOCHE }

    private Icones() {
    }

    public static void desenhar(Graphics2D g, Tipo tipo, int x, int y, int tamanho, Color cor) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        double escala = tamanho / 24.0;
        g2.scale(escala, escala);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (tipo) {
            case CARDAPIO:
                // garfo
                g2.draw(new Line2D.Double(5, 3, 5, 9));
                g2.draw(new Line2D.Double(8, 3, 8, 9));
                g2.draw(new Line2D.Double(11, 3, 11, 9));
                g2.draw(new Arc2D.Double(5, 5, 6, 8, 180, 180, Arc2D.OPEN));
                g2.draw(new Line2D.Double(8, 13, 8, 21));
                // faca
                Path2D faca = new Path2D.Double();
                faca.moveTo(17, 21);
                faca.lineTo(17, 3);
                faca.curveTo(20.5, 5, 20.5, 11, 17, 13);
                g2.draw(faca);
                break;
            case PEDIDOS:
                g2.draw(new RoundRectangle2D.Double(4.5, 4.5, 15, 17, 3, 3));
                g2.draw(new RoundRectangle2D.Double(8.5, 2, 7, 5, 2, 2));
                g2.draw(new Line2D.Double(8, 12, 16, 12));
                g2.draw(new Line2D.Double(8, 16, 14, 16));
                break;
            case CONTA:
                Path2D recibo = new Path2D.Double();
                recibo.moveTo(5, 2.5);
                recibo.lineTo(19, 2.5);
                recibo.lineTo(19, 21.5);
                recibo.lineTo(16.5, 19.5);
                recibo.lineTo(14, 21.5);
                recibo.lineTo(11.5, 19.5);
                recibo.lineTo(9, 21.5);
                recibo.lineTo(7.5, 20);
                recibo.lineTo(5, 21.5);
                recibo.closePath();
                g2.draw(recibo);
                g2.draw(new Line2D.Double(8.5, 8, 15.5, 8));
                g2.draw(new Line2D.Double(8.5, 12, 15.5, 12));
                g2.draw(new Line2D.Double(8.5, 16, 12, 16));
                break;
            case RELATORIOS:
                g2.draw(new Line2D.Double(3.5, 20.5, 20.5, 20.5));
                g2.draw(new RoundRectangle2D.Double(5, 12, 3.5, 8, 1.5, 1.5));
                g2.draw(new RoundRectangle2D.Double(10.3, 6, 3.5, 14, 1.5, 1.5));
                g2.draw(new RoundRectangle2D.Double(15.6, 9, 3.5, 11, 1.5, 1.5));
                break;
            case CLOCHE:
                g2.draw(new Arc2D.Double(3.5, 6.5, 17, 17, 0, 180, Arc2D.OPEN));
                g2.draw(new Line2D.Double(2, 15, 22, 15));
                g2.draw(new Line2D.Double(12, 6.5, 12, 4.5));
                g2.fill(new Ellipse2D.Double(10.6, 2.6, 2.8, 2.8));
                break;
            default:
                break;
        }
        g2.dispose();
    }
}
