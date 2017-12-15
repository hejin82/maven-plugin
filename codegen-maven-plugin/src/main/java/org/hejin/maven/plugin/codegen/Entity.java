package org.hejin.maven.plugin.codegen;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private String className;
    private String classComment;
    private List<Field> field = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public List<Field> getField() {
        return field;
    }

    public void setField(List<Field> field) {
        this.field = field;
    }

}
