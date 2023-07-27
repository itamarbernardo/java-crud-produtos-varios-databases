package jmysql;

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
		String CLASSE_DRIVE = "com.mysql.cj.jdbc.Driver";
		String USUARIO = "geek";
		String SENHA = "1234";
		String URL_SERVIDOR = "jdbc:mysql://localhost:3306/java_mysql";
		
		try {
			Class.forName(CLASSE_DRIVE);
			return DriverManager.getConnection(URL_SERVIDOR, USUARIO, SENHA);
		}catch(Exception e){
			if(e instanceof ClassNotFoundException) {
				System.out.println("Verifique o Drive de Conexão");
			}else {
				System.out.println("Verifique se o servidor está ativo");
			}
		}
		System.exit(-42); //Se for qualquer outro numero diferente de zero, quer dizer que houve algum erro na execucao
		return null;
	}
	
	public static void desconectar(Connection conexao) {
		if (conexao != null) {
			try {
				conexao.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Não foi possível fechar a conexão");
				e.printStackTrace();
			}
		}
	}
	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos";
		
		try {
			Connection conexao = conectar();
			
			Statement stmt = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultado = stmt.executeQuery(BUSCAR_TODOS);

			/* 
			PreparedStatement produtos = conexao.prepareStatement(BUSCAR_TODOS);
			ResultSet resultado = produtos.executeQuery();
			*/
			
			resultado.last(); //Vai pro final do resultado
			int qnt = resultado.getRow(); //Me retorna o numero da ultima linha -> Indica quantos elementos temos
			
			resultado.beforeFirst(); //Volta para o inicio da consulta
			
			if(qnt > 0) {
				System.out.println("Listando produtos...");
				System.out.println("---------------------");
				
				while(resultado.next()) { //Enquanto houver linhas de resultado -> temos produtos para mostrar
					System.out.println("ID: " + resultado.getInt(1)); //Cada linha do resultado começa na posição 1
					System.out.println("Nome: " + resultado.getString(2));
					System.out.println("Preço: " + resultado.getFloat(3));
					System.out.println("Estoque: " + resultado.getInt(4));
					System.out.println("---------------------");
					
				}
				
			}else {
				System.out.println("Não há produtos cadastrados");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao listar produtos");
			
		}
	}
	
	public static void inserir() {
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());

		System.out.println("Informe o estoque do produto: ");
		int estoque = teclado.nextInt();
		
		//Vamos fazer a string de conexão de forma a evitar SQL Injection
		String INSERIR_DADOS = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";
		
		try {
			Connection conexao = conectar();
			
			PreparedStatement salvar = conexao.prepareStatement(INSERIR_DADOS); 
			//O prepared statement vai proteger o sistema do SQL Injection, 
			//se houver algum codigo malicioso, ele será retirado com as funcoes abaixo
			salvar.setString(1, nome);
			salvar.setFloat(2, preco);
			salvar.setInt(3, estoque);
			
			salvar.executeUpdate();
			desconectar(conexao);			
			System.out.println("O produto " + nome + " foi inserido com sucesso!");
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao inserir produto: ");
			System.exit(-42);
		}
		
	}
	
	public static void atualizar() {
		System.out.println("Informe o código do produto:");
		int codigo = Integer.parseInt(teclado.nextLine());
		
		//Protegendo contra SQL Injection 
		String BUSCA_POR_ID = "SELECT * FROM produtos WHERE id=?";
		
		try {
			Connection conexao = conectar();
			PreparedStatement produto = conexao.prepareStatement(BUSCA_POR_ID);
			produto.setInt(1, codigo);
			ResultSet resultado = produto.executeQuery();
			
			//resultado.last(); //Vai pro final do resultado
			//int qnt = resultado.getRow(); //Me retorna o numero da ultima linha -> Indica quantos elementos temos
			//resultado.beforeFirst(); //Volta para o inicio da consulta

			if(resultado.next()) { //Retorna um boolean se houver algum conteudo em resultado
				System.out.println("Informe o nome do produto: ");
				String nome = teclado.nextLine();
				
				System.out.println("Informe o preço do produto: ");
				float preco = Float.parseFloat(teclado.nextLine());

				System.out.println("Informe o estoque do produto: ");
				int estoque = teclado.nextInt();

				//Protegendo contra SQL Injection 
				String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
				PreparedStatement atualizar = conexao.prepareStatement(ATUALIZAR);
				atualizar.setString(1, nome);
				atualizar.setFloat(2, preco);
				atualizar.setInt(3, estoque);
				atualizar.setInt(4, codigo);
				
				atualizar.executeUpdate();
				atualizar.close();
				desconectar(conexao);
				System.out.println("O produto " + nome + " foi atualizado com sucesso!");
				
			}else {
				System.out.println("Não existe produto com ID informado");
				
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao atualizar produto");
			System.exit(-42);
			
		}
	}
	
	public static void deletar() {
		String DELETAR = "DELETE FROM produtos WHERE id=?";
		String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";
		
		System.out.println("Informe o ID do produto:");
		int codigo = Integer.parseInt(teclado.nextLine());
		
		try {
			Connection conexao = conectar();
			PreparedStatement produto = conexao.prepareStatement(BUSCAR_POR_ID);
			produto.setInt(1, codigo);
			ResultSet resultado = produto.executeQuery();
			
			if(resultado.next()) {
				PreparedStatement deletar = conexao.prepareStatement(DELETAR);
				deletar.setInt(1, codigo);
				deletar.executeUpdate();
				deletar.close();
				
				desconectar(conexao);
				
				System.out.println("O produto foi deletado com sucesso!");
			}else {				
				System.out.println("Não existe produto com ID informado!");
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao deletar produto.");
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
