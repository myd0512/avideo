package com.fengtuan.videoanchor.bean;

public class GameTypeBean {
    private String id ;
    private String name ;
    private String ratio ;

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

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public boolean isShow(){
        return "1".equals(ratio) ;
    }
}
