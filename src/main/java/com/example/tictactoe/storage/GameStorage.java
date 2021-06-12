package com.example.tictactoe.storage;

import com.example.tictactoe.model.Game;
import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class GameStorage {
    private static GameStorage instance;
    private MongoClient mongoClient;


    private GameStorage(){}

    public static synchronized GameStorage getInstance(){
        if(instance == null){
            instance = new GameStorage();
        }

        return instance;
    }

    public Game getGame(String gameId)  {
        MongoCollection<Document> gamesCollection = this.getMongoDbGameCollection();
        Document doc = gamesCollection.find(eq("gameId",gameId)).first();

        if(doc == null){
            String gameNotFoundMessage = "Game with id " + gameId + " not found";
            System.out.println(gameNotFoundMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameNotFoundMessage);
        }

        mongoClient.close();
        Game game = this.mapGameDocumentToObject(doc);
        return game;
    }

    public ArrayList<Game> getGames(){
        MongoCollection<Document> gamesCollection = this.getMongoDbGameCollection();
        ArrayList<Game> gamesList = new ArrayList<>();

        try (MongoCursor<Document> cursor = gamesCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Game game = this.mapGameDocumentToObject(doc);
                gamesList.add(game);
            }
        }
        mongoClient.close();

        return gamesList;
    }


    public void createGame(Game game){
        Document gameDocument = this.mapGameObjectToDocument(game);
        MongoCollection<Document> gamesCollection = this.getMongoDbGameCollection();
        gamesCollection.insertOne(gameDocument);

        mongoClient.close();
    }


    public void patchGame(Game game){
        MongoCollection<Document> gamesCollection = this.getMongoDbGameCollection();
        Document gameDoc = this.mapGameObjectToDocument(game);
        gamesCollection.replaceOne(eq("gameId",game.getGameId()), gameDoc);

        mongoClient.close();
    }

    private MongoCollection<Document> getMongoDbGameCollection() {
        ConnectionString connString = new ConnectionString(
                System.getenv("MONGO_DB_CLUSTER_KEY")
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();

        mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("tic-tac-toe");
     return  database.getCollection("game");
    }

    private Game mapGameDocumentToObject(Document gameDocument){
        Gson gson = new Gson();
        Game model = gson.fromJson(gameDocument.toJson(), Game.class);
        return model;
    }

    private Document mapGameObjectToDocument(Game game){
        BasicDBObject dbGameObject  = new BasicDBObject("gameId",game.getGameId())
                .append("player1",game.getPlayer1())
                .append("player2",game.getPlayer2())
                .append("status",game.getStatus().toString())
                .append("board",game.getBoard())
                .append("winner",game.getWinner());


        String gameString = dbGameObject.toJson();
        Document gameDocument = Document.parse(gameString);

        return gameDocument;
    }
}
