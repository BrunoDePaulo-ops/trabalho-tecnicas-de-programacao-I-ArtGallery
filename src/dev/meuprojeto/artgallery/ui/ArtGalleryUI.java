package dev.meuprojeto.artgallery.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

import dev.meuprojeto.artgallery.model.*;
import dev.meuprojeto.artgallery.repository.*;
import dev.meuprojeto.artgallery.service.*;
import dev.meuprojeto.artgallery.exception.*;

public class ArtGalleryUI extends JFrame {
    private IArtGallery artGallery;
    private JTabbedPane abas;
    private DefaultTableModel tableModel;
    private Vector<Exposicao> exposicoes = new Vector<>();

    public ArtGalleryUI() {
        IRepositoryObra repository = new RepositoryObraVector();
        artGallery = new ArtGallery(repository);

        setTitle("Art Gallery - Sistema de Curadoria de Obras");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        abas = new JTabbedPane();
        abas.addTab("Cadastrar Obra", criarPainelCadastro());
        abas.addTab("Listar Obras", criarPainelListagem());
        abas.addTab("Avaliar Obra", criarPainelAvaliacao());
        abas.addTab("Buscar por Autor", criarPainelBusca());
        abas.addTab("Top Obras", criarPainelTopObras());
        abas.addTab("Exposições", criarPainelExposicoes());

        add(abas);
    }

    private void addCampo(JPanel panel, GridBagConstraints gbc, String label, JComponent campo, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void limparCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo != null) {
                campo.setText("");
            }
        }
    }

    private void atualizarLista() {
        tableModel.setRowCount(0);
        for (Obra obra : artGallery.listarObras()) {
            String tipo = "";
            String detalhes = "";
            if (obra instanceof PinturaDigital) {
                tipo = "Pintura Digital";
                PinturaDigital p = (PinturaDigital) obra;
                detalhes = p.getResolucao() + " | " + p.getSoftwareUtilizado();
            } else if (obra instanceof Modelagem3D) {
                tipo = "Modelagem 3D";
                Modelagem3D m = (Modelagem3D) obra;
                detalhes = m.getNumeroPoligonos() + " poligonos | " + m.getEngine();
            } else if (obra instanceof ArteGenerativa) {
                tipo = "Arte Generativa";
                ArteGenerativa a = (ArteGenerativa) obra;
                detalhes = a.getAlgoritmo() + " | Seed: " + a.getSeed();
            }
            tableModel.addRow(new Object[]{
                obra.getTitulo(), obra.getAutor(), tipo, detalhes, obra.mediaAvaliacoes()
            });
        }
    }

    private JPanel criarPainelCadastro() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = 1;

    // Campos comuns
    JTextField txtTitulo = new JTextField(20);
    JTextField txtAutor = new JTextField(20);
    JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Pintura Digital", "Modelagem 3D", "Arte Generativa"});

    // Dois campos reutilizáveis (usados para todos os tipos)
    JTextField txtCampo1 = new JTextField(15);
    JTextField txtCampo2 = new JTextField(15);
    JLabel lblCampo1 = new JLabel("Resolução:");
    JLabel lblCampo2 = new JLabel("Software:");

    int row = 0;
    
    // Título
    gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Título:"), gbc);
    gbc.gridx = 1; panel.add(txtTitulo, gbc);
    row++;
    
    // Autor
    gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Autor:"), gbc);
    gbc.gridx = 1; panel.add(txtAutor, gbc);
    row++;
    
    // Tipo de Obra
    gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Tipo de Obra:"), gbc);
    gbc.gridx = 1; panel.add(cbTipo, gbc);
    row++;
    
    // Campo 1 (label + textfield)
    gbc.gridx = 0; gbc.gridy = row; panel.add(lblCampo1, gbc);
    gbc.gridx = 1; panel.add(txtCampo1, gbc);
    row++;
    
    // Campo 2 (label + textfield)
    gbc.gridx = 0; gbc.gridy = row; panel.add(lblCampo2, gbc);
    gbc.gridx = 1; panel.add(txtCampo2, gbc);
    row++;

    // Botão Cadastrar
    JButton btnCadastrar = new JButton("Cadastrar Obra");
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    panel.add(btnCadastrar, gbc);
    row++;

    // Label de status
    JLabel lblStatus = new JLabel(" ");
    gbc.gridy = row;
    panel.add(lblStatus, gbc);

    // Listener do comboBox - só muda os textos dos labels
    cbTipo.addActionListener(e -> {
        String tipo = (String) cbTipo.getSelectedItem();
        
        if ("Pintura Digital".equals(tipo)) {
            lblCampo1.setText("Resolução:");
            lblCampo2.setText("Software:");
        } else if ("Modelagem 3D".equals(tipo)) {
            lblCampo1.setText("Polígonos:");
            lblCampo2.setText("Engine:");
        } else { // Arte Generativa
            lblCampo1.setText("Algoritmo:");
            lblCampo2.setText("Seed:");
        }
        
        // Limpa os campos ao trocar de tipo
        txtCampo1.setText("");
        txtCampo2.setText("");
    });

    // Ação do botão cadastrar
    btnCadastrar.addActionListener(e -> {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String tipo = (String) cbTipo.getSelectedItem();

        try {
            Obra obra = null;
            if ("Pintura Digital".equals(tipo)) {
                obra = new PinturaDigital(titulo, autor, txtCampo1.getText(), txtCampo2.getText());
            } else if ("Modelagem 3D".equals(tipo)) {
                int poligonos = Integer.parseInt(txtCampo1.getText());
                obra = new Modelagem3D(titulo, autor, poligonos, txtCampo2.getText());
            } else {
                long seed = Long.parseLong(txtCampo2.getText());
                obra = new ArteGenerativa(titulo, autor, txtCampo1.getText(), seed);
            }
            artGallery.publicObra(obra);
            lblStatus.setText("Obra cadastrada com sucesso!");
            
            // Limpar campos
            txtTitulo.setText("");
            txtAutor.setText("");
            txtCampo1.setText("");
            txtCampo2.setText("");
            
            // Atualizar listagem
            atualizarLista();
            
        } catch (ObraJaCadastradaException ex) {
            lblStatus.setText("Erro: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            lblStatus.setText("Erro: Valores numéricos inválidos");
        }
    });

    // Inicializar com o tipo correto (Pintura Digital)
    cbTipo.getActionListeners()[0].actionPerformed(null);
    
    return panel;
    }

    private JPanel criarPainelListagem() {
        JPanel panel = new JPanel(new BorderLayout());
    
    tableModel = new DefaultTableModel(new String[]{"Título", "Autor", "Tipo", "Detalhes", "Média"}, 0);
    JTable tabela = new JTable(tableModel);
    
    JPanel botoes = new JPanel(new FlowLayout());
    JButton btnAtualizar = new JButton("Atualizar Lista");
    JButton btnRemover = new JButton("Remover Obra Selecionada");
    
    botoes.add(btnAtualizar);
    botoes.add(btnRemover);
    
    btnAtualizar.addActionListener(e -> atualizarLista());
    
    btnRemover.addActionListener(e -> {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada >= 0) {
            String titulo = (String) tableModel.getValueAt(linhaSelecionada, 0);
            int confirm = JOptionPane.showConfirmDialog(
                panel,
                "Deseja remover a obra '" + titulo + "'?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    artGallery.removerObra(titulo);
                    JOptionPane.showMessageDialog(panel, "Obra removida com sucesso!");
                    atualizarLista();
                } catch (ObraNaoEncontradaException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Selecione uma obra para remover!");
        }
    });
    
    panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
    panel.add(botoes, BorderLayout.SOUTH);
    
    atualizarLista();
    return panel;

    }

    private JPanel criarPainelAvaliacao() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtTitulo = new JTextField(20);
        JTextField txtUsuario = new JTextField(15);
        JTextField txtNota = new JTextField(5);
        JTextArea txtComentario = new JTextArea(3, 20);
        
        int row = 0;
        addCampo(panel, gbc, "Título da Obra:", txtTitulo, row++);
        addCampo(panel, gbc, "Usuário:", txtUsuario, row++);
        addCampo(panel, gbc, "Nota (0-10):", txtNota, row++);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Comentário:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtComentario), gbc);
        
        JButton btnAvaliar = new JButton("Enviar Avaliação");
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.gridwidth = 2;
        panel.add(btnAvaliar, gbc);
        
        JLabel lblStatus = new JLabel(" ");
        gbc.gridy = row + 2;
        panel.add(lblStatus, gbc);
        
        btnAvaliar.addActionListener(e -> {
            try {
                int nota = Integer.parseInt(txtNota.getText());
                Avaliacao aval = new Avaliacao(txtUsuario.getText(), nota, txtComentario.getText());
                artGallery.avaliarObra(txtTitulo.getText(), aval);
                lblStatus.setText("Avaliação adicionada com sucesso!");
                atualizarLista();
            } catch (NumberFormatException ex) {
                lblStatus.setText("Erro: Nota deve ser um número inteiro");
            } catch (ObraNaoEncontradaException | NotaInvalidaException ex) {
                lblStatus.setText("Erro: " + ex.getMessage());
            }
        });
        
        return panel;
    }

    private JPanel criarPainelBusca() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topo = new JPanel(new FlowLayout());
        JTextField txtAutor = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        topo.add(new JLabel("Autor:"));
        topo.add(txtAutor);
        topo.add(btnBuscar);
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Título", "Tipo", "Detalhes"}, 0);
        JTable tabela = new JTable(model);
        
        btnBuscar.addActionListener(e -> {
            model.setRowCount(0);
            for (Obra obra : artGallery.buscarPorAutor(txtAutor.getText())) {
                String tipo = obra.getClass().getSimpleName();
                model.addRow(new Object[]{obra.getTitulo(), tipo, obra.exibirDetalhes()});
            }
        });
        
        panel.add(topo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelTopObras() {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Posição", "Título", "Autor", "Média"}, 0);
        JTable tabela = new JTable(model);
        JButton btnAtualizar = new JButton("Atualizar Ranking");
        
        btnAtualizar.addActionListener(e -> {
            model.setRowCount(0);
            int pos = 1;
            for (Obra obra : artGallery.topObras()) {
                model.addRow(new Object[]{pos++, obra.getTitulo(), obra.getAutor(), obra.mediaAvaliacoes()});
            }
        });
        
        btnAtualizar.doClick();
        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(btnAtualizar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel criarPainelExposicoes() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topo = new JPanel();
        JTextField txtNomeExpo = new JTextField(15);
        JButton btnCriar = new JButton("Nova Exposição");
        topo.add(new JLabel("Nome:"));
        topo.add(txtNomeExpo);
        topo.add(btnCriar);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> listaExpos = new JList<>(listModel);
        
        JTextArea txtObrasExpo = new JTextArea(10, 30);
        txtObrasExpo.setEditable(false);
        
        listaExpos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listaExpos.getSelectedIndex();
                if (idx >= 0) {
                    Exposicao exp = exposicoes.get(idx);
                    txtObrasExpo.setText("Exposição: " + exp.getNome() + "\n\nObras:\n");
                    for (Obra obra : exp.listarObras()) {
                        txtObrasExpo.append(" - " + obra.getTitulo() + " (" + obra.getAutor() + ")\n");
                    }
                }
            }
        });
        
        btnCriar.addActionListener(e -> {
            String nome = txtNomeExpo.getText();
            if (!nome.isEmpty()) {
                exposicoes.add(new Exposicao(nome));
                listModel.addElement(nome);
                txtNomeExpo.setText("");
            }
        });
        
        JPanel esquerda = new JPanel(new BorderLayout());
        esquerda.add(new JLabel("Exposições Criadas:"), BorderLayout.NORTH);
        esquerda.add(new JScrollPane(listaExpos), BorderLayout.CENTER);
        
        JButton btnAddObra = new JButton("Adicionar Obra à Exposição Selecionada");
        
        JPanel direita = new JPanel(new BorderLayout());
        direita.add(new JScrollPane(txtObrasExpo), BorderLayout.CENTER);
        direita.add(btnAddObra, BorderLayout.SOUTH);
        
        JPanel centro = new JPanel(new GridLayout(1, 2));
        centro.add(esquerda);
        centro.add(direita);
        
        panel.add(topo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        
        btnAddObra.addActionListener(e -> {
            int idx = listaExpos.getSelectedIndex();
            if (idx >= 0) {
                String titulo = JOptionPane.showInputDialog(panel, "Digite o título da obra:");
                if (titulo != null && !titulo.isEmpty()) {
            // ✅ USAR O REPOSITÓRIO EXISTENTE (via artGallery)
                    Obra obra = null;
                    for (Obra o : artGallery.listarObras()) {
                        if (o.getTitulo().equalsIgnoreCase(titulo)) {
                            obra = o;
                            break;
                        }
                    }
                if (obra != null && obra.isAtiva()) {
                    exposicoes.get(idx).adicionarObra(obra);
                // Atualizar o texto da área
                    txtObrasExpo.setText("Exposição: " + exposicoes.get(idx).getNome() + "\n\nObras:\n");
                    for (Obra o : exposicoes.get(idx).listarObras()) {
                        txtObrasExpo.append(" - " + o.getTitulo() + " (" + o.getAutor() + ")\n");
                    }
                    JOptionPane.showMessageDialog(panel, "Obra adicionada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(panel, "Obra não encontrada ou inativa!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Selecione uma exposição primeiro!");
        }
    });
        
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArtGalleryUI().setVisible(true));
    }
}