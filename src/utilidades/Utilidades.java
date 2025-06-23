package utilidades;

import java.awt.Color;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Llapapasca Montes
 */
public class Utilidades {

    private static final Color COLOR_ACTIVO_FONDO = new Color(22, 163, 74); // Verde
    private static final Color COLOR_ACTIVO_TEXTO = Color.WHITE;

    private static final Color COLOR_INACTIVO_FONDO = new Color(31, 41, 55); // Gris oscuro
    private static final Color COLOR_INACTIVO_TEXTO = Color.LIGHT_GRAY;

    private static final String LOGO_PATH_BASE = "src/recursos/logos/";

    /**
     * Cambia el color de fondo y texto de un botón.
     *
     * @param boton El botón a modificar.
     * @param colorFondo Color de fondo.
     * @param colorTexto Color del texto.
     */
    public static void cambiarColorBoton(JButton boton, Color colorFondo, Color colorTexto) {
        boton.setOpaque(true); // Asegura que el fondo sea visible
        boton.setBorderPainted(false); // Opcional: elimina bordes si es necesario
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
    }

    /**
     * Actualiza los colores de un conjunto de botones, destacando uno como
     * activo.
     *
     * @param botonActivo El botón que se mostrará como activo.
     * @param botones Todos los botones del grupo.
     */
    public static void actualizarColoresBotones(JButton botonActivo, JButton... botones) {
        for (JButton boton : botones) {
            if (boton.equals(botonActivo)) {
                cambiarColorBoton(boton, COLOR_ACTIVO_FONDO, COLOR_ACTIVO_TEXTO);
            } else {
                cambiarColorBoton(boton, COLOR_INACTIVO_FONDO, COLOR_INACTIVO_TEXTO);
            }
        }
    }

    public static void setImageLabel(JLabel labelName, String root) {
        setImageLabel(labelName, root, labelName.getWidth(), labelName.getHeight());
    }

    public static void setImageLabel(JLabel labelName, String root, int width, int height) {
        try {
            ImageIcon image = new ImageIcon(root);
            int finalWidth = width > 0 ? width : 210; // Usar el ancho proporcionado o un valor predeterminado
            int finalHeight = height > 0 ? height : 320; // Usar el alto proporcionado o un valor predeterminado

            Icon icon = new ImageIcon(image.getImage().getScaledInstance(finalWidth, finalHeight, Image.SCALE_SMOOTH));
            labelName.setIcon(icon);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }

    public static void cargarLogo(JLabel label, String nombreLogo) {
        setImageLabel(label, LOGO_PATH_BASE + nombreLogo);
    }

}
