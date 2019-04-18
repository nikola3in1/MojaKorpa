package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVPrinter {

    private Class objClass;
    private Collection data;
    private String fileName;
    private Character separator = ',';

    public CSVPrinter(Class objClass, Collection data, String fileName) {
        this.objClass = objClass;
        this.data = data;
        this.fileName = fileName;
    }

    public CSVPrinter(Class objClass, Collection data, String fileName, Character separator) {
        this.objClass = objClass;
        this.data = data;
        this.fileName = fileName;
        this.separator = separator;
    }

    public boolean print(boolean importantOnly) {
        try {
            PrintWriter out = new PrintWriter(new File(fileName + ".csv"));

            String headers = "";
            Field[] fields;
            if (importantOnly) {
                //Stampamo samo bitna header polja
                fields = new Field[]{objClass.getDeclaredField("name"), objClass.getDeclaredField("shop"),
                    objClass.getDeclaredField("cena"), objClass.getDeclaredField("quantity")};
            } else {
                //Staampamo sva header polja
                fields = objClass.getDeclaredFields();
            }

            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (fields.length - 1 == i) {
                    headers += f.getName();

                } else {
                    headers += f.getName() + separator + " ";
                }
            }
            headers = headers.replace("name", "ime").replace("shop", "prodavnica").replace("quantity", "kvantitet");
            out.println(headers);

            for (Object obj : data) {
                Field[] fieldsData = fields;
                String line = "";

                for (int i = 0; i < fieldsData.length; i++) {
                    Field f = fieldsData[i];
                    f.setAccessible(true);

                    if (fieldsData.length - 1 == i) {
                        line += f.get(obj);
                    } else {
                        line += f.get(obj) + "" + separator + " ";
                    }
                }
                out.println(line);
            }

            out.close();
            return true;
        } catch (IllegalAccessException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(CSVPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(CSVPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
