
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.*;
import org.sql2o.*;
import java.sql.Timestamp;
import org.apache.commons.lang.StringUtils;


/**
 * The controller class for the web application. It handles all the routes.
 *
 * @since 2017-08-12
 */
public class App {

    /** Number of questions per page to show in question list page */
    public static final Integer QUESTIONS_PER_PAGE = 10;

    public static void main(String[] args)
    {
        // The sets the folder in the 'resources' folder where the static files
        // are to be searched.
        staticFileLocation("/public");

        // Default layout template.
        String layout = "templates/layout.vtl";

        // Layout template for login/signup pages.
        String layout_signinup = "templates/layout_signinup.vtl";

        // Home page. Redirects to login page if not already logged in.
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

        // Admin dashboard. Currenlty, this page is not implemented.
        get("/admin", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/admin.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // This is a temporary route to display some message using GET
        // parameters. This should only be used for debugging.
        //
        // For example, if we want to display "404 NOT FOUND" message, we should
        // redirect to '/message?m=404+NOT+FOUND'
        get("/message", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/message.vtl");
            model.put("message", request.queryParams("m"));
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // The registration form.
        // NOTE: This is only for development phase. In
        // production, any user would need to be added by an admin.
        // Self-registration should not be allowed in production environment.
        get("/register", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/register.vtl");
            return new ModelAndView(model, layout_signinup);
        }, new VelocityTemplateEngine());

        // The registration form handler. It creates a new user and Redirects
        // to the login page.
        // NOTE: Again, self-registration is only for development
        // phase!
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

        // The login form.
        get("/login", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/login.vtl");
            return new ModelAndView(model, layout_signinup);
        }, new VelocityTemplateEngine());

        // Login form handler.
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

        // The logout handler.
        // NOTE: Logout should be handled by a POST method. We've used GET for
        // ease of implementation in the development phase.
        get("/logout", (request, response) -> {
            if (request.session().attribute("userId") == null) {
                response.redirect("/message?m=NOT+LOGGED+IN");
            } else {
                request.session().removeAttribute("userId");
                response.redirect("/login");
            }
            return 0;
        });

        // Paginated list of all questions in the database. This is useful for
        // deleting or updating existing questions, or adding a new one in the
        // database.
        get("/questions", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_list.vtl");

            Integer page = 1;   // default page number
            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            Integer start = (page - 1) * QUESTIONS_PER_PAGE;
            List<Question> questions = Question.limit(start, QUESTIONS_PER_PAGE);

            model.put("questions", questions);
            model.put("currentPage", page);
            model.put("prevPage", page-1);
            model.put("nextPage", page+1);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // New question form.
        get("/questions/add", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_add_form.vtl");
            model.put("categories", Category.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // New question form handler. Adds the new question in the database
        // and then redirects to question list page.
        post("/questions", (request, response) -> {
            Integer categoryId = Integer.parseInt(request.queryParams("category"));
            String question = request.queryParams("question");
            String answer1 = request.queryParams("answer1");
            String answer2 = request.queryParams("answer2");
            String answer3 = request.queryParams("answer3");
            String answer4 = request.queryParams("answer4");
            Integer difficulty = Integer.parseInt(request.queryParams("difficulty"));

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

        // Question delete handler. Data modifiers (add, update, delete) should
        // should always be handled by POST method.
        post("/questions/:qid/delete", (request, response) -> {
            Integer id = Integer.parseInt(request.params(":qid"));
            Question q = Question.findById(id);
            q.delete();
            response.redirect("/questions");
            return 0;
        });

        // Question edit form.
        get("/questions/:qid/edit", (request, response) -> {
            Question question = Question
                .findById(Integer.parseInt(request.params(":qid")));

            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/question_edit_form.vtl");
            model.put("categories", Category.all());
            model.put("question", question);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Question edit form handler.
        post("/questions/:qid/edit", (request, response) -> {
            Question q = Question.findById(Integer.parseInt(request.params(":qid")));

            Integer categoryId = Integer.parseInt(request.queryParams("category"));
            String text = request.queryParams("question");
            String answer1 = request.queryParams("answer1");
            String answer2 = request.queryParams("answer2");
            String answer3 = request.queryParams("answer3");
            String answer4 = request.queryParams("answer4");
            Integer difficulty = Integer.parseInt(request.queryParams("difficulty"));

            String sql;
            try (Connection con = DB.sql2o.open();) {
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

        // List of categories.
        get("/categories", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();Category.all();
            model.put("template", "templates/category_list.vtl");
            model.put("categories", Category.all());
            // System.out.println(categories_list);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Category form handler. Saves the category in the database and
        // redirects to list of categories.
        post("/categories",(request, response) -> {
            String category_name = request.queryParams("category_name");
            Category c = new Category(category_name).save();
            response.redirect("/categories");
            return "Success";
        });

        // Category delete handler.
        post("/categories/:cId/delete", (request, response) ->{
            Category category = Category.findById(Integer.parseInt(request.params(":cId")));
            category.delete();
            response.redirect("/categories");
            return "Success";
        });

        // User add form. This should be only available for admin users.
        get("/users/add", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();

            // Get the current active user ID
            Integer userId = request.session().attribute("userId");

            // If current active user is not admin, then throw 404 error
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }

            model.put("template", "templates/user_add_form.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // User add form handler. This is also available for admin users.
        post("/users", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();

            // Check if current active user is admin.
            Integer userId = request.session().attribute("userId");
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }

            Integer role = Integer.parseInt(request.queryParams("role"));
            String name = request.queryParams("name");
            String username = request.queryParams("username");
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            try {
                User u = new User(email, username, password, name, role).save();
                response.redirect("/users");
            } catch (Exception e) {
                model.put("template", "templates/message.vtl");
                model.put("message", "ERROR CREATING USER");
                e.printStackTrace();
                return new ModelAndView(model, layout);
            }

            return 0;
        });

        // List of users. Only available for admin users.
        get("/users", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();

            // Check if current active user is admin.
            Integer userId = request.session().attribute("userId");
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }

            model.put("template", "templates/user_list.vtl");
            model.put("users", User.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // List of exams.
        get("/exams", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/exam_list.vtl");
            model.put("exams", Exam.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // New exam form.
        // NOTE: Should only be avaiable for Examiner users.
        get("/exams/add", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer userId = request.session().attribute("userId");
            // if (userId == null || !User.findById(userId).isExaminer()) {
            //     response.redirect("/message?m=ACCESS+DENIED");
            // }
            model.put("template", "templates/exam_add_form.vtl");
            model.put("categories", Category.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Exam form handler. It not only creates an Exam instance, but also
        // creates 3 Set instances and also adds appropriate questions to those
        // sets automatically.
        //
        // NOTE: Should only be available for Examiner users.
        post("/exams", (request, response) -> {
            Integer userId = request.session().attribute("userId");
            // if (userId == null || !User.findById(userId).isExaminer()) {
            //     response.redirect("/message?m=ACCESS+DENIED");
            // }

            String title = request.queryParams("title");
            String timeString = request.queryParams("time").replace("T", " ");
            // make sure the seconds are set before parsing
            if (StringUtils.countMatches(timeString, ":") == 1) {
                timeString += ":00";
            }
            Integer duration = Integer.parseInt(request.queryParams("duration"));
            Integer difficulty = Integer.parseInt(request.queryParams("difficulty"));
            String[] categories = request.queryParamsValues("categories");

            // create exam

            Exam exam = new Exam();
            exam.setTitle(title);
            exam.setTime(timeString);
            exam.setDuration(duration);
            exam.setDifficulty(difficulty);
            exam.setUserId(userId);
            exam.save();

            // Create 3 sets.

            Set set1 = new Set(exam, 1).save();  // 1st set of this exam
            Set set2 = new Set(exam, 2).save();  // 2nd set of this exam
            Set set3 = new Set(exam, 3).save();  // 3rd set of this exam

            // Calculate question counts. We have assumed that each question is
            // allocated 3 minutes in average. Also, each set contains 50%
            // questions of the specified difficulty and 25% each of the other
            // two difficulty levels.
            Integer countPerSet = duration / 3;
            Integer countSelected = countPerSet / 2;
            Integer countOther1 = countPerSet / 4;
            Integer countOther2 = countPerSet - countSelected - countOther1;

            // Extract questions of various difficulty levels
            String query = "SELECT * FROM questions WHERE "
                + "difficulty=:difficulty AND "
                + "(FIND_IN_SET(questions.categoryId, :categoryIds) > 0) "
                + "ORDER BY RAND() LIMIT 0, :count";
            List<Question> questionsSelected;
            List<Question> questionsOther1;
            List<Question> questionsOther2;
            try (Connection con = DB.sql2o.open();) {
                questionsSelected = con.createQuery(query)
                        .addParameter("difficulty", difficulty)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", countSelected * 3)
                        .executeAndFetch(Question.class);
                questionsOther1 = con.createQuery(query)
                        .addParameter("difficulty", (difficulty + 1) % 3)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", countOther1 * 3)
                        .executeAndFetch(Question.class);
                questionsOther2 = con.createQuery(query)
                        .addParameter("difficulty", (difficulty + 2) % 3)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", countOther2 * 3)
                        .executeAndFetch(Question.class);
            }

            // separate the questions for each set

            List<Question> set1Questions = new ArrayList<Question>();
            List<Question> set2Questions = new ArrayList<Question>();
            List<Question> set3Questions = new ArrayList<Question>();

            for (Integer i = 0; i < questionsSelected.size(); i++) {
                Question q = questionsSelected.get(i);
                Integer test = i / countSelected;
                if (test == 0) {
                    set1Questions.add(q);
                } else if (test == 1) {
                    set2Questions.add(q);
                } else if (test == 2) {
                    set3Questions.add(q);
                }
            }

            for (Integer i = 0; i < questionsOther1.size(); i++) {
                Question q = questionsOther1.get(i);
                Integer test = i / countOther1;
                if (test == 0) {
                    set1Questions.add(q);
                } else if (test == 1) {
                    set2Questions.add(q);
                } else if (test == 2) {
                    set3Questions.add(q);
                }
            }

            for (Integer i = 0; i < questionsOther2.size(); i++) {
                Question q = questionsOther2.get(i);
                Integer test = i / countOther2;
                if (test == 0) {
                    set1Questions.add(q);
                } else if (test == 1) {
                    set2Questions.add(q);
                } else if (test == 2) {
                    set3Questions.add(q);
                }
            }

            // Check if all sets have correct question counts
            assert set1Questions.size() == countPerSet;
            assert set2Questions.size() == countPerSet;
            assert set3Questions.size() == countPerSet;

            Collections.shuffle(set1Questions);
            Collections.shuffle(set2Questions);
            Collections.shuffle(set3Questions);

            Random random = new Random();
            for (Integer i = 1; i <= countPerSet; i++) {
                set1.addQuestion(set1Questions.get(i-1), i, random.nextInt(4));
                set2.addQuestion(set2Questions.get(i-1), i, random.nextInt(4));
                set3.addQuestion(set3Questions.get(i-1), i, random.nextInt(4));
            }

            // Redirect to the newly generated exam sets page
            response.redirect("/exams/" + exam.getId());
            return 0;
        });

        // Exam detail page. Shows links to the 3 question sets.
        get("/exams/:id", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();

            Integer id = Integer.parseInt(request.params("id"));

            model.put("template", "templates/exam_detail.vtl");
            model.put("exam", Exam.findById(id));
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Exam delete handler. Also deletes all associated sets and
        // set-question relations.
        post("/exams/:id/delete", (request, response) -> {
            Integer id = Integer.parseInt(request.params("id"));
            Exam exam = Exam.findById(id);
            exam.delete();
            response.redirect("/exams");
            return 0;
        });

        // Question set. Displays the list of questions and 4 options for each
        // question. The questions and answers have pre-defined order for a
        // specified set.
        get("/exams/:id/:set", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer examId = Integer.parseInt(request.params("id"));
            // Validate set number. Must be one of {1,2,3}.
            Integer setNumber = Integer.parseInt(request.params("set"));
            if (setNumber < 1 || setNumber > 3) {
                response.redirect("/message?m=INVALID+SET+NUMBER");
            }
            Exam exam = Exam.findById(examId);
            Set set = exam.getSets().get(setNumber-1);

            model.put("template", "templates/question_set.vtl");
            model.put("set", set);
            model.put("exam", exam);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Answer sheet for a particular set. It displays the question number
        // and its corresponding index of correct answer, for each question in
        // the set.
        get("/exams/:id/:set/solution", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer examId = Integer.parseInt(request.params("id"));
            Integer setNumber = Integer.parseInt(request.params("set"));
            Exam exam = Exam.findById(examId);
            Set set = exam.getSets().get(setNumber-1);

            model.put("template", "templates/answer_sheet.vtl");
            model.put("set", set);
            model.put("exam", exam);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }
}
