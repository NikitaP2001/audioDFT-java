package ui;

import java.util.TreeMap;
import java.awt.Color;

class ThemeProvider {

        public enum Element { 
                Text, 
                Background,
        }

        public enum Theme { 
                Light, 
                Dark,
        }
        
        private static final TreeMap<Theme, TreeMap<Element, Color>> themeMap = new TreeMap<>();

        private static Theme currTheme = Theme.Dark;

        static {
                TreeMap<Element, Color> darkThemeMap = new TreeMap<>();
                darkThemeMap.put(Element.Text, Color.white);
                darkThemeMap.put(Element.Background, Color.darkGray);

                themeMap.put(Theme.Dark, darkThemeMap);
        }

        public static Color getColor(Element elementType) {
                return themeMap.get(currTheme).get(elementType);

        }

        public static void setTheme(Theme newTheme) {
                currTheme = newTheme;
        }

}