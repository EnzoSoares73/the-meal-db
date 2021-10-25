package com.topi.themealdb.servico;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topi.themealdb.modelo.Meal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MealServico {

    private final String ENDERECO_API = "https://www.themealdb.com/api/json/v1/";
    private final String CHAVE_API = "1";
    private final String PROCURAR_POR_NOME = "/search.php?f=";
    private final String PROCURAR_POR_ID = "/lookup.php?i=";
    private final int NUMERO_INGREDIENTES = 20;
    private final String VAR_INGREDIENTE = "strIngredient";
    private final String VAR_MEDIDA = "strMeasure";

    private HttpURLConnection conexao;

    private StringBuilder conectar(URL url) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.connect();

        int responsecode = conexao.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {
            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            scanner.close();
            return inline;
        }
    }

    public List<Meal> retornaTodasMeals() throws IOException {
        List<Meal> listaMeals = new ArrayList<>();
        for(char alphabet = 'a'; alphabet <='z'; alphabet++) {
            URL url = new URL(ENDERECO_API + CHAVE_API + PROCURAR_POR_NOME + alphabet);
            if (jsonParaListaMeals(conectar(url)) != null) {
                listaMeals.addAll(Objects.requireNonNull(jsonParaListaMeals(conectar(url))));
            }
        }
        return listaMeals;
    }

    public Meal obterMealPorId(int id) throws IOException {
        URL url = new URL(ENDERECO_API + CHAVE_API + PROCURAR_POR_ID + id);
        return Objects.requireNonNull(jsonParaListaMeals(conectar(url))).get(0);
    }

    private List<Meal> jsonParaListaMeals(StringBuilder inline) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(String.valueOf(inline));

            ObjectMapper mapper = new ObjectMapper();
            List<Meal> meals = mapper.readValue(obj.get("meals").toString(), new TypeReference<List<Meal>>() {
            });
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

        } catch (ParseException | JsonProcessingException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
