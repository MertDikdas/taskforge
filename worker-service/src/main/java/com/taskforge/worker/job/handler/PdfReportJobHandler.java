package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import com.taskforge.worker.job.exception.NonRetryableJobException;
import com.taskforge.worker.job.exception.RetryableJobException;
import com.taskforge.worker.job.execution.JobExecution;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
public class PdfReportJobHandler implements JobHandler {

    private static final Path OUTPUT_DIRECTORY =
            Path.of("data", "output");

    @Override
    public JobType supportedType() {
        return JobType.PDF_REPORT;
    }

    @Override
    public void execute(JobExecution job) {

        Map<String, Object> payload = job.payload();

        String reportType =
                String.valueOf(payload.getOrDefault("reportType", "UNKNOWN"));

        String userId =
                String.valueOf(payload.getOrDefault("userId", "UNKNOWN"));

        try {
            Files.createDirectories(OUTPUT_DIRECTORY);

            Path outputPath = OUTPUT_DIRECTORY.resolve(
                    job.id() + ".pdf"
            );

            createPdf(
                    outputPath,
                    job,
                    reportType,
                    userId
            );

            log.info(
                    "PDF report generated for job {} at {}",
                    job.id(),
                    outputPath.toAbsolutePath()
            );

        }catch (FileAlreadyExistsException exception) {

            throw new NonRetryableJobException(
                    "Output path is not a directory for job " + job.id(),
                    exception
            );

        } catch (IOException exception) {

            throw new RetryableJobException(
                    "Failed to generate PDF for job " + job.id(),
                    exception
            );
        }
    }

    private void createPdf(
            Path outputPath,
            JobExecution job,
            String reportType,
            String userId
    ) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font titleFont =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    );

            PDType1Font bodyFont =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    );

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                content.beginText();

                content.setFont(titleFont, 18);
                content.newLineAtOffset(50, 750);
                content.showText("TaskForge PDF Report");

                content.setFont(bodyFont, 12);

                content.newLineAtOffset(0, -40);
                content.showText(
                        "Job ID: " + job.id()
                );

                content.newLineAtOffset(0, -20);
                content.showText(
                        "Report Type: " + reportType
                );

                content.newLineAtOffset(0, -20);
                content.showText(
                        "User ID: " + userId
                );

                content.newLineAtOffset(0, -20);
                content.showText(
                        "Generated At: " + Instant.now()
                );

                content.endText();
            }catch (FileAlreadyExistsException exception) {

                throw new NonRetryableJobException(
                        "Output path is not a directory for job " + job.id(),
                        exception
                );

            } catch (IOException exception) {

                throw new RetryableJobException(
                        "Failed to generate PDF for job " + job.id(),
                        exception
                );
            }

            document.save(outputPath.toFile());
        }
    }
}