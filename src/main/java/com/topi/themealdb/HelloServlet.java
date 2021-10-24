package com.topi.themealdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topi.themealdb.modelos.Meal;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        URL url = new URL("https://www.themealdb.com/api/json/v1/1/search.php?s=");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responsecode = conn.getResponseCode();


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

                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h1>" + meals.size() + "</h1>");
                out.println("</body></html>");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
    }
}