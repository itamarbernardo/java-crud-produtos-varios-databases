package jredis;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static Jedis conectar() {
		Jedis conexao = new Jedis("localhost", 6379);
		return conexao;
	}
	
	public static void desconectar(Jedis conexao) {
		conexao.disconnect();
	}
	
	public static void listar() {
		Jedis conexao = conectar();
		
		try {
			Set<String> resultado = conexao.keys("produtos:*");
			
			if(resultado.size() > 0) {
				//Temos algum produto
				System.out.println("Listando produtos.....");
				System.out.println("----------------------");
				for(String chave : resultado) {
					//Map porque temos um conjunto de chave-valor
					Map<String, String> produto = conexao.hgetAll(chave);
					System.out.println("Chave: " + chave);
					System.out.println("Produto: " + produto.get("nome"));
					System.out.println("Preço: " + produto.get("preco"));
					System.out.println("Estoque: " + produto.get("estoque"));
					System.out.println("---------------------------");
					
						
				}
					
			}else {
				System.out.println("Não existem produtos cadastrados");
			}
		}catch(JedisConnectionException ex) {
			ex.printStackTrace();
			System.out.println("Verifique se o servidor Redis está ativo: " + ex.getMessage());
			
		}
	}
	
	public static void inserir() {
		
		Jedis conexao = conectar();
		
		System.out.println("Informe o nome do produto");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto");
		int estoque = Integer.parseInt(teclado.nextLine());
		
		Map<String, String> produto = new HashMap<String, String>();
		produto.put("nome", nome);
		produto.put("preco", preco+"");
		produto.put("estoque", estoque+"");
		
		String chave = "produtos:" + gerar_id();
		
		try {
			//hmset = multiplos sets no hash
			String resultado = conexao.hmset(chave, produto);
			
			if(resultado != null) {
				System.out.println("Produto inserido com sucesso!");
			}else {
				System.out.println("Produto não inserido!");
			}
		}catch(JedisConnectionException ex) {
			ex.printStackTrace();
			System.err.println("Verifique se o Redis está ativo: " + ex.getMessage());
		}
		
		desconectar(conexao);
	}
	
	public static String gerar_id() {
		Jedis conexao = conectar();
		
		
		String chave = conexao.get("chave");
		
		if(chave != null) {
			//Essa chave existe
			chave = conexao.incr("chave") + "";
			
			desconectar(conexao);
			return chave;
		}else {
			//A chave ainda não existe
			conexao.set("chave", "1");
			desconectar(conexao);
			return "1";
		}
		
	}
	
	
	public static void atualizar() {
		Jedis conexao = conectar();
		
		System.out.println("Informe a chave do produto");
		String chave = teclado.nextLine();
		
		System.out.println("Informe o nome do produto");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto");
		String preco = teclado.nextLine();
		
		System.out.println("Informe o estoque do produto");
		String estoque = teclado.nextLine();
		
		Map<String, String> produto = new HashMap<String, String>();
		produto.put("nome", nome);
		produto.put("preco", preco);
		produto.put("estoque", estoque);

		try {
			//String existeProduto = conexao.get(chave);
			if( conexao.exists(chave) ) { 
				//Verifica se essa chave já existe no Redis
				
				//hmset = multiplos sets no hash
				String resultado = conexao.hmset(chave, produto);
				
				if(resultado != null) {
					System.out.println("Produto atualizado com sucesso!");
				}else {
					System.out.println("Produto não atualizado!");
				}
			}
			else {
				System.out.println("Chave inválida! Não existe um produto com essa chave!");
			}
		}catch(JedisConnectionException ex) {
			ex.printStackTrace();
			System.err.println("Verifique se o Redis está ativo: " + ex.getMessage());
		}
		
		desconectar(conexao);
	}
	
	public static void deletar() {
		Jedis conexao = conectar();
		
		System.out.println("Informe a chave do produto");
		String chave = teclado.nextLine();
		
		try {
			long delete = conexao.del(chave);
			
			if(delete > 0) {
				System.out.println("Produto deletado com sucesso!");
			}else {
				System.out.println("Não existe produto com a chave informada!");
			}
		}catch(JedisConnectionException ex) {
			ex.printStackTrace();
			System.err.println("Verifique a conexão do Redis: " + ex.getMessage());
		}
		desconectar(conexao);
		
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
