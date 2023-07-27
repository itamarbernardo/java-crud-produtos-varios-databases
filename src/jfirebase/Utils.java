package jfirebase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;

import org.json.JSONObject;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static HttpClient conectar() {
		HttpClient conexao = HttpClient.newBuilder().build();
		
		return conexao;
	}
	
	public static void desconectar() {
		//Não precisa
	}
	public static void listar() {
		HttpClient conexao = conectar();
		
		String link = "https://java-firebase-33-default-rtdb.firebaseio.com/produtos.json";
		
		HttpRequest requisicao = HttpRequest.newBuilder().uri(URI.create(link)).build();
		
		try {
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			if(resposta.body().equals("null")) {
				System.out.println("Não existem produtos cadastrados");
				
			}else {
				JSONObject obj = new JSONObject(resposta.body()); //Transforma a String para o formato JSON

				System.out.println("Listando produtos.....");
				System.out.println("----------------------");
				for(int i=0; i < obj.length(); i++) {
					JSONObject prod = (JSONObject) obj.get(obj.names().getString(i));  //Pegamos o objeto pela chave
					System.out.println("ID: " + obj.names().getString(i));
					System.out.println("Nome: " + prod.get("nome"));
					System.out.println("Preço: " + prod.get("preco"));
					System.out.println("Estoque: " + prod.get("estoque"));
					System.out.println("----------------------------");					
				}
			}
		}catch(Exception ex) {
			System.err.println("Houve um erro na conexão");
			ex.printStackTrace();
		}
	}
	
	public static void inserir() {
		HttpClient conexao = conectar();
		
		String link = "https://java-firebase-33-default-rtdb.firebaseio.com/produtos.json";
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto: ");
		int estoque = Integer.parseInt(teclado.nextLine());

		JSONObject nproduto = new JSONObject();
		nproduto.put("nome", nome);
		nproduto.put("preco", preco);
		nproduto.put("estoque", estoque);
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.POST(BodyPublishers.ofString(nproduto.toString()))
				.header("Content-Type", "application/json")
				.build();
		
		try {
			
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 200) {
				System.out.println("O produto " + nome + " foi cadastrado com sucesso!");
			}else {
				System.out.println(obj);				
				System.out.println("Não foi possível inserir o produto. Status code: " + resposta.statusCode());
			}
			
		}catch(Exception ex) {
			System.err.println("Houve um erro na conexão");
			ex.printStackTrace();
		}
	}
	
	public static void atualizar() {
		HttpClient conexao = conectar();
				
		System.out.println("Informe o ID do produto: ");
		String id = teclado.nextLine();

		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto: ");
		int estoque = Integer.parseInt(teclado.nextLine());

		String link = "https://java-firebase-33-default-rtdb.firebaseio.com/produtos/" + id + ".json";

		JSONObject nproduto = new JSONObject();
		nproduto.put("nome", nome);
		nproduto.put("preco", preco);
		nproduto.put("estoque", estoque);
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.PUT(BodyPublishers.ofString(nproduto.toString()))
				.header("Content-Type", "application/json")
				.build();
		
		try {
			
			if(existeProduto(id)) {
							
				HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
				
				JSONObject obj = new JSONObject(resposta.body());
				
				if(resposta.statusCode() == 200) {
					System.out.println("O produto " + nome + " foi atualizado com sucesso!");
					//Se não tiver um objeto com esse ID, ele vai criar um novo
					System.out.println(resposta.body());
					
				}else {
					System.out.println(obj);				
					System.out.println("Não foi possível atualizar o produto. Status code: " + resposta.statusCode());
				}
			}else {
				System.out.println("O produto com Id informado '" + id + "'não existe!");
			}

		}catch(Exception ex) {
			System.err.println("Houve um erro com a conexão");
			ex.printStackTrace();
		}
	}
	
	public static boolean existeProduto(String id) {
		HttpClient conexao = conectar();
		
		String link = "https://java-firebase-33-default-rtdb.firebaseio.com/produtos/" + id + ".json";

		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.build();
				
		try {
			
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			if(resposta.statusCode() == 200 && !resposta.body().equals("null")) {
				System.out.println("O produto Existe");
				System.out.println(resposta.body());

				return true;
			}else {
				System.out.println("Não existe produto com ID informado: " + id + " Status Code: " + resposta.statusCode());
				return false;
			}
			
		}catch(Exception ex) {
			System.err.println("Houve um erro com a conexão");
			ex.printStackTrace();
		}
		return false;

	}
	public static void deletar() {
		HttpClient conexao = conectar();
		
		System.out.println("Informe o ID do produto: ");
		String id = teclado.nextLine();

		String link = "https://java-firebase-33-default-rtdb.firebaseio.com/produtos/" + id + ".json";

		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.DELETE()
				.header("Content-Type", "application/json")
				.build();
		
		try {
			
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			if(resposta.statusCode() == 200 && !resposta.body().equals("null")) {
				System.out.println("O produto foi Deletado com sucesso");
				System.out.println(resposta.body());
			}else {
				System.out.println("Não existe produto com ID informado: " + id + " Status Code: " + resposta.statusCode());
			}
			
		}catch(Exception ex) {
			System.err.println("Houve um erro com a conexão");
			ex.printStackTrace();
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
