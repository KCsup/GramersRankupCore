package org.kcsup.gramersrankupcore.util;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcore.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Manager {

    public final Main main;

    private final File dataFile;

    public Manager(Main main, String dataPath, JSONObject  fileStructure) {
        this.main = main;
        this.dataFile = filesCheck(dataPath, fileStructure);

        main.getManagers().add(this);
    }

    private File filesCheck(String dataPath, JSONObject fileStructure) {
        if(dataPath == null || fileStructure == null) return null;

        String tempDataPath = main.getDataFolder() + dataPath;
        File returnDataFile = new File(tempDataPath);
            try {
                if(returnDataFile.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(returnDataFile);
                    fileWriter.write(fileStructure.toString());
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return returnDataFile;
    }

    public void updateDataFile(JSONObject updatedFile) {
        if(dataFile == null) return;

        try {
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(updatedFile.toString());
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDataFile() {
        if(dataFile == null) return null;

        try {
            FileReader fileReader = new FileReader(dataFile);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            fileReader.close();

            return file;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startup() { }
    public void shutdown() { }
}
