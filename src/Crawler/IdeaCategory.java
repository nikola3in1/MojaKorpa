package Crawler;

import java.util.Arrays;

class IdeaCategory {
    String id;
    String name;
    String description;
    String image_path;
    IdeaCategory[] children;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage_path() {
        return image_path;
    }
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
    public IdeaCategory[] getChildren() {
        return children;
    }
    public void setChildren(IdeaCategory[] children) {
        this.children = children;
    }

    public IdeaCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Kategorija:" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image_path='" + image_path + '\'' +
                ", children=" + Arrays.toString(children);
    }

    public IdeaCategory() {}
}

