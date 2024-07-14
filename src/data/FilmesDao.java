package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FilmesDao {

    Connection conn;
    PreparedStatement st;
    ResultSet rs;
  
    public boolean conectar(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cenaflix_bd","root","Convergente1");
            return true;
        }
        catch(ClassNotFoundException | SQLException ex){
            System.out.println("Erro ao Conectar: "+ex.getMessage());
            return false;
        }
    }
    
    public int salvar(Filmes func){
        int status;
        try {
            st = conn.prepareStatement("INSERT INTO filmes VALUES(?,?,?,?)");
            st.setInt(1, func.getId());
            st.setString(2, func.getNome());
            st.setDate(3, new java.sql.Date(func.getData().getTime())); 
            st.setString(4, func.getCategoria());
            status = st.executeUpdate();
            return status;
        } 
        catch (SQLException ex) {
            System.out.println("Erro ao conectar: " + ex.getMessage());
            return ex.getErrorCode();
        }
    }
    
    public List<Filmes> getFilmes(String nome, String categoria) { 
        try {            
            String sqlfiltro = "SELECT * FROM filmes"; 
            
            List<String> parametros = new ArrayList<>();
            if (!nome.isEmpty()) {
                sqlfiltro += " WHERE nome LIKE ?";
                parametros.add("%" + nome + "%");
            }
            if (!"Geral".equals(categoria)) {
                if (parametros.isEmpty()) {
                    sqlfiltro += " WHERE categoria = ?";
                } else {
                    sqlfiltro += " AND categoria = ?";
                }
                parametros.add(categoria);
            }
            
            PreparedStatement stmt = this.conn.prepareStatement(sqlfiltro);
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setString(i + 1, parametros.get(i));
            }

            ResultSet rs = stmt.executeQuery();                        

            List<Filmes> listaFilmes = new ArrayList<>();                   

            while (rs.next()) { 
                Filmes filmes = new Filmes();
                filmes.setId(rs.getInt("id"));
                filmes.setNome(rs.getString("nome"));
                filmes.setCategoria(rs.getString("categoria"));
                filmes.setData(rs.getDate("datalancamento"));
                listaFilmes.add(filmes);                           
            }
            return listaFilmes;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }               
    }
    
    public int deletar(int id, String nome) {
        int status = 0;
        try {
            if (id > 0) {
                st = conn.prepareStatement("DELETE FROM filmes WHERE id = ?");
                st.setInt(1, id);
            } else if (nome != null && !nome.isEmpty()) {
                st = conn.prepareStatement("DELETE FROM filmes WHERE nome = ?");
                st.setString(1, nome);
            } else {
                throw new SQLException("ID ou Nome do filme devem ser fornecidos.");
            }
            status = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erro ao deletar: " + ex.getMessage());
            return ex.getErrorCode();
        }
        return status;
    }
    
    public int atualizar(Filmes filme) {
        int status;
        try {
            st = conn.prepareStatement("UPDATE filmes SET nome = ?, categoria = ?, datalancamento = ? WHERE id = ?");
            st.setString(1, filme.getNome());
            st.setString(2, filme.getCategoria());
            st.setDate(3, (Date) filme.getData());
            st.setInt(4, filme.getId());
            status = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar: " + ex.getMessage());
            return ex.getErrorCode();
        }
        return status;
    }   
   
    public void desconectar(){
        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }       
    }
}
