package com.stylight.urllookup.repository;

import com.google.common.collect.HashBiMap;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Repository
public class LookupRepository {

    private static final String CLASSPATH_STATIC = "static";
    private static final String MAPPING_TABLE_FILE = "mapping-table.csv";

    public HashMap<String, String> getDictionaryMap() throws IOException {

        HashMap<String, String> hashMap = new HashMap<>();
        ClassPathResource classPathResource = new ClassPathResource(CLASSPATH_STATIC + File.separator + MAPPING_TABLE_FILE);
        File file = classPathResource.getFile();

        CsvReader csvReader = new CsvReader();
        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow csvRow;
            while ((csvRow = csvParser.nextRow()) != null) {
                hashMap.put(csvRow.getField(0), csvRow.getField(1));
            }
        }

        return hashMap;
    }
}