package com.topi.themealdb.servico;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topi.themealdb.modelo.Meal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MealServico {

    public final String ENDERECO_API = "https://www.themealdb.com/api/json/v1/";
    public final String CHAVE_API = "1";
    public final String PROCURAR_POR_NOME = "/search.php?s=";
    public final int NUMERO_INGREDIENTES = 20;
    public final String VAR_INGREDIENTE = "strIngredient";
    public final String VAR_MEDIDA = "strMeasure";

    public List<Meal> retornaTodasMeals() throws IOException {
        URL url = new URL(ENDERECO_API + CHAVE_API + PROCURAR_POR_NOME);

        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.connect();

        int responsecode = conexao.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            //Close the scanner
            scanner.close();

            try {
                JSONObject obj = (JSONObject) new JSONParser().parse(String.valueOf(inline));

                ObjectMapper mapper = new ObjectMapper();
                List<Meal> meals = mapper.readValue(obj.get("meals").toString(), new TypeReference<List<Meal>>(){});
                JSONArray jsonArray = (JSONArray) obj.get("meals");

                for (int i = 1; i < meals.size(); i++) {
                    //Converte o atributo strTags do json para uma lista tags dentro do objeto
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.get("strTags") != null) {
                        String tags = jsonObject.get("strTags").toString();
                        tags = tags.replace(" ", "");
                        meals.get(i).setTags(tags.split(","));
                    }

                    //converte os atributos strIngredientx e strMeasurex em um hashmap<strIngredientx, strMeasurex>
                    for (int j = 1; j <= NUMERO_INGREDIENTES; j++) {
                        if ((jsonObject.get(VAR_INGREDIENTE + j) != null && jsonObject.get(VAR_MEDIDA + j) != null) &&
                            (!jsonObject.get(VAR_INGREDIENTE + j).toString().isEmpty() && !jsonObject.get(VAR_MEDIDA + j).toString().isEmpty())) {
                            meals.get(i).getIngredients_amount().put(jsonObject.get(VAR_INGREDIENTE + j).toString(), jsonObject.get(VAR_MEDIDA + j).toString());
                        } else {
                            break;
                        }
                    }
                }

                return meals;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
