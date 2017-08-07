
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.*;
import org.sql2o.*;
import java.sql.Timestamp;
import org.apache.commons.lang.StringUtils;


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

        //  List category
        get("/categories", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();Category.all();
            model.put("template", "templates/category_list.vtl");
            model.put("categories", Category.all());
            // System.out.println(categories_list);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // Category submit
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
        });

        // User add

        get("/users/add", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer userId = request.session().attribute("userId");
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }
            model.put("template", "templates/user_add_form.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        post("/users", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer userId = request.session().attribute("userId");
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }

            int role = Integer.parseInt(request.queryParams("role"));
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

        get("/users", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer userId = request.session().attribute("userId");
            if (userId == null || !User.findById(userId).isAdmin()) {
                model.put("template", "templates/404.vtl");
                return new ModelAndView(model, layout);
            }
            model.put("template", "templates/user_list.vtl");
            model.put("users", User.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // exam list
        get("/exams", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("template", "templates/exam_list.vtl");
            model.put("exams", Exam.all());
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // exams add
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

        // exam add submit
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
            Exam e = new Exam();
            e.setTitle(title);
            e.setTime(timeString);
            e.setDuration(duration);
            e.setDifficulty(difficulty);
            e.setUserId(userId);
            e.save();

            // create 3 sets
            Set set1 = new Set(e, 1).save();
            Set set2 = new Set(e, 2).save();
            Set set3 = new Set(e, 3).save();

            // calculate question counts
            Integer count_per_set = duration / 3;
            Integer count_selected = count_per_set / 2;
            Integer count_other_1 = count_per_set / 4;
            Integer count_other_2 = count_per_set - count_selected - count_other_1;

            // extract questions from database
            String sql = "SELECT * FROM questions WHERE difficulty=:difficulty AND (FIND_IN_SET(questions.categoryId, :categoryIds) > 0) ORDER BY RAND() LIMIT 0, :count";
            List<Question> questions_selected;
            List<Question> questions_other_1;
            List<Question> questions_other_2;
            try (Connection con = DB.sql2o.open();) {
                questions_selected = con.createQuery(sql)
                        .addParameter("difficulty", difficulty)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", count_selected * 3)
                        .executeAndFetch(Question.class);
                questions_other_1 = con.createQuery(sql)
                        .addParameter("difficulty", (difficulty + 1) % 3)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", count_other_1 * 3)
                        .executeAndFetch(Question.class);
                questions_other_2 = con.createQuery(sql)
                        .addParameter("difficulty", (difficulty + 2) % 3)
                        .addParameter("categoryIds", String.join(",", categories))
                        .addParameter("count", count_other_2 * 3)
                        .executeAndFetch(Question.class);
            }

            // separate the questions for each set

            List<Question> set1_questions = new ArrayList<Question>();
            List<Question> set2_questions = new ArrayList<Question>();
            List<Question> set3_questions = new ArrayList<Question>();

            for (int i = 0; i < questions_selected.size(); i++) {
                Question q = questions_selected.get(i);
                int test = i / count_selected;
                if (test == 0) {
                    set1_questions.add(q);
                } else if (test == 1) {
                    set2_questions.add(q);
                } else if (test == 2) {
                    set3_questions.add(q);
                }
            }

            for (int i = 0; i < questions_other_1.size(); i++) {
                Question q = questions_other_1.get(i);
                int test = i / count_other_1;
                if (test == 0) {
                    set1_questions.add(q);
                } else if (test == 1) {
                    set2_questions.add(q);
                } else if (test == 2) {
                    set3_questions.add(q);
                }
            }

            for (int i = 0; i < questions_other_2.size(); i++) {
                Question q = questions_other_2.get(i);
                int test = i / count_other_2;
                if (test == 0) {
                    set1_questions.add(q);
                } else if (test == 1) {
                    set2_questions.add(q);
                } else if (test == 2) {
                    set3_questions.add(q);
                }
            }

            assert set1_questions.size() == count_per_set;
            assert set2_questions.size() == count_per_set;
            assert set3_questions.size() == count_per_set;

            Collections.shuffle(set1_questions);
            Collections.shuffle(set2_questions);
            Collections.shuffle(set3_questions);

            Random random = new Random();
            for (int i = 1; i <= count_per_set; i++) {
                set1.addQuestion(set1_questions.get(i-1), i, random.nextInt(4));
                set2.addQuestion(set2_questions.get(i-1), i, random.nextInt(4));
                set3.addQuestion(set3_questions.get(i-1), i, random.nextInt(4));
            }

            response.redirect("/exams/" + e.getId());
            return 0;
        });

        // exam detail page
        get("/exams/:id", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();

            Integer id = Integer.parseInt(request.params("id"));

            model.put("template", "templates/exam_detail.vtl");
            model.put("exam", Exam.findById(id));
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        // exam delete
        post("/exams/:id/delete", (request, response) -> {
            Integer id = Integer.parseInt(request.params("id"));
            Exam exam = Exam.findById(id);
            exam.delete();
            response.redirect("/exams");
            return 0;
        });

        // question set
        get("/exams/:id/:set", (request, response) -> {
            Map<String,Object> model = new HashMap<String,Object>();
            Integer examId = Integer.parseInt(request.params("id"));
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

        // question set solution
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
