package com.alamaanah.capitalquiz;

public class LevelItem {
    public int level;
    public String type;
    public float score;
    public boolean isLocked;
    public LevelItem(){
        super();
    }
    
    public LevelItem(int level, String type, float score, boolean isLocked) {
        super();
        this.level = level;
        this.type = type;
        this.score = score;
        this.isLocked = isLocked;
        
    }
}