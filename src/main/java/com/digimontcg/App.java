package com.digimontcg;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Inicializa a interface Swing de forma segura na Thread de Eventos do Java
        SwingUtilities.invokeLater(() -> {
            // Instancia e torna visível a nossa Nova Tela Inicial (Dashboard)
            new DashboardFrame().setVisible(true);
        });
    }
}