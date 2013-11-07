package controllers;

import java.util.*;

import play.data.validation.*;

import models.*;
//import com.samsung.cloudpi.service.CloudpiService;
//import com.samsung.cloudpi.service.MysqlService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
public class Forums extends Application {

    public static void index() {
        List forums = Forum.findAll();
        long topicsCount = Topic.count();
        long postsCount = Post.count();
       render(forums, topicsCount, postsCount);

    }
    @Secure(admin = true)
    public static void create(@Required String name, String description) {
        if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            flash.error("Please correct these errors !");
            index();
        }
        Forum forum = new Forum(name, description);
        forum.save();
        index();
    }

    public static void show(Long forumId, Integer page) {
        Forum forum = Forum.findById(forumId);
        notFoundIfNull(forum);
        render(forum, page);
    }

    @Secure(admin = true)
    public static void delete(Long forumId) {
        Forum forum = Forum.findById(forumId);
        notFoundIfNull(forum);
        forum.delete();
        flash.success("The forum has been deleted");
        index();
    }
}

