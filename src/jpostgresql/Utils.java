package jpostgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static Connection conectar() {
		Properties props = new Properties();
		props.setProperty("user", "geek");
		props.setProperty("password", "1234");
		props.setProperty("ssl", "false");

		String URL_SERVIDOR = "jdbc:postgresql://localhost:5432/java_postgre";
		try {
			return DriverManager.getConnection(URL_SERVIDOR, props);
		}catch(Exception ex) {
			ex.printStackTrace();
			if(ex instanceof ClassNotFoundException) {
				System.err.println("Verifique o drive de conexão");
			}else {
				System.err.println("Verifique se o servidor está ativo");
			}
		}
		return null;
	}
	
	public static void desconectar(Connection conexao) {
		if(conexao != null) {
			try {
				conexao.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos";
		
		try {
			Connection conexao = conectar();
			PreparedStatement produtos = conexao.prepareStatement(BUSCAR_TODOS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultado = produtos.executeQuery();
			
			resultado.last();
			int qnt = resultado.getRow();
			resultado.beforeFirst();
			
			if(qnt > 0) {
				System.out.println("Listando produtos.....");
				System.out.println("----------------------");
				while(resultado.next()) { //Enquanto houver linhas de resultado -> temos produtos para mostrar
					System.out.println("ID: " + resultado.getInt(1)); //Cada linha do resultado começa na posição 1
					System.out.println("Nome: " + resultado.getString(2));
					System.out.println("Preço: " + resultado.getFloat(3));
					System.out.println("Estoque: " + resultado.getInt(4));
					System.out.println("---------------------");
				}
			}else {
				System.out.println("Não existem produtos cadastrados");
			}
			
			}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao buscar todos os produtos");
			System.exit(-42);
		}
	}
	
	public static void inserir() {
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto: ");
		int estoque = teclado.nextInt();
		
		String INSERIR = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";
		
		try {
			Connection conexao = conectar();
			PreparedStatement salvar = conexao.prepareStatement(INSERIR);
			//Proteção contra SQL Injection
			salvar.setString(1, nome); //Começa do 1
			salvar.setFloat(2, preco); //Começa do 1
			salvar.setInt(3, estoque); //Começa do 1
			
			salvar.executeUpdate();
			salvar.close();
			desconectar(conexao);
			
			System.out.println("O produto " + nome + " foi inserido com sucesso!");
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao salvar produto");
			System.exit(-42);
		}
	}
	
	public static void atualizar() {
		System.out.println("Informe o código do produto:");
		int codigo = Integer.parseInt(teclado.nextLine());
		
		String BUSCA_POR_ID = "SELECT * FROM produtos WHERE id=?";
		
		try {
			Connection conexao = conectar();
			PreparedStatement produto = conexao.prepareStatement(BUSCA_POR_ID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			produto.setInt(1, codigo);
			
			ResultSet resultado = produto.executeQuery();
			resultado.last();
			int qnt = resultado.getRow();
			resultado.beforeFirst();
			
			if(qnt > 0) {
				System.out.println("Informe o nome do produto: ");
				String nome = teclado.nextLine();
				
				System.out.println("Informe o preço do produto: ");
				float preco = Float.parseFloat(teclado.nextLine());
				
				System.out.println("Informe o estoque do produto: ");
				int estoque = teclado.nextInt();
				
				String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
				PreparedStatement atualizar = conexao.prepareStatement(ATUALIZAR);
				atualizar.setString(1, nome);
				atualizar.setFloat(2, preco);
				atualizar.setInt(3, estoque);
				atualizar.setInt(4, codigo);
				
				atualizar.executeUpdate();
				atualizar.close();
				
				desconectar(conexao);
				System.out.println("O produto" + nome + " foi atualizado com sucesso!");
			}else {
				System.out.println("Produto não encontrado!");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao atualizar produto");
			System.exit(-42);
		}
	}
	
	public static void deletar() {
		String DELETAR = "DELETE FROM produtos WHERE id=?";
		String BUSCA_POR_ID = "SELECT * FROM produtos WHERE id=?";

		System.out.println("Informe o código do produto: ");
		int codigo = Integer.parseInt(teclado.nextLine());
		
		try {
			Connection conexao = conectar();
			PreparedStatement produto = conexao.prepareStatement(BUSCA_POR_ID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			produto.setInt(1, codigo);
			ResultSet resultado = produto.executeQuery();
			
			resultado.last();
			int qnt = resultado.getRow();
			resultado.beforeFirst();
			
			if(qnt > 0) {
				PreparedStatement deletar = conexao.prepareStatement(DELETAR);
				deletar.setInt(1, codigo);
				deletar.executeUpdate();
				deletar.close();
				desconectar(conexao);
				System.out.println("Produto deletado com sucesso!");
			}else {
				System.out.println("Não existe produto com o ID informado");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Erro ao deletar produto");
			System.exit(-42);
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
