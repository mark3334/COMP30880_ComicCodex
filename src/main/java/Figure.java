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
    private final String pose;
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
                FigureData.POSES[rand.nextInt(FigureData.POSES.length)],
                facing
        );
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("    <figure>\n");
        sb.append(String.format("        <name>%s</name>\n", name));
        sb.append(String.format("        <appearance>%s</appearance>\n", appearance));
        if (skin != null) sb.append(String.format("        <skin>%s</skin>\n", skin));
        if (hair != null) sb.append(String.format("        <hair>%s</hair>\n", hair));
        if (beard != null) sb.append(String.format("        <beard>%s</beard>\n", beard));
        if (hairlength != null) sb.append(String.format("        <hairlength>%s</hairlength>\n", hairlength));
        if (hairstyle != null) sb.append(String.format("        <hairstyle>%s</hairstyle>\n", hairstyle));
        if (lips != null) sb.append(String.format("        <lips>%s</lips>\n", lips));
        if (pose != null) sb.append(String.format("        <pose>%s</pose>\n", pose));
        if (facing != null) sb.append(String.format("        <facing>%s</facing>\n", facing));
        sb.append("    </figure>\n");
        return sb.toString();
    }
}
