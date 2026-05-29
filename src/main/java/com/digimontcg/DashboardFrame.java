package com.digimontcg;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class DashboardFrame extends JFrame {
    private JLabel lblTotalCatalogo;
    private JLabel lblTotalColecao;
    private JLabel lblVolumeFisico;
    private transient CartaDAO dao;

    public DashboardFrame() {
        dao = new CartaDAO();
        
        setTitle("Digimon TCG - Gerenciador de Coleção Pessoal");
        setSize(550, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 245, 245)); // Fundo cinza claro limpo

        // --- 1. PAINEL SUPERIOR: LOGO DO JOGO ---
        JPanel pnlLogo = new JPanel();
        pnlLogo.setBackground(new Color(245, 245, 245));
        try {
            // Forma moderna de carregar a imagem da web no Java (Evita erros de depreciação)
            URI uri = new URI("https://images.digimoncard.io/images/assets/digimon_card_game_logo.png");
            ImageIcon icon = new ImageIcon(uri.toURL());
            Image img = icon.getImage().getScaledInstance(320, 110, Image.SCALE_SMOOTH);
            pnlLogo.add(new JLabel(new ImageIcon(img)));
        } catch (Exception e) {
            // Fallback caso a internet falhe no primeiro carregamento
            JLabel lblFallback = new JLabel("DIGIMON CARD GAME", SwingConstants.CENTER);
            lblFallback.setFont(new Font("Arial", Font.BOLD, 28));
            lblFallback.setForeground(new Color(0, 80, 136));
            pnlLogo.add(lblFallback);
        }
        add(pnlLogo, BorderLayout.NORTH);

        // --- 2. PAINEL CENTRAL: CARD DE ESTATÍSTICAS ---
        JPanel pnlStatsOuter = new JPanel(new BorderLayout());
        pnlStatsOuter.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        pnlStatsOuter.setBackground(new Color(245, 245, 245));

        JPanel pnlStats = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlStats.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)), 
                " Resumo do Seu Inventário ", 
                0, 0, 
                new Font("Arial", Font.BOLD, 14), 
                new Color(0, 80, 136))
        );
        pnlStats.setBackground(new Color(40, 44, 52)); // Fundo escuro estilo Dashboard moderno

        lblTotalCatalogo = criarLabelStatus("Carregando catálogo...");
        lblTotalColecao = criarLabelStatus("Calculando coleção...");
        lblVolumeFisico = criarLabelStatus("Somando cópias...");

        pnlStats.add(lblTotalCatalogo);
        pnlStats.add(lblTotalColecao);
        pnlStats.add(lblVolumeFisico);
        pnlStatsOuter.add(pnlStats, BorderLayout.CENTER);
        add(pnlStatsOuter, BorderLayout.CENTER);

        // --- 3. PAINEL INFERIOR: NAVEGAÇÃO ---
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        pnlBotoes.setBackground(new Color(245, 245, 245));

        JButton btnAbrirGerenciador = new JButton("Abrir Coleção / Registro");
        JButton btnAtualizarStats = new JButton("Recarregar Painel");

        // Estilização básica dos botões
        btnAbrirGerenciador.setFont(new Font("Arial", Font.BOLD, 14));
        btnAbrirGerenciador.setBackground(new Color(0, 80, 136));
        btnAbrirGerenciador.setForeground(Color.WHITE);
        btnAbrirGerenciador.setPreferredSize(new Dimension(210, 40));

        btnAtualizarStats.setFont(new Font("Arial", Font.BOLD, 13));
        btnAtualizarStats.setPreferredSize(new Dimension(160, 40));

        pnlBotoes.add(btnAbrirGerenciador);
        pnlBotoes.add(btnAtualizarStats);
        add(pnlBotoes, BorderLayout.SOUTH);

        // --- 4. TRATAMENTO DE CLIQUES ---
        btnAbrirGerenciador.addActionListener(e -> {
            // Instancia a tela de registros que você já tem pronta
            GerenciadorCartasFrame telaGerenciador = new GerenciadorCartasFrame();
            telaGerenciador.setVisible(true);
            this.dispose(); // Fecha o Dashboard para limpar memória do sistema
        });

        btnAtualizarStats.addActionListener(e -> carregarEstatisticas());

        // Faz a leitura inicial do banco de dados ao abrir a janela
        carregarEstatisticas();
    }

    private JLabel criarLabelStatus(String textoInicial) {
        JLabel label = new JLabel(textoInicial, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(new Color(230, 230, 230)); // Texto claro para contrastar com o fundo escuro
        return label;
    }

    private void carregarEstatisticas() {
        // Roda a query agregada que fizemos no CartaDAO
        int[] stats = dao.getEstatisticas();
        
        lblTotalCatalogo.setText("<html>Cartas Disponíveis no Catálogo: <font color='#FFA500'>" + stats[0] + "</font></html>");
        lblTotalColecao.setText("<html>Cards Únicos em Posse: <font color='#00FF7F'>" + stats[1] + "</font></html>");
        lblVolumeFisico.setText("<html>Total de Cópias na Estante: <font color='#00BFFF'>" + stats[2] + "</font></html>");
    }
}