package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import com.taskforge.worker.job.execution.JobExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class CsvImportJobHandler implements JobHandler {

    @Override
    public JobType supportedType() {
        return JobType.CSV_IMPORT;
    }

    @Override
    public void execute(JobExecution job) {

        Object filePathValue = job.payload().get("filePath");

        if (filePathValue == null) {
            throw new IllegalArgumentException(
                    "CSV_IMPORT job requires 'filePath' in payload"
            );
        }

        Path filePath = Path.of(filePathValue.toString());

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException(
                    "CSV file does not exist: " + filePath
            );
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {

            String header = reader.readLine();

            if (header == null) {
                throw new IllegalArgumentException(
                        "CSV file is empty: " + filePath
                );
            }

            int processedRows = 0;

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                processRow(line);

                processedRows++;
            }

            log.info(
                    "CSV import completed for job {}. File: {}, processed rows: {}",
                    job.id(),
                    filePath.toAbsolutePath(),
                    processedRows
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to process CSV file for job " + job.id(),
                    exception
            );
        }
    }

    private void processRow(String line) {

        String[] columns = line.split(",");

        log.debug(
                "Processing CSV row with {} columns",
                columns.length
        );
    }
}