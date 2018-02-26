package com.example.codyhammond.newshub;

import java.io.Serializable;
import java.util.List;

/**
 * Created by codyhammond on 1/8/18.
 */


public class GSONCenter{
    List<Article>articles;
    List<Source>sources;
}


class Source {
    String id;
    String name;
}
class Article implements Serializable{
    Source source;
    String author;
    String title;
    String description;
    String url;
    String urlToImage;
}


