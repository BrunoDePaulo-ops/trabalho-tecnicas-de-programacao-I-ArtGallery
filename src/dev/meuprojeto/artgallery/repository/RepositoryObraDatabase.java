package dev.meuprojeto.artgallery.repository;
import java.sql.Connection;        // ← ESSA É A QUE FALTAVA!
import java.sql.DriverManager;     // ← ESSA TAMBÉM!
import java.sql.PreparedStatement; // ← E ESSA!
import java.sql.ResultSet;         // ← E ESSA!
import java.sql.SQLException;      // ← E ESSA!
import java.sql.Statement;         // ← E ESSA!
import java.util.Vector;

import dev.meuprojeto.artgallery.exception.NotaInvalidaException;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;
import dev.meuprojeto.artgallery.model.ArteGenerativa;
import dev.meuprojeto.artgallery.model.Avaliacao;
import dev.meuprojeto.artgallery.model.Exposicao;
import dev.meuprojeto.artgallery.model.Modelagem3D;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.model.PinturaDigital;

public class RepositoryObraDatabase implements IRepositoryObra{
    private Connection connection;

    public RepositoryObraDatabase (){
        try{
            connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/artgallery",
                "postgres",
                "brunox123"
            );
            criarTabelas();
            System.out.println("✅ Conectado ao PostgreSQL!");
        }catch (SQLException e){
             System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }
    private void criarTabelas(){
        String sqlExposicoes = """
            CREATE TABLE IF NOT EXISTS exposicoes(
            id SERIAL PRIMARY KEY,
            nome VARCHAR(50) NOT NULL UNIQUE
            )
        """;
        String sqlObras = """   
            CREATE TABLE IF NOT EXISTS obras(
            id SERIAL PRIMARY KEY,
            titulo VARCHAR (70) NOT NULL,
            autor VARCHAR(100) NOT NULL,
            ativa BOOLEAN DEFAULT TRUE,
            tipo VARCHAR(50) NOT NULL CHECK (tipo IN('Pintura Digital', 'Modelagem 3D', 'Arte Generativa')),
            campo1 VARCHAR(50),
            campo2 VARCHAR(50),
            UNIQUE(titulo,autor)
            )  
        """;
        String sqlExposicao_Obras = """
            CREATE TABLE IF NOT EXISTS exposicao_obras (
                exposicao_id INTEGER,
                obra_id INTEGER,
                PRIMARY KEY (exposicao_id, obra_id),
                FOREIGN KEY (exposicao_id) REFERENCES exposicoes(id) ON DELETE CASCADE,
                FOREIGN KEY (obra_id) REFERENCES obras(id) ON DELETE CASCADE
            )
        """;

        String sqlAvaliacoes = """
            CREATE TABLE IF NOT EXISTS avaliacoes(
                id SERIAL PRIMARY KEY,
                obra_id INTEGER NOT NULL,
                usuario VARCHAR (100) NOT NULL,
                comentario TEXT,
                nota INTEGER NOT NULL CHECK (nota >= 0 AND nota <=10),
                FOREIGN KEY (obra_id) REFERENCES obras(id) ON DELETE CASCADE
            )
        """;
        
        try(Statement stmt = connection.createStatement()){
            stmt.execute(sqlExposicoes);
            stmt.execute(sqlObras);
            stmt.execute(sqlExposicao_Obras);
            stmt.execute(sqlAvaliacoes);

            System.out.println("✅ Tabelas criadas/verificads com sucesso!");
        
        }catch (SQLException e){
            System.out.println("❌ Erro ao criar tabelas: " + e.getMessage());
        }
        
    }

    public void criarExposicao(String nome){
        String sql = " INSERT INTO exposicoes (nome) VALUES(?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
            System.out.println("✅ Exposição criada: " + nome);
        
        }catch (SQLException e){
            System.out.println("❌ Erro ao criar exposição: " + e.getMessage());
        }
    }

    public Vector<Exposicao> listarExposicoes(){
        Vector<Exposicao> exposicoes = new Vector <>();
        String sql = "SELECT id, nome FROM exposicoes";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                Exposicao exp = new  Exposicao(rs.getString("nome"));
                exposicoes.add(exp); 
            }
        }catch (SQLException e){
            System.out.println("❌ Erro ao listar exposição: " + e.getMessage());
        }
        return exposicoes;
    }

    public void removerExposicao(String nome){
        String sql = " DELETE FROM exposicoes WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
            System.out.println("✅ Exposição removida: " + nome);

        }catch(SQLException e){
            System.out.println("❌ Erro ao remover exposição: " + e.getMessage());
        }
    }

    public void removerObraDaExposicao(String nomeExposicao, String tituloObra){
        int id_expo = -1;
        String sqlBusca = "SELECT id FROM exposicoes WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, nomeExposicao);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                id_expo = rs.getInt("id");
            }else{
                System.out.println("❌ Exposição não encontrada: " + nomeExposicao);
                return;
            }
            
            int id_obra = -2;
            String sqlBusca2 = """
                    SELECT o.id FROM obras o
                    JOIN exposicao_obras eo ON o.id = eo.obra_id
                    WHERE eo.exposicao_id = ? AND LOWER(TRIM(o.titulo)) = LOWER(TRIM(?))
            """;
            
            try(PreparedStatement pstmt2 = connection.prepareStatement(sqlBusca2)){
                pstmt2.setInt(1, id_expo);
                pstmt2.setString(2, tituloObra);
                ResultSet rs2 = pstmt2.executeQuery();
                if(rs2.next()){
                    id_obra = rs2.getInt("id");

                }else{
                    System.out.println("❌ Obra não encontrada na exposição procurada: " + nomeExposicao);
                    return;
                }
            }catch(SQLException e){
                System.out.println("❌ Obra não encontrada na exposição procurada: " + nomeExposicao);
                return;
            }

            String sqlBusca3 = "DELETE FROM exposicao_obras WHERE exposicao_id = ? AND obra_id = ? ";
            try(PreparedStatement pstmt3 = connection.prepareStatement(sqlBusca3)){
                pstmt3.setInt(1,id_expo);
                pstmt3.setInt(2, id_obra);
                pstmt3.executeUpdate();
                System.out.println("✅ Obra removida com sucesso: " + tituloObra);

            }catch(SQLException e){
                System.out.println("❌ Não foi possível remover a obra: " + tituloObra);
            }

        }catch(SQLException e){
            System.out.println("❌ Não foi possível remover a obra da exposição: " + tituloObra);
        }
    }

    public void cadastrar(Obra obra) throws ObraJaCadastradaException{
        obra.setTitulo(obra.getTitulo().trim());
        obra.setAutor(obra.getAutor().trim());
        String sql = "INSERT INTO obras (titulo, autor, ativa, tipo, campo1, campo2) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, obra.getTitulo());
            pstmt.setString(2, obra.getAutor());
            pstmt.setBoolean(3, obra.isAtiva());

            if(obra instanceof PinturaDigital){
                pstmt.setString(4, "Pintura Digital");
                pstmt.setString(5, ((PinturaDigital) obra).getResolucao());
                pstmt.setString(6,((PinturaDigital)obra).getSoftwareUtilizado());

            }else if(obra instanceof Modelagem3D){
                pstmt.setString(4, "Modelagem 3D");
                pstmt.setString(5, String.valueOf(((Modelagem3D)obra).getNumeroPoligonos()));
                pstmt.setString(6, ((Modelagem3D)obra).getEngine());

            }else if(obra instanceof ArteGenerativa){
                pstmt.setString(4, "Arte Generativa");
                pstmt.setString(5, ((ArteGenerativa) obra).getAlgoritmo());
                pstmt.setString(6, String.valueOf(((ArteGenerativa)obra).getSeed()));

            }
            pstmt.executeUpdate();
            System.out.println("✅ Obra cadastrada com sucesso.");
        }catch(SQLException e){
            System.out.println("❌ Erro ao cadastrar obra: " + e.getMessage());
        }
    }
    
    public Vector<Obra> listar(){
        
        Vector<Obra> obras = new Vector<>();
        String sql = "SELECT * FROM obras";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String tipo = rs.getString("tipo");
                String campo1 = rs.getString("campo1");
                String campo2 = rs.getString("campo2");
                boolean ativa = rs.getBoolean("ativa");

                Obra obra = null;
                if ("Pintura Digital".equals(tipo)) {
                    obra = new PinturaDigital(titulo, autor, campo1, campo2);
                } else if ("Modelagem 3D".equals(tipo)) {
                    int poligonos = Integer.parseInt(campo1);
                    obra = new Modelagem3D(titulo, autor, poligonos, campo2);
                } else if ("Arte Generativa".equals(tipo)) {
                    long seed = Long.parseLong(campo2);
                    obra = new ArteGenerativa(titulo, autor, campo1, seed);
                }
                if (obra != null) {
                    obra.setId(id);
                    carregarAvaliacoes(obra);
                    obra.setAtiva(ativa);
                    obras.add(obra);
                }
            }
        }catch(SQLException e){
            System.out.println("❌ Erro ao listar obras: " + e.getMessage());
        }
        return obras;
    }
    public void adicionarObraAExposicao(String nomeExposicao, Obra obra ){
        String sqlBusca = " SELECT id FROM exposicoes WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?))";
        int idExpo = -1;

        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, nomeExposicao);
            ResultSet rs = pstmt.executeQuery(); 
            if(rs.next()){
                idExpo = rs.getInt("id");
            }else{
                System.out.println("❌ Exposição não encontrada: " + nomeExposicao);
                return;
            }
        }catch(SQLException e){
            System.out.println("❌ Erro ao buscar exposição: " + e.getMessage());
        }
        
        int idObra = buscarIdObra(obra.getTitulo(), obra.getAutor());

        String sql = "INSERT INTO exposicao_obras VALUES ( ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, idExpo);
            pstmt.setInt(2, idObra);
            pstmt.executeUpdate();
        }catch (SQLException e){
            System.out.println("❌ Erro ao inserir obra na exposição: " + e.getMessage());
        }
    }
    private int buscarIdObra(String titulo, String autor) {
            String sql = "SELECT id FROM obras WHERE LOWER(TRIM(titulo)) = LOWER(TRIM(?)) AND LOWER(TRIM(autor)) = LOWER(TRIM(?))";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, titulo);
                pstmt.setString(2, autor);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException e) {
                System.out.println("❌ Erro ao buscar obra: " + e.getMessage());
            }
        return -1;
    }

    public Connection getConnection() {
        return connection;
    }
    public Vector<Obra> listarObrasDaExposicao(String nomeExposicao) {
        Vector<Obra> obras = new Vector<>();
        String sql = """
            SELECT o.id, o.titulo, o.autor, o.ativa, o.tipo, o.campo1, o.campo2
            FROM obras o
            JOIN exposicao_obras eo ON o.id = eo.obra_id
            WHERE eo.exposicao_id = (SELECT id FROM exposicoes WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?)))
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomeExposicao);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String tipo = rs.getString("tipo");
                String campo1 = rs.getString("campo1");
                String campo2 = rs.getString("campo2");
                boolean ativa = rs.getBoolean("ativa");

                Obra obra = null;
                if ("Pintura Digital".equals(tipo)) {
                    obra = new PinturaDigital(titulo, autor, campo1, campo2);
                } else if ("Modelagem 3D".equals(tipo)) {
                    obra = new Modelagem3D(titulo, autor, Integer.parseInt(campo1), campo2);
                } else if ("Arte Generativa".equals(tipo)) {
                    obra = new ArteGenerativa(titulo, autor, campo1, Long.parseLong(campo2));
                }

                if (obra != null) {
                    obra.setAtiva(ativa);
                    obras.add(obra);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar obras da exposição: " + e.getMessage());
        }
        return obras;
    }

    public void avaliarObra(String titulo, Avaliacao avaliacao) throws ObraNaoEncontradaException{
        int id_obr = buscarIdObraPorTitulo(titulo);
        if(id_obr == -1){
            throw new ObraNaoEncontradaException("Obra com título '" + titulo + "' não encontrada!");
        }

        boolean ativa = buscarAtivaObraPorTitulo(titulo);
        if (!ativa){
            throw new ObraNaoEncontradaException("Obra com título '" + titulo + "' está inativa!");
        }
        
        String sqlCheck = "SELECT COUNT (*) FROM avaliacoes WHERE obra_id = ? AND LOWER(TRIM(usuario)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlCheck)){
            pstmt.setInt(1, id_obr);
            pstmt.setString(2, avaliacao.getUsuario());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next() && rs.getInt(1) > 0){
                String sqlUpdate = """
                    UPDATE avaliacoes 
                    SET nota = ?, comentario = ? 
                    WHERE obra_id = ? AND LOWER(TRIM(usuario)) = LOWER(TRIM(?))
                """;
                try(PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate)){
                    pstmtUpdate.setInt(1, avaliacao.getNota());
                    pstmtUpdate.setString(2, avaliacao.getComentario());
                    pstmtUpdate.setInt(3, id_obr);
                    pstmtUpdate.setString(4, avaliacao.getUsuario());
                    pstmtUpdate.executeUpdate();
                    System.out.println("✅ Avaliação atualizada com sucesso!");
                    return;
                    
                }
            }
        }catch(SQLException e){
            System.out.println("❌ Erro ao procurar obra: " + e.getMessage());
        }
        
        
        String sql = "INSERT INTO avaliacoes (obra_id, usuario, comentario, nota) VALUES (?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id_obr);
            pstmt.setString(2, avaliacao.getUsuario());
            pstmt.setString(3, avaliacao.getComentario());
            pstmt.setInt(4, avaliacao.getNota());
            pstmt.executeUpdate();

        }catch (SQLException e){
            System.out.println("❌ Erro ao adicionar avaliação: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir avaliação: " + e.getMessage(), e);
        }

    }
    private int buscarIdObraPorTitulo(String titulo){
        if(titulo == null || titulo.trim().isEmpty()){
            System.out.println("❌ Título vazio!");
            return -1;
        }
        String tituloNormalizado = titulo.trim();
        System.out.println("🔍 Buscando obra: '" + tituloNormalizado + "'");

        String sqlBusca = "SELECT id FROM obras WHERE LOWER(TRIM(titulo)) = LOWER(TRIM(?))";
            
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, tituloNormalizado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("   ✅ ID encontrado: " + id);
                return id;
            }

        }catch(SQLException e){
            System.out.println("❌ Erro: " + e.getMessage());
        }
        String sqlFallback = "SELECT id FROM obras WHERE titulo ILIKE ?";
        try (PreparedStatement pstmt2 = connection.prepareStatement(sqlFallback)) {
            pstmt2.setString(1, "%" + tituloNormalizado + "%");
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                int id = rs2.getInt("id");
                System.out.println("   ✅ ID encontrado via ILIKE: " + id);
                return id;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro no ILIKE: " + e.getMessage());
        }
        
        System.out.println("   ❌ Nenhuma obra encontrada para: '" + tituloNormalizado + "'");
        return -1;
    }
    

    private boolean buscarAtivaObraPorTitulo(String titulo){
        String sqlBusca = "SELECT ativa FROM obras WHERE LOWER(TRIM(titulo)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            
            pstmt.setString(1, titulo);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getBoolean("ativa");
            }
        }catch(SQLException e){
            System.out.println("❌ Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Vector<Avaliacao> listarAvaliacoes(String tituloObra) {
        Vector<Avaliacao> avaliacoes = new Vector<>();
        String sql = """
            SELECT a.usuario, a.nota, a.comentario
            FROM avaliacoes a
            JOIN obras o ON a.obra_id = o.id
            WHERE LOWER(TRIM(o.titulo)) = LOWER(TRIM(?))
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tituloObra);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    Avaliacao aval = new Avaliacao(
                        rs.getString("usuario"),
                        rs.getInt("nota"),
                        rs.getString("comentario")
                    );
                    avaliacoes.add(aval);
                } catch (NotaInvalidaException e) {
                    System.out.println("⚠️ Avaliação inválida ignorada: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar avaliações: " + e.getMessage());
        }
        return avaliacoes;
    }
    
    public void remover(String titulo){
        int id_obra = -1;
        String sqlBusca = "SELECT id FROM obras WHERE LOWER(TRIM(titulo)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, titulo);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                id_obra = rs.getInt("id");
            
            }else{
                System.out.println("❌ Obra não encontrada no cadastrado!");
                return;
            }

            String sqlDelete = "DELETE FROM obras WHERE id = ?";
            try(PreparedStatement pstmt2 = connection.prepareStatement(sqlDelete)){
                pstmt2.setInt(1, id_obra);
                int linhas = pstmt2.executeUpdate();
                if(linhas > 0){
                    System.out.println("✅ Obra removida com sucesso: " + titulo);
                }else{
                    System.out.println("⚠️ Erro ao remover obra.");
                    return;
                }
            }

        }catch(SQLException e){
           System.out.println("❌ Obra não encontrada no cadastrado! " + e.getMessage());
        } 
    }

    public void atualizar(int id, Obra obra) throws ObraNaoEncontradaException{
        System.out.println("🔍 ID recebido no atualizar: " + id);
        
        if(id == -1){
            throw new ObraNaoEncontradaException ("Obra não encontrada!");
        }
                
        String sqlUpdate = "UPDATE obras SET titulo = ?, autor = ?, ativa = ?, tipo = ?, campo1 = ?, campo2 = ? WHERE id = ?";
        try(PreparedStatement pstmt2 = connection.prepareStatement(sqlUpdate)){
            pstmt2.setString(1, obra.getTitulo());
            pstmt2.setString(2, obra.getAutor());
            pstmt2.setBoolean(3, obra.isAtiva());

            if(obra instanceof PinturaDigital){
                pstmt2.setString(4, "Pintura Digital");
                pstmt2.setString(5, ((PinturaDigital) obra).getResolucao());
                pstmt2.setString(6, ((PinturaDigital) obra).getSoftwareUtilizado());
            }else if(obra instanceof Modelagem3D){
                pstmt2.setString(4, "Modelagem 3D");
                pstmt2.setString(5, String.valueOf(((Modelagem3D) obra).getNumeroPoligonos()));
                pstmt2.setString(6, ((Modelagem3D) obra).getEngine());
            }else{
                pstmt2.setString(4, "Arte Generativa");
                pstmt2.setString(5, ((ArteGenerativa) obra).getAlgoritmo());
                pstmt2.setString(6, String.valueOf(((ArteGenerativa) obra).getSeed()));
            }
            pstmt2.setInt(7, id);

            int linhas = pstmt2.executeUpdate();
            if(linhas > 0 ){
                System.out.println("✅ Atualização realizada com sucesso: " + obra.getTitulo());
            }else{
                System.out.println("❌ Nenhuma linha foi atualizada");
                
            }

        }catch(SQLException e){
            System.out.println("❌ Erro no acesso ao banco de dados.");
            e.printStackTrace();
        }
    }
    
    public int getId(Obra obra){
        int id_ob = -1;
        
        String sqlBusca = "SELECT id FROM obras WHERE titulo ILIKE ? AND ativa = ? AND autor ILIKE ? AND tipo ILIKE ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, "%" + obra.getTitulo() + "%");
            pstmt.setBoolean(2, obra.isAtiva());
            pstmt.setString(3, "%" + obra.getAutor() + "%");
            
            if (obra instanceof PinturaDigital) {
                pstmt.setString(4, "%Pintura Digital%");
            } else if (obra instanceof Modelagem3D) {
                pstmt.setString(4, "%Modelagem 3D%");
            } else if (obra instanceof ArteGenerativa) {
                pstmt.setString(4, "%Arte Generativa%");
            } else {
                throw new IllegalArgumentException("Tipo de obra desconhecido!");
            }

            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                id_ob = rs.getInt("id");
                System.out.println("✅ Obra encontrada! ID: " + id_ob);
                return id_ob;

            }
        }catch(SQLException e){
             System.out.println("❌ Erro na conexão com o banco de dados!" + e.getMessage());
        }
        return id_ob;
    }

    public Obra buscar(String titulo){
        
        String sqlBusca = "SELECT * FROM obras WHERE LOWER(TRIM(titulo)) = LOWER(TRIM(?))";
        try(PreparedStatement pstmt = connection.prepareStatement(sqlBusca)){
            pstmt.setString(1, titulo);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String autor = rs.getString("autor");
                String tipo = rs.getString("tipo");
                Boolean ativa = rs.getBoolean("ativa");
                String campo1 = rs.getString("campo1");
                String campo2 = rs.getString("campo2");
                

                Obra obraEncontrada = null;

                if ("Pintura Digital".equals(tipo)) {
                    obraEncontrada = new PinturaDigital(titulo, autor, campo1, campo2);
                } else if ("Modelagem 3D".equals(tipo)) {
                    int poligonos = Integer.parseInt(campo1);
                    obraEncontrada = new Modelagem3D(titulo, autor, poligonos, campo2);
                } else if ("Arte Generativa".equals(tipo)) {
                    long seed = Long.parseLong(campo2);
                    obraEncontrada = new ArteGenerativa(titulo, autor, campo1, seed);
                }
                if (obraEncontrada != null) {
                    int id = rs.getInt("id");
                    carregarAvaliacoes(obraEncontrada);
                    obraEncontrada.setId(id); 
                    obraEncontrada.setAtiva(ativa);
                    return obraEncontrada;
                }
            }
        }catch(SQLException e){
            System.out.println("❌ Erro ao buscar obra: " + e.getMessage());
        }
        return null;
    }
    /* 
    public void carregarAvaliacoes(Obra obra){
        
        
        try{
            String sqlBusca = "SELECT usuario, nota, comentario FROM avaliacoes WHERE obra_id = " + obra.getId();
            System.out.println("   SQL: " + sqlBusca);
            java.sql.Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlBusca);
            int count = 0;
            while (rs.next()){
                count++;
                System.out.println("   ✅ Linha " + count + " encontrada!");
                try{
                    Avaliacao aval = new Avaliacao(
                        rs.getString("usuario"),
                        rs.getInt("nota"),
                        rs.getString("comentario")
                    );
                    obra.getAvaliacoes().add(aval);  // ← ADICIONA DIRETAMENTE
                }catch(NotaInvalidaException e){
                    System.out.println("⚠️ Avaliação inválida ignorada: " + e.getMessage());
                }
            }
            System.out.println("   📋 Total de avaliações adicionadas: " + count);
        }catch(SQLException e){
            System.out.println("❌ Erro ao carregar avaliações: " + e.getMessage());
            e.printStackTrace();

        }
    }
    */
    public void carregarAvaliacoes(Obra obra) {
        System.out.println("🔍 Carregando avaliações para: " + obra.getTitulo() + " (ID: " + obra.getId() + ")");
    
        String sql = "SELECT usuario, nota, comentario FROM avaliacoes WHERE obra_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, obra.getId());
            System.out.println("   SQL: " + sql);
            System.out.println("   Parâmetro obra_id: " + obra.getId());
            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("   ✅ Linha " + count + " encontrada!");
                try {
                    Avaliacao aval = new Avaliacao(
                        rs.getString("usuario"),
                        rs.getInt("nota"),
                        rs.getString("comentario")
                    );
                    obra.getAvaliacoes().add(aval);
                } catch (NotaInvalidaException e) {
                    System.out.println("   ⚠️ Avaliação inválida ignorada: " + e.getMessage());
                }
            }
            System.out.println("   📋 Total de avaliações adicionadas: " + count);
        } catch (SQLException e) {
            System.out.println("❌ Erro ao carregar avaliações: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    @Override
    public void adicionarAvaliacao(int obraId, Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacoes (obra_id, usuario, comentario, nota) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, obraId);
            pstmt.setString(2, avaliacao.getUsuario());
            pstmt.setString(3, avaliacao.getComentario());
            pstmt.setInt(4, avaliacao.getNota());
            pstmt.executeUpdate();
            System.out.println("✅ Avaliação salva no banco!");
        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar avaliação: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
