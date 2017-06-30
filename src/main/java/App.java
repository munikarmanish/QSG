import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;


public class App {

    public static void main(String[] args)
    {
        staticFileLocation("/public");
        String layout = "templates/layout.vtl";

        // Home
        get("/", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/index.vtl");
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
            return new ModelAndView(model, layout);
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

            Map<String,Object> model = new HashMap<String,Object>();
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Login
        get("/login", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/login.vtl");
            return new ModelAndView(model, layout);
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
                // TODO
                response.redirect("/message?m=SUCCESS");
            } else {
                // TODO
                response.redirect("/message?m=FAIL");
            }

            Map<String,Object> model = new HashMap<String,Object>();
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // // Add user (for admin)
        // get("/users/add", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/user_add.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Add user submit
        // post("/users", (request, response) -> {
        //     // TODO: process form
        //     // redirect to home
        //     response.redirect("/");
        // }, new VelocityTemplateEngine());
        //
        // // List questions
        // get("/questions", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/question_list.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Question add form
        // get("/questions/add", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/question_add.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Question detail (update form)
        // get("/questions/:qid", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/question_update.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Question submit
        // post("/questions", (request, response) -> {
        //     // TODO: process the form
        //     // redirect to list
        //     response.redirect("/questions");
        // }, new VelocityTemplateEngine());
        //
        // // List interview
        // get("/interviews", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/interview_list.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Interview add
        // get("/interviews/add", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/interview_add.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Interview detail
        // get("/interviews/:id", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/interview_detail.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
    }
}
