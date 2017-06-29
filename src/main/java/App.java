import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;


public class App {

    public static void main(String[] args)
    {
        // staticFileLocation("/public");
        // String layout = "templates/layout.vtl";
        //
        // // Home
        // get("/", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/index.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
        // // Login
        // get("/login", (request, response) -> {
        //     Map<String,Object> model = new HashMap<String,Object>();
        //     model.put("template", "templates/login.vtl");
        //     return new ModelAndView(model, layout);
        // }, new VelocityTemplateEngine());
        //
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
