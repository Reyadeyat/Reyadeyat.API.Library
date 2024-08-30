/*
 * Copyright (C) 2023 Reyadeyat
 *
 * Reyadeyat/RELATIONAL.API is licensed under the
 * BSD 3-Clause "New" or "Revised" License
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://reyadeyat.net/LICENSE/RELATIONAL.API.LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.reyadeyat.api.library.util;

import java.awt.Color;
import java.util.HashMap;

/**
 * 
 * Description
 * 
 *
 * @author Mohammad Nabil Mostafa
 * <a href="mailto:code@reyadeyat.net">code@reyadeyat.net</a>
 * 
 * @since 2023.01.01
 */
public class NamedColor {
    static private HashMap<String, NamedColor> colorMap = new HashMap<String, NamedColor>() {{
        put("aliceblue", new NamedColor("AliceBlue", 0xF0, 0xF8, 0xFF));
        put("antiquewhite", new NamedColor("AntiqueWhite", 0xFA, 0xEB, 0xD7));
        put("aqua", new NamedColor("Aqua", 0x00, 0xFF, 0xFF));
        put("aquamarine", new NamedColor("Aquamarine", 0x7F, 0xFF, 0xD4));
        put("azure", new NamedColor("Azure", 0xF0, 0xFF, 0xFF));
        put("beige", new NamedColor("Beige", 0xF5, 0xF5, 0xDC));
        put("bisque", new NamedColor("Bisque", 0xFF, 0xE4, 0xC4));
        put("black", new NamedColor("Black", 0x00, 0x00, 0x00));
        put("blanchedalmond", new NamedColor("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
        put("blue", new NamedColor("Blue", 0x00, 0x00, 0xFF));
        put("blueviolet", new NamedColor("BlueViolet", 0x8A, 0x2B, 0xE2));
        put("brown", new NamedColor("Brown", 0xA5, 0x2A, 0x2A));
        put("burlywood", new NamedColor("BurlyWood", 0xDE, 0xB8, 0x87));
        put("cadetblue", new NamedColor("CadetBlue", 0x5F, 0x9E, 0xA0));
        put("chartreuse", new NamedColor("Chartreuse", 0x7F, 0xFF, 0x00));
        put("chocolate", new NamedColor("Chocolate", 0xD2, 0x69, 0x1E));
        put("coral", new NamedColor("Coral", 0xFF, 0x7F, 0x50));
        put("cornflowerblue", new NamedColor("CornflowerBlue", 0x64, 0x95, 0xED));
        put("cornsilk", new NamedColor("Cornsilk", 0xFF, 0xF8, 0xDC));
        put("crimson", new NamedColor("Crimson", 0xDC, 0x14, 0x3C));
        put("cyan", new NamedColor("Cyan", 0x00, 0xFF, 0xFF));
        put("darkblue", new NamedColor("DarkBlue", 0x00, 0x00, 0x8B));
        put("darkcyan", new NamedColor("DarkCyan", 0x00, 0x8B, 0x8B));
        put("darkgoldenrod", new NamedColor("DarkGoldenRod", 0xB8, 0x86, 0x0B));
        put("darkgray", new NamedColor("DarkGray", 0xA9, 0xA9, 0xA9));
        put("darkgreen", new NamedColor("DarkGreen", 0x00, 0x64, 0x00));
        put("darkkhaki", new NamedColor("DarkKhaki", 0xBD, 0xB7, 0x6B));
        put("darkmagenta", new NamedColor("DarkMagenta", 0x8B, 0x00, 0x8B));
        put("darkolivegreen", new NamedColor("DarkOliveGreen", 0x55, 0x6B, 0x2F));
        put("darkorange", new NamedColor("DarkOrange", 0xFF, 0x8C, 0x00));
        put("darkorchid", new NamedColor("DarkOrchid", 0x99, 0x32, 0xCC));
        put("darkred", new NamedColor("DarkRed", 0x8B, 0x00, 0x00));
        put("darksalmon", new NamedColor("DarkSalmon", 0xE9, 0x96, 0x7A));
        put("darkseagreen", new NamedColor("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
        put("darkslateblue", new NamedColor("DarkSlateBlue", 0x48, 0x3D, 0x8B));
        put("darkslategray", new NamedColor("DarkSlateGray", 0x2F, 0x4F, 0x4F));
        put("darkturquoise", new NamedColor("DarkTurquoise", 0x00, 0xCE, 0xD1));
        put("darkviolet", new NamedColor("DarkViolet", 0x94, 0x00, 0xD3));
        put("deeppink", new NamedColor("DeepPink", 0xFF, 0x14, 0x93));
        put("deepskyblue", new NamedColor("DeepSkyBlue", 0x00, 0xBF, 0xFF));
        put("dimgray", new NamedColor("DimGray", 0x69, 0x69, 0x69));
        put("dodgerblue", new NamedColor("DodgerBlue", 0x1E, 0x90, 0xFF));
        put("firebrick", new NamedColor("FireBrick", 0xB2, 0x22, 0x22));
        put("floralwhite", new NamedColor("FloralWhite", 0xFF, 0xFA, 0xF0));
        put("forestgreen", new NamedColor("ForestGreen", 0x22, 0x8B, 0x22));
        put("fuchsia", new NamedColor("Fuchsia", 0xFF, 0x00, 0xFF));
        put("gainsboro", new NamedColor("Gainsboro", 0xDC, 0xDC, 0xDC));
        put("ghostwhite", new NamedColor("GhostWhite", 0xF8, 0xF8, 0xFF));
        put("gold", new NamedColor("Gold", 0xFF, 0xD7, 0x00));
        put("goldenrod", new NamedColor("GoldenRod", 0xDA, 0xA5, 0x20));
        put("gray", new NamedColor("Gray", 0x80, 0x80, 0x80));
        put("green", new NamedColor("Green", 0x00, 0xFF, 0x00));
        put("greenyellow", new NamedColor("GreenYellow", 0xAD, 0xFF, 0x2F));
        put("honeydew", new NamedColor("HoneyDew", 0xF0, 0xFF, 0xF0));
        put("hotpink", new NamedColor("HotPink", 0xFF, 0x69, 0xB4));
        put("indianred", new NamedColor("IndianRed", 0xCD, 0x5C, 0x5C));
        put("indigo", new NamedColor("Indigo", 0x4B, 0x00, 0x82));
        put("ivory", new NamedColor("Ivory", 0xFF, 0xFF, 0xF0));
        put("khaki", new NamedColor("Khaki", 0xF0, 0xE6, 0x8C));
        put("lavender", new NamedColor("Lavender", 0xE6, 0xE6, 0xFA));
        put("lavenderblush", new NamedColor("LavenderBlush", 0xFF, 0xF0, 0xF5));
        put("lawngreen", new NamedColor("LawnGreen", 0x7C, 0xFC, 0x00));
        put("lemonchiffon", new NamedColor("LemonChiffon", 0xFF, 0xFA, 0xCD));
        put("lightblue", new NamedColor("LightBlue", 0xAD, 0xD8, 0xE6));
        put("lightcoral", new NamedColor("LightCoral", 0xF0, 0x80, 0x80));
        put("lightcyan", new NamedColor("LightCyan", 0xE0, 0xFF, 0xFF));
        put("lightgoldenrodyellow", new NamedColor("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
        put("lightgray", new NamedColor("LightGray", 0xD3, 0xD3, 0xD3));
        put("lightgreen", new NamedColor("LightGreen", 0x90, 0xEE, 0x90));
        put("lightpink", new NamedColor("LightPink", 0xFF, 0xB6, 0xC1));
        put("lightsalmon", new NamedColor("LightSalmon", 0xFF, 0xA0, 0x7A));
        put("lightseagreen", new NamedColor("LightSeaGreen", 0x20, 0xB2, 0xAA));
        put("lightskyblue", new NamedColor("LightSkyBlue", 0x87, 0xCE, 0xFA));
        put("lightslategray", new NamedColor("LightSlateGray", 0x77, 0x88, 0x99));
        put("lightsteelblue", new NamedColor("LightSteelBlue", 0xB0, 0xC4, 0xDE));
        put("lightyellow", new NamedColor("LightYellow", 0xFF, 0xFF, 0xE0));
        put("lime", new NamedColor("Lime", 0x00, 0xFF, 0x00));
        put("limegreen", new NamedColor("LimeGreen", 0x32, 0xCD, 0x32));
        put("linen", new NamedColor("Linen", 0xFA, 0xF0, 0xE6));
        put("magenta", new NamedColor("Magenta", 0xFF, 0x00, 0xFF));
        put("maroon", new NamedColor("Maroon", 0x80, 0x00, 0x00));
        put("mediumaquamarine", new NamedColor("MediumAquaMarine", 0x66, 0xCD, 0xAA));
        put("mediumblue", new NamedColor("MediumBlue", 0x00, 0x00, 0xCD));
        put("mediumorchid", new NamedColor("MediumOrchid", 0xBA, 0x55, 0xD3));
        put("mediumpurple", new NamedColor("MediumPurple", 0x93, 0x70, 0xDB));
        put("mediumseagreen", new NamedColor("MediumSeaGreen", 0x3C, 0xB3, 0x71));
        put("mediumslateblue", new NamedColor("MediumSlateBlue", 0x7B, 0x68, 0xEE));
        put("mediumspringgreen", new NamedColor("MediumSpringGreen", 0x00, 0xFA, 0x9A));
        put("mediumturquoise", new NamedColor("MediumTurquoise", 0x48, 0xD1, 0xCC));
        put("mediumvioletred", new NamedColor("MediumVioletRed", 0xC7, 0x15, 0x85));
        put("midnightblue", new NamedColor("MidnightBlue", 0x19, 0x19, 0x70));
        put("mintcream", new NamedColor("MintCream", 0xF5, 0xFF, 0xFA));
        put("mistyrose", new NamedColor("MistyRose", 0xFF, 0xE4, 0xE1));
        put("moccasin", new NamedColor("Moccasin", 0xFF, 0xE4, 0xB5));
        put("navajowhite", new NamedColor("NavajoWhite", 0xFF, 0xDE, 0xAD));
        put("navy", new NamedColor("Navy", 0x00, 0x00, 0x80));
        put("oldlace", new NamedColor("OldLace", 0xFD, 0xF5, 0xE6));
        put("olive", new NamedColor("Olive", 0x80, 0x80, 0x00));
        put("olivedrab", new NamedColor("OliveDrab", 0x6B, 0x8E, 0x23));
        put("orange", new NamedColor("Orange", 0xFF, 0xA5, 0x00));
        put("orangered", new NamedColor("OrangeRed", 0xFF, 0x45, 0x00));
        put("orchid", new NamedColor("Orchid", 0xDA, 0x70, 0xD6));
        put("palegoldenrod", new NamedColor("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
        put("palegreen", new NamedColor("PaleGreen", 0x98, 0xFB, 0x98));
        put("paleturquoise", new NamedColor("PaleTurquoise", 0xAF, 0xEE, 0xEE));
        put("palevioletred", new NamedColor("PaleVioletRed", 0xDB, 0x70, 0x93));
        put("papayawhip", new NamedColor("PapayaWhip", 0xFF, 0xEF, 0xD5));
        put("peachpuff", new NamedColor("PeachPuff", 0xFF, 0xDA, 0xB9));
        put("peru", new NamedColor("Peru", 0xCD, 0x85, 0x3F));
        put("pink", new NamedColor("Pink", 0xFF, 0xC0, 0xCB));
        put("plum", new NamedColor("Plum", 0xDD, 0xA0, 0xDD));
        put("powderblue", new NamedColor("PowderBlue", 0xB0, 0xE0, 0xE6));
        put("purple", new NamedColor("Purple", 0x80, 0x00, 0x80));
        put("red", new NamedColor("Red", 0xFF, 0x00, 0x00));
        put("rosybrown", new NamedColor("RosyBrown", 0xBC, 0x8F, 0x8F));
        put("royalblue", new NamedColor("RoyalBlue", 0x41, 0x69, 0xE1));
        put("saddlebrown", new NamedColor("SaddleBrown", 0x8B, 0x45, 0x13));
        put("salmon", new NamedColor("Salmon", 0xFA, 0x80, 0x72));
        put("sandybrown", new NamedColor("SandyBrown", 0xF4, 0xA4, 0x60));
        put("seagreen", new NamedColor("SeaGreen", 0x2E, 0x8B, 0x57));
        put("seashell", new NamedColor("SeaShell", 0xFF, 0xF5, 0xEE));
        put("sienna", new NamedColor("Sienna", 0xA0, 0x52, 0x2D));
        put("silver", new NamedColor("Silver", 0xC0, 0xC0, 0xC0));
        put("skyblue", new NamedColor("SkyBlue", 0x87, 0xCE, 0xEB));
        put("slateblue", new NamedColor("SlateBlue", 0x6A, 0x5A, 0xCD));
        put("slategray", new NamedColor("SlateGray", 0x70, 0x80, 0x90));
        put("snow", new NamedColor("Snow", 0xFF, 0xFA, 0xFA));
        put("springgreen", new NamedColor("SpringGreen", 0x00, 0xFF, 0x7F));
        put("steelblue", new NamedColor("SteelBlue", 0x46, 0x82, 0xB4));
        put("tan", new NamedColor("Tan", 0xD2, 0xB4, 0x8C));
        put("teal", new NamedColor("Teal", 0x00, 0x80, 0x80));
        put("thistle", new NamedColor("Thistle", 0xD8, 0xBF, 0xD8));
        put("tomato", new NamedColor("Tomato", 0xFF, 0x63, 0x47));
        put("turquoise", new NamedColor("Turquoise", 0x40, 0xE0, 0xD0));
        put("violet", new NamedColor("Violet", 0xEE, 0x82, 0xEE));
        put("wheat", new NamedColor("Wheat", 0xF5, 0xDE, 0xB3));
        put("white", new NamedColor("White", 0xFF, 0xFF, 0xFF));
        put("whitesmoke", new NamedColor("WhiteSmoke", 0xF5, 0xF5, 0xF5));
        put("yellow", new NamedColor("Yellow", 0xFF, 0xFF, 0x00));
        put("yellowgreen", new NamedColor("YellowGreen", 0x9A, 0xCD, 0x32));
    }};
    
    public int red, green, blue, color;
    public String name, nameL;
    public Color newColor;
    /*
    00000000 00000000 00000000 00000000  <-- 32bit int
         ^             ^      ^
         |             |      |
         +--red here   |      +--green here
              8bit     |            8bit
                       |
                       +--blue here
                             8bit
    */

    public NamedColor(String name, String cssColor) throws Exception {
        if (cssColor.startsWith("#") == false) {
            throw new Exception("Invalid CSS color");
        }
        int red = Integer.parseInt(cssColor.substring(1, 3),16);
        int green = Integer.parseInt(cssColor.substring(3, 5),16);
        int blue = Integer.parseInt(cssColor.substring(5, 7),16);
        setColor(name, red, green, blue);
    }
    
    public NamedColor(String name, int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        setColor(name, red, green, blue);
    }
    
    private void setColor(String name, int red, int green, int blue) {
        this.name = name;
        this.nameL = name.toLowerCase();
        color = 0;
        color |= (red << 16);// & 0xffffff;
        color |= (green << 8);// & 0xffff;
        color |= blue;// & 0xff;
        newColor = new Color(red, green, blue);
        /*
        red &= (color >> 16) & 0xff;
        green &= (color >> 8) & 0xff;
        color &= color & 0xff;*/
        if (colorMap != null && colorMap.containsKey(nameL) == false) {
            colorMap.put(nameL, this);
        }
    }
    
    static public int getRGB(String colorName) {
        String colorname = colorName.toLowerCase();
        if (colorMap.containsKey(colorname.toLowerCase()) == false) {
            return 0;
        }
        NamedColor namedColor = colorMap.get(colorname.toLowerCase());
        return namedColor.color;
    }
    
    static public Color getColor(String colorName) {
        String colorname = colorName.toLowerCase();
        if (colorMap.containsKey(colorname.toLowerCase()) == false) {
            return null;
        }
        NamedColor namedColor = colorMap.get(colorname.toLowerCase());
        return namedColor.newColor;
    }
    
    static public NamedColor get(String colorName) {
        return colorMap.get(colorName.toLowerCase());
    }
    
    public Color getColor() {
        return newColor;
    }
}
