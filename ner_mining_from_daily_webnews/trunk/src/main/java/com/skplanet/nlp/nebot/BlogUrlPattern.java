package com.skplanet.nlp.nebot;

public class BlogUrlPattern {
    private String blogIdVal;
    private String blogPostNoVal;
    private String baseUrlTag;
    private StringBuilder fullUrlTag;
    private StringBuilder fullUrlPost;
    private String title;

    public BlogUrlPattern(){
        this.fullUrlTag = new StringBuilder();
        this.fullUrlPost = new StringBuilder();

    }

    public void appendFullUrlTag(String tag) {
        this.fullUrlTag.append(tag);
    }

    public void appendFullUrlPost(String post) {
        this.fullUrlPost.append(post);
    }

    public String getBlogIdVal() {
        return blogIdVal;
    }

    public void setBlogIdVal(String blogIdVal) {
        this.blogIdVal = blogIdVal;
    }

    public String getBlogPostNoVal() {
        return blogPostNoVal;
    }

    public void setBlogPostNoVal(String blogPostNoVal) {
        this.blogPostNoVal = blogPostNoVal;
    }

    public String getBaseUrlTag() {
        return baseUrlTag;
    }

    public void setBaseUrlTag(String baseUrlTag) {
        this.baseUrlTag = baseUrlTag;
    }

    public StringBuilder getFullUrlTag() {
        return fullUrlTag;
    }

    public void setFullUrlTag(StringBuilder fullUrlTag) {
        this.fullUrlTag = fullUrlTag;
    }

    public StringBuilder getFullUrlPost() {
        return fullUrlPost;
    }

    public void setFullUrlPost(StringBuilder fullUrlPost) {
        this.fullUrlPost = fullUrlPost;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
