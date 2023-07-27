package jsqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static Connection conectar() {
		//Já cria o banco de dados 'java_sqlite.db'
		String URL_SERVIDOR = "jdbc:sqlite:src/jsqlite/java_sqlite.db";
		
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			Connection conexao = DriverManager.getConnection(URL_SERVIDOR);
			
			
			String TABELA = "CREATE TABLE IF NOT EXISTS produtos("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "nome TEXT NOT NULL,"
					+ "preco REAL NOT NULL,"
					+ "estoque INTEGER NOT NULL);";
			
			//Já cria a tabela também
			Statement stmt = conexao.createStatement();
			stmt.executeUpdate(TABELA);
			
			return conexao;
		}catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Não foi possível conectar ao servidor: " + ex.getMessage());
		}
		
		return null;
	}
	
	public static void desconectar(Connection conexao) {
		try {
			conexao.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Erro ao fechar conexao");
		}
	}
	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos";
		
		try {
			Connection conexao = conectar();
			PreparedStatement produtos =  conexao.prepareStatement(BUSCAR_TODOS);
			ResultSet resultado = produtos.executeQuery();
			
			//Aqui no SQLite, não tem como ir e voltar pra saber se ele retornou algum produto, como fizemos nos outros
			System.out.println("Listando produtos");
			System.out.println("-----------------");
			while(resultado.next()) {
				System.out.println("ID: " + resultado.getInt(1));
				System.out.println("Nome: " + resultado.getString(2));
				System.out.println("Preço: " + resultado.getFloat(3));
				System.out.println("Estoque: " + resultado.getInt(4));
				System.out.println("-----------------");				
			}
			
			desconectar(conexao);
			
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao buscar todos os produtos");
			System.exit(-42);
		}

	}
	
	public static void inserir() {
		try {

			System.out.println("Informe o nome do produto: ");
			String nome = teclado.nextLine();
			
			System.out.println("Informe o preço do produto: ");
			float preco = Float.parseFloat(teclado.nextLine());
	
			System.out.println("Informe o estoque do produto: ");
			int estoque = Integer.parseInt(teclado.nextLine());
			
			String INSERIR = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";
		
			Connection conexao = conectar();
			PreparedStatement salvar = conexao.prepareStatement(INSERIR);
			//Proteção contra SQL Injection -> No Java, começa do 1
			salvar.setString(1, nome);
			salvar.setFloat(2, preco);
			salvar.setInt(3, estoque);
			
			int resultado = salvar.executeUpdate(); //Retorna a quantidade de itens inseridos/atualizados
			if (resultado > 0) {
				System.out.println("O produto " + nome + " foi inserido com sucesso!");
			}else {
				System.out.println("Não foi possível inserir o produto");
			}
			salvar.close();
			desconectar(conexao);
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao inserir produto");
			System.exit(-42);
		}
	}
	
	public static void atualizar() {
		
		try {
			System.out.println("Informe o código do produto: ");
			int codigo = Integer.parseInt(teclado.nextLine());

			Connection conexao = conectar();

			System.out.println("Informe o nome do produto: ");
			String nome = teclado.nextLine();
			
			System.out.println("Informe o preço do produto: ");
			float preco = Float.parseFloat(teclado.nextLine());

			System.out.println("Informe o estoque do produto: ");
			int estoque = Integer.parseInt(teclado.nextLine());
			
			String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
			PreparedStatement update = conexao.prepareStatement(ATUALIZAR);
			update.setString(1, nome);
			update.setFloat(2, preco);
			update.setInt(3, estoque);
			update.setInt(4, codigo);
			
			int resultado = update.executeUpdate(); //Retorna a quantidade de linhas atualizada
			
			if(resultado > 0){
				System.out.println("O produto foi atualizado com sucesso!");
			}else {
				System.out.println("Não foi possível atualizar o produto");
			}
			update.close();
			desconectar(conexao);
			
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao atualizar produto");
		}
	}
	
	public static void deletar() {
		String DELETAR = "DELETE FROM produtos WHERE id=?";
				
		try {
			System.out.println("Informe o código do produto: ");
			int codigo = Integer.parseInt(teclado.nextLine());
			
			Connection conexao = conectar();
			PreparedStatement deletar = conexao.prepareStatement(DELETAR);
			deletar.setInt(1, codigo);
			
			int resultado = deletar.executeUpdate();
			
			if(resultado > 0) {
				System.out.println("Produto excluido com sucesso!");
			}else {
				System.out.println("Não foi possível excluir o Produto com ID " + codigo);
			}
			
			desconectar(conexao);
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao deletar produto");
		}
		
	}
	
	public static void menu() {
		System.out.println("==================Gerenciamento de Produtos===============");
		System.out.println("Selecione uma opção: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Deletar produtos.");
		
		int opcao = Integer.parseInt(teclado.nextLine());
		if(opcao == 1) {
			listar();
		}else if(opcao == 2) {
			inserir();
		}else if(opcao == 3) {
			atualizar();
		}else if(opcao == 4) {
			deletar();
		}else {
			System.out.println("Opção inválida.");
		}
	}
}
