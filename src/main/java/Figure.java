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
    private final String facing;

    public Figure(String name, String appearance, String skin, String hair, String beard,
                  String hairlength, String hairstyle, String lips, String facing) {
        this.name = name;
        this.appearance = appearance;
        this.skin = skin;
        this.hair = hair;
        this.beard = beard;
        this.hairlength = hairlength;
        this.hairstyle = hairstyle;
        this.lips = lips;
        this.facing = facing;
    }

    public static Figure generateRandomFigure(String facing) {
        Random rand = new Random();
        return new Figure(
                FigureData.NAMES[rand.nextInt(FigureData.NAMES.length)],
                FigureData.APPEARANCES[rand.nextInt(FigureData.APPEARANCES.length)],
                FigureData.SKINS[rand.nextInt(FigureData.SKINS.length)],
                FigureData.HAIRS[rand.nextInt(FigureData.HAIRS.length)],
                FigureData.BEARDS[rand.nextInt(FigureData.BEARDS.length)],
                FigureData.HAIR_LENGTHS[rand.nextInt(FigureData.HAIR_LENGTHS.length)],
                FigureData.HAIRSTYLES[rand.nextInt(FigureData.HAIRSTYLES.length)],
                FigureData.LIPS[rand.nextInt(FigureData.LIPS.length)],
                facing
        );
    }

    public String getName() {return name;}
    public String getAppearance() {return appearance;}
    public String getSkin() {return skin;}
    public String getHair() {return hair;}
    public String getBeard() {return beard;}
    public String getHairLength() {return hairlength;}
    public String getHairstyle() {return hairstyle;}
    public String getLips() {return lips;}
    public String getFacing() {return facing;}
}
