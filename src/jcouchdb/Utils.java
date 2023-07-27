package jcouchdb;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static HttpClient conectar() {
		HttpClient conexao = HttpClient.newBuilder().build();
		
		//Retorna apenas um cliente HTTP
		return conexao;
	}
	
	public static void desconectar() {
		//Não vamos precisar
	}
	
	private static String getAuthenticationHeader(String username, String password) {
	    String auth = username + ":" + password;
	    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
	}
	
	public static void listar() {
		HttpClient conexao = conectar();
		
		//Requisicao HTTP para retornar todos os documentos no banco de dados java_couch
		String link = "http://localhost:5984/java_couch/_all_docs?include_docs=true";
		
		//Por padrão, a requisição é GET -> Por isso não precisamos colocar
		HttpRequest requisicao = HttpRequest.newBuilder().uri(URI.create(link))
				.header("Authorization", getAuthenticationHeader("admin", "1234"))
				.build();
		
		try {
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			JSONObject obj = new JSONObject(resposta.body());
			
			if( (int) obj.get("total_rows") > 0) {
				JSONArray produtos = (JSONArray) obj.get("rows");

				System.out.println("Listando produtos.......");
				System.out.println("------------------------");
				
				for(Object produto : produtos) {
					JSONObject doc = (JSONObject) produto;
					JSONObject prod = (JSONObject) doc.get("doc");
					
					System.out.println("ID: " + prod.get("_id"));
					System.out.println("Rev: " + prod.get("_rev"));
					System.out.println("Nome: " + prod.get("nome"));
					System.out.println("Preço: " + prod.get("preco"));
					System.out.println("Estoque: " + prod.get("estoque"));
					System.out.println("---------------------------");
					
				}
			}else {
				System.out.println("Não existem produtos cadastrados");
			}
		}catch(IOException ex) {
			ex.printStackTrace();
			System.err.println("Houve um erro durante a conexão");
		}catch(InterruptedException ex) {
			ex.printStackTrace();
			System.err.println("Houve um erro durante a conexão");

		}
	}
	
	public static void inserir() {
		HttpClient conexao = conectar();
		
		String link = "http://localhost:5984/java_couch";
		
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
				.header("Authorization", getAuthenticationHeader("admin", "1234"))
				.build();
		try{
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 201) {
				System.out.println("Produto " + nome + " foi criado com sucesso!");
			}else {
				System.out.println(obj);
				System.out.println("Não foi possível inserir o produto: Status Code: " + resposta.statusCode());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Houve um erro durante a conexao");
		}
	}
	
	public static void atualizar() {
		HttpClient conexao = conectar();
		
		System.out.println("Informe o ID do produto: ");
		String id = teclado.nextLine();
		
		System.out.println("Informe a Rev (Revisão) do produto: ");
		String rev = teclado.nextLine();
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto: ");
		int estoque = Integer.parseInt(teclado.nextLine());

		String link = "http://localhost:5984/java_couch/" + id + "?rev=" + rev;
		
		JSONObject nproduto = new JSONObject();
		nproduto.put("nome", nome);
		nproduto.put("preco", preco);
		nproduto.put("estoque", estoque);
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.PUT(BodyPublishers.ofString(nproduto.toString()))
				.header("Authorization", getAuthenticationHeader("admin", "1234"))
				.header("Content-Type", "application/json")
				.build();
		
		try {
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 201) {
				System.out.println("O produto " + nome + " foi atualizado com sucesso!");
			}else if(resposta.statusCode() == 400){
				System.out.println("Não existe produto com ID informado!");				
			}else {
				System.out.println(obj);
				System.out.println("Não foi possível atualizar o produto: Status Code: " + resposta.statusCode());
			}
			
		}catch(Exception ex) {
			System.err.println("Houve um erro durante a conexao");
			ex.printStackTrace();
		}
	}
	
	public static void deletar() {
		HttpClient conexao = conectar();
		
		System.out.println("Informe o ID do produto: ");
		String id = teclado.nextLine();
		
		System.out.println("Informe a Rev (Revisão) do produto: ");
		String rev = teclado.nextLine();

		//O Link é igual ao de atualizar, o que muda é o tipo de Requisição 
		String link = "http://localhost:5984/java_couch/" + id + "?rev=" + rev;
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.DELETE()
				.header("Authorization", getAuthenticationHeader("admin", "1234"))
				.build();
		
		try {
			HttpResponse<String> resposta = conexao.send(requisicao, BodyHandlers.ofString());

			if(resposta.statusCode() == 200) {
				System.out.println("O produto foi deletado com sucesso!");
			}else if(resposta.statusCode() == 404){
				System.out.println("Não existe produto com ID informado!");				
			}
			else {
				System.out.println("Não foi possível deletar o produto: Status Code: " + resposta.statusCode());
			}
			
			
		}catch(Exception ex) {
			System.err.println("Houve um erro durante a conexao");
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
