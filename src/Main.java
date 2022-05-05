import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import org.json.*;

public class Main {
    static String[] questions;
    static String[] answers;
    static int n = 4;

    public static void main(String[] args){
        try {
            preparingForGame();
            startGame();
        } catch (Exception e) {
            System.out.println("Ошибка во время загрузки");
        }
    }

    private static void startGame() {
        int correct_answers = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Здравствуйте, у меня есть для вас несколько вопросов. Сыграем? (yes/no)");
        String ans = sc.nextLine();
        if (!ans.equalsIgnoreCase("yes")){
            System.out.println("Ну и ладно, всего доброго!)");
            return;
        }
        for (int i = 0; i < n; i++){
            System.out.println("Вопрос №" + (i + 1));
            System.out.println(questions[i]);
            ans = sc.nextLine();
            if (ans.equalsIgnoreCase(answers[i]))
                correct_answers += 1;
        }
        System.out.println("Игра окончена. Вы ответили верно на "
                            + correct_answers + " из " + n
                            + " вопросов. Спасибо за игру");
    }

    public static void preparingForGame() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://jservice.io/api/random?count=" + n)).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        JSONArray array = new JSONArray(jsonString);
        questions = new String[n];
        answers = new String[n];
        for (int i = 0; i < n; i++){
            JSONObject obj = array.getJSONObject(i);
            answers[i] = translate((String) obj.get("answer"));
            questions[i] = translate((String) obj.get("question"));
        }
    }

    public static String translate(String word) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "application/gzip")
                .header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .header("X-RapidAPI-Key", "0e0345dbc8msh8a0de13a6e012d1p1c4379jsn3e8c02db7303")
                .method("POST", HttpRequest.BodyPublishers.ofString("q=" + word + "&target=ru&source=en"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject obj = new JSONObject(response.body());
        obj = (JSONObject) obj.get("data");
        JSONArray arr = (JSONArray) obj.get("translations");
        obj = (JSONObject) arr.get(0);
        return (String) obj.get("translatedText");
    }
}