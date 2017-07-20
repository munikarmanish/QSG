
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;


public class App {

    public static final int QUESTIONS_PER_PAGE = 10;

    public static void main(String[] args)
    {
        staticFileLocation("/public");
        String layout = "templates/layout.vtl";
        String layout_signinup = "templates/layout_signinup.vtl";

        // Home
        get("/", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            if (request.session().attribute("userId") == null) {
                response.redirect("/login");
            } else {
                response.redirect("/admin");
            }
            model.put("template", "templates/index.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        //Dashboard
        get("/admin", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();


            model.put("template", "templates/admin.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Message
        get("/message", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/message.vtl");
            model.put("message", request.queryParams("m"));
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // register
        get("/register", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/register.vtl");
            return new ModelAndView(model, layout_signinup);
        }, new VelocityTemplateEngine());

        // register submit
        post("/register", (request, response) -> {
            String name = request.queryParams("name");
            String email = request.queryParams("email");
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            User u = new User(email, username, password, name).save();

            if (u == null) {
                response.redirect("/message?m=ERROR");
            }

            response.redirect("/login");
            return 0;
        });

        // Login
        get("/login", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/login.vtl");
            return new ModelAndView(model, layout_signinup);
        }, new VelocityTemplateEngine());

        // login submit
        post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            User u = User.findByUsername(username);

            if (u == null) {
                response.redirect("/message?m=USERNAME_NOT_FOUND");
            }

            if (u.checkPassword(password)) {
                request.session().attribute("userId", u.getId());
                response.redirect("/message?m=LOGIN+SUCCESS+" + username);
            } else {
                // TODO
                response.redirect("/message?m=LOGIN+FAIL");
            }

            return 0;
        });

        // Logout
        get("/logout", (request, response) -> {
            if (request.session().attribute("userId") == null) {
                response.redirect("/message?m=NOT+LOGGED+IN");
            } else {
                request.session().removeAttribute("userId");
                response.redirect("/login");
            }
            return 0;
        });

        // List questions
        get("/questions", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_list.vtl");

            int page = 1;
            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            int start = (page - 1) * QUESTIONS_PER_PAGE;
            List<Question> questions = Question.limit(start, QUESTIONS_PER_PAGE);

            model.put("questions", questions);
            model.put("currentPage", page);
            model.put("prevPage", page-1);
            model.put("nextPage", page+1);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Add question
        get("/questions/add", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_add_form.vtl");
            model.put("categories", Category.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Submit question
        post("/questions", (request, response) -> {
            int categoryId = Integer.parseInt(request.queryParams("category"));
            String question = request.queryParams("question");
            String answer1 = request.queryParams("answer1");
            String answer2 = request.queryParams("answer2");
            String answer3 = request.queryParams("answer3");
            String answer4 = request.queryParams("answer4");
            int difficulty = Integer.parseInt(request.queryParams("difficulty"));

            // userId is 0 = NULL for now
            Question q = new Question(0, categoryId, question, difficulty).save();
            Answer a;
            a = new Answer(q, answer1, true).save();
            a = new Answer(q, answer2, false).save();
            a = new Answer(q, answer3, false).save();
            a = new Answer(q, answer4, false).save();

            response.redirect("/questions");
            return 0;
        });

        // delete question
        post("/questions/:qid/delete", (request, response) -> {
            int id = Integer.parseInt(request.params(":qid"));
            Question q = Question.findById(id);
            q.delete();
            response.redirect("/questions");
            return 0;
        });

        // edit question
        get("/questions/:qid/edit", (request, response) -> {
            Question question = Question
                .findById(Integer.parseInt(request.params(":qid")));

            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_edit_form.vtl");
            model.put("categories", Category.all());
            model.put("question", question);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // update question
        post("/questions/:qid/edit", (request, response) -> {
            Question q = Question.findById(Integer.parseInt(request.params(":qid")));

            int categoryId = Integer.parseInt(request.queryParams("category"));
            String text = request.queryParams("question");
            String answer1 = request.queryParams("answer1");
            String answer2 = request.queryParams("answer2");
            String answer3 = request.queryParams("answer3");
            String answer4 = request.queryParams("answer4");
            int difficulty = Integer.parseInt(request.queryParams("difficulty"));

            String sql;
            try (Connection con = DB.sql2o.open()) {
                sql = "UPDATE questions SET categoryId=:categoryId, text=:text, difficulty=:difficulty WHERE id=:id";
                con.createQuery(sql)
                    .addParameter("categoryId", categoryId)
                    .addParameter("text", text)
                    .addParameter("difficulty", difficulty)
                    .addParameter("id", q.getId())
                    .executeUpdate();
            }

            // delete old answers and add new answers
            for (Answer a : q.getAnswers()) {
                a.delete();
            }
            q.addAnswer(answer1, true);
            q.addAnswer(answer2, false);
            q.addAnswer(answer3, false);
            q.addAnswer(answer4, false);

            response.redirect("/questions/" + q.getId() + "/edit");
            return 0;
        });

        //  List category
        get("/categories", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            List<Category> categories_list = new ArrayList();
            categories_list = Category.all();

            model.put("template", "templates/category_list.vtl");
            model.put("categories", categories_list);
            // System.out.println(categories_list);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // // Category submit
        post("/categories",(request, response) -> {
            String category_name = request.queryParams("category_name");
            Category obj = new Category(category_name);
            obj.save();
            response.redirect("/categories");
            return "Success";
        });

        post("/categories/:cId/delete", (request, response) ->{
            Category category = Category.findById(Integer.parseInt(request.params(":cId")));
            category.delete();
            response.redirect("/categories");
            return "Success";
        });

    }
}
