package org.hejin.maven.plugin.codegen;

public class Field {

    public static final int INDEX_COMMENT = 1;
    public static final int INDEX_JAVANAME = 2;
    public static final int INDEX_LEVEL = 3;
    public static final int INDEX_TYPE = 8;

    private String comment;
    private String javaName;
    private int level;
    private String type;
    private String javaType;

    public Field() {

    }

    public Field(String comment, String name, int level, String type) {
        super();
        this.comment = comment;
        this.javaName = name;
        this.level = level;
        this.type = type;
        this.javaType = "";
    }

    public Field(String comment, String name, int level, String type, String javaType) {
        super();
        this.comment = comment;
        this.javaName = name;
        this.level = level;
        this.type = type;
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJavaType() {
        if ("Element".equals(type)) {
            String lowerName = javaName.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append(lowerName.substring(0, 1).toUpperCase());
            for (int i = 1; i < lowerName.length(); i++) {
                if ("_".equals(lowerName.substring(i, i + 1))) {
                    sb.append(lowerName.substring(i + 1, i + 2).toUpperCase());
                    i++;
                } else {
                    sb.append(lowerName.substring(i, i + 1));
                }
            }
            return "ArrayList<" + sb.toString() + ">";
        } else {
            return "String";
        }
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

}
