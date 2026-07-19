package dev.meuprojeto.artgallery.repository;

import java.util.Vector;

import dev.meuprojeto.artgallery.model.Avaliacao;
import dev.meuprojeto.artgallery.model.Exposicao;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;

public class RepositoryObraVector implements IRepositoryObra {
    private Vector<Obra> obras;
    private Vector<Exposicao> exposicoes;
    private IRepositoryObra repository;

    public RepositoryObraVector() {
        this.obras = new Vector<>();
        this.exposicoes = new Vector<>();
    }
    
    public void criarExposicao(String nome) {
        exposicoes.add(new Exposicao(nome));
    }

    @Override
    public void cadastrar(Obra obra) throws ObraJaCadastradaException {
        if (obra == null) {
            return;
        }
        
        // Verifica se já existe obra com mesmo título e autor (case insensitive)
        String novoTitulo = obra.getTitulo().trim();
        String novoAutor = obra.getAutor().trim();
    
    // Verifica se já existe obra com mesmo título e autor (case insensitive)
        for (Obra o : obras) {
            String tituloExistente = o.getTitulo().trim();
            String autorExistente = o.getAutor().trim();
        
            if (tituloExistente.equalsIgnoreCase(novoTitulo) && 
                autorExistente.equalsIgnoreCase(novoAutor)) {
                throw new ObraJaCadastradaException(
                    "Obra '" + novoTitulo + "' de " + novoAutor + " já está cadastrada."
                );
            }
        }
        obras.add(obra);
    }

    @Override
    public Obra buscar(String titulo) {
        if (titulo == null) {
            return null;
        }
        String tituloBusca = titulo.trim();
        
        for (Obra obra : obras) {
            if (obra.getTitulo().trim().equalsIgnoreCase(tituloBusca)) {
                return obra;
            }
        }
        return null;
    }

    @Override
    public void atualizar(int id, Obra obra) throws ObraNaoEncontradaException {
        if (obra == null) {
            return;
        }
        
        for (int i = 0; i < obras.size(); i++) {
            Obra existente = obras.get(i);
            if (existente.getTitulo().equalsIgnoreCase(obra.getTitulo())) {
                obras.set(i, obra);
                return;
            }
        }
        throw new ObraNaoEncontradaException(
            "Obra com título '" + obra.getTitulo() + "' não foi encontrada para atualização."
        );
    }

    @Override
    public void remover(String titulo) {
        if (titulo == null) {
            return;
        }
        for (Obra obra : obras) {
            if (obra.getTitulo().trim().equalsIgnoreCase(titulo.trim())) {
                obra.setAtiva(false);  // ← DESATIVA a obra (não remove!)
                System.out.println("🔄 Obra desativada: " + titulo);
                return;
            }
        }
    }

    @Override
    public Vector<Obra> listar() {
        return obras;
    }

    @Override
    public void adicionarObraAExposicao(String nomeExposicao, Obra obra) {
        for (Exposicao exp : exposicoes) {
            if (exp.getNome().equalsIgnoreCase(nomeExposicao)) {
                exp.adicionarObra(obra);
                return;
            }
        }
    }

    public Vector<Exposicao> listarExposicoes() {  // ← retorna EXPOSIÇÕES
        return exposicoes;
    }

    @Override
    public void removerExposicao(String nome) {
        for (int i = 0; i < exposicoes.size(); i++) {
            if (exposicoes.get(i).getNome().equalsIgnoreCase(nome)) {
                exposicoes.remove(i);
                return;
            }
        }
    }

    @Override
    public void removerObraDaExposicao(String nomeExposicao, String tituloObra) {
        for (Exposicao exp : exposicoes) {
            if (exp.getNome().equalsIgnoreCase(nomeExposicao)) {
                Vector<Obra> obras = exp.listarObras();
                for (int i = 0; i < obras.size(); i++) {
                    if (obras.get(i).getTitulo().equalsIgnoreCase(tituloObra)) {
                        obras.remove(i);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Vector<Obra> listarObrasDaExposicao(String nomeExposicao) {
        Vector<Obra> obras = new Vector<>();
    
        // 1. Procura a exposição pelo nome
        for (Exposicao exp : exposicoes) {
            if (exp.getNome().equalsIgnoreCase(nomeExposicao)) {
            // 2. Pega as obras da exposição
                for (Obra obra : exp.listarObras()) {
                    if (obra.isAtiva()) {
                        obras.add(obra);
                    }
                }
                break;
            }
        }
    
        return obras;
    }

    @Override
    public void avaliarObra(String titulo, Avaliacao avaliacao) throws ObraNaoEncontradaException {
    // Procura a obra pelo título
        for (Obra obra : obras) {
            if (obra.getTitulo().equalsIgnoreCase(titulo)) {
                obra.adicionarAvaliacao(avaliacao);
                System.out.println("✅ Avaliação adicionada em memória!");
                return;
            }
        }
        throw new ObraNaoEncontradaException("Obra com título '" + titulo + "' não encontrada!");
    }

    @Override
    public void adicionarAvaliacao(int obraId, Avaliacao avaliacao) {

        for (Obra obra : obras) {
            if (obra.getId() == obraId) {
                obra.adicionarAvaliacao(avaliacao);
                System.out.println("✅ Avaliação adicionada em memória (Vector) para obra ID: " + obraId);
                return;
            }
        }
        System.out.println("⚠️ Obra com ID " + obraId + " não encontrada no Vector!");
    }
    
    @Override
    public Vector<Avaliacao> listarAvaliacoes(String tituloObra){
        
        Vector<Avaliacao> avaliacoes = repository.listarAvaliacoes(tituloObra);
        if(avaliacoes == null || avaliacoes.isEmpty()){
            System.out.println("⚠️ Não existem avaliações para esta obra no banco de dados!");
            return avaliacoes;
        }else{
            for (Avaliacao aval : avaliacoes){
                System.out.println("Usuário: " + aval.getUsuario() + 
                           " | Nota: " + aval.getNota() + 
                           " | Comentário: " + aval.getComentario());
            }
            
        }
        return avaliacoes;
    }
    

    
}
