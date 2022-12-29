package org.kcsup.gramersrankupcore.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.gramersrankupcore.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Manager {

    public final Main main;
    public final File dataFile;

    public Manager(Main main, String dataPath, JSONObject  fileStructure) {
        this.main = main;
        this.dataFile = filesCheck(dataPath, fileStructure);
    }

    private File filesCheck(String dataPath, JSONObject fileStructure) {
        String tempDataPath = main.getDataFolder() + dataPath;
        File returnDataFile = new File(tempDataPath);
        if(!returnDataFile.exists()) {
            try {
                returnDataFile.createNewFile();

                FileWriter fileWriter = new FileWriter(returnDataFile);
                fileWriter.write(fileStructure.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return returnDataFile;
    }
}
