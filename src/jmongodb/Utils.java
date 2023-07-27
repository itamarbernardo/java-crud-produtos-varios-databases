package jmongodb;

import java.util.Scanner;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Updates.combine; //O Static permite usar diretamente como se fossem funções
import static com.mongodb.client.model.Updates.set;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static MongoCollection<Document> conectar() {
		try {
			MongoClient conexao = new MongoClient("localhost", 27017);
			
			MongoDatabase database = conexao.getDatabase("java_mongo");
			MongoCollection<Document> collection = database.getCollection("produtos");
			
			return collection;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void desconectar() {
		//A própria conexão gerencia isso -> Ela abre quando for usar e fecha quando não tiver mais sendo utilizada
	}
	public static void listar() {
		
		MongoCollection<Document> collection = conectar();
		
		if(collection.countDocuments() > 0) {
			
			MongoCursor<Document> cursor = collection.find().iterator();
			try {
				System.out.println("Listando produtos......");
				System.out.println("-----------------------");
				while(cursor.hasNext()) {
					String json = cursor.next().toJson();
					
					
					JSONObject obj = new JSONObject(json);
					JSONObject id = obj.getJSONObject("_id");
					
					System.out.println("ID: " + id.get("$oid"));
					System.out.println("Nome: " + obj.get("nome"));
					System.out.println("Preço: " + obj.get("preco"));
					System.out.println("Estoque: " + obj.get("estoque"));
					System.out.println("------------------------");
				}
				
				
			}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Erro ao listar produtos");
			}
			cursor.close();
		}else {
			System.out.println("Não existem documentos cadastrados");
		}
	}
	
	public static void inserir() {
		MongoCollection<Document> collection = conectar();
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = Float.parseFloat(teclado.nextLine());
		
		System.out.println("Informe o estoque do produto: ");
		int estoque =  Integer.parseInt(teclado.nextLine());
		
		JSONObject nproduto = new JSONObject();
		nproduto.put("nome", nome);
		nproduto.put("preco", preco);
		nproduto.put("estoque", estoque);
		
		collection.insertOne(Document.parse(nproduto.toString()));
		
		System.out.println("O produto " + nome + " foi inserido com sucesso");
		
	}
	
	public static void atualizar() {
		try {
			MongoCollection<Document> collection = conectar();
			
			System.out.println("Informe o ID do produto: ");
			String _id =  teclado.nextLine();
			
			
			System.out.println("Informe o nome do produto: ");
			String nome = teclado.nextLine();
			
			System.out.println("Informe o preço do produto: ");
			float preco = Float.parseFloat(teclado.nextLine());
			
			System.out.println("Informe o estoque do produto: ");
			int estoque =  Integer.parseInt(teclado.nextLine());
			
			Bson query = combine(set("nome", nome), set("preco", preco), set("estoque", estoque));
			UpdateResult resultado = collection.updateOne(new Document("_id", new ObjectId(_id)), query);
			
			if(resultado.getModifiedCount() == 1) {
				System.out.println("O produto " + nome + " foi atualizado com sucesso!");
			}else {
				System.out.println("Não foi possível atualizar o produto");
			}
		}catch(IllegalArgumentException ex) {
			ex.printStackTrace();
			System.out.println("ID Inválido!");
		}
	}
	
	public static void deletar() {

		try {
			MongoCollection<Document> collection = conectar();
			
			System.out.println("Informe o ID do produto: ");
			String _id =  teclado.nextLine();
			
			DeleteResult resultado = collection.deleteOne(new Document("_id", new ObjectId(_id)));
	
			if(resultado.getDeletedCount() == 1) {
				System.out.println("Produto excluido com sucesso!");
			}else {
				System.out.println("Não foi possível excluir o produto");
			}
		}catch(IllegalArgumentException ex) {
			ex.printStackTrace();
			System.out.println("Este ID não existe");
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
