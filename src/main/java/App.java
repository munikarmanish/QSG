
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;
import java.sql.Timestamp;
import java.util.Date;
import java.time.LocalDate;
import java.text.DateFormat;
import java.util.Random;
import java.util.Arrays;


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
            int start = (page - 1) * 20;
            List<Question> questions = Question.limit(start, QUESTIONS_PER_PAGE);

            model.put("questions", questions);
            model.put("currentPage", page);
            model.put("prevPage", page-1);
            model.put("nextPage", page+1);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // List questions setwise
        get("/questions_set", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_sets.vtl");
            int page = 1;
            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            int difficulty = Integer.parseInt(request.queryParams("difficulty"));
            int duration = Integer.parseInt(request.queryParams("duration"));
            List<Question> questions = Question.limitset(duration, difficulty);
            model.put("questions", questions);
            model.put("currentPage", page);
            model.put("prevPage", page-1);
            model.put("nextPage", page+1);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        


        post("/save_set", (request, response) -> {
        	String str = request.queryParams("questionid[]");
            int[] qids = Arrays.stream(str.substring(1, str.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        	String str1 = request.queryParams("questionnum[]");
            int[] qnums = Arrays.stream(str1.substring(1, str1.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        	int len = qids.length;
        	int newid= Set.getsetId();
            newid=newid-2; 
        	for (int y=0; y<3;y++) 
        	{
            for(int z=0; z<len; z++) 
            {
            Set.saveSet(newid, qids[z], qnums[z], 1 ); //(setId, questionId, questionNumber, correctIndex)
            }
            ++newid;
            }
            response.redirect("/interview_selector");
            return 0;
        });


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
        
        //Interview selector
        get("/interview_selector", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/interview_selector.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
       
        
        post("/interview_selector", (request, response) -> {       
            String title= request.queryParams("title");
            // time stamp format is yyyy-mm-dd hh:mm:ss
            // datetime-local is 2017-06-01T08:30
            String dateString= request.queryParams("time");
            String dateModified = dateString.replace( "T" , " " );
            String fulldateString= dateModified +":00";
            Timestamp ts = Timestamp.valueOf(fulldateString);
            int difficulty = Integer.parseInt(request.queryParams("difficulty"));
            int duration = Integer.parseInt(request.queryParams("duration"));
            String category = request.queryParams("category");
            Interview i = new Interview(0, title, ts, duration).save(); //userid is 0 for now
            for(int sn=1; sn<=3; sn++)
        	{
            int intervid= Interview.getinterviewId();
        	Set s = new Set(intervid,sn).save(); //Set(int interviewId, int set)
        	}
        	int newid= Set.getsetId();
            if (i == null) {
                response.redirect("/message?m=ERROR");
            }
            String address="/questions_set?difficulty="+ difficulty +"&duration="+ duration+"&setId="+ newid;
            response.redirect(address);
            Map<String,Object> model = new HashMap<String,Object>();
            return new ModelAndView(model, layout_signinup);
        }, new VelocityTemplateEngine());

        //  List interview
        get("/interviews", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();Interview.all();
            model.put("template", "templates/interview_list.vtl");
            model.put("interviews", Interview.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        //delete interview
        post("/interviews/:iId/delete", (request, response) ->{
            Interview interview = Interview.findById(Integer.parseInt(request.params(":iId")));
            interview.delete();
            response.redirect("/interviews");
            return "Success";
        });    



      //  List category
        get("/categories", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();Category.all();
            model.put("template", "templates/category_list.vtl");
            model.put("categories", Category.all());
            // System.out.println(categories_list);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // // Category submit
        post("/categories",(request, response) -> {
            String category_name = request.queryParams("category_name");
            Category c = new Category(category_name).save();
            response.redirect("/categories");
            return "Success";
        });

        post("/categories/:cId/delete", (request, response) ->{
            Category category = Category.findById(Integer.parseInt(request.params(":cId")));
            category.delete();
            response.redirect("/categories");
            return "Success";
        });    }
}
