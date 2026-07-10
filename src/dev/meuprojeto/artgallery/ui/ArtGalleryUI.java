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
    private JList<String> listaExpos;
    private JTextArea txtObrasExpo;
    private DefaultListModel<String> listModel;

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

    private void atualizarExposicoes() {
        System.out.println("🔄 Atualizando exposições...");

    // 🔥 PASSO 1: FORÇA A RECARGA TOTAL DA JList
        if (listaExpos != null) {
            DefaultListModel<String> model = (DefaultListModel<String>) listaExpos.getModel();
            model.clear();

        // 🔥 BUSCA AS EXPOSIÇÕES DIRETAMENTE DO SERVICE (QUE BUSCA DO REPOSITÓRIO)
            Vector<Exposicao> exposicoesAtuais = artGallery.listarExposicoes();
            System.out.println("📋 Total de exposições: " + exposicoesAtuais.size());

            for (Exposicao exp : exposicoesAtuais) {
                model.addElement(exp.getNome());
                System.out.println("   - Exposição: " + exp.getNome());
            }

        // 🔥 PASSO 2: ATUALIZA A ÁREA DE TEXTO DA EXPOSIÇÃO SELECIONADA
            int idx = listaExpos.getSelectedIndex();
            if (idx >= 0 && idx < exposicoesAtuais.size()) {
                Exposicao exp = exposicoesAtuais.get(idx);
                System.out.println("📋 Exposição selecionada: " + exp.getNome());

                txtObrasExpo.setText("Exposição: " + exp.getNome() + "\n\nObras:\n");

                int count = 0;
                for (Obra obra : exp.listarObras()) {
                    if (obra.isAtiva()) {  // ← SÓ MOSTRA OBRAS ATIVAS
                        txtObrasExpo.append(" - " + obra.getTitulo() + " (" + obra.getAutor() + ")\n");
                        count++;
                    } else {
                        System.out.println("   - Obra inativa IGNORADA: " + obra.getTitulo());
                    }
                }
                System.out.println("📋 Total de obras ativas na exposição: " + count);
            } else {
                txtObrasExpo.setText("Selecione uma exposição");
            }
        }
    }

    private JPanel criarPainelCadastro() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        JTextField txtTitulo = new JTextField(20);
        JTextField txtAutor = new JTextField(20);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Pintura Digital", "Modelagem 3D", "Arte Generativa"});

        JTextField txtCampo1 = new JTextField(15);
        JTextField txtCampo2 = new JTextField(15);
        JLabel lblCampo1 = new JLabel("Resolução:");
        JLabel lblCampo2 = new JLabel("Software:");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; panel.add(txtTitulo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; panel.add(txtAutor, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Tipo de Obra:"), gbc);
        gbc.gridx = 1; panel.add(cbTipo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; panel.add(lblCampo1, gbc);
        gbc.gridx = 1; panel.add(txtCampo1, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; panel.add(lblCampo2, gbc);
        gbc.gridx = 1; panel.add(txtCampo2, gbc);
        row++;

        JButton btnCadastrar = new JButton("Cadastrar Obra");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(btnCadastrar, gbc);
        row++;

        JLabel lblStatus = new JLabel(" ");
        gbc.gridy = row;
        panel.add(lblStatus, gbc);

        cbTipo.addActionListener(e -> {
            String tipo = (String) cbTipo.getSelectedItem();
            if ("Pintura Digital".equals(tipo)) {
                lblCampo1.setText("Resolução:");
                lblCampo2.setText("Software:");
            } else if ("Modelagem 3D".equals(tipo)) {
                lblCampo1.setText("Polígonos:");
                lblCampo2.setText("Engine:");
            } else {
                lblCampo1.setText("Algoritmo:");
                lblCampo2.setText("Seed:");
            }
            txtCampo1.setText("");
            txtCampo2.setText("");
        });

        btnCadastrar.addActionListener(e -> {
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            String tipo = (String) cbTipo.getSelectedItem();
            String campo1 = txtCampo1.getText().trim();
            String campo2 = txtCampo2.getText().trim();

            if (titulo.isEmpty()) {
                lblStatus.setText("Erro: O título é obrigatório!");
                return;
            }
            if (autor.isEmpty()) {
                lblStatus.setText("Erro: O autor é obrigatório!");
                return;
            }
            if (campo1.isEmpty() || campo2.isEmpty()) {
                lblStatus.setText("Erro: Preencha todos os campos específicos!");
                return;
            }

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
                txtTitulo.setText("");
                txtAutor.setText("");
                txtCampo1.setText("");
                txtCampo2.setText("");
                atualizarLista();
            } catch (ObraJaCadastradaException ex) {
                lblStatus.setText("Erro: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                lblStatus.setText("Erro: Valores numéricos inválidos");
            }
        });

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
                int confirm = JOptionPane.showConfirmDialog(panel, "Deseja remover a obra '" + titulo + "'?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        artGallery.removerObra(titulo);
                        JOptionPane.showMessageDialog(panel, "Obra removida com sucesso!");
                        atualizarLista();
                        atualizarExposicoes();
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

        listModel = new DefaultListModel<>();
        listaExpos = new JList<>(listModel);

        txtObrasExpo = new JTextArea(10, 30);
        txtObrasExpo.setEditable(false);

        listaExpos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listaExpos.getSelectedIndex();
                if (idx >= 0) {
                    Vector<Exposicao> exposicoes = artGallery.listarExposicoes();
                    if (idx < exposicoes.size()) {
                        Exposicao exp = exposicoes.get(idx);
                        txtObrasExpo.setText("Exposição: " + exp.getNome() + "\n\nObras:\n");
                        for (Obra obra : exp.listarObras()) {
                            if (obra.isAtiva()) {
                                txtObrasExpo.append(" - " + obra.getTitulo() + " (" + obra.getAutor() + ")\n");
                            }
                        }
                    }
                }
            }
        });

        btnCriar.addActionListener(e -> {
            String nome = txtNomeExpo.getText();
            if (!nome.isEmpty()) {
                artGallery.criarExposicao(nome);
                listModel.addElement(nome);
                txtNomeExpo.setText("");
            }
        });

        JButton btnAddObra = new JButton("Adicionar Obra à Exposição Selecionada");
        btnAddObra.addActionListener(e -> {
            int idx = listaExpos.getSelectedIndex();
            if (idx >= 0) {
                String titulo = JOptionPane.showInputDialog(panel, "Digite o título da obra:");
                if (titulo != null && !titulo.isEmpty()) {
                    Obra obra = null;
                    String tituloBusca = titulo.trim();
                    for (Obra o : artGallery.listarObras()) {
                        if (o.getTitulo().trim().equalsIgnoreCase(tituloBusca)) {
                            obra = o;
                            break;
                        }
                    }
                    if (obra != null && obra.isAtiva()) {
                        String nomeExpo = listaExpos.getSelectedValue();

                        boolean existe = false;
                        for (Exposicao exp : artGallery.listarExposicoes()) {
                            if (exp.getNome().equalsIgnoreCase(nomeExpo) && exp.contemObra(obra)) {
                                existe = true;
                                break;
                            }
                        }

                        if (existe) {
                            JOptionPane.showMessageDialog(panel, "Esta obra já está na exposição!");
                        } else {
                            artGallery.adicionarObraAExposicao(nomeExpo, obra);
                            txtObrasExpo.setText("Exposição: " + nomeExpo + "\n\nObras:\n");
                            for (Exposicao exp : artGallery.listarExposicoes()) {
                                if (exp.getNome().equalsIgnoreCase(nomeExpo)) {
                                    for (Obra o : exp.listarObras()) {
                                        txtObrasExpo.append(" - " + o.getTitulo() + " (" + o.getAutor() + ")\n");
                                    }
                                    break;
                                }
                            }
                            JOptionPane.showMessageDialog(panel, "Obra adicionada com sucesso!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Obra não encontrada ou inativa!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione uma exposição primeiro!");
            }
        });

        // ===== BOTÃO REMOVER EXPOSIÇÃO =====
        JButton btnRemoverExposicao = new JButton("Remover Exposição Selecionada");
        btnRemoverExposicao.addActionListener(e -> {
            String nomeExpo = listaExpos.getSelectedValue();
            if (nomeExpo != null) {
                int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Deseja remover a exposição '" + nomeExpo + "'?",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    artGallery.removerExposicao(nomeExpo);
                    atualizarExposicoes();
                    JOptionPane.showMessageDialog(panel, "Exposição removida com sucesso!");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione uma exposição para remover!");
        }
    });

        JPanel esquerda = new JPanel(new BorderLayout());
        esquerda.add(new JLabel("Exposições Criadas:"), BorderLayout.NORTH);
        esquerda.add(new JScrollPane(listaExpos), BorderLayout.CENTER);
        esquerda.add(btnRemoverExposicao, BorderLayout.SOUTH);

        JPanel direita = new JPanel(new BorderLayout());
        direita.add(new JScrollPane(txtObrasExpo), BorderLayout.CENTER);
        direita.add(btnAddObra, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridLayout(1, 2));
        centro.add(esquerda);
        centro.add(direita);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArtGalleryUI().setVisible(true));
    }
}