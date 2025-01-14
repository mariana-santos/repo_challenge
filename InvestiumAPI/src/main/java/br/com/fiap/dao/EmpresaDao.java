package br.com.fiap.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.com.fiap.connection.ConnectionFactory;
import br.com.fiap.model.Empresa;
import br.com.fiap.model.Setor;
import br.com.fiap.model.IndicadorFinanceiro;

public class EmpresaDao {

	SetorDao sdao = new SetorDao();
	//GovernancaDao gdao = new GovernancaDao();
	
	public void insert(Empresa empresa) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        Statement statement;
       
        try {	
        	//"convertendo" o boolean da empresa pra char
        	String ativoIPO = empresa.getAtivoIpo() ? "S" : "N";
        	
            String query = String.format("INSERT INTO empresa"
            		+ "(id_empresa, nome_empresa, descricao_empresa, ativo_ipo,"
            		+ "valor_inicial_ipo, descricao_ipo, link_empresa,"
            		+ "link_prospecto, img_empresa, dt_inicio_ipo, cnpj, fk_setor, cor) "
            		+ "VALUES (%s, '%s', '%s', '%s', %s, '%s', '%s', '%s', '%s', %s)", 
            		empresa.getId(), empresa.getNome(), empresa.getDescricao(),
            		ativoIPO, empresa.getValorInicialIpo(), empresa.getDescricaoIpo(),
            		empresa.getLinkEmpresa(), empresa.getLinkProspecto(), 
            		empresa.getImagem(), empresa.getDataInicioIPO(), empresa.getCnpj(),
            		empresa.getSetor().getId(), empresa.getCor());
           
            statement = conn.createStatement();          
            statement.executeUpdate(query);
            
        }catch (Exception e){
            System.out.println("Erro ao inserir na tabela empresa! - " + e);
        }
        finally {
        	conn.close();
        }
	}
	
	public ArrayList<Empresa> getAll ()throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		Statement statement;
		ResultSet rs = null;
		ArrayList<Empresa> list = null;
		
		try {
            String query = "SELECT * FROM empresa ORDER BY id_empresa";
            
            statement = conn.createStatement();
           
            rs = statement.executeQuery(query);
           
            list = new ArrayList<Empresa>(); 
            while(rs.next()){
            	Empresa e = new Empresa();
            	e.setId(rs.getInt("id_empresa"));
            	e.setNome(rs.getString("nome_empresa"));
            	e.setDescricao(rs.getString("descricao_empresa"));
            	
            	String ativo = rs.getString("ativo_ipo");
            	e.setAtivoIpo(ativo.contentEquals("S") ? true : false);
            	
            	e.setValorInicialIpo(rs.getDouble("valor_inicial_ipo"));
            	e.setDescricaoIpo(rs.getString("descricao_ipo"));
            	e.setLinkEmpresa(rs.getString("link_empresa"));
            	e.setLinkProspecto(rs.getString("link_prospecto"));
            	e.setImagem(rs.getString("img_empresa"));
            	e.setCor(rs.getString("cor"));
            	e.setCnpj(rs.getString("cnpj"));
            	e.setDataInicioIPO(rs.getDate("dt_inicio_ipo"));
            	
            	String queryIndicador = String.format("SELECT id_indicador, descricao, tipo, valor, "
                		+ "EXTRACT(YEAR FROM ano) as ano, fk_empresa "
                		+ "FROM indicador_financeiro "
                		+ "WHERE fk_empresa = %s ORDER BY ano", rs.getInt("id_empresa"));
                
                Statement statementIndicador = conn.createStatement();
                ResultSet rsIndicador = statementIndicador.executeQuery(queryIndicador);
               
                ArrayList<IndicadorFinanceiro> listIndicadores = new ArrayList<IndicadorFinanceiro>(); 
                
                while(rsIndicador.next()){
                	IndicadorFinanceiro indicador = new IndicadorFinanceiro();
                	indicador.setId(rsIndicador.getInt("id_indicador"));
                	indicador.setDescricao(rsIndicador.getString("descricao"));
                	indicador.setAno(rsIndicador.getInt("ano"));
                	indicador.setTipo(rsIndicador.getString("tipo"));
                	indicador.setValor(rsIndicador.getString("valor"));
                    listIndicadores.add(indicador);
                }
            	
            	e.setIndicadoresFinanceiros(listIndicadores);
            	
            	Setor s = sdao.getSetor(rs.getInt("fk_setor"));
            	e.setSetor(s);
            	
            	list.add(e);
            }
        }catch (Exception e){
            System.out.println("Erro ao exibir as empresas! - " + e);
        }
        finally {
        	conn.close();
        }
        
        return list;
		
	}
	
	public Empresa getEmpresa (int id)throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		Statement statement;
		ResultSet rs = null;
		Empresa empresa = null;
		
		ArrayList<IndicadorFinanceiro> list;
		
		try {
            String query = String.format("SELECT * FROM empresa "
            		+ "WHERE id_empresa = %s", id);
            
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
           
            while(rs.next()){
            	empresa = new Empresa();
            	empresa.setId(rs.getInt("id_empresa"));
            	empresa.setNome(rs.getString("nome_empresa"));
            	empresa.setDescricao(rs.getString("descricao_empresa"));
            	
            	String ativo = rs.getString("ativo_ipo");
            	empresa.setAtivoIpo(ativo.contentEquals("S") ? true : false);
            	
            	empresa.setValorInicialIpo(rs.getDouble("valor_inicial_ipo"));
            	empresa.setDescricaoIpo(rs.getString("descricao_ipo"));
            	empresa.setLinkEmpresa(rs.getString("link_empresa"));
            	empresa.setLinkProspecto(rs.getString("link_prospecto"));
            	empresa.setImagem(rs.getString("img_empresa"));
            	empresa.setCor(rs.getString("cor"));
            	empresa.setCnpj(rs.getString("cnpj"));
            	empresa.setDataInicioIPO(rs.getDate("dt_inicio_ipo"));
            	
            	String queryIndicador = String.format("SELECT id_indicador, descricao, tipo, valor, "
                		+ "EXTRACT(YEAR FROM ano) as ano, fk_empresa "
                		+ "FROM indicador_financeiro "
                		+ "WHERE fk_empresa = %s ORDER BY ano", id);
                
                Statement statementIndicador = conn.createStatement();
                ResultSet rsIndicador = statementIndicador.executeQuery(queryIndicador);
               
                list = new ArrayList<IndicadorFinanceiro>(); 
                
                while(rsIndicador.next()){
                	IndicadorFinanceiro indicador = new IndicadorFinanceiro();
                	indicador.setId(rsIndicador.getInt("id_indicador"));
                	indicador.setDescricao(rsIndicador.getString("descricao"));
                	indicador.setAno(rsIndicador.getInt("ano"));
                	indicador.setTipo(rsIndicador.getString("tipo"));
                	indicador.setValor(rsIndicador.getString("valor"));
                    list.add(indicador);
                }
            	
            	empresa.setIndicadoresFinanceiros(list);
            	
            	Setor s = sdao.getSetor(rs.getInt("fk_setor"));
            	empresa.setSetor(s);
            }
        }catch (Exception e){
            System.out.println("Erro ao exibir as empresas! - " + e);
        }
        finally {
        	conn.close();
        }
        
        return empresa;
	}
	
	public void delete(int id) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        Statement statement;
       
        try {
            String query = String.format("DELETE FROM empresa "
            		+ "WHERE id_empresa = %s", id);
           
            statement = conn.createStatement();          
            statement.executeUpdate(query);
        }catch (Exception e){
            System.out.println("Erro ao excluir da tabela empresa! - " + e);
        }
        finally {
        	conn.close();
        }
	}
	
	public void update(Empresa empresa) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        Statement statement;
       
        try {
        	//"convertendo" o boolean da empresa pra char
        	String ativoIPO = empresa.getAtivoIpo() ? "S" : "N";
        	
            String query = String.format("update empresa set nome_empresa = '%s', descricao_empresa = '%s',"
            		+ "ativo_ipo = '%s', valor_inicial_ipo = %s,"
            		+ "descricao_ipo = '%s', link_empresa = '%s',"
            		+ "link_prospecto = '%s', img_empresa = '%s',"
            		+ "fk_setor = %s, cor = '%s'"
            		+ "WHERE id_empresa = %s", 
            		empresa.getNome(), empresa.getDescricao(), 
            		ativoIPO, empresa.getValorInicialIpo(), empresa.getDescricaoIpo(),
            		empresa.getLinkEmpresa(), empresa.getLinkProspecto(),
            		empresa.getImagem(), empresa.getSetor().getId(), empresa.getCor(), empresa.getId());
            
           
            statement = conn.createStatement();          
            statement.executeUpdate(query);
        }catch (Exception e){
            System.out.println("Erro ao atualizar a empresa! - " + e);
        }
        finally {
        	conn.close();
        }
	}
}
