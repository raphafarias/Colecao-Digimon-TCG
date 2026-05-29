package com.digimontcg;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

public class GerenciadorCartasFrame extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> ordenadorFiltro;
    private JTextField campoBusca;
    private JCheckBox chkSomenteMinhaColecao;
    private JLabel labelImagem;
    private transient CartaDAO cartaDAO;
    private transient List<Carta> listaCartas;

    public GerenciadorCartasFrame() {
        cartaDAO = new CartaDAO();
        setTitle("Gerenciador de Coleção Pessoal - Digimon TCG");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL SUPERIOR (BUSCA E FILTROS) ---
        JPanel pnlSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        campoBusca = new JTextField(20);
        chkSomenteMinhaColecao = new JCheckBox("Ver somente minha coleção");
        JButton btnSincronizar = new JButton("Sincronizar API Completa");
        JButton btnVoltar = new JButton("Voltar ao Menu");

        pnlSuperior.add(new JLabel("Filtrar por Nome:"));
        pnlSuperior.add(campoBusca);
        pnlSuperior.add(chkSomenteMinhaColecao);
        pnlSuperior.add(btnSincronizar);
        pnlSuperior.add(btnVoltar);
        add(pnlSuperior, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (TABELA) ---
        modeloTabela = new DefaultTableModel(new String[]{"Código", "Nome", "Cor", "Nível", "Qtd Coleção"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        tabela = new JTable(modeloTabela);
        ordenadorFiltro = new TableRowSorter<>(modeloTabela);
        tabela.setRowSorter(ordenadorFiltro);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // --- PAINEL DIREITA (PREVISUALIZAÇÃO DA IMAGEM) ---
        JPanel pnlDireita = new JPanel(new BorderLayout());
        pnlDireita.setBorder(BorderFactory.createTitledBorder("Visualização da Carta"));
        pnlDireita.setPreferredSize(new Dimension(350, 0));
        labelImagem = new JLabel("Selecione uma carta", SwingConstants.CENTER);
        pnlDireita.add(labelImagem, BorderLayout.CENTER);
        add(pnlDireita, BorderLayout.EAST);

        // --- PAINEL INFERIOR (BOTÕES DE QUANTIDADE) ---
        JPanel pnlInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdicionar = new JButton("+ Adicionar à Coleção");
        JButton btnRemover = new JButton("- Remover da Coleção");
        
        btnAdicionar.setBackground(new Color(46, 139, 87));
        btnAdicionar.setForeground(Color.WHITE);
        btnRemover.setBackground(new Color(178, 34, 34));
        btnRemover.setForeground(Color.WHITE);

        pnlInferior.add(btnAdicionar);
        pnlInferior.add(btnRemover);
        add(pnlInferior, BorderLayout.SOUTH);

        // --- EVENTOS E LÓGICA DE COMPORTAMENTO ---
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                int linhaSelecionada = tabela.convertRowIndexToModel(tabela.getSelectedRow());
                String codigo = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
                Carta carta = buscarNaListaPorCodigo(codigo);
                if (carta != null) {
                    // Executa o Smart Getter ultra robusto atualizado
                    carregarImagem(carta.getImagemUrl());
                }
            }
        });

        ActionListener filterAction = e -> aplicarFiltros();
        chkSomenteMinhaColecao.addActionListener(filterAction);
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        btnAdicionar.addActionListener(e -> alterarQuantidadeSelecionada(1));
        btnRemover.addActionListener(e -> alterarQuantidadeSelecionada(-1));

        btnSincronizar.addActionListener(e -> {
            btnSincronizar.setEnabled(false);
            btnSincronizar.setText("Sincronizando... Aguarde");
            new Thread(() -> {
                Carta[] todos = DigimonService.sincronizarCatalogoCompleto();
                if (todos != null) {
                    for (Carta c : todos) {
                        cartaDAO.salvar(c);
                    }
                    SwingUtilities.invokeLater(() -> {
                        atualizarTabela();
                        JOptionPane.showMessageDialog(this, "Catálogo sincronizado com todas as cartas da API!");
                    });
                }
                SwingUtilities.invokeLater(() -> {
                    btnSincronizar.setText("Sincronizar API Completa");
                    btnSincronizar.setEnabled(true);
                });
            }).start();
        });

        btnVoltar.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            this.dispose();
        });

        atualizarTabela();
    }

    private void aplicarFiltros() {
        String texto = campoBusca.getText();
        RowFilter<DefaultTableModel, Object> rfNome = RowFilter.regexFilter("(?i)" + texto, 1);
        
        if (chkSomenteMinhaColecao.isSelected()) {
            RowFilter<DefaultTableModel, Object> rfColecao = RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, 0, 4);
            java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
            filters.add(rfNome);
            filters.add(rfColecao);
            ordenadorFiltro.setRowFilter(RowFilter.andFilter(filters));
        } else {
            ordenadorFiltro.setRowFilter(rfNome);
        }
    }

    private void alterarQuantidadeSelecionada(int modificador) {
        if (tabela.getSelectedRow() != -1) {
            int linhaModel = tabela.convertRowIndexToModel(tabela.getSelectedRow());
            String codigo = (String) modeloTabela.getValueAt(linhaModel, 0);
            int qtdAtual = (int) modeloTabela.getValueAt(linhaModel, 4);
            int novaQtd = Math.max(0, qtdAtual + modificador);

            cartaDAO.atualizarQuantidade(codigo, novaQtd);
            modeloTabela.setValueAt(novaQtd, linhaModel, 4);
            
            Carta c = buscarNaListaPorCodigo(codigo);
            if (c != null) c.setQuantidade(novaQtd);
        }
    }

    private Carta buscarNaListaPorCodigo(String codigo) {
        if (listaCartas != null) {
            for (Carta c : listaCartas) {
                if (c.getCodigo().equals(codigo)) return c;
            }
        }
        return null;
    }

    private void carregarImagem(String urlString) {
        new Thread(() -> {
            try {
                if (urlString == null || urlString.isEmpty()) {
                    throw new java.io.IOException("URL Inválida");
                }
                URI uri = new URI(urlString);
                ImageIcon icon = new ImageIcon(uri.toURL());
                Image img = icon.getImage().getScaledInstance(300, 420, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> labelImagem.setIcon(new ImageIcon(img)));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> labelImagem.setIcon(null));
            }
        }).start();
    }

    public void atualizarTabela() {
        modeloTabela.setRowCount(0);
        listaCartas = cartaDAO.listarTodos();
        for (Carta c : listaCartas) {
            modeloTabela.addRow(new Object[]{c.getCodigo(), c.getName(), c.getColor(), c.getLevel(), c.getQuantidade()});
        }
    }
}