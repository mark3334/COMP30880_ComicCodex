import java.util.Random;

public class Figure {
    private final String name;
    private final String appearance;
    private final String skin;
    private final String hair;
    private final String beard;
    private final String hairlength;
    private final String hairstyle;
    private final String lips;
    private String pose;
    private final String facing;

    public Figure(String name, String appearance, String skin, String hair, String beard,
                  String hairlength, String hairstyle, String lips, String pose, String facing) {
        this.name = name;
        this.appearance = appearance;
        this.skin = skin;
        this.hair = hair;
        this.beard = beard;
        this.hairlength = hairlength;
        this.hairstyle = hairstyle;
        this.lips = lips;
        this.pose = pose;
        this.facing = facing;
    }

    public String getName() {return name;}
    public String getAppearance() {return appearance;}
    public String getSkin() {return skin;}
    public String getHair() {return hair;}
    public String getBeard() {return beard;}
    public String getHairLength() {return hairlength;}
    public String getHairstyle() {return hairstyle;}
    public String getLips() {return lips;}
    public String getPose() {return pose;}
    public String getFacing() {return facing;}
}
