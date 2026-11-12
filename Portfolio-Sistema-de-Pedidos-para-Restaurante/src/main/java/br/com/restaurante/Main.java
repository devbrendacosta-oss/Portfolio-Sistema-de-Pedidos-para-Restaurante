package br.com.restaurante;

import br.com.restaurante.view.JanelaPrincipal;
import br.com.restaurante.view.Tema;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Instala o visual (cores, fontes e componentes) antes de criar qualquer janela.
                Tema.instalar();
                new JanelaPrincipal().setVisible(true);
            }
        });
    }
}
